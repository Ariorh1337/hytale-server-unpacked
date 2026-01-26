/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.sphincsplus;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusSigner;
import org.bouncycastle.pqc.jcajce.provider.sphincsplus.BCSPHINCSPlusPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.sphincsplus.BCSPHINCSPlusPublicKey;

public class SignatureSpi
extends java.security.SignatureSpi {
    private final Digest digest;
    private final SPHINCSPlusSigner signer;

    protected SignatureSpi(Digest digest, SPHINCSPlusSigner sPHINCSPlusSigner) {
        this.digest = digest;
        this.signer = sPHINCSPlusSigner;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCSPHINCSPlusPublicKey)) {
            throw new InvalidKeyException("unknown public key passed to SPHINCS+");
        }
        BCSPHINCSPlusPublicKey bCSPHINCSPlusPublicKey = (BCSPHINCSPlusPublicKey)publicKey;
        CipherParameters cipherParameters = bCSPHINCSPlusPublicKey.getKeyParams();
        this.signer.init(false, cipherParameters);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.appRandom = secureRandom;
        this.engineInitSign(privateKey);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCSPHINCSPlusPrivateKey) {
            BCSPHINCSPlusPrivateKey bCSPHINCSPlusPrivateKey = (BCSPHINCSPlusPrivateKey)privateKey;
            SPHINCSPlusPrivateKeyParameters sPHINCSPlusPrivateKeyParameters = bCSPHINCSPlusPrivateKey.getKeyParams();
            if (this.appRandom != null) {
                this.signer.init(true, new ParametersWithRandom(sPHINCSPlusPrivateKeyParameters, this.appRandom));
            } else {
                this.signer.init(true, sPHINCSPlusPrivateKeyParameters);
            }
        } else {
            throw new InvalidKeyException("unknown private key passed to SPHINCS+");
        }
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

    public static class Direct
    extends SignatureSpi {
        public Direct() {
            super(new NullDigest(), new SPHINCSPlusSigner());
        }
    }
}

