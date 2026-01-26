/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.Ed448Signer;
import org.bouncycastle.jcajce.provider.asymmetric.edec.EdECUtil;

public class SignatureSpi
extends java.security.SignatureSpi {
    private static final byte[] EMPTY_CONTEXT = new byte[0];
    private final String algorithm;
    private Signer signer;

    SignatureSpi(String string) {
        this.algorithm = string;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameter = SignatureSpi.getLwEdDSAKeyPublic(publicKey);
        if (asymmetricKeyParameter instanceof Ed25519PublicKeyParameters) {
            this.signer = this.getSigner("Ed25519");
        } else if (asymmetricKeyParameter instanceof Ed448PublicKeyParameters) {
            this.signer = this.getSigner("Ed448");
        } else {
            throw new InvalidKeyException("unsupported public key type");
        }
        this.signer.init(false, asymmetricKeyParameter);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameter = SignatureSpi.getLwEdDSAKeyPrivate(privateKey);
        if (asymmetricKeyParameter instanceof Ed25519PrivateKeyParameters) {
            this.signer = this.getSigner("Ed25519");
        } else if (asymmetricKeyParameter instanceof Ed448PrivateKeyParameters) {
            this.signer = this.getSigner("Ed448");
        } else {
            throw new InvalidKeyException("unsupported private key type");
        }
        this.signer.init(true, asymmetricKeyParameter);
    }

    private static AsymmetricKeyParameter getLwEdDSAKeyPrivate(PrivateKey privateKey) throws InvalidKeyException {
        return EdECUtil.generatePrivateKeyParameter(privateKey);
    }

    private static AsymmetricKeyParameter getLwEdDSAKeyPublic(PublicKey publicKey) throws InvalidKeyException {
        return EdECUtil.generatePublicKeyParameter(publicKey);
    }

    private Signer getSigner(String string) throws InvalidKeyException {
        if (this.algorithm != null && !string.equals(this.algorithm)) {
            throw new InvalidKeyException("inappropriate key for " + this.algorithm);
        }
        if (string.equals("Ed448")) {
            return new Ed448Signer(EMPTY_CONTEXT);
        }
        return new Ed25519Signer();
    }

    @Override
    protected void engineUpdate(byte by) throws SignatureException {
        this.signer.update(by);
    }

    @Override
    protected void engineUpdate(byte[] byArray, int n, int n2) throws SignatureException {
        this.signer.update(byArray, n, n2);
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature();
        }
        catch (CryptoException cryptoException) {
            throw new SignatureException(cryptoException.getMessage());
        }
    }

    @Override
    protected boolean engineVerify(byte[] byArray) throws SignatureException {
        return this.signer.verifySignature(byArray);
    }

    @Override
    protected void engineSetParameter(String string, Object object) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected Object engineGetParameter(String string) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    public static final class Ed25519
    extends SignatureSpi {
        public Ed25519() {
            super("Ed25519");
        }
    }

    public static final class Ed448
    extends SignatureSpi {
        public Ed448() {
            super("Ed448");
        }
    }

    public static final class EdDSA
    extends SignatureSpi {
        public EdDSA() {
            super(null);
        }
    }
}

