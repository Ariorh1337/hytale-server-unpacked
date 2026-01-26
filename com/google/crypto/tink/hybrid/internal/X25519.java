/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid.internal;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface X25519 {
    public KeyPair generateKeyPair() throws GeneralSecurityException;

    public byte[] computeSharedSecret(byte[] var1, byte[] var2) throws GeneralSecurityException;

    public static final class KeyPair {
        public final byte[] privateKey;
        public final byte[] publicKey;

        public KeyPair(byte[] privateKey, byte[] publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }
}

