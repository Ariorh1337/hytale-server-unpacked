/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.mayo;

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
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoSigner;
import org.bouncycastle.pqc.jcajce.provider.mayo.BCMayoPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.mayo.BCMayoPublicKey;
import org.bouncycastle.util.Strings;

public class SignatureSpi
extends Signature {
    private final ByteArrayOutputStream bOut;
    private final MayoSigner signer;
    private SecureRandom random;
    private final MayoParameters parameters;

    protected SignatureSpi(MayoSigner mayoSigner) {
        super("Mayo");
        this.bOut = new ByteArrayOutputStream();
        this.signer = mayoSigner;
        this.parameters = null;
    }

    protected SignatureSpi(MayoSigner mayoSigner, MayoParameters mayoParameters) {
        super(Strings.toUpperCase(mayoParameters.getName()));
        this.parameters = mayoParameters;
        this.bOut = new ByteArrayOutputStream();
        this.signer = mayoSigner;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        String string;
        if (!(publicKey instanceof BCMayoPublicKey)) {
            try {
                publicKey = new BCMayoPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
            }
            catch (Exception exception) {
                throw new InvalidKeyException("unknown public key passed to Mayo: " + exception.getMessage());
            }
        }
        BCMayoPublicKey bCMayoPublicKey = (BCMayoPublicKey)publicKey;
        if (this.parameters != null && !(string = Strings.toUpperCase(this.parameters.getName())).equals(bCMayoPublicKey.getAlgorithm())) {
            throw new InvalidKeyException("signature configured for " + string);
        }
        this.signer.init(false, bCMayoPublicKey.getKeyParams());
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        this.engineInitSign(privateKey);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCMayoPrivateKey) {
            String string;
            BCMayoPrivateKey bCMayoPrivateKey = (BCMayoPrivateKey)privateKey;
            MayoPrivateKeyParameters mayoPrivateKeyParameters = bCMayoPrivateKey.getKeyParams();
            if (this.parameters != null && !(string = Strings.toUpperCase(this.parameters.getName())).equals(bCMayoPrivateKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + string);
            }
            if (this.random != null) {
                this.signer.init(true, new ParametersWithRandom(mayoPrivateKeyParameters, this.random));
            } else {
                this.signer.init(true, mayoPrivateKeyParameters);
            }
        } else {
            throw new InvalidKeyException("unknown private key passed to Mayo");
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
            super(new MayoSigner());
        }
    }

    public static class Mayo1
    extends SignatureSpi {
        public Mayo1() {
            super(new MayoSigner(), MayoParameters.mayo1);
        }
    }

    public static class Mayo2
    extends SignatureSpi {
        public Mayo2() {
            super(new MayoSigner(), MayoParameters.mayo2);
        }
    }

    public static class Mayo3
    extends SignatureSpi {
        public Mayo3() {
            super(new MayoSigner(), MayoParameters.mayo3);
        }
    }

    public static class Mayo5
    extends SignatureSpi {
        public Mayo5() {
            super(new MayoSigner(), MayoParameters.mayo5);
        }
    }
}

