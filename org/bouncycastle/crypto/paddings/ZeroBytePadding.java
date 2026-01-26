/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class ZeroBytePadding
implements BlockCipherPadding {
    @Override
    public void init(SecureRandom secureRandom) throws IllegalArgumentException {
    }

    @Override
    public String getPaddingName() {
        return "ZeroByte";
    }

    @Override
    public int addPadding(byte[] byArray, int n) {
        int n2 = byArray.length - n;
        while (n < byArray.length) {
            byArray[n] = 0;
            ++n;
        }
        return n2;
    }

    @Override
    public int padCount(byte[] byArray) throws InvalidCipherTextException {
        int n = 0;
        int n2 = -1;
        int n3 = byArray.length;
        while (--n3 >= 0) {
            int n4 = byArray[n3] & 0xFF;
            int n5 = (n4 ^ 0) - 1 >> 31;
            n -= (n2 &= n5);
        }
        return n;
    }
}

