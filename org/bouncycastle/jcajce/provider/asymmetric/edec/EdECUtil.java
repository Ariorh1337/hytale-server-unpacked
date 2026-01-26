/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPublicKey;

class EdECUtil {
    EdECUtil() {
    }

    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCXDHPublicKey) {
            return ((BCXDHPublicKey)publicKey).engineGetKeyParameters();
        }
        if (publicKey instanceof BCEdDSAPublicKey) {
            return ((BCEdDSAPublicKey)publicKey).engineGetKeyParameters();
        }
        try {
            byte[] byArray = publicKey.getEncoded();
            if (byArray == null) {
                throw new InvalidKeyException("no encoding for EdEC/XDH public key");
            }
            return PublicKeyFactory.createKey(byArray);
        }
        catch (Exception exception) {
            throw new InvalidKeyException("cannot identify EdEC/XDH public key: " + exception.getMessage());
        }
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCXDHPrivateKey) {
            return ((BCXDHPrivateKey)privateKey).engineGetKeyParameters();
        }
        if (privateKey instanceof BCEdDSAPrivateKey) {
            return ((BCEdDSAPrivateKey)privateKey).engineGetKeyParameters();
        }
        try {
            byte[] byArray = privateKey.getEncoded();
            if (byArray == null) {
                throw new InvalidKeyException("no encoding for EdEC/XDH private key");
            }
            return PrivateKeyFactory.createKey(byArray);
        }
        catch (Exception exception) {
            throw new InvalidKeyException("cannot identify EdEC/XDH private key: " + exception.getMessage());
        }
    }
}

