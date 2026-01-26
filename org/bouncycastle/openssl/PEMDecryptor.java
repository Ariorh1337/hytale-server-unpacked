/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import org.bouncycastle.openssl.PEMException;

public interface PEMDecryptor {
    public byte[] decrypt(byte[] var1, byte[] var2) throws PEMException;
}

