/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.falcon;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconSigner;
import org.bouncycastle.pqc.jcajce.provider.falcon.BCFalconPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.falcon.BCFalconPublicKey;
import org.bouncycastle.util.Strings;

public class SignatureSpi
extends Signature {
    private ByteArrayOutputStream bOut;
    private FalconSigner signer;
    private SecureRandom random;
    private FalconParameters parameters;

    protected SignatureSpi(FalconSigner falconSigner) {
        super("FALCON");
        this.bOut = new ByteArrayOutputStream();
        this.signer = falconSigner;
        this.parameters = null;
    }

    protected SignatureSpi(FalconSigner falconSigner, FalconParameters falconParameters) {
        super(Strings.toUpperCase(falconParameters.getName()));
        this.parameters = falconParameters;
        this.bOut = new ByteArrayOutputStream();
        this.signer = falconSigner;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        String string;
        if (!(publicKey instanceof BCFalconPublicKey)) {
            try {
                publicKey = new BCFalconPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
            }
            catch (Exception exception) {
                throw new InvalidKeyException("unknown public key passed to Falcon: " + exception.getMessage(), exception);
            }
        }
        BCFalconPublicKey bCFalconPublicKey = (BCFalconPublicKey)publicKey;
        if (this.parameters != null && !(string = Strings.toUpperCase(this.parameters.getName())).equals(bCFalconPublicKey.getAlgorithm())) {
            throw new InvalidKeyException("signature configured for " + string);
        }
        this.signer.init(false, bCFalconPublicKey.getKeyParams());
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        this.engineInitSign(privateKey);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCFalconPrivateKey) {
            String string;
            BCFalconPrivateKey bCFalconPrivateKey = (BCFalconPrivateKey)privateKey;
            FalconPrivateKeyParameters falconPrivateKeyParameters = bCFalconPrivateKey.getKeyParams();
            if (this.parameters != null && !(string = Strings.toUpperCase(this.parameters.getName())).equals(bCFalconPrivateKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + string);
            }
            if (this.random != null) {
                this.signer.init(true, new ParametersWithRandom(falconPrivateKeyParameters, this.random));
            } else {
                this.signer.init(true, falconPrivateKeyParameters);
            }
        } else {
            throw new InvalidKeyException("unknown private key passed to Falcon");
        }
    }

    @Override
    protected void engineUpdate(byte by) throws SignatureException {
        this.bOut.write(by);
    }

    @Override
    protected void engineUpdate(byte[] byArray, int n, int n2) throws SignatureException {
        this.bOut.write(byArray, n, n2);
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            byte[] byArray = this.bOut.toByteArray();
            this.bOut.reset();
            return this.signer.generateSignature(byArray);
        }
        catch (Exception exception) {
            throw new SignatureException(exception.toString());
        }
    }

    @Override
    protected boolean engineVerify(byte[] byArray) throws SignatureException {
        byte[] byArray2 = this.bOut.toByteArray();
        this.bOut.reset();
        return this.signer.verifySignature(byArray2, byArray);
    }

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected void engineSetParameter(String string, Object object) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected Object engineGetParameter(String string) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    public static class Base
    extends SignatureSpi {
        public Base() {
            super(new FalconSigner());
        }
    }

    public static class Falcon1024
    extends SignatureSpi {
        public Falcon1024() {
            super(new FalconSigner(), FalconParameters.falcon_1024);
        }
    }

    public static class Falcon512
    extends SignatureSpi {
        public Falcon512() {
            super(new FalconSigner(), FalconParameters.falcon_512);
        }
    }
}

