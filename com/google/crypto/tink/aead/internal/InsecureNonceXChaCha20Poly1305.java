/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Base;
import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Poly1305Base;
import com.google.crypto.tink.aead.internal.InsecureNonceXChaCha20;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

public final class InsecureNonceXChaCha20Poly1305
extends InsecureNonceChaCha20Poly1305Base {
    public InsecureNonceXChaCha20Poly1305(byte[] key) throws GeneralSecurityException {
        super(key);
    }

    @Override
    InsecureNonceChaCha20Base newChaCha20Instance(byte[] key, int initialCounter) throws InvalidKeyException {
        return new InsecureNonceXChaCha20(key, initialCounter);
    }
}

