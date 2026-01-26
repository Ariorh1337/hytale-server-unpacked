/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.KeyWrap;
import com.google.crypto.tink.subtle.EngineFactory;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Deprecated
public class Kwp
implements KeyWrap {
    private final SecretKey aesKey;
    static final int MIN_WRAP_KEY_SIZE = 16;
    static final int MAX_WRAP_KEY_SIZE = 4096;
    static final int ROUNDS = 6;
    static final byte[] PREFIX = new byte[]{-90, 89, 89, -90};

    public Kwp(byte[] key) throws GeneralSecurityException {
        if (key.length != 16 && key.length != 32) {
            throw new GeneralSecurityException("Unsupported key length");
        }
        this.aesKey = new SecretKeySpec(key, "AES");
    }

    private int wrappingSize(int inputSize) {
        int paddingSize = 7 - (inputSize + 7) % 8;
        return inputSize + paddingSize + 8;
    }

    private byte[] computeW(byte[] iv, byte[] key) throws GeneralSecurityException {
        if (key.length <= 8 || key.length > 0x7FFFFFEF || iv.length != 8) {
            throw new GeneralSecurityException("computeW called with invalid parameters");
        }
        byte[] data = new byte[this.wrappingSize(key.length)];
        System.arraycopy(iv, 0, data, 0, iv.length);
        System.arraycopy(key, 0, data, 8, key.length);
        int blocks = data.length / 8 - 1;
        Cipher aes = EngineFactory.CIPHER.getInstance("AES/ECB/NoPadding");
        aes.init(1, this.aesKey);
        byte[] block = new byte[16];
        System.arraycopy(data, 0, block, 0, 8);
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < blocks; ++j) {
                System.arraycopy(data, 8 * (j + 1), block, 8, 8);
                int length = aes.doFinal(block, 0, 16, block);
                assert (length == 16);
                int roundConst = i * blocks + j + 1;
                for (int b = 0; b < 4; ++b) {
                    int n = 7 - b;
                    block[n] = (byte)(block[n] ^ (byte)(roundConst & 0xFF));
                    roundConst >>>= 8;
                }
                System.arraycopy(block, 8, data, 8 * (j + 1), 8);
            }
        }
        System.arraycopy(block, 0, data, 0, 8);
        return data;
    }

    private byte[] invertW(byte[] wrapped) throws GeneralSecurityException {
        if (wrapped.length < 24 || wrapped.length % 8 != 0) {
            throw new GeneralSecurityException("Incorrect data size");
        }
        byte[] data = Arrays.copyOf(wrapped, wrapped.length);
        int blocks = data.length / 8 - 1;
        Cipher aes = EngineFactory.CIPHER.getInstance("AES/ECB/NoPadding");
        aes.init(2, this.aesKey);
        byte[] block = new byte[16];
        System.arraycopy(data, 0, block, 0, 8);
        for (int i = 5; i >= 0; --i) {
            for (int j = blocks - 1; j >= 0; --j) {
                System.arraycopy(data, 8 * (j + 1), block, 8, 8);
                int roundConst = i * blocks + j + 1;
                for (int b = 0; b < 4; ++b) {
                    int n = 7 - b;
                    block[n] = (byte)(block[n] ^ (byte)(roundConst & 0xFF));
                    roundConst >>>= 8;
                }
                int length = aes.doFinal(block, 0, 16, block);
                assert (length == 16);
                System.arraycopy(block, 8, data, 8 * (j + 1), 8);
            }
        }
        System.arraycopy(block, 0, data, 0, 8);
        return data;
    }

    @Override
    public byte[] wrap(byte[] data) throws GeneralSecurityException {
        if (data.length < 16) {
            throw new GeneralSecurityException("Key size of key to wrap too small");
        }
        if (data.length > 4096) {
            throw new GeneralSecurityException("Key size of key to wrap too large");
        }
        byte[] iv = new byte[8];
        System.arraycopy(PREFIX, 0, iv, 0, PREFIX.length);
        for (int i = 0; i < 4; ++i) {
            iv[4 + i] = (byte)(data.length >> 8 * (3 - i) & 0xFF);
        }
        return this.computeW(iv, data);
    }

    @Override
    public byte[] unwrap(byte[] data) throws GeneralSecurityException {
        if (data.length < this.wrappingSize(16)) {
            throw new GeneralSecurityException("Wrapped key size is too small");
        }
        if (data.length > this.wrappingSize(4096)) {
            throw new GeneralSecurityException("Wrapped key size is too large");
        }
        if (data.length % 8 != 0) {
            throw new GeneralSecurityException("Wrapped key size must be a multiple of 8 bytes");
        }
        byte[] unwrapped = this.invertW(data);
        boolean ok = true;
        for (int i = 0; i < 4; ++i) {
            if (PREFIX[i] == unwrapped[i]) continue;
            ok = false;
        }
        int encodedSize = 0;
        for (int i = 4; i < 8; ++i) {
            encodedSize = (encodedSize << 8) + (unwrapped[i] & 0xFF);
        }
        if (this.wrappingSize(encodedSize) != unwrapped.length) {
            ok = false;
        } else {
            for (int j = 8 + encodedSize; j < unwrapped.length; ++j) {
                if (unwrapped[j] == 0) continue;
                ok = false;
            }
        }
        if (ok) {
            return Arrays.copyOfRange(unwrapped, 8, 8 + encodedSize);
        }
        throw new BadPaddingException("Invalid padding");
    }
}

