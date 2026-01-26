/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface HybridEncrypt {
    public byte[] encrypt(byte[] var1, byte[] var2) throws GeneralSecurityException;
}

