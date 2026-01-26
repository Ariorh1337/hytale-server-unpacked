/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.pqc.crypto.saber.SABEREngine;
import org.bouncycastle.pqc.crypto.saber.Utils;

class Poly {
    private static final int KARATSUBA_N = 64;
    private static int SCHB_N = 16;
    private final int N_RES;
    private final int N_SB;
    private final int N_SB_RES;
    private final int SABER_N;
    private final int SABER_L;
    private final SABEREngine engine;
    private final Utils utils;

    public Poly(SABEREngine sABEREngine) {
        this.engine = sABEREngine;
        this.SABER_L = sABEREngine.getSABER_L();
        this.SABER_N = sABEREngine.getSABER_N();
        this.N_RES = this.SABER_N << 1;
        this.N_SB = this.SABER_N >> 2;
        this.N_SB_RES = 2 * this.N_SB - 1;
        this.utils = sABEREngine.getUtils();
    }

    public void GenMatrix(short[][][] sArray, byte[] byArray) {
        byte[] byArray2 = new byte[this.SABER_L * this.engine.getSABER_POLYVECBYTES()];
        this.engine.symmetric.prf(byArray2, byArray, this.engine.getSABER_SEEDBYTES(), byArray2.length);
        for (int i = 0; i < this.SABER_L; ++i) {
            this.utils.BS2POLVECq(byArray2, i * this.engine.getSABER_POLYVECBYTES(), sArray[i]);
        }
    }

    public void GenSecret(short[][] sArray, byte[] byArray) {
        byte[] byArray2 = new byte[this.SABER_L * this.engine.getSABER_POLYCOINBYTES()];
        this.engine.symmetric.prf(byArray2, byArray, this.engine.getSABER_NOISE_SEEDBYTES(), byArray2.length);
        for (int i = 0; i < this.SABER_L; ++i) {
            if (!this.engine.usingEffectiveMasking) {
                this.cbd(sArray[i], byArray2, i * this.engine.getSABER_POLYCOINBYTES());
                continue;
            }
            for (int j = 0; j < this.SABER_N / 4; ++j) {
                sArray[i][4 * j] = (short)((byArray2[j + i * this.engine.getSABER_POLYCOINBYTES()] & 3 ^ 2) - 2);
                sArray[i][4 * j + 1] = (short)((byArray2[j + i * this.engine.getSABER_POLYCOINBYTES()] >>> 2 & 3 ^ 2) - 2);
                sArray[i][4 * j + 2] = (short)((byArray2[j + i * this.engine.getSABER_POLYCOINBYTES()] >>> 4 & 3 ^ 2) - 2);
                sArray[i][4 * j + 3] = (short)((byArray2[j + i * this.engine.getSABER_POLYCOINBYTES()] >>> 6 & 3 ^ 2) - 2);
            }
        }
    }

    private long load_littleendian(byte[] byArray, int n, int n2) {
        long l = byArray[n + 0] & 0xFF;
        for (int i = 1; i < n2; ++i) {
            l |= (long)(byArray[n + i] & 0xFF) << 8 * i;
        }
        return l;
    }

