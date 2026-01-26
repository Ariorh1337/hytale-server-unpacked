/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MultiBlockCipher;

public abstract class DefaultMultiBlockCipher
implements MultiBlockCipher {
    protected DefaultMultiBlockCipher() {
    }

    @Override
    public int getMultiBlockSize() {
        return this.getBlockSize();
    }

    @Override
    public int processBlocks(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws DataLengthException, IllegalStateException {
        int n4 = 0;
        int n5 = this.getBlockSize();
        int n6 = n2 * n5;
        if (byArray == byArray2) {
            byArray = new byte[n6];
            System.arraycopy(byArray2, n, byArray, 0, n6);
            n = 0;
        }
        for (int i = 0; i != n2; ++i) {
            n4 += this.processBlock(byArray, n, byArray2, n3 + n4);
            n += n5;
        }
        return n4;
    }
}

