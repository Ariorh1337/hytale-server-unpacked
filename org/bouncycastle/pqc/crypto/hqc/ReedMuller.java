/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.pqc.crypto.hqc.Utils;
import org.bouncycastle.util.Arrays;

class ReedMuller {
    ReedMuller() {
    }

    static void encodeSub(Codeword codeword, int n) {
        int n2 = ReedMuller.Bit0Mask(n >> 7);
        n2 ^= ReedMuller.Bit0Mask(n) & 0xAAAAAAAA;
        n2 ^= ReedMuller.Bit0Mask(n >> 1) & 0xCCCCCCCC;
        n2 ^= ReedMuller.Bit0Mask(n >> 2) & 0xF0F0F0F0;
        n2 ^= ReedMuller.Bit0Mask(n >> 3) & 0xFF00FF00;
        codeword.type32[0] = n2 ^= ReedMuller.Bit0Mask(n >> 4) & 0xFFFF0000;
        codeword.type32[1] = n2 ^= ReedMuller.Bit0Mask(n >> 5);
        codeword.type32[3] = n2 ^= ReedMuller.Bit0Mask(n >> 6);
        codeword.type32[2] = n2 ^= ReedMuller.Bit0Mask(n >> 5);
    }

    private static void hadamardTransform(int[] nArray, int[] nArray2) {
        int[] nArray3 = Arrays.clone(nArray);
        int[] nArray4 = Arrays.clone(nArray2);
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 64; ++j) {
                nArray4[j] = nArray3[2 * j] + nArray3[2 * j + 1];
                nArray4[j + 64] = nArray3[2 * j] - nArray3[2 * j + 1];
            }
            int[] nArray5 = nArray3;
            nArray3 = nArray4;
            nArray4 = nArray5;
        }
        System.arraycopy(nArray4, 0, nArray, 0, nArray.length);
        System.arraycopy(nArray3, 0, nArray2, 0, nArray2.length);
    }

    private static void expandThenSum(int[] nArray, Codeword[] codewordArray, int n, int n2) {
        int n3;
        int n4;
        for (n4 = 0; n4 < 4; ++n4) {
            for (n3 = 0; n3 < 32; ++n3) {
                nArray[n4 * 32 + n3] = codewordArray[n].type32[n4] >> n3 & 1;
            }
        }
        for (n4 = 1; n4 < n2; ++n4) {
            for (n3 = 0; n3 < 4; ++n3) {
                for (int i = 0; i < 32; ++i) {
                    int n5 = n3 * 32 + i;
                    nArray[n5] = nArray[n5] + (codewordArray[n4 + n].type32[n3] >> i & 1);
                }
            }
        }
    }

    private static int findPeaks(int[] nArray) {
        int n;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        for (n = 0; n < 128; ++n) {
            int n5 = nArray[n];
            int n6 = n5 > 0 ? -1 : 0;
            int n7 = n6 & n5 | ~n6 & -n5;
            n3 = n7 > n2 ? n5 : n3;
            n4 = n7 > n2 ? n : n4;
            n2 = Math.max(n7, n2);
        }
        n = n3 > 0 ? 1 : 0;
        return n4 |= 128 * n;
    }

    private static int Bit0Mask(int n) {
        return -(n & 1);
    }

    public static void encode(long[] lArray, byte[] byArray, int n, int n2) {
        int n3;
        byte[] byArray2 = Arrays.clone(byArray);
        Codeword[] codewordArray = new Codeword[n * n2];
        for (n3 = 0; n3 < codewordArray.length; ++n3) {
            codewordArray[n3] = new Codeword();
        }
        for (n3 = 0; n3 < n; ++n3) {
            int n4 = n3 * n2;
            ReedMuller.encodeSub(codewordArray[n4], byArray2[n3]);
            for (int i = 1; i < n2; ++i) {
                codewordArray[n4 + i] = codewordArray[n4];
            }
        }
        ReedMuller.CopyCWD(lArray, codewordArray);
    }

    private static void CopyCWD(long[] lArray, Codeword[] codewordArray) {
        int[] nArray = new int[codewordArray.length * 4];
        int n = 0;
        for (int i = 0; i < codewordArray.length; ++i) {
            System.arraycopy(codewordArray[i].type32, 0, nArray, n, codewordArray[i].type32.length);
            n += 4;
        }
        Utils.fromByte32ArrayToLongArray(lArray, nArray);
    }

    public static void decode(byte[] byArray, long[] lArray, int n, int n2) {
        byte[] byArray2 = Arrays.clone(byArray);
        Codeword[] codewordArray = new Codeword[lArray.length / 2];
        int[] nArray = new int[lArray.length * 2];
        Utils.fromLongArrayToByte32Array(nArray, lArray);
        for (int i = 0; i < codewordArray.length; ++i) {
            codewordArray[i] = new Codeword();
            System.arraycopy(nArray, i * 4, codewordArray[i].type32, 0, 4);
        }
        int[] nArray2 = new int[128];
        int[] nArray3 = new int[128];
        for (int i = 0; i < n; ++i) {
            ReedMuller.expandThenSum(nArray2, codewordArray, i * n2, n2);
            ReedMuller.hadamardTransform(nArray2, nArray3);
            nArray3[0] = nArray3[0] - 64 * n2;
            byArray2[i] = (byte)ReedMuller.findPeaks(nArray3);
        }
        ReedMuller.CopyCWD(lArray, codewordArray);
        System.arraycopy(byArray2, 0, byArray, 0, byArray.length);
    }

    static class Codeword {
        int[] type32 = new int[4];
        int[] type8 = new int[16];
    }
}

