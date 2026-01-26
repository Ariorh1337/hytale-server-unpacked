/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public interface StreamSegmentDecrypter {
    public void init(ByteBuffer var1, byte[] var2) throws GeneralSecurityException;

    public void decryptSegment(ByteBuffer var1, int var2, boolean var3, ByteBuffer var4) throws GeneralSecurityException;
}

