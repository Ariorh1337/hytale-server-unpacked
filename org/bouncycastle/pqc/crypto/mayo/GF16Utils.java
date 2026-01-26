/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.util.GF16;

class GF16Utils {
    static final long NIBBLE_MASK_MSB = 0x7777777777777777L;
    static final long MASK_MSB = -8608480567731124088L;
    static final long MASK_LSB = 0x1111111111111111L;
    static final long NIBBLE_MASK_LSB = -1229782938247303442L;

    GF16Utils() {
    }

    static void mVecMulAdd(int n, long[] lArray, int n2, int n3, long[] lArray2, int n4) {
        long l = (long)n3 & 0xFFFFFFFFL;
        long l2 = l & 1L;
        long l3 = l >>> 1 & 1L;
        long l4 = l >>> 2 & 1L;
        long l5 = l >>> 3 & 1L;
        for (int i = 0; i < n; ++i) {
            long l6 = lArray[n2++];
            long l7 = l6 & -l2;
            long l8 = l6 & 0x8888888888888888L;
            l6 &= 0x7777777777777777L;
            long l9 = l8 >>> 3;
            l6 = l6 << 1 ^ l9 + (l9 << 1);
            l7 ^= l6 & -l3;
            l8 = l6 & 0x8888888888888888L;
            l6 &= 0x7777777777777777L;
            l9 = l8 >>> 3;
            l6 = l6 << 1 ^ l9 + (l9 << 1);
            l7 ^= l6 & -l4;
            l8 = l6 & 0x8888888888888888L;
            l6 &= 0x7777777777777777L;
            l9 = l8 >>> 3;
            l6 = l6 << 1 ^ l9 + (l9 << 1);
            int n5 = n4++;
            lArray2[n5] = lArray2[n5] ^ (l7 ^ l6 & -l5);
        }
    }

    static void mulAddMUpperTriangularMatXMat(int n, long[] lArray, byte[] byArray, long[] lArray2, int n2, int n3, int n4) {
        int n5 = 0;
        int n6 = n4 * n;
        int n7 = 0;
        int n8 = 0;
        int n9 = 0;
        while (n7 < n3) {
            int n10 = n7;
            int n11 = n8;
            while (n10 < n3) {
                int n12 = 0;
                int n13 = 0;
                while (n12 < n4) {
                    GF16Utils.mVecMulAdd(n, lArray, n5, byArray[n11 + n12], lArray2, n2 + n9 + n13);
                    ++n12;
                    n13 += n;
                }
                n5 += n;
                ++n10;
                n11 += n4;
            }
            ++n7;
            n8 += n4;
            n9 += n6;
        }
    }

    static void mulAddMatTransXMMat(int n, byte[] byArray, long[] lArray, int n2, long[] lArray2, int n3, int n4) {
        int n5 = n4 * n;
        int n6 = 0;
        int n7 = 0;
        while (n6 < n4) {
            int n8 = 0;
            int n9 = 0;
            int n10 = 0;
            while (n8 < n3) {
                byte by = byArray[n9 + n6];
                int n11 = 0;
                int n12 = 0;
                while (n11 < n4) {
                    GF16Utils.mVecMulAdd(n, lArray, n2 + n10 + n12, by, lArray2, n7 + n12);
                    ++n11;
                    n12 += n;
                }
                ++n8;
                n9 += n4;
                n10 += n5;
            }
            ++n6;
            n7 += n5;
        }
    }

    static void mulAddMatXMMat(int n, byte[] byArray, long[] lArray, long[] lArray2, int n2, int n3) {
        int n4 = n * n2;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        while (n5 < n2) {
            int n8 = 0;
            int n9 = 0;
            while (n8 < n3) {
                byte by = byArray[n6 + n8];
                int n10 = 0;
                int n11 = 0;
                while (n10 < n2) {
                    GF16Utils.mVecMulAdd(n, lArray, n9 + n11, by, lArray2, n7 + n11);
                    ++n10;
                    n11 += n;
                }
                ++n8;
                n9 += n4;
            }
            ++n5;
            n6 += n3;
            n7 += n4;
        }
    }

    static void mulAddMatXMMat(int n, byte[] byArray, long[] lArray, int n2, long[] lArray2, int n3, int n4, int n5) {
        int n6 = n * n5;
        int n7 = 0;
        int n8 = 0;
        int n9 = 0;
        while (n7 < n3) {
            int n10 = 0;
            int n11 = 0;
            while (n10 < n4) {
                byte by = byArray[n9 + n10];
                int n12 = 0;
                int n13 = 0;
                while (n12 < n5) {
                    GF16Utils.mVecMulAdd(n, lArray, n11 + n13 + n2, by, lArray2, n8 + n13);
                    ++n12;
                    n13 += n;
                }
                ++n10;
                n11 += n6;
            }
            ++n7;
            n8 += n6;
            n9 += n4;
        }
    }

    static void mulAddMUpperTriangularMatXMatTrans(int n, long[] lArray, byte[] byArray, long[] lArray2, int n2, int n3) {
        int n4 = 0;
        int n5 = n * n3;
        int n6 = 0;
        int n7 = 0;
        while (n6 < n2) {
            for (int i = n6; i < n2; ++i) {
                int n8 = 0;
                int n9 = 0;
                int n10 = 0;
                while (n8 < n3) {
                    GF16Utils.mVecMulAdd(n, lArray, n4, byArray[n9 + i], lArray2, n7 + n10);
                    ++n8;
                    n9 += n2;
                    n10 += n;
                }
                n4 += n;
            }
            ++n6;
            n7 += n5;
        }
    }

    static long mulFx8(byte by, long l) {
        int n = by & 0xFF;
        long l2 = (long)(-(n & 1)) & l ^ (long)(-(n >> 1 & 1)) & l << 1 ^ (long)(-(n >> 2 & 1)) & l << 2 ^ (long)(-(n >> 3 & 1)) & l << 3;
        long l3 = l2 & 0xF0F0F0F0F0F0F0F0L;
        return (l2 ^ l3 >>> 4 ^ l3 >>> 3) & 0xF0F0F0F0F0F0F0FL;
    }

    static void matMul(byte[] byArray, byte[] byArray2, int n, byte[] byArray3, int n2, int n3) {
        int n4 = 0;
        int n5 = 0;
        for (int i = 0; i < n3; ++i) {
            byte by = 0;
            for (int j = 0; j < n2; ++j) {
                by = (byte)(by ^ GF16.mul(byArray[n4++], byArray2[n + j]));
            }
            byArray3[n5++] = by;
        }
    }
}

