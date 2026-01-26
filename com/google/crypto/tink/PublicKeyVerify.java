/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface PublicKeyVerify {
    public void verify(byte[] var1, byte[] var2) throws GeneralSecurityException;
}

