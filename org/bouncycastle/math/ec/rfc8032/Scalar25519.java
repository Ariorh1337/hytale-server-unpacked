/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.math.ec.rfc8032.Codec;
import org.bouncycastle.math.ec.rfc8032.ScalarUtil;
import org.bouncycastle.math.ec.rfc8032.Wnaf;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

abstract class Scalar25519 {
    static final int SIZE = 8;
    private static final int SCALAR_BYTES = 32;
    private static final long M08L = 255L;
    private static final long M28L = 0xFFFFFFFL;
    private static final long M32L = 0xFFFFFFFFL;
    private static final int TARGET_LENGTH = 254;
    private static final int[] L = new int[]{1559614445, 1477600026, -1560830762, 350157278, 0, 0, 0, 0x10000000};
    private static final int[] LSq = new int[]{-1424848535, -487721339, 580428573, 1745064566, -770181698, 1036971123, 461123738, -1582065343, 1268693629, -889041821, -731974758, 43769659, 0, 0, 0, 0x1000000};
    private static final int L0 = -50998291;
    private static final int L1 = 19280294;
    private static final int L2 = 127719000;
    private static final int L3 = -6428113;
    private static final int L4 = 5343;

    Scalar25519() {
    }

    static boolean checkVar(byte[] byArray, int[] nArray) {
        Scalar25519.decode(byArray, nArray);
        return !Nat256.gte(nArray, L);
    }

    static void decode(byte[] byArray, int[] nArray) {
        Codec.decode32(byArray, 0, nArray, 0, 8);
    }

    static void getOrderWnafVar(int n, byte[] byArray) {
        Wnaf.getSignedVar(L, n, byArray);
    }

    static void multiply128Var(int[] nArray, int[] nArray2, int[] nArray3) {
        int[] nArray4 = new int[12];
        Nat256.mul128(nArray, nArray2, nArray4);
        if (nArray2[3] < 0) {
            Nat256.addTo(L, 0, nArray4, 4, 0);
            Nat256.subFrom(nArray, 0, nArray4, 4, 0);
        }
        byte[] byArray = new byte[48];
        Codec.encode32(nArray4, 0, 12, byArray, 0);
        byte[] byArray2 = Scalar25519.reduce384(byArray);
        Scalar25519.decode(byArray2, nArray3);
    }

