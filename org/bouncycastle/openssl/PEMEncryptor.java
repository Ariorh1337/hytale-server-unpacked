/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import org.bouncycastle.openssl.PEMException;

public interface PEMEncryptor {
    public String getAlgorithm();

    public byte[] getIV();

    public byte[] encrypt(byte[] var1) throws PEMException;
}

