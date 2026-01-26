/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface PublicKeySign {
    public byte[] sign(byte[] var1) throws GeneralSecurityException;
}

