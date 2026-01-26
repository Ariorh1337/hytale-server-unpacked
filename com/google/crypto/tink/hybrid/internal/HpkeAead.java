/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid.internal;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface HpkeAead {
    default public byte[] seal(byte[] key, byte[] nonce, byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        return this.seal(key, nonce, plaintext, 0, associatedData);
    }

    public byte[] seal(byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5) throws GeneralSecurityException;

    default public byte[] open(byte[] key, byte[] nonce, byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
        return this.open(key, nonce, ciphertext, 0, associatedData);
    }

    public byte[] open(byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5) throws GeneralSecurityException;

    public byte[] getAeadId() throws GeneralSecurityException;

    public int getKeyLength();

    public int getNonceLength();
}

