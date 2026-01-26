/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.mac;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public interface ChunkedMacComputation {
    public void update(ByteBuffer var1) throws GeneralSecurityException;

    public byte[] computeMac() throws GeneralSecurityException;
}

