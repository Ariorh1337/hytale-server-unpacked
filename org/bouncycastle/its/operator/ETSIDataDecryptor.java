/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.operator;

public interface ETSIDataDecryptor {
    public byte[] decrypt(byte[] var1, byte[] var2, byte[] var3);

    public byte[] getKey();
}

