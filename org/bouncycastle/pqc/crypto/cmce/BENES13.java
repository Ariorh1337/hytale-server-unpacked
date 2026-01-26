/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.pqc.crypto.cmce.BENES;
import org.bouncycastle.pqc.crypto.cmce.Utils;

class BENES13
extends BENES {
    public BENES13(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    static void layer_in(long[] lArray, long[] lArray2, int n) {
        int n2 = 0;
        int n3 = 1 << n;
        for (int i = 0; i < 64; i += n3 * 2) {
            for (int j = i; j < i + n3; ++j) {
                long l = lArray[j + 0] ^ lArray[j + n3];
                int n4 = n2++;
                int n5 = j + 0;
                lArray[n5] = lArray[n5] ^ (l &= lArray2[n4]);
                int n6 = j + n3;
                lArray[n6] = lArray[n6] ^ l;
                l = lArray[64 + j + 0] ^ lArray[64 + j + n3];
                int n7 = n2++;
                int n8 = 64 + j + 0;
                lArray[n8] = lArray[n8] ^ (l &= lArray2[n7]);
                int n9 = 64 + j + n3;
                lArray[n9] = lArray[n9] ^ l;
            }
        }
    }

    static void layer_ex(long[] lArray, long[] lArray2, int n) {
        int n2 = 0;
        int n3 = 1 << n;
        for (int i = 0; i < 128; i += n3 * 2) {
            for (int j = i; j < i + n3; ++j) {
                long l = lArray[j + 0] ^ lArray[j + n3];
                int n4 = n2++;
                int n5 = j + 0;
                lArray[n5] = lArray[n5] ^ (l &= lArray2[n4]);
                int n6 = j + n3;
                lArray[n6] = lArray[n6] ^ l;
            }
        }
    }

    void apply_benes(byte[] byArray, byte[] byArray2, int n) {
        int n2;
        int n3;
        int n4;
        int n5 = 0;
        int n6 = 0;
        long[] lArray = new long[128];
        long[] lArray2 = new long[128];
        long[] lArray3 = new long[64];
        long[] lArray4 = new long[64];
        if (n == 0) {
            n6 = this.SYS_T * 2 + 40;
            n4 = 0;
        } else {
            n6 = this.SYS_T * 2 + 40 + 12288;
            n4 = -1024;
        }
        for (n3 = 0; n3 < 64; ++n3) {
            lArray[n3 + 0] = Utils.load8(byArray, n5 + n3 * 16 + 0);
            lArray[n3 + 64] = Utils.load8(byArray, n5 + n3 * 16 + 8);
        }
        BENES13.transpose_64x64(lArray2, lArray, 0);
        BENES13.transpose_64x64(lArray2, lArray, 64);
        for (n2 = 0; n2 <= 6; ++n2) {
            for (n3 = 0; n3 < 64; ++n3) {
                lArray3[n3] = Utils.load8(byArray2, n6);
                n6 += 8;
            }
            n6 += n4;
            BENES13.transpose_64x64(lArray4, lArray3);
            BENES13.layer_ex(lArray2, lArray4, n2);
        }
        BENES13.transpose_64x64(lArray, lArray2, 0);
        BENES13.transpose_64x64(lArray, lArray2, 64);
        for (n2 = 0; n2 <= 5; ++n2) {
            for (n3 = 0; n3 < 64; ++n3) {
                lArray3[n3] = Utils.load8(byArray2, n6);
                n6 += 8;
            }
            n6 += n4;
            BENES13.layer_in(lArray, lArray3, n2);
        }
        for (n2 = 4; n2 >= 0; --n2) {
            for (n3 = 0; n3 < 64; ++n3) {
                lArray3[n3] = Utils.load8(byArray2, n6);
                n6 += 8;
            }
            n6 += n4;
            BENES13.layer_in(lArray, lArray3, n2);
        }
        BENES13.transpose_64x64(lArray2, lArray, 0);
        BENES13.transpose_64x64(lArray2, lArray, 64);
        for (n2 = 6; n2 >= 0; --n2) {
            for (n3 = 0; n3 < 64; ++n3) {
                lArray3[n3] = Utils.load8(byArray2, n6);
                n6 += 8;
            }
            n6 += n4;
            BENES13.transpose_64x64(lArray4, lArray3);
            BENES13.layer_ex(lArray2, lArray4, n2);
        }
        BENES13.transpose_64x64(lArray, lArray2, 0);
        BENES13.transpose_64x64(lArray, lArray2, 64);
        for (n3 = 0; n3 < 64; ++n3) {
            Utils.store8(byArray, n5 + n3 * 16 + 0, lArray[0 + n3]);
            Utils.store8(byArray, n5 + n3 * 16 + 8, lArray[64 + n3]);
        }
    }

    @Override
    public void support_gen(short[] sArray, byte[] byArray) {
        int n;
        int n2;
        byte[][] byArray2 = new byte[this.GFBITS][(1 << this.GFBITS) / 8];
        for (n2 = 0; n2 < this.GFBITS; ++n2) {
            for (n = 0; n < (1 << this.GFBITS) / 8; ++n) {
                byArray2[n2][n] = 0;
            }
        }
        for (n2 = 0; n2 < 1 << this.GFBITS; ++n2) {
            short s = Utils.bitrev((short)n2, this.GFBITS);
            for (n = 0; n < this.GFBITS; ++n) {
                byte[] byArray3 = byArray2[n];
                int n3 = n2 / 8;
                byArray3[n3] = (byte)(byArray3[n3] | (s >> n & 1) << n2 % 8);
            }
        }
        for (n = 0; n < this.GFBITS; ++n) {
            this.apply_benes(byArray2[n], byArray, 0);
        }
        for (n2 = 0; n2 < this.SYS_N; ++n2) {
            sArray[n2] = 0;
            for (n = this.GFBITS - 1; n >= 0; --n) {
                int n4 = n2;
                sArray[n4] = (short)(sArray[n4] << 1);
                int n5 = n2;
                sArray[n5] = (short)(sArray[n5] | byArray2[n][n2 / 8] >> n2 % 8 & 1);
            }
        }
    }
}

