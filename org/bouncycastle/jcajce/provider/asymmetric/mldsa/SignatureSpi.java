/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.jcajce.MLDSAProxyPrivateKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSASigner;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;

public class SignatureSpi
extends BaseDeterministicOrRandomSignature {
    protected MLDSASigner signer;
    protected MLDSAParameters parameters;

    protected SignatureSpi(MLDSASigner mLDSASigner) {
        super("MLDSA");
        this.signer = mLDSASigner;
        this.parameters = null;
    }

    protected SignatureSpi(MLDSASigner mLDSASigner, MLDSAParameters mLDSAParameters) {
        super(MLDSAParameterSpec.fromName(mLDSAParameters.getName()).getName());
        this.signer = mLDSASigner;
        this.parameters = mLDSAParameters;
    }

    @Override
    protected void verifyInit(PublicKey publicKey) throws InvalidKeyException {
        Object object;
        if (publicKey instanceof BCMLDSAPublicKey) {
            object = (BCMLDSAPublicKey)publicKey;
            this.keyParams = ((BCMLDSAPublicKey)object).getKeyParams();
        } else {
            try {
                object = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
                this.keyParams = PublicKeyFactory.createKey((SubjectPublicKeyInfo)object);
                publicKey = new BCMLDSAPublicKey((MLDSAPublicKeyParameters)this.keyParams);
            }
            catch (Exception exception) {
                throw new InvalidKeyException("unknown public key passed to ML-DSA");
            }
        }
        if (this.parameters != null && !((String)(object = MLDSAParameterSpec.fromName(this.parameters.getName()).getName())).equals(publicKey.getAlgorithm())) {
            throw new InvalidKeyException("signature configured for " + (String)object);
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
        } else if (privateKey instanceof MLDSAProxyPrivateKey && this instanceof MLDSACalcMu) {
            String string;
            MLDSAProxyPrivateKey mLDSAProxyPrivateKey = (MLDSAProxyPrivateKey)privateKey;
            MLDSAPublicKey mLDSAPublicKey = mLDSAProxyPrivateKey.getPublicKey();
            try {
                this.keyParams = PublicKeyFactory.createKey(mLDSAPublicKey.getEncoded());
            }
            catch (IOException iOException) {
                throw new InvalidKeyException(iOException.getMessage());
            }
            if (this.parameters != null && !(string = MLDSAParameterSpec.fromName(this.parameters.getName()).getName()).equals(mLDSAPublicKey.getAlgorithm())) {
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
    extends SignatureSpi {
        public MLDSA() {
            super(new MLDSASigner());
        }
    }

    public static class MLDSA44
    extends SignatureSpi {
        public MLDSA44() {
            super(new MLDSASigner(), MLDSAParameters.ml_dsa_44);
        }
    }

    public static class MLDSA65
    extends SignatureSpi {
        public MLDSA65() {
            super(new MLDSASigner(), MLDSAParameters.ml_dsa_65);
        }
    }

    public static class MLDSA87
    extends SignatureSpi {
        public MLDSA87() throws NoSuchAlgorithmException {
            super(new MLDSASigner(), MLDSAParameters.ml_dsa_87);
        }
    }

    public static class MLDSACalcMu
    extends SignatureSpi {
        public MLDSACalcMu() {
            super(new MLDSASigner());
        }

        @Override
        protected byte[] engineSign() throws SignatureException {
            try {
                return this.signer.generateMu();
            }
            catch (Exception exception) {
                throw new SignatureException(exception.toString());
            }
        }

        @Override
        protected boolean engineVerify(byte[] byArray) throws SignatureException {
            return this.signer.verifyMu(byArray);
        }
    }

    public static class MLDSAExtMu
    extends SignatureSpi {
        private ByteArrayOutputStream bOut = new ByteArrayOutputStream(64);

        public MLDSAExtMu() {
            super(new MLDSASigner());
        }

        @Override
        protected void updateEngine(byte by) throws SignatureException {
            this.bOut.write(by);
        }

        @Override
        protected void updateEngine(byte[] byArray, int n, int n2) throws SignatureException {
            this.bOut.write(byArray, n, n2);
        }

        @Override
        protected byte[] engineSign() throws SignatureException {
            try {
                byte[] byArray = this.bOut.toByteArray();
                this.bOut.reset();
                return this.signer.generateMuSignature(byArray);
            }
            catch (DataLengthException dataLengthException) {
                throw new SignatureException(dataLengthException.getMessage());
            }
            catch (Exception exception) {
                throw new SignatureException(exception.toString());
            }
        }

        @Override
        protected boolean engineVerify(byte[] byArray) throws SignatureException {
            byte[] byArray2 = this.bOut.toByteArray();
            this.bOut.reset();
            try {
                return this.signer.verifyMuSignature(byArray2, byArray);
            }
            catch (DataLengthException dataLengthException) {
                throw new SignatureException(dataLengthException.getMessage());
            }
        }
    }
}

