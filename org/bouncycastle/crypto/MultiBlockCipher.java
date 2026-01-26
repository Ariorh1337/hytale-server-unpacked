/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;

public interface MultiBlockCipher
extends BlockCipher {
    public int getMultiBlockSize();

    public int processBlocks(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException, IllegalStateException;
}

