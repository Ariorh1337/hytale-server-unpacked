/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.KeysetHandle;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface KeysetDeriver {
    public KeysetHandle deriveKeyset(byte[] var1) throws GeneralSecurityException;
}

