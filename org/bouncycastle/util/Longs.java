/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public class Longs {
    public static final int BYTES = 8;
    public static final int SIZE = 64;

    public static long highestOneBit(long l) {
        return Long.highestOneBit(l);
    }

    public static long lowestOneBit(long l) {
        return Long.lowestOneBit(l);
    }

    public static int numberOfLeadingZeros(long l) {
        return Long.numberOfLeadingZeros(l);
    }

    public static int numberOfTrailingZeros(long l) {
        return Long.numberOfTrailingZeros(l);
    }

    public static long reverse(long l) {
        return Long.reverse(l);
    }

    public static long reverseBytes(long l) {
        return Long.reverseBytes(l);
    }

    public static long rotateLeft(long l, int n) {
        return Long.rotateLeft(l, n);
    }

    public static long rotateRight(long l, int n) {
        return Long.rotateRight(l, n);
    }

    public static Long valueOf(long l) {
        return l;
    }

    public static void xorTo(int n, long[] lArray, int n2, long[] lArray2, int n3) {
        for (int i = 0; i < n; ++i) {
            int n4 = n3 + i;
            lArray2[n4] = lArray2[n4] ^ lArray[n2 + i];
        }
    }
}

