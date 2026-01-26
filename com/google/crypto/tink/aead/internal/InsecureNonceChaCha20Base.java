/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.aead.internal.ChaCha20Util;
import com.google.crypto.tink.subtle.Bytes;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

abstract class InsecureNonceChaCha20Base {
    int[] key;
    private final int initialCounter;

    public InsecureNonceChaCha20Base(byte[] key, int initialCounter) throws InvalidKeyException {
        if (key.length != 32) {
            throw new InvalidKeyException("The key length in bytes must be 32.");
        }
        this.key = ChaCha20Util.toIntArray(key);
        this.initialCounter = initialCounter;
    }

    abstract int[] createInitialState(int[] var1, int var2);

    abstract int nonceSizeInBytes();

    public byte[] encrypt(byte[] nonce, byte[] plaintext) throws GeneralSecurityException {
        ByteBuffer ciphertext = ByteBuffer.allocate(plaintext.length);
        this.encrypt(ciphertext, nonce, plaintext);
        return ciphertext.array();
    }

    public void encrypt(ByteBuffer output, byte[] nonce, byte[] plaintext) throws GeneralSecurityException {
        if (output.remaining() < plaintext.length) {
            throw new IllegalArgumentException("Given ByteBuffer output is too small");
        }
        this.process(nonce, output, ByteBuffer.wrap(plaintext));
    }

    public byte[] decrypt(byte[] nonce, byte[] ciphertext) throws GeneralSecurityException {
        return this.decrypt(nonce, ByteBuffer.wrap(ciphertext));
    }

    public byte[] decrypt(byte[] nonce, ByteBuffer ciphertext) throws GeneralSecurityException {
        ByteBuffer plaintext = ByteBuffer.allocate(ciphertext.remaining());
        this.process(nonce, plaintext, ciphertext);
        return plaintext.array();
    }

    private void process(byte[] nonce, ByteBuffer output, ByteBuffer input) throws GeneralSecurityException {
        if (nonce.length != this.nonceSizeInBytes()) {
            throw new GeneralSecurityException("The nonce length (in bytes) must be " + this.nonceSizeInBytes());
        }
        int length = input.remaining();
        int numBlocks = length / 64 + 1;
        for (int i = 0; i < numBlocks; ++i) {
            ByteBuffer keyStreamBlock = this.chacha20Block(nonce, i + this.initialCounter);
            if (i == numBlocks - 1) {
                Bytes.xor(output, input, keyStreamBlock, length % 64);
                continue;
            }
            Bytes.xor(output, input, keyStreamBlock, 64);
        }
    }

    ByteBuffer chacha20Block(byte[] nonce, int counter) {
        int[] state = this.createInitialState(ChaCha20Util.toIntArray(nonce), counter);
        int[] workingState = (int[])state.clone();
        ChaCha20Util.shuffleState(workingState);
        for (int i = 0; i < state.length; ++i) {
            int n = i;
            state[n] = state[n] + workingState[i];
        }
        ByteBuffer out = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
        out.asIntBuffer().put(state, 0, 16);
        return out;
    }
}

