/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class PKCS7Padding
implements BlockCipherPadding {
    @Override
    public void init(SecureRandom secureRandom) throws IllegalArgumentException {
    }

    @Override
    public String getPaddingName() {
        return "PKCS7";
    }

    @Override
    public int addPadding(byte[] byArray, int n) {
        byte by = (byte)(byArray.length - n);
        while (n < byArray.length) {
            byArray[n] = by;
            ++n;
        }
        return by;
    }

    @Override
    public int padCount(byte[] byArray) throws InvalidCipherTextException {
        byte by = byArray[byArray.length - 1];
        int n = by & 0xFF;
        int n2 = byArray.length - n;
        int n3 = (n2 | n - 1) >> 31;
        for (int i = 0; i < byArray.length; ++i) {
            n3 |= (byArray[i] ^ by) & ~(i - n2 >> 31);
        }
        if (n3 != 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return n;
    }
}

