/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.aead.internal.InsecureNonceXChaCha20;
import com.google.crypto.tink.subtle.IndCpaCipher;
import com.google.crypto.tink.subtle.Random;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;

class XChaCha20
implements IndCpaCipher {
    static final int NONCE_LENGTH_IN_BYTES = 24;
    private final InsecureNonceXChaCha20 cipher;

    XChaCha20(byte[] key, int initialCounter) throws InvalidKeyException {
        this.cipher = new InsecureNonceXChaCha20(key, initialCounter);
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
        ByteBuffer output = ByteBuffer.allocate(24 + plaintext.length);
        byte[] nonce = Random.randBytes(24);
        output.put(nonce);
        this.cipher.encrypt(output, nonce, plaintext);
        return output.array();
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws GeneralSecurityException {
        if (ciphertext.length < 24) {
            throw new GeneralSecurityException("ciphertext too short");
        }
        byte[] nonce = Arrays.copyOf(ciphertext, 24);
        ByteBuffer rawCiphertext = ByteBuffer.wrap(ciphertext, 24, ciphertext.length - 24);
        return this.cipher.decrypt(nonce, rawCiphertext);
    }
}

