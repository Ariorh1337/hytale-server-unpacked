/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAEngine;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;

public class MLDSASigner
implements Signer {
    private static final byte[] EMPTY_CONTEXT = new byte[0];
    private MLDSAPublicKeyParameters pubKey;
    private MLDSAPrivateKeyParameters privKey;
    private SecureRandom random;
    private MLDSAEngine engine;
    private SHAKEDigest msgDigest;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        Object object;
        byte[] byArray = EMPTY_CONTEXT;
        if (cipherParameters instanceof ParametersWithContext) {
            object = (ParametersWithContext)cipherParameters;
            byArray = ((ParametersWithContext)object).getContext();
            cipherParameters = ((ParametersWithContext)object).getParameters();
            if (byArray.length > 255) {
                throw new IllegalArgumentException("context too long");
            }
        }
        if (bl) {
            this.pubKey = null;
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.privKey = (MLDSAPrivateKeyParameters)parametersWithRandom.getParameters();
                this.random = parametersWithRandom.getRandom();
            } else {
                this.privKey = (MLDSAPrivateKeyParameters)cipherParameters;
                this.random = null;
            }
            object = this.privKey.getParameters();
            this.engine = ((MLDSAParameters)object).getEngine(this.random);
            this.engine.initSign(this.privKey.tr, false, byArray);
        } else {
            this.pubKey = (MLDSAPublicKeyParameters)cipherParameters;
            this.privKey = null;
            this.random = null;
            object = this.pubKey.getParameters();
            this.engine = ((MLDSAParameters)object).getEngine(null);
            this.engine.initVerify(this.pubKey.rho, this.pubKey.t1, false, byArray);
        }
        if (((MLDSAParameters)object).isPreHash()) {
            throw new IllegalArgumentException("\"pure\" ml-dsa must use non pre-hash parameters");
        }
        this.reset();
    }

    @Override
    public void update(byte by) {
        this.msgDigest.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.msgDigest.update(byArray, n, n2);
    }

    public byte[] generateMu() throws CryptoException, DataLengthException {
        byte[] byArray = this.engine.generateMu(this.msgDigest);
        this.reset();
        return byArray;
    }

    public byte[] generateMuSignature(byte[] byArray) throws CryptoException, DataLengthException {
        if (byArray.length != 64) {
            throw new DataLengthException("mu value must be 64 bytes");
        }
        byte[] byArray2 = new byte[32];
        if (this.random != null) {
            this.random.nextBytes(byArray2);
        }
        this.msgDigest.reset();
        byte[] byArray3 = this.engine.generateSignature(byArray, this.msgDigest, this.privKey.rho, this.privKey.k, this.privKey.t0, this.privKey.s1, this.privKey.s2, byArray2);
        this.reset();
        return byArray3;
    }

    @Override
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        byte[] byArray = new byte[32];
        if (this.random != null) {
            this.random.nextBytes(byArray);
        }
        byte[] byArray2 = this.engine.generateMu(this.msgDigest);
        byte[] byArray3 = this.engine.generateSignature(byArray2, this.msgDigest, this.privKey.rho, this.privKey.k, this.privKey.t0, this.privKey.s1, this.privKey.s2, byArray);
        this.reset();
        return byArray3;
    }

    public boolean verifyMu(byte[] byArray) {
        if (byArray.length != 64) {
            throw new DataLengthException("mu value must be 64 bytes");
        }
        boolean bl = this.engine.verifyInternalMu(byArray);
        this.reset();
        return bl;
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        boolean bl = this.engine.verifyInternal(byArray, byArray.length, this.msgDigest, this.pubKey.rho, this.pubKey.t1);
        this.reset();
        return bl;
    }

    public boolean verifyMuSignature(byte[] byArray, byte[] byArray2) {
        if (byArray.length != 64) {
            throw new DataLengthException("mu value must be 64 bytes");
        }
        this.msgDigest.reset();
        boolean bl = this.engine.verifyInternalMuSignature(byArray, byArray2, byArray2.length, this.msgDigest, this.pubKey.rho, this.pubKey.t1);
        this.reset();
        return bl;
    }

    @Override
    public void reset() {
        this.msgDigest = this.engine.getShake256Digest();
    }

    protected byte[] internalGenerateSignature(byte[] byArray, byte[] byArray2) {
        MLDSAEngine mLDSAEngine = this.privKey.getParameters().getEngine(this.random);
        mLDSAEngine.initSign(this.privKey.tr, false, null);
        return mLDSAEngine.signInternal(byArray, byArray.length, this.privKey.rho, this.privKey.k, this.privKey.t0, this.privKey.s1, this.privKey.s2, byArray2);
    }

    protected boolean internalVerifySignature(byte[] byArray, byte[] byArray2) {
        MLDSAEngine mLDSAEngine = this.pubKey.getParameters().getEngine(this.random);
        mLDSAEngine.initVerify(this.pubKey.rho, this.pubKey.t1, false, null);
        SHAKEDigest sHAKEDigest = mLDSAEngine.getShake256Digest();
        sHAKEDigest.update(byArray, 0, byArray.length);
        return mLDSAEngine.verifyInternal(byArray2, byArray2.length, sHAKEDigest, this.pubKey.rho, this.pubKey.t1);
    }
}

