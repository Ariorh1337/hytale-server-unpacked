/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.prf;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface Prf {
    public byte[] compute(byte[] var1, int var2) throws GeneralSecurityException;
}

