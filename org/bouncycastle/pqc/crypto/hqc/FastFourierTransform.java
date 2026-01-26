/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.pqc.crypto.hqc.GFCalculator;
import org.bouncycastle.pqc.crypto.hqc.Utils;

class FastFourierTransform {
    FastFourierTransform() {
    }

    static void fastFourierTransform(int[] nArray, int[] nArray2, int n, int n2) {
        int n3;
        int n4 = 8;
        int n5 = 128;
        int n6 = 1 << n2;
        int[] nArray3 = new int[n6];
        int[] nArray4 = new int[n6];
        int[] nArray5 = new int[n4 - 1];
        int[] nArray6 = new int[n5];
        int[] nArray7 = new int[n5];
        int[] nArray8 = new int[n4 - 1];
        int[] nArray9 = new int[n5];
        FastFourierTransform.computeFFTBetas(nArray8, n4);
        FastFourierTransform.computeSubsetSum(nArray9, nArray8, n4 - 1);
        FastFourierTransform.computeRadix(nArray3, nArray4, nArray2, n2, n2);
        for (n3 = 0; n3 < n4 - 1; ++n3) {
            nArray5[n3] = GFCalculator.mult(nArray8[n3], nArray8[n3]) ^ nArray8[n3];
        }
        FastFourierTransform.computeFFTRec(nArray6, nArray3, (n + 1) / 2, n4 - 1, n2 - 1, nArray5, n2, n4);
        FastFourierTransform.computeFFTRec(nArray7, nArray4, n / 2, n4 - 1, n2 - 1, nArray5, n2, n4);
        n3 = 1 << n4 - 1;
        System.arraycopy(nArray7, 0, nArray, n3, n3);
        nArray[0] = nArray6[0];
        int n7 = n3;
        nArray[n7] = nArray[n7] ^ nArray6[0];
        for (int i = 1; i < n3; ++i) {
            nArray[i] = nArray6[i] ^ GFCalculator.mult(nArray9[i], nArray7[i]);
            int n8 = n3 + i;
            nArray[n8] = nArray[n8] ^ nArray[i];
        }
    }

    static void computeFFTBetas(int[] nArray, int n) {
        for (int i = 0; i < n - 1; ++i) {
            nArray[i] = 1 << n - 1 - i;
        }
    }

