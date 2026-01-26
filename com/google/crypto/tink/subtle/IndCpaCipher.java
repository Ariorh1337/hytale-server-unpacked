/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import java.security.GeneralSecurityException;

public interface IndCpaCipher {
    public byte[] encrypt(byte[] var1) throws GeneralSecurityException;

    public byte[] decrypt(byte[] var1) throws GeneralSecurityException;
}

