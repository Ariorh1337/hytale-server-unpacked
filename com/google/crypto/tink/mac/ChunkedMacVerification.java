/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.mac;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public interface ChunkedMacVerification {
    public void update(ByteBuffer var1) throws GeneralSecurityException;

    public void verifyMac() throws GeneralSecurityException;
}

