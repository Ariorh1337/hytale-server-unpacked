/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.subtle;

import com.google.crypto.tink.Aead;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface AeadFactory {
    public int getKeySizeInBytes();

    public Aead createAead(byte[] var1) throws GeneralSecurityException;
}

