/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.DigestUtils;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAEngine;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;

public class HashMLDSASigner
implements Signer {
    private static final byte[] EMPTY_CONTEXT = new byte[0];
    private MLDSAPublicKeyParameters pubKey;
    private MLDSAPrivateKeyParameters privKey;
    private SecureRandom random;
    private MLDSAEngine engine;
    private Digest digest;
    private byte[] digestOIDEncoding;

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
            this.engine.initSign(this.privKey.tr, true, byArray);
        } else {
            this.pubKey = (MLDSAPublicKeyParameters)cipherParameters;
            this.privKey = null;
            this.random = null;
            object = this.pubKey.getParameters();
            this.engine = ((MLDSAParameters)object).getEngine(null);
            this.engine.initVerify(this.pubKey.rho, this.pubKey.t1, true, byArray);
        }
        this.initDigest((MLDSAParameters)object);
    }

    private void initDigest(MLDSAParameters mLDSAParameters) {
        this.digest = HashMLDSASigner.createDigest(mLDSAParameters);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = DigestUtils.getDigestOid(this.digest.getAlgorithmName());
        try {
            this.digestOIDEncoding = aSN1ObjectIdentifier.getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new IllegalStateException("oid encoding failed: " + iOException.getMessage());
        }
    }

    @Override
    public void update(byte by) {
        this.digest.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.digest.update(byArray, n, n2);
    }

    @Override
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        SHAKEDigest sHAKEDigest = this.finishPreHash();
        byte[] byArray = new byte[32];
        if (this.random != null) {
            this.random.nextBytes(byArray);
        }
        byte[] byArray2 = this.engine.generateMu(sHAKEDigest);
        return this.engine.generateSignature(byArray2, sHAKEDigest, this.privKey.rho, this.privKey.k, this.privKey.t0, this.privKey.s1, this.privKey.s2, byArray);
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        SHAKEDigest sHAKEDigest = this.finishPreHash();
        return this.engine.verifyInternal(byArray, byArray.length, sHAKEDigest, this.pubKey.rho, this.pubKey.t1);
    }

    @Override
    public void reset() {
        this.digest.reset();
    }

    private SHAKEDigest finishPreHash() {
        byte[] byArray = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray, 0);
        SHAKEDigest sHAKEDigest = this.engine.getShake256Digest();
        sHAKEDigest.update(this.digestOIDEncoding, 0, this.digestOIDEncoding.length);
        sHAKEDigest.update(byArray, 0, byArray.length);
        return sHAKEDigest;
    }

    private static Digest createDigest(MLDSAParameters mLDSAParameters) {
        switch (mLDSAParameters.getType()) {
            case 0: 
            case 1: {
                return new SHA512Digest();
            }
        }
        throw new IllegalArgumentException("unknown parameters type");
    }
}

