/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

public interface CharToByteConverter {
    public String getType();

    public byte[] convert(char[] var1);
}

