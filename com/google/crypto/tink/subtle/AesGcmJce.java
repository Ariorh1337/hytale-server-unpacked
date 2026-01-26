/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.AesGcmKey;
import com.google.crypto.tink.aead.internal.AesGcmJceUtil;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.subtle.Random;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

@Immutable
public final class AesGcmJce
implements Aead {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
    private static final int IV_SIZE_IN_BYTES = 12;
    private static final int TAG_SIZE_IN_BYTES = 16;
    private final SecretKey keySpec;
    private final byte[] outputPrefix;

    private AesGcmJce(byte[] key, Bytes outputPrefix) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use AES-GCM in FIPS-mode, as BoringCrypto module is not available.");
        }
        this.keySpec = AesGcmJceUtil.getSecretKey(key);
        this.outputPrefix = outputPrefix.toByteArray();
    }

    public AesGcmJce(byte[] key) throws GeneralSecurityException {
        this(key, Bytes.copyFrom(new byte[0]));
    }

    @AccessesPartialKey
    public static Aead create(AesGcmKey key) throws GeneralSecurityException {
        if (key.getParameters().getIvSizeBytes() != 12) {
            throw new GeneralSecurityException("Expected IV Size 12, got " + key.getParameters().getIvSizeBytes());
        }
        if (key.getParameters().getTagSizeBytes() != 16) {
            throw new GeneralSecurityException("Expected tag Size 16, got " + key.getParameters().getTagSizeBytes());
        }
        return new AesGcmJce(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix());
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        int outputSize;
        if (plaintext == null) {
            throw new NullPointerException("plaintext is null");
        }
        byte[] nonce = Random.randBytes(12);
        AlgorithmParameterSpec params = AesGcmJceUtil.getParams(nonce);
        Cipher cipher = AesGcmJceUtil.getThreadLocalCipher();
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
        AlgorithmParameterSpec params = AesGcmJceUtil.getParams(ciphertext, this.outputPrefix.length, 12);
        Cipher cipher = AesGcmJceUtil.getThreadLocalCipher();
        cipher.init(2, (Key)this.keySpec, params);
        if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
        }
        int offset = this.outputPrefix.length + 12;
        int len = ciphertext.length - this.outputPrefix.length - 12;
        return cipher.doFinal(ciphertext, offset, len);
    }
}