    static void computeSubsetSum(int[] nArray, int[] nArray2, int n) {
        nArray[0] = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < 1 << i; ++j) {
                nArray[(1 << i) + j] = nArray2[i] ^ nArray[j];
            }
        }
    }

    static void computeRadix(int[] nArray, int[] nArray2, int[] nArray3, int n, int n2) {
        switch (n) {
            case 4: {
                nArray[4] = nArray3[8] ^ nArray3[12];
                nArray[6] = nArray3[12] ^ nArray3[14];
                nArray[7] = nArray3[14] ^ nArray3[15];
                nArray2[5] = nArray3[11] ^ nArray3[13];
                nArray2[6] = nArray3[13] ^ nArray3[14];
                nArray2[7] = nArray3[15];
                nArray[5] = nArray3[10] ^ nArray3[12] ^ nArray2[5];
                nArray2[4] = nArray3[9] ^ nArray3[13] ^ nArray[5];
                nArray[0] = nArray3[0];
                nArray2[3] = nArray3[7] ^ nArray3[11] ^ nArray3[15];
                nArray[3] = nArray3[6] ^ nArray3[10] ^ nArray3[14] ^ nArray2[3];
                nArray[2] = nArray3[4] ^ nArray[4] ^ nArray[3] ^ nArray2[3];
                nArray2[1] = nArray3[3] ^ nArray3[5] ^ nArray3[9] ^ nArray3[13] ^ nArray2[3];
                nArray2[2] = nArray3[3] ^ nArray2[1] ^ nArray[3];
                nArray[1] = nArray3[2] ^ nArray[2] ^ nArray2[1];
                nArray2[0] = nArray3[1] ^ nArray[1];
                return;
            }
            case 3: {
                nArray[0] = nArray3[0];
                nArray[2] = nArray3[4] ^ nArray3[6];
                nArray[3] = nArray3[6] ^ nArray3[7];
                nArray2[1] = nArray3[3] ^ nArray3[5] ^ nArray3[7];
                nArray2[2] = nArray3[5] ^ nArray3[6];
                nArray2[3] = nArray3[7];
                nArray[1] = nArray3[2] ^ nArray[2] ^ nArray2[1];
                nArray2[0] = nArray3[1] ^ nArray[1];
                return;
            }
            case 2: {
                nArray[0] = nArray3[0];
                nArray[1] = nArray3[2] ^ nArray3[3];
                nArray2[0] = nArray3[1] ^ nArray[1];
                nArray2[1] = nArray3[3];
                return;
            }
            case 1: {
                nArray[0] = nArray3[0];
                nArray2[0] = nArray3[1];
                return;
            }
        }
        FastFourierTransform.computeRadixBig(nArray, nArray2, nArray3, n, n2);
    }

    static void computeRadixBig(int[] nArray, int[] nArray2, int[] nArray3, int n, int n2) {
        int n3 = 1;
        n3 <<= n - 2;
        int n4 = 1 << n2 - 2;
        int[] nArray4 = new int[2 * n4 + 1];
        int[] nArray5 = new int[2 * n4 + 1];
        int[] nArray6 = new int[n4];
        int[] nArray7 = new int[n4];
        int[] nArray8 = new int[n4];
        int[] nArray9 = new int[n4];
        Utils.copyBytes(nArray3, 3 * n3, nArray4, 0, 2 * n3);
        Utils.copyBytes(nArray3, 3 * n3, nArray4, n3, 2 * n3);
        Utils.copyBytes(nArray3, 0, nArray5, 0, 4 * n3);
        for (int i = 0; i < n3; ++i) {
            int n5 = i;
            nArray4[n5] = nArray4[n5] ^ nArray3[2 * n3 + i];
            int n6 = n3 + i;
            nArray5[n6] = nArray5[n6] ^ nArray4[i];
        }
        FastFourierTransform.computeRadix(nArray6, nArray7, nArray4, n - 1, n2);
        FastFourierTransform.computeRadix(nArray8, nArray9, nArray5, n - 1, n2);
        Utils.copyBytes(nArray8, 0, nArray, 0, 2 * n3);
        Utils.copyBytes(nArray6, 0, nArray, n3, 2 * n3);
        Utils.copyBytes(nArray9, 0, nArray2, 0, 2 * n3);
        Utils.copyBytes(nArray7, 0, nArray2, n3, 2 * n3);
    }

    static void computeFFTRec(int[] nArray, int[] nArray2, int n, int n2, int n3, int[] nArray3, int n4, int n5) {
        int n6;
        int n7 = 1 << n4 - 2;
        int n8 = 1 << n5 - 2;
        int[] nArray4 = new int[n7];
        int[] nArray5 = new int[n7];
        int[] nArray6 = new int[n5 - 2];
        int[] nArray7 = new int[n5 - 2];
        int[] nArray8 = new int[n8];
        int[] nArray9 = new int[n8];
        int[] nArray10 = new int[n8];
        int[] nArray11 = new int[n5 - n4 + 1];
        if (n3 == 1) {
            int n9;
            for (n9 = 0; n9 < n2; ++n9) {
                nArray11[n9] = GFCalculator.mult(nArray3[n9], nArray2[1]);
            }
            nArray[0] = nArray2[0];
            int n10 = 1;
            for (n9 = 0; n9 < n2; ++n9) {
                for (int i = 0; i < n10; ++i) {
                    nArray[n10 + i] = nArray[i] ^ nArray11[n9];
                }
                n10 <<= 1;
            }
            return;
        }
        if (nArray3[n2 - 1] != 1) {
            n6 = 1;
            int n11 = 1;
            n11 <<= n3;
            for (int i = 1; i < n11; ++i) {
                n6 = GFCalculator.mult(n6, nArray3[n2 - 1]);
                nArray2[i] = GFCalculator.mult(n6, nArray2[i]);
            }
        }
        FastFourierTransform.computeRadix(nArray4, nArray5, nArray2, n3, n4);
        for (n6 = 0; n6 < n2 - 1; ++n6) {
            nArray6[n6] = GFCalculator.mult(nArray3[n6], GFCalculator.inverse(nArray3[n2 - 1]));
            nArray7[n6] = GFCalculator.mult(nArray6[n6], nArray6[n6]) ^ nArray6[n6];
        }
        FastFourierTransform.computeSubsetSum(nArray8, nArray6, n2 - 1);
        FastFourierTransform.computeFFTRec(nArray9, nArray4, (n + 1) / 2, n2 - 1, n3 - 1, nArray7, n4, n5);
        int n12 = 1;
        n12 <<= n2 - 1 & 0xF;
        if (n <= 3) {
            nArray[0] = nArray9[0];
            nArray[n12] = nArray9[0] ^ nArray5[0];
            for (n6 = 1; n6 < n12; ++n6) {
                nArray[n6] = nArray9[n6] ^ GFCalculator.mult(nArray8[n6], nArray5[0]);
                nArray[n12 + n6] = nArray[n6] ^ nArray5[0];
            }
        } else {
            FastFourierTransform.computeFFTRec(nArray10, nArray5, n / 2, n2 - 1, n3 - 1, nArray7, n4, n5);
            System.arraycopy(nArray10, 0, nArray, n12, n12);
            nArray[0] = nArray9[0];
            int n13 = n12;
            nArray[n13] = nArray[n13] ^ nArray9[0];
            for (n6 = 1; n6 < n12; ++n6) {
                nArray[n6] = nArray9[n6] ^ GFCalculator.mult(nArray8[n6], nArray10[n6]);
                int n14 = n12 + n6;
                nArray[n14] = nArray[n14] ^ nArray[n6];
            }
        }
    }

    static void fastFourierTransformGetError(byte[] byArray, int[] nArray, int n, int[] nArray2) {
        int n2 = 8;
        int n3 = 255;
        int[] nArray3 = new int[n2 - 1];
        int[] nArray4 = new int[n];
        FastFourierTransform.computeFFTBetas(nArray3, n2);
        FastFourierTransform.computeSubsetSum(nArray4, nArray3, n2 - 1);
        byArray[0] = (byte)(byArray[0] ^ (1 ^ Utils.toUnsigned16Bits(-nArray[0] >> 15)));
        byArray[0] = (byte)(byArray[0] ^ (1 ^ Utils.toUnsigned16Bits(-nArray[n] >> 15)));
        for (int i = 1; i < n; ++i) {
            int n4;
            int n5 = n4 = n3 - nArray2[nArray4[i]];
            byArray[n5] = (byte)(byArray[n5] ^ (1 ^ Math.abs(-nArray[i] >> 15)));
            int n6 = n4 = n3 - nArray2[nArray4[i] ^ 1];
            byArray[n6] = (byte)(byArray[n6] ^ (1 ^ Math.abs(-nArray[n + i] >> 15)));
        }
    }
}

