/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Arrays;

class GF2PolynomialCalculator {
    private final int VEC_N_SIZE_64;
    private final int PARAM_N;
    private final long RED_MASK;

    GF2PolynomialCalculator(int n, int n2, long l) {
        this.VEC_N_SIZE_64 = n;
        this.PARAM_N = n2;
        this.RED_MASK = l;
    }

    public void vectMul(long[] lArray, long[] lArray2, long[] lArray3) {
        long[] lArray4 = new long[this.VEC_N_SIZE_64 << 1];
        long[] lArray5 = new long[this.VEC_N_SIZE_64 << 4];
        this.karatsuba(lArray4, 0, lArray2, 0, lArray3, 0, this.VEC_N_SIZE_64, lArray5, 0);
        this.reduce(lArray, lArray4);
    }

    private void schoolbookMul(long[] lArray, int n, long[] lArray2, int n2, long[] lArray3, int n3, int n4) {
        Arrays.fill(lArray, n, n + (n4 << 1), 0L);
        int n5 = 0;
        while (n5 < n4) {
            long l = lArray2[n5 + n2];
            for (int i = 0; i < 64; ++i) {
                int n6;
                int n7;
                int n8;
                long l2 = -(l >> i & 1L);
                if (i == 0) {
                    n8 = 0;
                    n7 = n;
                    n6 = n3;
                    while (n8 < n4) {
                        int n9 = n7++;
                        lArray[n9] = lArray[n9] ^ lArray3[n6] & l2;
                        ++n8;
                        ++n6;
                    }
                    continue;
                }
                n8 = 64 - i;
                n7 = 0;
                n6 = n;
                int n10 = n3;
                while (n7 < n4) {
                    int n11 = n6++;
                    lArray[n11] = lArray[n11] ^ lArray3[n10] << i & l2;
                    int n12 = n6;
                    lArray[n12] = lArray[n12] ^ lArray3[n10] >>> n8 & l2;
                    ++n7;
                    ++n10;
                }
            }
            ++n5;
            ++n;
        }
    }

    private void karatsuba(long[] lArray, int n, long[] lArray2, int n2, long[] lArray3, int n3, int n4, long[] lArray4, int n5) {
        long l;
        long l2;
        int n6;
        if (n4 <= 16) {
            this.schoolbookMul(lArray, n, lArray2, n2, lArray3, n3, n4);
            return;
        }
        int n7 = n4 >> 1;
        int n8 = n4 - n7;
        int n9 = n4 << 1;
        int n10 = n7 << 1;
        int n11 = n8 << 1;
        int n12 = n5 + n9;
        int n13 = n12 + n9;
        int n14 = n13 + n9;
        int n15 = n14 + n4;
        int n16 = n5 + (n4 << 3);
        this.karatsuba(lArray4, n5, lArray2, n2, lArray3, n3, n7, lArray4, n16);
        this.karatsuba(lArray4, n12, lArray2, n2 + n7, lArray3, n3 + n7, n8, lArray4, n16);
        for (n6 = 0; n6 < n8; ++n6) {
            l2 = n6 < n7 ? lArray2[n2 + n6] : 0L;
            l = n6 < n7 ? lArray3[n3 + n6] : 0L;
            lArray4[n14 + n6] = l2 ^ lArray2[n2 + n7 + n6];
            lArray4[n15 + n6] = l ^ lArray3[n3 + n7 + n6];
        }
        this.karatsuba(lArray4, n13, lArray4, n14, lArray4, n15, n8, lArray4, n16);
        System.arraycopy(lArray4, n5, lArray, n, n10);
        System.arraycopy(lArray4, n12, lArray, n + n10, n11);
        for (n6 = 0; n6 < 2 * n8; ++n6) {
            l2 = n6 < n10 ? lArray4[n5 + n6] : 0L;
            l = n6 < n11 ? lArray4[n12 + n6] : 0L;
            int n17 = n + n7 + n6;
            lArray[n17] = lArray[n17] ^ (lArray4[n13 + n6] ^ l2 ^ l);
        }
    }

    private void reduce(long[] lArray, long[] lArray2) {
        for (int i = 0; i < this.VEC_N_SIZE_64; ++i) {
            lArray[i] = lArray2[i] ^ lArray2[i + this.VEC_N_SIZE_64 - 1] >>> (this.PARAM_N & 0x3F) ^ lArray2[i + this.VEC_N_SIZE_64] << (int)(64L - ((long)this.PARAM_N & 0x3FL));
        }
        int n = this.VEC_N_SIZE_64 - 1;
        lArray[n] = lArray[n] & this.RED_MASK;
    }
}

