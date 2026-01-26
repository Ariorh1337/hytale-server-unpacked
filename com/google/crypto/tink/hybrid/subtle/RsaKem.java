/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid.subtle;

import com.google.crypto.tink.internal.BigIntegerEncoding;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.Cipher;

class RsaKem {
    static final byte[] EMPTY_AAD = new byte[0];
    static final int MIN_RSA_KEY_LENGTH_BITS = 2048;

    private RsaKem() {
    }

    static void validateRsaModulus(BigInteger mod) throws GeneralSecurityException {
        if (mod.bitLength() < 2048) {
            throw new GeneralSecurityException(String.format("RSA key must be of at least size %d bits, but got %d", 2048, mod.bitLength()));
        }
    }

    static int bigIntSizeInBytes(BigInteger mod) {
        return (mod.bitLength() + 7) / 8;
    }

    static byte[] rsaEncrypt(PublicKey publicKey, byte[] x) throws GeneralSecurityException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        rsaCipher.init(1, publicKey);
        try {
            return rsaCipher.doFinal(x);
        }
        catch (RuntimeException e) {
            throw new GeneralSecurityException(e);
        }
    }

    static byte[] rsaDecrypt(PrivateKey privateKey, byte[] x) throws GeneralSecurityException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        rsaCipher.init(2, privateKey);
        try {
            return rsaCipher.doFinal(x);
        }
        catch (RuntimeException e) {
            throw new GeneralSecurityException(e);
        }
    }

    static byte[] generateSecret(BigInteger max) {
        BigInteger r;
        int maxSizeInBytes = RsaKem.bigIntSizeInBytes(max);
        SecureRandom rand = new SecureRandom();
        while ((r = new BigInteger(max.bitLength(), rand)).signum() <= 0 || r.compareTo(max) >= 0) {
        }
        try {
            return BigIntegerEncoding.toBigEndianBytesOfFixedLength(r, maxSizeInBytes);
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to convert BigInteger to byte array", e);
        }
    }

    static KeyPair generateRsaKeyPair(int keySize) {
        KeyPairGenerator rsaGenerator;
        try {
            rsaGenerator = KeyPairGenerator.getInstance("RSA");
            rsaGenerator.initialize(keySize);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No support for RSA algorithm.", e);
        }
        return rsaGenerator.generateKeyPair();
    }
}

