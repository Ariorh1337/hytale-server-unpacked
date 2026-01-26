/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.slhdsa;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.slhdsa.BCSLHDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.slhdsa.BCSLHDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSASigner;

public class SignatureSpi
extends BaseDeterministicOrRandomSignature {
    private final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    private final SLHDSASigner signer;

    protected SignatureSpi(SLHDSASigner sLHDSASigner) {
        super("SLH-DSA");
        this.signer = sLHDSASigner;
    }

    @Override
    protected void verifyInit(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCSLHDSAPublicKey)) {
            throw new InvalidKeyException("unknown public key passed to SLH-DSA");
        }
        BCSLHDSAPublicKey bCSLHDSAPublicKey = (BCSLHDSAPublicKey)publicKey;
        this.keyParams = bCSLHDSAPublicKey.getKeyParams();
    }

    @Override
    protected void signInit(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.appRandom = secureRandom;
        if (!(privateKey instanceof BCSLHDSAPrivateKey)) {
            throw new InvalidKeyException("unknown private key passed to SLH-DSA");
        }
        BCSLHDSAPrivateKey bCSLHDSAPrivateKey = (BCSLHDSAPrivateKey)privateKey;
        this.keyParams = bCSLHDSAPrivateKey.getKeyParams();
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
        AsymmetricKeyParameter asymmetricKeyParameter = this.keyParams;
        if (!(asymmetricKeyParameter instanceof SLHDSAPrivateKeyParameters)) {
            throw new SignatureException("engine initialized for verification");
        }
        try {
            byte[] byArray;
            byte[] byArray2 = byArray = this.signer.generateSignature(this.bOut.toByteArray());
            return byArray2;
        }
        catch (Exception exception) {
            throw new SignatureException(exception.toString());
        }
        finally {
            this.isInitState = true;
            this.bOut.reset();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean engineVerify(byte[] byArray) throws SignatureException {
        AsymmetricKeyParameter asymmetricKeyParameter = this.keyParams;
        if (!(asymmetricKeyParameter instanceof SLHDSAPublicKeyParameters)) {
            throw new SignatureException("engine initialized for signing");
        }
        try {
            boolean bl = this.signer.verifySignature(this.bOut.toByteArray(), byArray);
            return bl;
        }
        finally {
            this.isInitState = true;
            this.bOut.reset();
        }
    }

    @Override
    protected void reInitialize(boolean bl, CipherParameters cipherParameters) {
        this.signer.init(bl, cipherParameters);
        this.bOut.reset();
    }

    public static class Direct
    extends SignatureSpi {
        public Direct() {
            super(new SLHDSASigner());
        }
    }
}

