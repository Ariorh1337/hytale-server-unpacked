/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

abstract class BENES {
    private static final long[] TRANSPOSE_MASKS = new long[]{0x5555555555555555L, 0x3333333333333333L, 0xF0F0F0F0F0F0F0FL, 0xFF00FF00FF00FFL, 0xFFFF0000FFFFL, 0xFFFFFFFFL};
    protected final int SYS_N;
    protected final int SYS_T;
    protected final int GFBITS;

    public BENES(int n, int n2, int n3) {
        this.SYS_N = n;
        this.SYS_T = n2;
        this.GFBITS = n3;
    }

    static void transpose_64x64(long[] lArray, long[] lArray2) {
        BENES.transpose_64x64(lArray, lArray2, 0);
    }

    static void transpose_64x64(long[] lArray, long[] lArray2, int n) {
        long l;
        long l2;
        long l3;
        int n2;
        int n3;
        int n4;
        long l4;
        System.arraycopy(lArray2, n, lArray, n, 64);
        int n5 = 5;
        do {
            l4 = TRANSPOSE_MASKS[n5];
            n4 = 1 << n5;
            for (n3 = n; n3 < n + 64; n3 += n4 * 2) {
                for (n2 = n3; n2 < n3 + n4; n2 += 4) {
                    l3 = lArray[n2 + 0];
                    l2 = lArray[n2 + 1];
                    l = lArray[n2 + 2];
                    long l5 = lArray[n2 + 3];
                    long l6 = lArray[n2 + n4 + 0];
                    long l7 = lArray[n2 + n4 + 1];
                    long l8 = lArray[n2 + n4 + 2];
                    long l9 = lArray[n2 + n4 + 3];
                    long l10 = (l3 >>> n4 ^ l6) & l4;
                    long l11 = (l2 >>> n4 ^ l7) & l4;
                    long l12 = (l >>> n4 ^ l8) & l4;
                    long l13 = (l5 >>> n4 ^ l9) & l4;
                    lArray[n2 + 0] = l3 ^ l10 << n4;
                    lArray[n2 + 1] = l2 ^ l11 << n4;
                    lArray[n2 + 2] = l ^ l12 << n4;
                    lArray[n2 + 3] = l5 ^ l13 << n4;
                    lArray[n2 + n4 + 0] = l6 ^ l10;
                    lArray[n2 + n4 + 1] = l7 ^ l11;
                    lArray[n2 + n4 + 2] = l8 ^ l12;
                    lArray[n2 + n4 + 3] = l9 ^ l13;
                }
            }
        } while (--n5 >= 2);
        do {
            l4 = TRANSPOSE_MASKS[n5];
            n4 = 1 << n5;
            for (n3 = n; n3 < n + 64; n3 += n4 * 2) {
                for (n2 = n3; n2 < n3 + n4; ++n2) {
                    l3 = lArray[n2 + 0];
                    l2 = lArray[n2 + n4];
                    l = (l3 >>> n4 ^ l2) & l4;
                    lArray[n2 + 0] = l3 ^ l << n4;
                    lArray[n2 + n4] = l2 ^ l;
                }
            }
        } while (--n5 >= 0);
    }

    protected abstract void support_gen(short[] var1, byte[] var2);
}

