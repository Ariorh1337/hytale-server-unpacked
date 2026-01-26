/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface Mac {
    public byte[] computeMac(byte[] var1) throws GeneralSecurityException;

    public void verifyMac(byte[] var1, byte[] var2) throws GeneralSecurityException;
}

