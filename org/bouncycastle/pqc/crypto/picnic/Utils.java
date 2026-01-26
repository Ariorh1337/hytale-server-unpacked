/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.util.Integers;

class Utils {
    Utils() {
    }

    protected static int numBytes(int n) {
        return n == 0 ? 0 : (n - 1) / 8 + 1;
    }

    protected static int ceil_log2(int n) {
        if (n == 0) {
            return 0;
        }
        return 32 - Utils.nlz(n - 1);
    }

    private static int nlz(int n) {
        if (n == 0) {
            return 32;
        }
        int n2 = 1;
        if (n >>> 16 == 0) {
            n2 += 16;
            n <<= 16;
        }
        if (n >>> 24 == 0) {
            n2 += 8;
            n <<= 8;
        }
        if (n >>> 28 == 0) {
            n2 += 4;
            n <<= 4;
        }
        if (n >>> 30 == 0) {
            n2 += 2;
            n <<= 2;
        }
        return n2 -= n >>> 31;
    }

    protected static int parity(byte[] byArray, int n) {
        byte by = byArray[0];
        for (int i = 1; i < n; ++i) {
            by = (byte)(by ^ byArray[i]);
        }
        return Integers.bitCount(by & 0xFF) & 1;
    }

    protected static int parity16(int n) {
        return Integers.bitCount(n & 0xFFFF) & 1;
    }

    protected static int parity32(int n) {
        return Integers.bitCount(n) & 1;
    }

    protected static void setBitInWordArray(int[] nArray, int n, int n2) {
        Utils.setBit(nArray, n, n2);
    }

    protected static int getBitFromWordArray(int[] nArray, int n) {
        return Utils.getBit(nArray, n);
    }

    protected static byte getBit(byte[] byArray, int n) {
        int n2 = n >>> 3;
        int n3 = n & 7 ^ 7;
        return (byte)(byArray[n2] >>> n3 & 1);
    }

    protected static byte getCrumbAligned(byte[] byArray, int n) {
        int n2 = n >>> 2;
        int n3 = n << 1 & 6 ^ 6;
        int n4 = byArray[n2] >>> n3;
        return (byte)((n4 & 1) << 1 | (n4 & 2) >> 1);
    }

    protected static int getBit(int n, int n2) {
        int n3 = n2 ^ 7;
        return n >>> n3 & 1;
    }

    protected static int getBit(int[] nArray, int n) {
        int n2 = n >>> 5;
        int n3 = n & 0x1F ^ 7;
        return nArray[n2] >>> n3 & 1;
    }

    protected static void setBit(byte[] byArray, int n, byte by) {
        int n2 = n >>> 3;
        int n3 = n & 7 ^ 7;
        int n4 = byArray[n2];
        n4 &= ~(1 << n3);
        byArray[n2] = (byte)(n4 |= by << n3);
    }

    protected static int setBit(int n, int n2, int n3) {
        int n4 = n2 ^ 7;
        n &= ~(1 << n4);
        return n |= n3 << n4;
    }

    protected static void setBit(int[] nArray, int n, int n2) {
        int n3 = n >>> 5;
        int n4 = n & 0x1F ^ 7;
        int n5 = nArray[n3];
        n5 &= ~(1 << n4);
        nArray[n3] = n5 |= n2 << n4;
    }

    protected static void zeroTrailingBits(int[] nArray, int n) {
        int n2 = n & 0x1F;
        if (n2 != 0) {
            int n3 = n >>> 5;
            nArray[n3] = nArray[n3] & Utils.getTrailingBitsMask(n);
        }
    }

    protected static int getTrailingBitsMask(int n) {
        int n2 = n & 0xFFFFFFF8;
        int n3 = ~(-1 << n2);
        int n4 = n & 7;
        if (n4 != 0) {
            n3 ^= (65280 >>> n4 & 0xFF) << n2;
        }
        return n3;
    }
}

