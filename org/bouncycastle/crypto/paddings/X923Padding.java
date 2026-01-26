/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class X923Padding
implements BlockCipherPadding {
    SecureRandom random = null;

    @Override
    public void init(SecureRandom secureRandom) throws IllegalArgumentException {
        this.random = secureRandom;
    }

    @Override
    public String getPaddingName() {
        return "X9.23";
    }

    @Override
    public int addPadding(byte[] byArray, int n) {
        byte by = (byte)(byArray.length - n);
        while (n < byArray.length - 1) {
            byArray[n] = this.random == null ? (byte)0 : (byte)this.random.nextInt();
            ++n;
        }
        byArray[n] = by;
        return by;
    }

    @Override
    public int padCount(byte[] byArray) throws InvalidCipherTextException {
        int n = byArray[byArray.length - 1] & 0xFF;
        int n2 = byArray.length - n;
        int n3 = (n2 | n - 1) >> 31;
        if (n3 != 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return n;
    }
}

