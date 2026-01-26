/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.slhdsa.ADRS;
import org.bouncycastle.pqc.crypto.slhdsa.Fors;
import org.bouncycastle.pqc.crypto.slhdsa.HT;
import org.bouncycastle.pqc.crypto.slhdsa.IndexedDigest;
import org.bouncycastle.pqc.crypto.slhdsa.SIG;
import org.bouncycastle.pqc.crypto.slhdsa.SIG_FORS;
import org.bouncycastle.pqc.crypto.slhdsa.SIG_XMSS;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAEngine;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class SLHDSASigner
implements MessageSigner {
    private static final byte[] DEFAULT_PREFIX = new byte[]{0, 0};
    private byte[] msgPrefix;
    private SLHDSAPublicKeyParameters pubKey;
    private SLHDSAPrivateKeyParameters privKey;
    private SecureRandom random;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        Object object;
        if (cipherParameters instanceof ParametersWithContext) {
            object = (ParametersWithContext)cipherParameters;
            cipherParameters = ((ParametersWithContext)object).getParameters();
            int n = ((ParametersWithContext)object).getContextLength();
            if (n > 255) {
                throw new IllegalArgumentException("context too long");
            }
            this.msgPrefix = new byte[2 + n];
            this.msgPrefix[0] = 0;
            this.msgPrefix[1] = (byte)n;
            ((ParametersWithContext)object).copyContextTo(this.msgPrefix, 2, n);
        } else {
            this.msgPrefix = DEFAULT_PREFIX;
        }
        if (bl) {
            this.pubKey = null;
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.privKey = (SLHDSAPrivateKeyParameters)parametersWithRandom.getParameters();
                this.random = parametersWithRandom.getRandom();
            } else {
                this.privKey = (SLHDSAPrivateKeyParameters)cipherParameters;
                this.random = null;
            }
            object = this.privKey.getParameters();
        } else {
            this.pubKey = (SLHDSAPublicKeyParameters)cipherParameters;
            this.privKey = null;
            this.random = null;
            object = this.pubKey.getParameters();
        }
        if (((SLHDSAParameters)object).isPreHash()) {
            throw new IllegalArgumentException("\"pure\" slh-dsa must use non pre-hash parameters");
        }
    }

    @Override
    public byte[] generateSignature(byte[] byArray) {
        SLHDSAEngine sLHDSAEngine = this.privKey.getParameters().getEngine();
        sLHDSAEngine.init(this.privKey.pk.seed);
        byte[] byArray2 = new byte[sLHDSAEngine.N];
        if (this.random != null) {
            this.random.nextBytes(byArray2);
        } else {
            System.arraycopy(this.privKey.pk.seed, 0, byArray2, 0, byArray2.length);
        }
        return SLHDSASigner.internalGenerateSignature(this.privKey, this.msgPrefix, byArray, byArray2);
    }

    @Override
    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        return SLHDSASigner.internalVerifySignature(this.pubKey, this.msgPrefix, byArray, byArray2);
    }

    protected boolean internalVerifySignature(byte[] byArray, byte[] byArray2) {
        return SLHDSASigner.internalVerifySignature(this.pubKey, null, byArray, byArray2);
    }

    private static boolean internalVerifySignature(SLHDSAPublicKeyParameters sLHDSAPublicKeyParameters, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        SLHDSAEngine sLHDSAEngine = sLHDSAPublicKeyParameters.getParameters().getEngine();
        sLHDSAEngine.init(sLHDSAPublicKeyParameters.getSeed());
        ADRS aDRS = new ADRS();
        if ((1 + sLHDSAEngine.K * (1 + sLHDSAEngine.A) + sLHDSAEngine.H + sLHDSAEngine.D * sLHDSAEngine.WOTS_LEN) * sLHDSAEngine.N != byArray3.length) {
            return false;
        }
        SIG sIG = new SIG(sLHDSAEngine.N, sLHDSAEngine.K, sLHDSAEngine.A, sLHDSAEngine.D, sLHDSAEngine.H_PRIME, sLHDSAEngine.WOTS_LEN, byArray3);
        byte[] byArray4 = sIG.getR();
        SIG_FORS[] sIG_FORSArray = sIG.getSIG_FORS();
        SIG_XMSS[] sIG_XMSSArray = sIG.getSIG_HT();
        IndexedDigest indexedDigest = sLHDSAEngine.H_msg(byArray4, sLHDSAPublicKeyParameters.getSeed(), sLHDSAPublicKeyParameters.getRoot(), byArray, byArray2);
        byte[] byArray5 = indexedDigest.digest;
        long l = indexedDigest.idx_tree;
        int n = indexedDigest.idx_leaf;
        aDRS.setTypeAndClear(3);
        aDRS.setLayerAddress(0);
        aDRS.setTreeAddress(l);
        aDRS.setKeyPairAddress(n);
        byte[] byArray6 = new Fors(sLHDSAEngine).pkFromSig(sIG_FORSArray, byArray5, sLHDSAPublicKeyParameters.getSeed(), aDRS);
        aDRS.setTypeAndClear(2);
        aDRS.setLayerAddress(0);
        aDRS.setTreeAddress(l);
        aDRS.setKeyPairAddress(n);
        HT hT = new HT(sLHDSAEngine, null, sLHDSAPublicKeyParameters.getSeed());
        return hT.verify(byArray6, sIG_XMSSArray, sLHDSAPublicKeyParameters.getSeed(), l, n, sLHDSAPublicKeyParameters.getRoot());
    }

    protected byte[] internalGenerateSignature(byte[] byArray, byte[] byArray2) {
        return SLHDSASigner.internalGenerateSignature(this.privKey, null, byArray, byArray2);
    }

    private static byte[] internalGenerateSignature(SLHDSAPrivateKeyParameters sLHDSAPrivateKeyParameters, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        SLHDSAEngine sLHDSAEngine = sLHDSAPrivateKeyParameters.getParameters().getEngine();
        sLHDSAEngine.init(sLHDSAPrivateKeyParameters.pk.seed);
        Fors fors = new Fors(sLHDSAEngine);
        byte[] byArray4 = sLHDSAEngine.PRF_msg(sLHDSAPrivateKeyParameters.sk.prf, byArray3, byArray, byArray2);
        IndexedDigest indexedDigest = sLHDSAEngine.H_msg(byArray4, sLHDSAPrivateKeyParameters.pk.seed, sLHDSAPrivateKeyParameters.pk.root, byArray, byArray2);
        byte[] byArray5 = indexedDigest.digest;
        long l = indexedDigest.idx_tree;
        int n = indexedDigest.idx_leaf;
        ADRS aDRS = new ADRS();
        aDRS.setTypeAndClear(3);
        aDRS.setTreeAddress(l);
        aDRS.setKeyPairAddress(n);
        SIG_FORS[] sIG_FORSArray = fors.sign(byArray5, sLHDSAPrivateKeyParameters.sk.seed, sLHDSAPrivateKeyParameters.pk.seed, aDRS);
        aDRS = new ADRS();
        aDRS.setTypeAndClear(3);
        aDRS.setTreeAddress(l);
        aDRS.setKeyPairAddress(n);
        byte[] byArray6 = fors.pkFromSig(sIG_FORSArray, byArray5, sLHDSAPrivateKeyParameters.pk.seed, aDRS);
        ADRS aDRS2 = new ADRS();
        aDRS2.setTypeAndClear(2);
        HT hT = new HT(sLHDSAEngine, sLHDSAPrivateKeyParameters.getSeed(), sLHDSAPrivateKeyParameters.getPublicSeed());
        byte[] byArray7 = hT.sign(byArray6, l, n);
        byte[][] byArrayArray = new byte[sIG_FORSArray.length + 2][];
        byArrayArray[0] = byArray4;
        for (int i = 0; i != sIG_FORSArray.length; ++i) {
            byArrayArray[1 + i] = Arrays.concatenate(sIG_FORSArray[i].sk, Arrays.concatenate(sIG_FORSArray[i].authPath));
        }
        byArrayArray[byArrayArray.length - 1] = byArray7;
        return Arrays.concatenate(byArrayArray);
    }
}

