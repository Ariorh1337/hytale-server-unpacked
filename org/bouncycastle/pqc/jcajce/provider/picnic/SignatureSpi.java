/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.picnic;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicSigner;
import org.bouncycastle.pqc.jcajce.provider.picnic.BCPicnicPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.picnic.BCPicnicPublicKey;

public class SignatureSpi
extends Signature {
    private SecureRandom random;
    private Digest digest;
    private PicnicSigner signer;

    protected SignatureSpi(Digest digest, PicnicSigner picnicSigner) {
        super("Picnic");
        this.digest = digest;
        this.signer = picnicSigner;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCPicnicPublicKey)) {
            throw new InvalidKeyException("unknown public key passed to Picnic");
        }
        BCPicnicPublicKey bCPicnicPublicKey = (BCPicnicPublicKey)publicKey;
        PicnicPublicKeyParameters picnicPublicKeyParameters = bCPicnicPublicKey.getKeyParams();
        this.digest.reset();
        this.signer.init(false, picnicPublicKeyParameters);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        this.engineInitSign(privateKey);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof BCPicnicPrivateKey)) {
            throw new InvalidKeyException("unknown private key passed to Picnic");
        }
        BCPicnicPrivateKey bCPicnicPrivateKey = (BCPicnicPrivateKey)privateKey;
        PicnicPrivateKeyParameters picnicPrivateKeyParameters = bCPicnicPrivateKey.getKeyParams();
        this.digest.reset();
        this.signer.init(true, picnicPrivateKeyParameters);
    }

    @Override
    protected void engineUpdate(byte by) throws SignatureException {
        this.digest.update(by);
    }

    @Override
    protected void engineUpdate(byte[] byArray, int n, int n2) throws SignatureException {
        this.digest.update(byArray, n, n2);
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        byte[] byArray = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray, 0);
        try {
            byte[] byArray2 = this.signer.generateSignature(byArray);
            return byArray2;
        }
        catch (Exception exception) {
            throw new SignatureException(exception.toString());
        }
    }

    @Override
    protected boolean engineVerify(byte[] byArray) throws SignatureException {
        byte[] byArray2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray2, 0);
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
            super(new NullDigest(), new PicnicSigner());
        }
    }

    public static class withSha3512
    extends SignatureSpi {
        public withSha3512() {
            super(new SHA3Digest(512), new PicnicSigner());
        }
    }

    public static class withSha512
    extends SignatureSpi {
        public withSha512() {
            super(new SHA512Digest(), new PicnicSigner());
        }
    }

    public static class withShake256
    extends SignatureSpi {
        public withShake256() {
            super(new SHAKEDigest(256), new PicnicSigner());
        }
    }
}

