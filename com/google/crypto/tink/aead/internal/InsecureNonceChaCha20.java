/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.aead.internal.ChaCha20Util;
import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Base;
import java.security.InvalidKeyException;

public class InsecureNonceChaCha20
extends InsecureNonceChaCha20Base {
    public InsecureNonceChaCha20(byte[] key, int initialCounter) throws InvalidKeyException {
        super(key, initialCounter);
    }

    @Override
    public int[] createInitialState(int[] nonce, int counter) {
        if (nonce.length != this.nonceSizeInBytes() / 4) {
            throw new IllegalArgumentException(String.format("ChaCha20 uses 96-bit nonces, but got a %d-bit nonce", nonce.length * 32));
        }
        int[] state = new int[16];
        ChaCha20Util.setSigmaAndKey(state, this.key);
        state[12] = counter;
        System.arraycopy(nonce, 0, state, 13, nonce.length);
        return state;
    }

    @Override
    public int nonceSizeInBytes() {
        return 12;
    }
}