    private void cbd(short[] sArray, byte[] byArray, int n) {
        block7: {
            int[] nArray;
            int[] nArray2;
            block8: {
                block6: {
                    nArray2 = new int[4];
                    nArray = new int[4];
                    if (this.engine.getSABER_MU() != 6) break block6;
                    for (int i = 0; i < this.SABER_N / 4; ++i) {
                        int n2 = (int)this.load_littleendian(byArray, n + 3 * i, 3);
                        int n3 = 0;
                        for (int j = 0; j < 3; ++j) {
                            n3 += n2 >> j & 0x249249;
                        }
                        nArray2[0] = n3 & 7;
                        nArray[0] = n3 >>> 3 & 7;
                        nArray2[1] = n3 >>> 6 & 7;
                        nArray[1] = n3 >>> 9 & 7;
                        nArray2[2] = n3 >>> 12 & 7;
                        nArray[2] = n3 >>> 15 & 7;
                        nArray2[3] = n3 >>> 18 & 7;
                        nArray[3] = n3 >>> 21;
                        sArray[4 * i + 0] = (short)(nArray2[0] - nArray[0]);
                        sArray[4 * i + 1] = (short)(nArray2[1] - nArray[1]);
                        sArray[4 * i + 2] = (short)(nArray2[2] - nArray[2]);
                        sArray[4 * i + 3] = (short)(nArray2[3] - nArray[3]);
                    }
                    break block7;
                }
                if (this.engine.getSABER_MU() != 8) break block8;
                for (int i = 0; i < this.SABER_N / 4; ++i) {
                    int n4 = (int)this.load_littleendian(byArray, n + 4 * i, 4);
                    int n5 = 0;
                    for (int j = 0; j < 4; ++j) {
                        n5 += n4 >>> j & 0x11111111;
                    }
                    nArray2[0] = n5 & 0xF;
                    nArray[0] = n5 >>> 4 & 0xF;
                    nArray2[1] = n5 >>> 8 & 0xF;
                    nArray[1] = n5 >>> 12 & 0xF;
                    nArray2[2] = n5 >>> 16 & 0xF;
                    nArray[2] = n5 >>> 20 & 0xF;
                    nArray2[3] = n5 >>> 24 & 0xF;
                    nArray[3] = n5 >>> 28;
                    sArray[4 * i + 0] = (short)(nArray2[0] - nArray[0]);
                    sArray[4 * i + 1] = (short)(nArray2[1] - nArray[1]);
                    sArray[4 * i + 2] = (short)(nArray2[2] - nArray[2]);
                    sArray[4 * i + 3] = (short)(nArray2[3] - nArray[3]);
                }
                break block7;
            }
            if (this.engine.getSABER_MU() != 10) break block7;
            for (int i = 0; i < this.SABER_N / 4; ++i) {
                long l = this.load_littleendian(byArray, n + 5 * i, 5);
                long l2 = 0L;
                for (int j = 0; j < 5; ++j) {
                    l2 += l >>> j & 0x842108421L;
                }
                nArray2[0] = (int)(l2 & 0x1FL);
                nArray[0] = (int)(l2 >>> 5 & 0x1FL);
                nArray2[1] = (int)(l2 >>> 10 & 0x1FL);
                nArray[1] = (int)(l2 >>> 15 & 0x1FL);
                nArray2[2] = (int)(l2 >>> 20 & 0x1FL);
                nArray[2] = (int)(l2 >>> 25 & 0x1FL);
                nArray2[3] = (int)(l2 >>> 30 & 0x1FL);
                nArray[3] = (int)(l2 >>> 35);
                sArray[4 * i + 0] = (short)(nArray2[0] - nArray[0]);
                sArray[4 * i + 1] = (short)(nArray2[1] - nArray[1]);
                sArray[4 * i + 2] = (short)(nArray2[2] - nArray[2]);
                sArray[4 * i + 3] = (short)(nArray2[3] - nArray[3]);
            }
        }
    }

    private short OVERFLOWING_MUL(int n, int n2) {
        return (short)(n * n2);
    }

