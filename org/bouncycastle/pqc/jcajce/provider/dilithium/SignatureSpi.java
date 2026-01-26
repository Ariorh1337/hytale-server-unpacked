/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumSigner;
import org.bouncycastle.pqc.jcajce.provider.dilithium.BCDilithiumPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.dilithium.BCDilithiumPublicKey;
import org.bouncycastle.util.Strings;

public class SignatureSpi
extends Signature {
    private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    private DilithiumSigner signer;
    private SecureRandom random;
    private DilithiumParameters parameters;

    protected SignatureSpi(DilithiumSigner dilithiumSigner) {
        super("Dilithium");
        this.signer = dilithiumSigner;
        this.parameters = null;
    }

    protected SignatureSpi(DilithiumSigner dilithiumSigner, DilithiumParameters dilithiumParameters) {
        super(Strings.toUpperCase(dilithiumParameters.getName()));
        this.signer = dilithiumSigner;
        this.parameters = dilithiumParameters;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        String string;
        if (!(publicKey instanceof BCDilithiumPublicKey)) {
            try {
                publicKey = new BCDilithiumPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
            }
            catch (Exception exception) {
                throw new InvalidKeyException("unknown public key passed to Dilithium: " + exception.getMessage(), exception);
            }
        }
        BCDilithiumPublicKey bCDilithiumPublicKey = (BCDilithiumPublicKey)publicKey;
        if (this.parameters != null && !(string = Strings.toUpperCase(this.parameters.getName())).equals(bCDilithiumPublicKey.getAlgorithm())) {
            throw new InvalidKeyException("signature configured for " + string);
        }
        this.signer.init(false, bCDilithiumPublicKey.getKeyParams());
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        this.engineInitSign(privateKey);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCDilithiumPrivateKey) {
            String string;
            BCDilithiumPrivateKey bCDilithiumPrivateKey = (BCDilithiumPrivateKey)privateKey;
            DilithiumPrivateKeyParameters dilithiumPrivateKeyParameters = bCDilithiumPrivateKey.getKeyParams();
            if (this.parameters != null && !(string = Strings.toUpperCase(this.parameters.getName())).equals(bCDilithiumPrivateKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + string);
            }
            if (this.random != null) {
                this.signer.init(true, new ParametersWithRandom(dilithiumPrivateKeyParameters, this.random));
            } else {
                this.signer.init(true, dilithiumPrivateKeyParameters);
            }
        } else {
            throw new InvalidKeyException("unknown private key passed to Dilithium");
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
            super(new DilithiumSigner());
        }
    }

    public static class Base2
    extends SignatureSpi {
        public Base2() {
            super(new DilithiumSigner(), DilithiumParameters.dilithium2);
        }
    }

    public static class Base3
    extends SignatureSpi {
        public Base3() {
            super(new DilithiumSigner(), DilithiumParameters.dilithium3);
        }
    }

    public static class Base5
    extends SignatureSpi {
        public Base5() throws NoSuchAlgorithmException {
            super(new DilithiumSigner(), DilithiumParameters.dilithium5);
        }
    }
}

