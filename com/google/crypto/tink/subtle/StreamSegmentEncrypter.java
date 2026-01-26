/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public interface StreamSegmentEncrypter {
    public ByteBuffer getHeader();

    public void encryptSegment(ByteBuffer var1, boolean var2, ByteBuffer var3) throws GeneralSecurityException;

    public void encryptSegment(ByteBuffer var1, ByteBuffer var2, boolean var3, ByteBuffer var4) throws GeneralSecurityException;
}

