/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.CipherHelper;
import com.nimbusds.jose.crypto.opts.CipherMode;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class RSA_OAEP {
    private static final String RSA_OEAP_JCA_ALG = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";

    public static byte[] encryptCEK(RSAPublicKey pub, SecretKey cek, CipherMode mode, Provider provider) throws JOSEException {
        assert (mode == CipherMode.WRAP_UNWRAP || mode == CipherMode.ENCRYPT_DECRYPT);
        try {
            Cipher cipher = CipherHelper.getInstance(RSA_OEAP_JCA_ALG, provider);
            cipher.init(mode.getForJWEEncrypter(), (Key)pub, new SecureRandom());
            if (mode == CipherMode.WRAP_UNWRAP) {
                return cipher.wrap(cek);
            }
            return cipher.doFinal(cek.getEncoded());
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("RSA block size exception: The RSA key is too short, try a longer one", e);
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public static SecretKey decryptCEK(PrivateKey priv, byte[] encryptedCEK, CipherMode mode, Provider provider) throws JOSEException {
        assert (mode == CipherMode.WRAP_UNWRAP || mode == CipherMode.ENCRYPT_DECRYPT);
        try {
            Cipher cipher = CipherHelper.getInstance(RSA_OEAP_JCA_ALG, provider);
            cipher.init(mode.getForJWEDecrypter(), priv);
            if (mode == CipherMode.WRAP_UNWRAP) {
                return (SecretKey)cipher.unwrap(encryptedCEK, "AES", 3);
            }
            return new SecretKeySpec(cipher.doFinal(encryptedCEK), "AES");
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    private RSA_OAEP() {
    }
}

