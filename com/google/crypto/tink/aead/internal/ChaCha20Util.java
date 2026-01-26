/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

final class ChaCha20Util {
    static final int BLOCK_SIZE_IN_INTS = 16;
    static final int KEY_SIZE_IN_INTS = 8;
    static final int BLOCK_SIZE_IN_BYTES = 64;
    static final int KEY_SIZE_IN_BYTES = 32;
    private static final int[] sigma = ChaCha20Util.toIntArray(new byte[]{101, 120, 112, 97, 110, 100, 32, 51, 50, 45, 98, 121, 116, 101, 32, 107});

    static void setSigmaAndKey(int[] state, int[] key) {
        System.arraycopy(sigma, 0, state, 0, sigma.length);
        System.arraycopy(key, 0, state, sigma.length, 8);
    }

    static void shuffleState(int[] state) {
        for (int i = 0; i < 10; ++i) {
            ChaCha20Util.quarterRound(state, 0, 4, 8, 12);
            ChaCha20Util.quarterRound(state, 1, 5, 9, 13);
            ChaCha20Util.quarterRound(state, 2, 6, 10, 14);
            ChaCha20Util.quarterRound(state, 3, 7, 11, 15);
            ChaCha20Util.quarterRound(state, 0, 5, 10, 15);
            ChaCha20Util.quarterRound(state, 1, 6, 11, 12);
            ChaCha20Util.quarterRound(state, 2, 7, 8, 13);
            ChaCha20Util.quarterRound(state, 3, 4, 9, 14);
        }
    }

    static void quarterRound(int[] x, int a, int b, int c, int d) {
        int n = a;
        x[n] = x[n] + x[b];
        x[d] = ChaCha20Util.rotateLeft(x[d] ^ x[a], 16);
        int n2 = c;
        x[n2] = x[n2] + x[d];
        x[b] = ChaCha20Util.rotateLeft(x[b] ^ x[c], 12);
        int n3 = a;
        x[n3] = x[n3] + x[b];
        x[d] = ChaCha20Util.rotateLeft(x[d] ^ x[a], 8);
        int n4 = c;
        x[n4] = x[n4] + x[d];
        x[b] = ChaCha20Util.rotateLeft(x[b] ^ x[c], 7);
    }

    static int[] toIntArray(byte[] input) {
        if (input.length % 4 != 0) {
            throw new IllegalArgumentException("invalid input length");
        }
        IntBuffer intBuffer = ByteBuffer.wrap(input).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        int[] ret = new int[intBuffer.remaining()];
        intBuffer.get(ret);
        return ret;
    }

    static byte[] toByteArray(int[] input) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(input.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.asIntBuffer().put(input);
        return byteBuffer.array();
    }

    static int[] hChaCha20(int[] key, int[] nonce) {
        int[] state = new int[16];
        ChaCha20Util.setSigmaAndKey(state, key);
        state[12] = nonce[0];
        state[13] = nonce[1];
        state[14] = nonce[2];
        state[15] = nonce[3];
        ChaCha20Util.shuffleState(state);
        state[4] = state[12];
        state[5] = state[13];
        state[6] = state[14];
        state[7] = state[15];
        return Arrays.copyOf(state, 8);
    }

    static byte[] hChaCha20(byte[] key, byte[] nonce) {
        return ChaCha20Util.toByteArray(ChaCha20Util.hChaCha20(ChaCha20Util.toIntArray(key), ChaCha20Util.toIntArray(nonce)));
    }

    private static int rotateLeft(int x, int y) {
        return x << y | x >>> -y;
    }

    private ChaCha20Util() {
    }
}

