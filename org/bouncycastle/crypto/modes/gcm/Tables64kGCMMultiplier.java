/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.util.Pack;

public class Tables64kGCMMultiplier
implements GCMMultiplier {
    private byte[] H;
    private long[][][] T;

    @Override
    public void init(byte[] byArray) {
        if (this.T == null) {
            this.T = new long[16][256][2];
        } else if (0 != GCMUtil.areEqual(this.H, byArray)) {
            return;
        }
        this.H = new byte[16];
        GCMUtil.copy(byArray, this.H);
        for (int i = 0; i < 16; ++i) {
            long[][] lArray = this.T[i];
            if (i == 0) {
                GCMUtil.asLongs(this.H, lArray[1]);
                GCMUtil.multiplyP7(lArray[1], lArray[1]);
            } else {
                GCMUtil.multiplyP8(this.T[i - 1][1], lArray[1]);
            }
            for (int j = 2; j < 256; j += 2) {
                GCMUtil.divideP(lArray[j >> 1], lArray[j]);
                GCMUtil.xor(lArray[j], lArray[1], lArray[j + 1]);
            }
        }
    }

    @Override
    public void multiplyH(byte[] byArray) {
        long[] lArray = this.T[0][byArray[0] & 0xFF];
        long[] lArray2 = this.T[1][byArray[1] & 0xFF];
        long[] lArray3 = this.T[2][byArray[2] & 0xFF];
        long[] lArray4 = this.T[3][byArray[3] & 0xFF];
        long[] lArray5 = this.T[4][byArray[4] & 0xFF];
        long[] lArray6 = this.T[5][byArray[5] & 0xFF];
        long[] lArray7 = this.T[6][byArray[6] & 0xFF];
        long[] lArray8 = this.T[7][byArray[7] & 0xFF];
        long[] lArray9 = this.T[8][byArray[8] & 0xFF];
        long[] lArray10 = this.T[9][byArray[9] & 0xFF];
        long[] lArray11 = this.T[10][byArray[10] & 0xFF];
        long[] lArray12 = this.T[11][byArray[11] & 0xFF];
        long[] lArray13 = this.T[12][byArray[12] & 0xFF];
        long[] lArray14 = this.T[13][byArray[13] & 0xFF];
        long[] lArray15 = this.T[14][byArray[14] & 0xFF];
        long[] lArray16 = this.T[15][byArray[15] & 0xFF];
        long l = lArray[0] ^ lArray2[0] ^ lArray3[0] ^ lArray4[0] ^ lArray5[0] ^ lArray6[0] ^ lArray7[0] ^ lArray8[0] ^ lArray9[0] ^ lArray10[0] ^ lArray11[0] ^ lArray12[0] ^ lArray13[0] ^ lArray14[0] ^ lArray15[0] ^ lArray16[0];
        long l2 = lArray[1] ^ lArray2[1] ^ lArray3[1] ^ lArray4[1] ^ lArray5[1] ^ lArray6[1] ^ lArray7[1] ^ lArray8[1] ^ lArray9[1] ^ lArray10[1] ^ lArray11[1] ^ lArray12[1] ^ lArray13[1] ^ lArray14[1] ^ lArray15[1] ^ lArray16[1];
        Pack.longToBigEndian(l, byArray, 0);
        Pack.longToBigEndian(l2, byArray, 8);
    }
}