    private void karatsuba_simple(int[] nArray, int[] nArray2, int[] nArray3) {
        int n;
        int[] nArray4 = new int[31];
        int[] nArray5 = new int[31];
        int[] nArray6 = new int[31];
        int[] nArray7 = new int[63];
        for (n = 0; n < 16; ++n) {
            int n2 = nArray[n];
            int n3 = nArray[n + 16];
            int n4 = nArray[n + 32];
            int n5 = nArray[n + 48];
            for (int i = 0; i < 16; ++i) {
                int n6 = nArray2[i];
                int n7 = nArray2[i + 16];
                nArray3[n + i + 0] = nArray3[n + i + 0] + this.OVERFLOWING_MUL(n2, n6);
                nArray3[n + i + 32] = nArray3[n + i + 32] + this.OVERFLOWING_MUL(n3, n7);
                int n8 = n6 + n7;
                int n9 = n2 + n3;
                nArray4[n + i] = (int)((long)nArray4[n + i] + (long)n8 * (long)n9);
                n8 = nArray2[i + 32];
                n9 = nArray2[i + 48];
                nArray3[n + i + 64] = nArray3[n + i + 64] + this.OVERFLOWING_MUL(n8, n4);
                nArray3[n + i + 96] = nArray3[n + i + 96] + this.OVERFLOWING_MUL(n9, n5);
                int n10 = n4 + n5;
                int n11 = n8 + n9;
                nArray6[n + i] = nArray6[n + i] + this.OVERFLOWING_MUL(n10, n11);
                n6 += n8;
                n8 = n2 + n4;
                nArray7[n + i + 0] = nArray7[n + i + 0] + this.OVERFLOWING_MUL(n6, n8);
                n7 += n9;
                n9 = n3 + n5;
                nArray7[n + i + 32] = nArray7[n + i + 32] + this.OVERFLOWING_MUL(n7, n9);
                nArray5[n + i] = nArray5[n + i] + this.OVERFLOWING_MUL(n6 += n7, n8 += n9);
            }
        }
        for (n = 0; n < 31; ++n) {
            nArray5[n] = nArray5[n] - nArray7[n + 0] - nArray7[n + 32];
            nArray4[n] = nArray4[n] - nArray3[n + 0] - nArray3[n + 32];
            nArray6[n] = nArray6[n] - nArray3[n + 64] - nArray3[n + 96];
        }
        for (n = 0; n < 31; ++n) {
            nArray7[n + 16] = nArray7[n + 16] + nArray5[n];
            nArray3[n + 16] = nArray3[n + 16] + nArray4[n];
            nArray3[n + 80] = nArray3[n + 80] + nArray6[n];
        }
        for (n = 0; n < 63; ++n) {
            nArray7[n] = nArray7[n] - nArray3[n] - nArray3[n + 64];
        }
        for (n = 0; n < 63; ++n) {
            nArray3[n + 32] = nArray3[n + 32] + nArray7[n];
        }
    }

