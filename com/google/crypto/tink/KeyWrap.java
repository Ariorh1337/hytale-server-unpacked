/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface KeyWrap {
    public byte[] wrap(byte[] var1) throws GeneralSecurityException;

    public byte[] unwrap(byte[] var1) throws GeneralSecurityException;
}

