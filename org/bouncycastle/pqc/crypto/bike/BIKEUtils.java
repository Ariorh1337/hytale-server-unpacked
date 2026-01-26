/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Pack;

class BIKEUtils {
    BIKEUtils() {
    }

    static int getHammingWeight(byte[] byArray) {
        int n = 0;
        for (int i = 0; i < byArray.length; ++i) {
            n += byArray[i];
        }
        return n;
    }

    static void fromBitArrayToByteArray(byte[] byArray, byte[] byArray2, int n, int n2) {
        int n3 = 0;
        int n4 = 0;
        long l = n2;
        while ((long)n3 < l) {
            int n5;
            int n6;
            if (n3 + 8 >= n2) {
                n6 = byArray2[n + n3];
                for (n5 = n2 - n3 - 1; n5 >= 1; --n5) {
                    n6 |= byArray2[n + n3 + n5] << n5;
                }
                byArray[n4] = (byte)n6;
            } else {
                n6 = byArray2[n + n3];
                for (n5 = 7; n5 >= 1; --n5) {
                    n6 |= byArray2[n + n3 + n5] << n5;
                }
                byArray[n4] = (byte)n6;
            }
            n3 += 8;
            ++n4;
        }
    }

    static void generateRandomByteArray(byte[] byArray, int n, int n2, Xof xof) {
        byte[] byArray2 = new byte[4];
        for (int i = n2 - 1; i >= 0; --i) {
            xof.doOutput(byArray2, 0, 4);
            long l = (long)Pack.littleEndianToInt(byArray2, 0) & 0xFFFFFFFFL;
            l = l * (long)(n - i) >> 32;
            int n3 = (int)l;
            if (BIKEUtils.CHECK_BIT(byArray, n3 += i) != 0) {
                n3 = i;
            }
            BIKEUtils.SET_BIT(byArray, n3);
        }
    }

    protected static int CHECK_BIT(byte[] byArray, int n) {
        int n2 = n / 8;
        int n3 = n % 8;
        return byArray[n2] >>> n3 & 1;
    }

    protected static void SET_BIT(byte[] byArray, int n) {
        int n2 = n / 8;
        int n3 = n % 8;
        int n4 = n2;
        byArray[n4] = (byte)((long)byArray[n4] | 1L << (int)((long)n3));
    }
}

