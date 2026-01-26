/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.DigestUtils;
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

public class HashSLHDSASigner
implements Signer {
    private byte[] msgPrefix;
    private SLHDSAPublicKeyParameters pubKey;
    private SLHDSAPrivateKeyParameters privKey;
    private SecureRandom random;
    private Digest digest;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        SLHDSAParameters sLHDSAParameters;
        ParametersWithContext parametersWithContext = null;
        if (cipherParameters instanceof ParametersWithContext) {
            parametersWithContext = (ParametersWithContext)cipherParameters;
            cipherParameters = ((ParametersWithContext)cipherParameters).getParameters();
            if (parametersWithContext.getContextLength() > 255) {
                throw new IllegalArgumentException("context too long");
            }
        }
        if (bl) {
            this.pubKey = null;
            if (cipherParameters instanceof ParametersWithRandom) {
                this.privKey = (SLHDSAPrivateKeyParameters)((ParametersWithRandom)cipherParameters).getParameters();
                this.random = ((ParametersWithRandom)cipherParameters).getRandom();
            } else {
                this.privKey = (SLHDSAPrivateKeyParameters)cipherParameters;
                this.random = null;
            }
            sLHDSAParameters = this.privKey.getParameters();
        } else {
            this.pubKey = (SLHDSAPublicKeyParameters)cipherParameters;
            this.privKey = null;
            this.random = null;
            sLHDSAParameters = this.pubKey.getParameters();
        }
        this.initDigest(sLHDSAParameters, parametersWithContext);
    }

    private void initDigest(SLHDSAParameters sLHDSAParameters, ParametersWithContext parametersWithContext) {
        byte[] byArray;
        this.digest = HashSLHDSASigner.createDigest(sLHDSAParameters);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = DigestUtils.getDigestOid(this.digest.getAlgorithmName());
        try {
            byArray = aSN1ObjectIdentifier.getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new IllegalStateException("oid encoding failed: " + iOException.getMessage());
        }
        int n = parametersWithContext == null ? 0 : parametersWithContext.getContextLength();
        this.msgPrefix = new byte[2 + n + byArray.length];
        this.msgPrefix[0] = 1;
        this.msgPrefix[1] = (byte)n;
        if (parametersWithContext != null) {
            parametersWithContext.copyContextTo(this.msgPrefix, 2, n);
        }
        System.arraycopy(byArray, 0, this.msgPrefix, 2 + n, byArray.length);
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
        SLHDSAEngine sLHDSAEngine = this.privKey.getParameters().getEngine();
        sLHDSAEngine.init(this.privKey.pk.seed);
        byte[] byArray = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray, 0);
        byte[] byArray2 = new byte[sLHDSAEngine.N];
        if (this.random != null) {
            this.random.nextBytes(byArray2);
        } else {
            System.arraycopy(this.privKey.pk.seed, 0, byArray2, 0, byArray2.length);
        }
        return HashSLHDSASigner.internalGenerateSignature(this.privKey, this.msgPrefix, byArray, byArray2);
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        byte[] byArray2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray2, 0);
        return HashSLHDSASigner.internalVerifySignature(this.pubKey, this.msgPrefix, byArray2, byArray);
    }

    @Override
    public void reset() {
        this.digest.reset();
    }

    protected byte[] internalGenerateSignature(byte[] byArray, byte[] byArray2) {
        return HashSLHDSASigner.internalGenerateSignature(this.privKey, null, byArray, byArray2);
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

    protected boolean internalVerifySignature(byte[] byArray, byte[] byArray2) {
        return HashSLHDSASigner.internalVerifySignature(this.pubKey, null, byArray, byArray2);
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

    private static Digest createDigest(SLHDSAParameters sLHDSAParameters) {
        switch (sLHDSAParameters.getType()) {
            case 0: {
                String string = sLHDSAParameters.getName();
                if (string.startsWith("sha2")) {
                    if (SLHDSAParameters.sha2_128f == sLHDSAParameters || SLHDSAParameters.sha2_128s == sLHDSAParameters) {
                        return SHA256Digest.newInstance();
                    }
                    return new SHA512Digest();
                }
                if (SLHDSAParameters.shake_128f == sLHDSAParameters || SLHDSAParameters.shake_128s == sLHDSAParameters) {
                    return new SHAKEDigest(128);
                }
                return new SHAKEDigest(256);
            }
            case 1: {
                return SHA256Digest.newInstance();
            }
            case 2: {
                return new SHA512Digest();
            }
            case 3: {
                return new SHAKEDigest(128);
            }
            case 4: {
                return new SHAKEDigest(256);
            }
        }
        throw new IllegalArgumentException("unknown parameters type");
    }
}

