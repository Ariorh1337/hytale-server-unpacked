/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.operator;

public interface ETSIDataEncryptor {
    public byte[] encrypt(byte[] var1);

    public byte[] getKey();

    public byte[] getNonce();
}

