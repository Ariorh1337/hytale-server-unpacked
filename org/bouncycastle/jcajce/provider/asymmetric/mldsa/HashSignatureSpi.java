/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.HashMLDSASigner;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;

public class HashSignatureSpi
extends BaseDeterministicOrRandomSignature {
    private HashMLDSASigner signer;
    private MLDSAParameters parameters;

    protected HashSignatureSpi(HashMLDSASigner hashMLDSASigner) {
        super("HashMLDSA");
        this.signer = hashMLDSASigner;
        this.parameters = null;
    }

    protected HashSignatureSpi(HashMLDSASigner hashMLDSASigner, MLDSAParameters mLDSAParameters) {
        super(MLDSAParameterSpec.fromName(mLDSAParameters.getName()).getName());
        this.signer = hashMLDSASigner;
        this.parameters = mLDSAParameters;
    }

    @Override
    protected void verifyInit(PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCMLDSAPublicKey) {
            String string;
            BCMLDSAPublicKey bCMLDSAPublicKey = (BCMLDSAPublicKey)publicKey;
            this.keyParams = bCMLDSAPublicKey.getKeyParams();
            if (this.parameters != null && !(string = MLDSAParameterSpec.fromName(this.parameters.getName()).getName()).equals(bCMLDSAPublicKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + string);
            }
        } else {
            throw new InvalidKeyException("unknown public key passed to ML-DSA");
        }
    }

    @Override
    protected void signInit(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.appRandom = secureRandom;
        if (privateKey instanceof BCMLDSAPrivateKey) {
            String string;
            BCMLDSAPrivateKey bCMLDSAPrivateKey = (BCMLDSAPrivateKey)privateKey;
            this.keyParams = bCMLDSAPrivateKey.getKeyParams();
            if (this.parameters != null && !(string = MLDSAParameterSpec.fromName(this.parameters.getName()).getName()).equals(bCMLDSAPrivateKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + string);
            }
        } else {
            throw new InvalidKeyException("unknown private key passed to ML-DSA");
        }
    }

    @Override
    protected void updateEngine(byte by) throws SignatureException {
        this.signer.update(by);
    }

    @Override
    protected void updateEngine(byte[] byArray, int n, int n2) throws SignatureException {
        this.signer.update(byArray, n, n2);
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature();
        }
        catch (Exception exception) {
            throw new SignatureException(exception.toString());
        }
    }

    @Override
    protected boolean engineVerify(byte[] byArray) throws SignatureException {
        return this.signer.verifySignature(byArray);
    }

    @Override
    protected void reInitialize(boolean bl, CipherParameters cipherParameters) {
        this.signer.init(bl, cipherParameters);
    }

    public static class MLDSA
    extends HashSignatureSpi {
        public MLDSA() {
            super(new HashMLDSASigner());
        }
    }

    public static class MLDSA44
    extends HashSignatureSpi {
        public MLDSA44() {
            super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_44_with_sha512);
        }
    }

    public static class MLDSA65
    extends HashSignatureSpi {
        public MLDSA65() {
            super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_65_with_sha512);
        }
    }

    public static class MLDSA87
    extends HashSignatureSpi {
        public MLDSA87() throws NoSuchAlgorithmException {
            super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_87_with_sha512);
        }
    }
}

