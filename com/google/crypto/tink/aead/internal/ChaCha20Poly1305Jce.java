/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.ChaCha20Poly1305Key;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.Hex;
import com.google.crypto.tink.subtle.Random;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Provider;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Immutable
public final class ChaCha20Poly1305Jce
implements Aead {
    private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
    private static final int NONCE_SIZE_IN_BYTES = 12;
    private static final int TAG_SIZE_IN_BYTES = 16;
    private static final int KEY_SIZE_IN_BYTES = 32;
    private static final String CIPHER_NAME = "ChaCha20-Poly1305";
    private static final String KEY_NAME = "ChaCha20";
    private static final byte[] testKey = Hex.decode("808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9f");
    private static final byte[] testNonce = Hex.decode("070000004041424344454647");
    private static final byte[] testCiphertextOfEmpty = Hex.decode("a0784d7a4716f3feb4f64e7f4b39bf04");
    private final SecretKey keySpec;
    private final byte[] outputPrefix;
    private final Provider provider;

    private static boolean isValid(Cipher cipher) {
        try {
            IvParameterSpec params = new IvParameterSpec(testNonce);
            cipher.init(2, (Key)new SecretKeySpec(testKey, KEY_NAME), params);
            byte[] output = cipher.doFinal(testCiphertextOfEmpty);
            if (output.length != 0) {
                return false;
            }
            cipher.init(2, (Key)new SecretKeySpec(testKey, KEY_NAME), params);
            byte[] output2 = cipher.doFinal(testCiphertextOfEmpty);
            return output2.length == 0;
        }
        catch (GeneralSecurityException ex) {
            return false;
        }
    }

    private ChaCha20Poly1305Jce(byte[] key, byte[] outputPrefix, Provider provider) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use ChaCha20Poly1305 in FIPS-mode.");
        }
        if (key.length != 32) {
            throw new InvalidKeyException("The key length in bytes must be 32.");
        }
        this.keySpec = new SecretKeySpec(key, KEY_NAME);
        this.outputPrefix = outputPrefix;
        this.provider = provider;
    }

    @AccessesPartialKey
    public static Aead create(ChaCha20Poly1305Key key) throws GeneralSecurityException {
        Cipher cipher = ChaCha20Poly1305Jce.getValidCipherInstance();
        return new ChaCha20Poly1305Jce(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix().toByteArray(), cipher.getProvider());
    }

    static Cipher getValidCipherInstance() throws GeneralSecurityException {
        Cipher cipher = EngineFactory.CIPHER.getInstance(CIPHER_NAME);
        if (!ChaCha20Poly1305Jce.isValid(cipher)) {
            throw new GeneralSecurityException("JCE does not support algorithm: ChaCha20-Poly1305");
        }
        return cipher;
    }

    static Cipher getCipherInstance(Provider provider) throws GeneralSecurityException {
        return Cipher.getInstance(CIPHER_NAME, provider);
    }

    public static boolean isSupported() {
        try {
            Cipher unused = ChaCha20Poly1305Jce.getValidCipherInstance();
            return true;
        }
        catch (GeneralSecurityException ex) {
            return false;
        }
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        int outputSize;
        if (plaintext == null) {
            throw new NullPointerException("plaintext is null");
        }
        byte[] nonce = Random.randBytes(12);
        IvParameterSpec params = new IvParameterSpec(nonce);
        Cipher cipher = ChaCha20Poly1305Jce.getCipherInstance(this.provider);
        cipher.init(1, (Key)this.keySpec, params);
        if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
        }
        if ((outputSize = cipher.getOutputSize(plaintext.length)) > Integer.MAX_VALUE - this.outputPrefix.length - 12) {
            throw new GeneralSecurityException("plaintext too long");
        }
        int len = this.outputPrefix.length + 12 + outputSize;
        byte[] output = Arrays.copyOf(this.outputPrefix, len);
        System.arraycopy(nonce, 0, output, this.outputPrefix.length, 12);
        int written = cipher.doFinal(plaintext, 0, plaintext.length, output, this.outputPrefix.length + 12);
        if (written != outputSize) {
            throw new GeneralSecurityException("not enough data written");
        }
        return output;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
        if (ciphertext == null) {
            throw new NullPointerException("ciphertext is null");
        }
        if (ciphertext.length < this.outputPrefix.length + 12 + 16) {
            throw new GeneralSecurityException("ciphertext too short");
        }
        if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
            throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
        }
        byte[] nonce = new byte[12];
        System.arraycopy(ciphertext, this.outputPrefix.length, nonce, 0, 12);
        IvParameterSpec params = new IvParameterSpec(nonce);
        Cipher cipher = ChaCha20Poly1305Jce.getCipherInstance(this.provider);
        cipher.init(2, (Key)this.keySpec, params);
        if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
        }
        int offset = this.outputPrefix.length + 12;
        int len = ciphertext.length - this.outputPrefix.length - 12;
        return cipher.doFinal(ciphertext, offset, len);
    }
}