    private void toom_cook_4way(short[] sArray, short[] sArray2, short[] sArray3) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10 = 43691;
        int n11 = 36409;
        int n12 = 61167;
        int[] nArray = new int[this.N_SB];
        int[] nArray2 = new int[this.N_SB];
        int[] nArray3 = new int[this.N_SB];
        int[] nArray4 = new int[this.N_SB];
        int[] nArray5 = new int[this.N_SB];
        int[] nArray6 = new int[this.N_SB];
        int[] nArray7 = new int[this.N_SB];
        int[] nArray8 = new int[this.N_SB];
        int[] nArray9 = new int[this.N_SB];
        int[] nArray10 = new int[this.N_SB];
        int[] nArray11 = new int[this.N_SB];
        int[] nArray12 = new int[this.N_SB];
        int[] nArray13 = new int[this.N_SB];
        int[] nArray14 = new int[this.N_SB];
        int[] nArray15 = new int[this.N_SB_RES];
        int[] nArray16 = new int[this.N_SB_RES];
        int[] nArray17 = new int[this.N_SB_RES];
        int[] nArray18 = new int[this.N_SB_RES];
        int[] nArray19 = new int[this.N_SB_RES];
        int[] nArray20 = new int[this.N_SB_RES];
        int[] nArray21 = new int[this.N_SB_RES];
        short[] sArray4 = sArray3;
        for (n9 = 0; n9 < this.N_SB; ++n9) {
            n8 = sArray[n9];
            n7 = sArray[n9 + this.N_SB];
            n6 = sArray[n9 + this.N_SB * 2];
            n5 = sArray[n9 + this.N_SB * 3];
            n4 = n8 + n6;
            n3 = n7 + n5;
            n2 = n4 + n3;
            n = n4 - n3;
            nArray3[n9] = n2;
            nArray4[n9] = n;
            n4 = (short)((n8 << 2) + n6 << 1);
            n3 = (short)((n7 << 2) + n5);
            n2 = (short)(n4 + n3);
            n = (short)(n4 - n3);
            nArray5[n9] = n2;
            nArray6[n9] = n;
            nArray2[n9] = n4 = (int)((short)((n5 << 3) + (n6 << 2) + (n7 << 1) + n8));
            nArray7[n9] = n8;
            nArray[n9] = n5;
        }
        for (n9 = 0; n9 < this.N_SB; ++n9) {
            n8 = sArray2[n9];
            n7 = sArray2[n9 + this.N_SB];
            n6 = sArray2[n9 + this.N_SB * 2];
            n5 = sArray2[n9 + this.N_SB * 3];
            n4 = n8 + n6;
            n3 = n7 + n5;
            n2 = n4 + n3;
            n = n4 - n3;
            nArray10[n9] = n2;
            nArray11[n9] = n;
            n4 = (n8 << 2) + n6 << 1;
            n3 = (n7 << 2) + n5;
            n2 = n4 + n3;
            n = n4 - n3;
            nArray12[n9] = n2;
            nArray13[n9] = n;
            nArray9[n9] = n4 = (n5 << 3) + (n6 << 2) + (n7 << 1) + n8;
            nArray14[n9] = n8;
            nArray8[n9] = n5;
        }
        this.karatsuba_simple(nArray, nArray8, nArray15);
        this.karatsuba_simple(nArray2, nArray9, nArray16);
        this.karatsuba_simple(nArray3, nArray10, nArray17);
        this.karatsuba_simple(nArray4, nArray11, nArray18);
        this.karatsuba_simple(nArray5, nArray12, nArray19);
        this.karatsuba_simple(nArray6, nArray13, nArray20);
        this.karatsuba_simple(nArray7, nArray14, nArray21);
        for (int i = 0; i < this.N_SB_RES; ++i) {
            n8 = nArray15[i];
            n7 = nArray16[i];
            n6 = nArray17[i];
            n5 = nArray18[i];
            n4 = nArray19[i];
            n3 = nArray20[i];
            n2 = nArray21[i];
            n7 += n4;
            n3 -= n4;
            n5 = (n5 & 0xFFFF) - (n6 & 0xFFFF) >>> 1;
            n4 -= n8;
            n4 -= n2 << 6;
            n4 = (n4 << 1) + n3;
            n7 = n7 - ((n6 += n5) << 6) - n6;
            n6 -= n2;
            n4 = ((n4 & 0xFFFF) - (n6 << 3)) * n10 >> 3;
            n3 += (n7 += 45 * (n6 -= n8));
            n7 = ((n7 & 0xFFFF) + ((n5 & 0xFFFF) << 4)) * n11 >> 1;
            n5 = -(n5 + n7);
            n3 = (30 * (n7 & 0xFFFF) - (n3 & 0xFFFF)) * n12 >> 2;
            n6 -= n4;
            n7 -= n3;
            int n13 = i;
            sArray4[n13] = (short)(sArray4[n13] + (n2 & 0xFFFF));
            int n14 = i + 64;
            sArray4[n14] = (short)(sArray4[n14] + (n3 & 0xFFFF));
            int n15 = i + 128;
            sArray4[n15] = (short)(sArray4[n15] + (n4 & 0xFFFF));
            int n16 = i + 192;
            sArray4[n16] = (short)(sArray4[n16] + (n5 & 0xFFFF));
            int n17 = i + 256;
            sArray4[n17] = (short)(sArray4[n17] + (n6 & 0xFFFF));
            int n18 = i + 320;
            sArray4[n18] = (short)(sArray4[n18] + (n7 & 0xFFFF));
            int n19 = i + 384;
            sArray4[n19] = (short)(sArray4[n19] + (n8 & 0xFFFF));
        }
    }

    private void poly_mul_acc(short[] sArray, short[] sArray2, short[] sArray3) {
        short[] sArray4 = new short[2 * this.SABER_N];
        this.toom_cook_4way(sArray, sArray2, sArray4);
        for (int i = this.SABER_N; i < 2 * this.SABER_N; ++i) {
            int n = i - this.SABER_N;
            sArray3[n] = (short)(sArray3[n] + (sArray4[i - this.SABER_N] - sArray4[i]));
        }
    }

    public void MatrixVectorMul(short[][][] sArray, short[][] sArray2, short[][] sArray3, int n) {
        for (int i = 0; i < this.SABER_L; ++i) {
            for (int j = 0; j < this.SABER_L; ++j) {
                if (n == 1) {
                    this.poly_mul_acc(sArray[j][i], sArray2[j], sArray3[i]);
                    continue;
                }
                this.poly_mul_acc(sArray[i][j], sArray2[j], sArray3[i]);
            }
        }
    }

    public void InnerProd(short[][] sArray, short[][] sArray2, short[] sArray3) {
        for (int i = 0; i < this.SABER_L; ++i) {
            this.poly_mul_acc(sArray[i], sArray2[i], sArray3);
        }
    }
}

