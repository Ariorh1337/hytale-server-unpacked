/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20;
import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Base;
import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Poly1305Base;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

public final class InsecureNonceChaCha20Poly1305
extends InsecureNonceChaCha20Poly1305Base {
    public InsecureNonceChaCha20Poly1305(byte[] key) throws GeneralSecurityException {
        super(key);
    }

    @Override
    InsecureNonceChaCha20Base newChaCha20Instance(byte[] key, int initialCounter) throws InvalidKeyException {
        return new InsecureNonceChaCha20(key, initialCounter);
    }
}

