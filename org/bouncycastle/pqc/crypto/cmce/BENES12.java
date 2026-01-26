/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.pqc.crypto.cmce.BENES;
import org.bouncycastle.pqc.crypto.cmce.Utils;

class BENES12
extends BENES {
    public BENES12(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    static void layerBenes(long[] lArray, long[] lArray2, int n) {
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
            }
        }
    }

    private void apply_benes(byte[] byArray, byte[] byArray2, int n) {
        int n2;
        int n3;
        int n4;
        int n5;
        long[] lArray = new long[64];
        long[] lArray2 = new long[64];
        for (n5 = 0; n5 < 64; ++n5) {
            lArray[n5] = Utils.load8(byArray, n5 * 8);
        }
        if (n == 0) {
            n4 = 256;
            n3 = this.SYS_T * 2 + 40;
        } else {
            n4 = -256;
            n3 = this.SYS_T * 2 + 40 + (2 * this.GFBITS - 2) * 256;
        }
        BENES12.transpose_64x64(lArray, lArray);
        for (n2 = 0; n2 <= 5; ++n2) {
            for (n5 = 0; n5 < 64; ++n5) {
                lArray2[n5] = Utils.load4(byArray2, n3 + n5 * 4);
            }
            BENES12.transpose_64x64(lArray2, lArray2);
            BENES12.layerBenes(lArray, lArray2, n2);
            n3 += n4;
        }
        BENES12.transpose_64x64(lArray, lArray);
        for (n2 = 0; n2 <= 5; ++n2) {
            for (n5 = 0; n5 < 32; ++n5) {
                lArray2[n5] = Utils.load8(byArray2, n3 + n5 * 8);
            }
            BENES12.layerBenes(lArray, lArray2, n2);
            n3 += n4;
        }
        for (n2 = 4; n2 >= 0; --n2) {
            for (n5 = 0; n5 < 32; ++n5) {
                lArray2[n5] = Utils.load8(byArray2, n3 + n5 * 8);
            }
            BENES12.layerBenes(lArray, lArray2, n2);
            n3 += n4;
        }
        BENES12.transpose_64x64(lArray, lArray);
        for (n2 = 5; n2 >= 0; --n2) {
            for (n5 = 0; n5 < 64; ++n5) {
                lArray2[n5] = Utils.load4(byArray2, n3 + n5 * 4);
            }
            BENES12.transpose_64x64(lArray2, lArray2);
            BENES12.layerBenes(lArray, lArray2, n2);
            n3 += n4;
        }
        BENES12.transpose_64x64(lArray, lArray);
        for (n5 = 0; n5 < 64; ++n5) {
            Utils.store8(byArray, n5 * 8, lArray[n5]);
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
        for (n2 = 0; n2 < this.GFBITS; ++n2) {
            this.apply_benes(byArray2[n2], byArray, 0);
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

