/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface DeterministicAead {
    public byte[] encryptDeterministically(byte[] var1, byte[] var2) throws GeneralSecurityException;

    public byte[] decryptDeterministically(byte[] var1, byte[] var2) throws GeneralSecurityException;
}

