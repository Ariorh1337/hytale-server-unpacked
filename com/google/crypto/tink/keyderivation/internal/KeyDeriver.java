/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.keyderivation.internal;

import com.google.crypto.tink.Key;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface KeyDeriver {
    public Key deriveKey(byte[] var1) throws GeneralSecurityException;
}

