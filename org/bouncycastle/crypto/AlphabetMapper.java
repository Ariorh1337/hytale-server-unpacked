/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

public interface AlphabetMapper {
    public int getRadix();

    public byte[] convertToIndexes(char[] var1);

    public char[] convertToChars(byte[] var1);
}