    static byte[] reduce384(byte[] byArray) {
        long l = (long)Codec.decode32(byArray, 0) & 0xFFFFFFFFL;
        long l2 = (long)(Codec.decode24(byArray, 4) << 4) & 0xFFFFFFFFL;
        long l3 = (long)Codec.decode32(byArray, 7) & 0xFFFFFFFFL;
        long l4 = (long)(Codec.decode24(byArray, 11) << 4) & 0xFFFFFFFFL;
        long l5 = (long)Codec.decode32(byArray, 14) & 0xFFFFFFFFL;
        long l6 = (long)(Codec.decode24(byArray, 18) << 4) & 0xFFFFFFFFL;
        long l7 = (long)Codec.decode32(byArray, 21) & 0xFFFFFFFFL;
        long l8 = (long)(Codec.decode24(byArray, 25) << 4) & 0xFFFFFFFFL;
        long l9 = (long)Codec.decode32(byArray, 28) & 0xFFFFFFFFL;
        long l10 = (long)(Codec.decode24(byArray, 32) << 4) & 0xFFFFFFFFL;
        long l11 = (long)Codec.decode32(byArray, 35) & 0xFFFFFFFFL;
        long l12 = (long)(Codec.decode24(byArray, 39) << 4) & 0xFFFFFFFFL;
        long l13 = (long)Codec.decode32(byArray, 42) & 0xFFFFFFFFL;
        long l14 = (long)(Codec.decode16(byArray, 46) << 4) & 0xFFFFFFFFL;
        l14 += l13 >> 28;
        l13 &= 0xFFFFFFFL;
        l5 -= l14 * -50998291L;
        l6 -= l14 * 19280294L;
        l7 -= l14 * 127719000L;
        l8 -= l14 * -6428113L;
        l9 -= l14 * 5343L;
        l13 += l12 >> 28;
        l12 &= 0xFFFFFFFL;
        l4 -= l13 * -50998291L;
        l5 -= l13 * 19280294L;
        l6 -= l13 * 127719000L;
        l7 -= l13 * -6428113L;
        l8 -= l13 * 5343L;
        l12 += l11 >> 28;
        l11 &= 0xFFFFFFFL;
        l3 -= l12 * -50998291L;
        l4 -= l12 * 19280294L;
        l5 -= l12 * 127719000L;
        l6 -= l12 * -6428113L;
        l7 -= l12 * 5343L;
        l11 += l10 >> 28;
        l10 &= 0xFFFFFFFL;
        l2 -= l11 * -50998291L;
        l3 -= l11 * 19280294L;
        l4 -= l11 * 127719000L;
        l5 -= l11 * -6428113L;
        l6 -= l11 * 5343L;
        l9 += l8 >> 28;
        l8 &= 0xFFFFFFFL;
        l10 += l9 >> 28;
        long l15 = (l9 &= 0xFFFFFFFL) >>> 27;
        l2 -= l10 * 19280294L;
        l3 -= l10 * 127719000L;
        l4 -= l10 * -6428113L;
        l5 -= l10 * 5343L;
        l &= 0xFFFFFFFL;
        l2 &= 0xFFFFFFFL;
        l3 &= 0xFFFFFFFL;
        l4 &= 0xFFFFFFFL;
        l5 &= 0xFFFFFFFL;
        l6 &= 0xFFFFFFFL;
        l7 &= 0xFFFFFFFL;
        l8 &= 0xFFFFFFFL;
        l10 = (l9 += (l8 += (l7 += (l6 += (l5 += (l4 += (l3 += (l2 += (l -= (l10 += l15) * -50998291L) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28;
        l9 &= 0xFFFFFFFL;
        l2 += l10 & 0x12631A6L;
        l3 += l10 & 0x79CD658L;
        l4 += l10 & 0xFFFFFFFFFF9DEA2FL;
        l5 += l10 & 0x14DFL;
        l &= 0xFFFFFFFL;
        l2 &= 0xFFFFFFFL;
        l3 &= 0xFFFFFFFL;
        l4 &= 0xFFFFFFFL;
        l5 &= 0xFFFFFFFL;
        l6 &= 0xFFFFFFFL;
        l7 &= 0xFFFFFFFL;
        l9 += (l8 += (l7 += (l6 += (l5 += (l4 += (l3 += (l2 += (l += (l10 -= l15) & 0xFFFFFFFFFCF5D3EDL) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28;
        byte[] byArray2 = new byte[64];
        Codec.encode56(l | l2 << 28, byArray2, 0);
        Codec.encode56(l3 | l4 << 28, byArray2, 7);
        Codec.encode56(l5 | l6 << 28, byArray2, 14);
        Codec.encode56(l7 | (l8 &= 0xFFFFFFFL) << 28, byArray2, 21);
        Codec.encode32((int)l9, byArray2, 28);
        return byArray2;
    }

    static byte[] reduce512(byte[] byArray) {
        long l = (long)Codec.decode32(byArray, 0) & 0xFFFFFFFFL;
        long l2 = (long)(Codec.decode24(byArray, 4) << 4) & 0xFFFFFFFFL;
        long l3 = (long)Codec.decode32(byArray, 7) & 0xFFFFFFFFL;
        long l4 = (long)(Codec.decode24(byArray, 11) << 4) & 0xFFFFFFFFL;
        long l5 = (long)Codec.decode32(byArray, 14) & 0xFFFFFFFFL;
        long l6 = (long)(Codec.decode24(byArray, 18) << 4) & 0xFFFFFFFFL;
        long l7 = (long)Codec.decode32(byArray, 21) & 0xFFFFFFFFL;
        long l8 = (long)(Codec.decode24(byArray, 25) << 4) & 0xFFFFFFFFL;
        long l9 = (long)Codec.decode32(byArray, 28) & 0xFFFFFFFFL;
        long l10 = (long)(Codec.decode24(byArray, 32) << 4) & 0xFFFFFFFFL;
        long l11 = (long)Codec.decode32(byArray, 35) & 0xFFFFFFFFL;
        long l12 = (long)(Codec.decode24(byArray, 39) << 4) & 0xFFFFFFFFL;
        long l13 = (long)Codec.decode32(byArray, 42) & 0xFFFFFFFFL;
        long l14 = (long)(Codec.decode24(byArray, 46) << 4) & 0xFFFFFFFFL;
        long l15 = (long)Codec.decode32(byArray, 49) & 0xFFFFFFFFL;
        long l16 = (long)(Codec.decode24(byArray, 53) << 4) & 0xFFFFFFFFL;
        long l17 = (long)Codec.decode32(byArray, 56) & 0xFFFFFFFFL;
        long l18 = (long)(Codec.decode24(byArray, 60) << 4) & 0xFFFFFFFFL;
        long l19 = (long)byArray[63] & 0xFFL;
        l10 -= l19 * -50998291L;
        l11 -= l19 * 19280294L;
        l12 -= l19 * 127719000L;
        l13 -= l19 * -6428113L;
        l14 -= l19 * 5343L;
        l18 += l17 >> 28;
        l17 &= 0xFFFFFFFL;
        l9 -= l18 * -50998291L;
        l10 -= l18 * 19280294L;
        l11 -= l18 * 127719000L;
        l12 -= l18 * -6428113L;
        l13 -= l18 * 5343L;
        l8 -= l17 * -50998291L;
        l9 -= l17 * 19280294L;
        l10 -= l17 * 127719000L;
        l11 -= l17 * -6428113L;
        l12 -= l17 * 5343L;
        l16 += l15 >> 28;
        l15 &= 0xFFFFFFFL;
        l7 -= l16 * -50998291L;
        l8 -= l16 * 19280294L;
        l9 -= l16 * 127719000L;
        l10 -= l16 * -6428113L;
        l11 -= l16 * 5343L;
        l6 -= l15 * -50998291L;
        l7 -= l15 * 19280294L;
        l8 -= l15 * 127719000L;
        l9 -= l15 * -6428113L;
        l10 -= l15 * 5343L;
        l14 += l13 >> 28;
        l13 &= 0xFFFFFFFL;
        l5 -= l14 * -50998291L;
        l6 -= l14 * 19280294L;
        l7 -= l14 * 127719000L;
        l8 -= l14 * -6428113L;
        l9 -= l14 * 5343L;
        l13 += l12 >> 28;
        l12 &= 0xFFFFFFFL;
        l4 -= l13 * -50998291L;
        l5 -= l13 * 19280294L;
        l6 -= l13 * 127719000L;
        l7 -= l13 * -6428113L;
        l8 -= l13 * 5343L;
        l12 += l11 >> 28;
        l11 &= 0xFFFFFFFL;
        l3 -= l12 * -50998291L;
        l4 -= l12 * 19280294L;
        l5 -= l12 * 127719000L;
        l6 -= l12 * -6428113L;
        l7 -= l12 * 5343L;
        l11 += l10 >> 28;
        l10 &= 0xFFFFFFFL;
        l2 -= l11 * -50998291L;
        l3 -= l11 * 19280294L;
        l4 -= l11 * 127719000L;
        l5 -= l11 * -6428113L;
        l6 -= l11 * 5343L;
        l9 += l8 >> 28;
        l8 &= 0xFFFFFFFL;
        l10 += l9 >> 28;
        long l20 = (l9 &= 0xFFFFFFFL) >>> 27;
        l2 -= l10 * 19280294L;
        l3 -= l10 * 127719000L;
        l4 -= l10 * -6428113L;
        l5 -= l10 * 5343L;
        l &= 0xFFFFFFFL;
        l2 &= 0xFFFFFFFL;
        l3 &= 0xFFFFFFFL;
        l4 &= 0xFFFFFFFL;
        l5 &= 0xFFFFFFFL;
        l6 &= 0xFFFFFFFL;
        l7 &= 0xFFFFFFFL;
        l8 &= 0xFFFFFFFL;
        l10 = (l9 += (l8 += (l7 += (l6 += (l5 += (l4 += (l3 += (l2 += (l -= (l10 += l20) * -50998291L) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28;
        l9 &= 0xFFFFFFFL;
        l2 += l10 & 0x12631A6L;
        l3 += l10 & 0x79CD658L;
        l4 += l10 & 0xFFFFFFFFFF9DEA2FL;
        l5 += l10 & 0x14DFL;
        l &= 0xFFFFFFFL;
        l2 &= 0xFFFFFFFL;
        l3 &= 0xFFFFFFFL;
        l4 &= 0xFFFFFFFL;
        l5 &= 0xFFFFFFFL;
        l6 &= 0xFFFFFFFL;
        l7 &= 0xFFFFFFFL;
        l9 += (l8 += (l7 += (l6 += (l5 += (l4 += (l3 += (l2 += (l += (l10 -= l20) & 0xFFFFFFFFFCF5D3EDL) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28;
        byte[] byArray2 = new byte[32];
        Codec.encode56(l | l2 << 28, byArray2, 0);
        Codec.encode56(l3 | l4 << 28, byArray2, 7);
        Codec.encode56(l5 | l6 << 28, byArray2, 14);
        Codec.encode56(l7 | (l8 &= 0xFFFFFFFL) << 28, byArray2, 21);
        Codec.encode32((int)l9, byArray2, 28);
        return byArray2;
    }

    static boolean reduceBasisVar(int[] nArray, int[] nArray2, int[] nArray3) {
        int[] nArray4 = new int[16];
        System.arraycopy(LSq, 0, nArray4, 0, 16);
        int[] nArray5 = new int[16];
        Nat256.square(nArray, nArray5);
        nArray5[0] = nArray5[0] + 1;
        int[] nArray6 = new int[16];
        Nat256.mul(L, nArray, nArray6);
        int[] nArray7 = new int[16];
        int[] nArray8 = new int[4];
        System.arraycopy(L, 0, nArray8, 0, 4);
        int[] nArray9 = new int[4];
        int[] nArray10 = new int[4];
        System.arraycopy(nArray, 0, nArray10, 0, 4);
        int[] nArray11 = new int[4];
        nArray11[0] = 1;
        int n = 1016;
        int n2 = 15;
        int n3 = ScalarUtil.getBitLengthPositive(n2, nArray5);
        while (n3 > 254) {
            if (--n < 0) {
                return false;
            }
            int n4 = ScalarUtil.getBitLength(n2, nArray6);
            int n5 = n4 - n3;
            n5 &= ~(n5 >> 31);
            if (nArray6[n2] < 0) {
                ScalarUtil.addShifted_NP(n2, n5, nArray4, nArray5, nArray6, nArray7);
                ScalarUtil.addShifted_UV(3, n5, nArray8, nArray9, nArray10, nArray11);
            } else {
                ScalarUtil.subShifted_NP(n2, n5, nArray4, nArray5, nArray6, nArray7);
                ScalarUtil.subShifted_UV(3, n5, nArray8, nArray9, nArray10, nArray11);
            }
            if (!ScalarUtil.lessThan(n2, nArray4, nArray5)) continue;
            int[] nArray12 = nArray8;
            nArray8 = nArray10;
            nArray10 = nArray12;
            int[] nArray13 = nArray9;
            nArray9 = nArray11;
            nArray11 = nArray13;
            int[] nArray14 = nArray4;
            nArray4 = nArray5;
            nArray5 = nArray14;
            n2 = n3 >>> 5;
            n3 = ScalarUtil.getBitLengthPositive(n2, nArray5);
        }
        System.arraycopy(nArray10, 0, nArray2, 0, 4);
        System.arraycopy(nArray11, 0, nArray3, 0, 4);
        return true;
    }

    static void toSignedDigits(int n, int[] nArray) {
        Nat.caddTo(8, ~nArray[0] & 1, L, nArray);
        Nat.shiftDownBit(8, nArray, 1);
    }
}

