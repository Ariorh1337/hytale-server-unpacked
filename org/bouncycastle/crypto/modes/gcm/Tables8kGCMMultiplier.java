/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.util.Pack;

public class Tables8kGCMMultiplier
implements GCMMultiplier {
    private byte[] H;
    private long[][][] T;

    @Override
    public void init(byte[] byArray) {
        if (this.T == null) {
            this.T = new long[2][256][2];
        } else if (0 != GCMUtil.areEqual(this.H, byArray)) {
            return;
        }
        this.H = new byte[16];
        GCMUtil.copy(byArray, this.H);
        for (int i = 0; i < 2; ++i) {
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
        long[][] lArray = this.T[0];
        long[][] lArray2 = this.T[1];
        long[] lArray3 = lArray[byArray[14] & 0xFF];
        long[] lArray4 = lArray2[byArray[15] & 0xFF];
        long l = lArray3[0] ^ lArray4[0];
        long l2 = lArray3[1] ^ lArray4[1];
        for (int i = 12; i >= 0; i -= 2) {
            lArray3 = lArray[byArray[i] & 0xFF];
            lArray4 = lArray2[byArray[i + 1] & 0xFF];
            long l3 = l2 << 48;
            l2 = lArray3[1] ^ lArray4[1] ^ (l2 >>> 16 | l << 48);
            l = lArray3[0] ^ lArray4[0] ^ l >>> 16 ^ l3 ^ l3 >>> 1 ^ l3 >>> 2 ^ l3 >>> 7;
        }
        Pack.longToBigEndian(l, byArray, 0);
        Pack.longToBigEndian(l2, byArray, 8);
    }
}

