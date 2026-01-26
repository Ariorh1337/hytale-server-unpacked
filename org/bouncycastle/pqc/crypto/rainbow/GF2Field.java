/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Pack;

class GF2Field {
    static final byte[][] gfMulTable;
    static final byte[] gfInvTable;
    public static final int MASK = 255;

    GF2Field() {
    }

    private static short gf4Mul2(short s) {
        int n = s << 1;
        return (short)((n ^= (s >>> 1) * 7) & 0xFF);
    }

    private static short gf4Mul3(short s) {
        int n = s - 2 >>> 1;
        int n2 = n & s * 3 | ~n & s - 1;
        return (short)(n2 & 0xFF);
    }

    private static short gf4Mul(short s, short s2) {
        int n = s * (s2 & 1);
        return (short)((n ^= GF2Field.gf4Mul2(s) * (s2 >>> 1)) & 0xFF);
    }

    private static short gf4Squ(short s) {
        int n = s ^ s >>> 1;
        return (short)(n & 0xFF);
    }

    private static short gf16Mul(short s, short s2) {
        short s3 = (short)(s & 3 & 0xFF);
        short s4 = (short)(s >>> 2 & 0xFF);
        short s5 = (short)(s2 & 3 & 0xFF);
        short s6 = (short)(s2 >>> 2 & 0xFF);
        short s7 = GF2Field.gf4Mul(s3, s5);
        short s8 = GF2Field.gf4Mul(s4, s6);
        short s9 = (short)(GF2Field.gf4Mul((short)(s3 ^ s4), (short)(s5 ^ s6)) ^ s7);
        short s10 = GF2Field.gf4Mul2(s8);
        return (short)((s9 << 2 ^ s7 ^ s10) & 0xFF);
    }

    private static short gf16Squ(short s) {
        short s2 = (short)(s & 3 & 0xFF);
        short s3 = (short)(s >>> 2 & 0xFF);
        s3 = GF2Field.gf4Squ(s3);
        short s4 = GF2Field.gf4Mul2(s3);
        return (short)((s3 << 2 ^ s4 ^ GF2Field.gf4Squ(s2)) & 0xFF);
    }

    private static short gf16Mul8(short s) {
        short s2 = (short)(s & 3 & 0xFF);
        short s3 = (short)(s >>> 2 & 0xFF);
        int n = GF2Field.gf4Mul2((short)(s2 ^ s3)) << 2;
        return (short)((n |= GF2Field.gf4Mul3(s3)) & 0xFF);
    }

    private static short gf256Mul(short s, short s2) {
        short s3 = (short)(s & 0xF & 0xFF);
        short s4 = (short)(s >>> 4 & 0xFF);
        short s5 = (short)(s2 & 0xF & 0xFF);
        short s6 = (short)(s2 >>> 4 & 0xFF);
        short s7 = GF2Field.gf16Mul(s3, s5);
        short s8 = GF2Field.gf16Mul(s4, s6);
        short s9 = (short)(GF2Field.gf16Mul((short)(s3 ^ s4), (short)(s5 ^ s6)) ^ s7);
        short s10 = GF2Field.gf16Mul8(s8);
        return (short)((s9 << 4 ^ s7 ^ s10) & 0xFF);
    }

    private static short gf256Squ(short s) {
        short s2 = (short)(s & 0xF & 0xFF);
        short s3 = (short)(s >>> 4 & 0xFF);
        s3 = GF2Field.gf16Squ(s3);
        short s4 = GF2Field.gf16Mul8(s3);
        return (short)((s3 << 4 ^ s4 ^ GF2Field.gf16Squ(s2)) & 0xFF);
    }

    private static short gf256Inv(short s) {
        short s2 = GF2Field.gf256Squ(s);
        short s3 = GF2Field.gf256Squ(s2);
        short s4 = GF2Field.gf256Squ(s3);
        short s5 = GF2Field.gf256Mul(s3, s2);
        short s6 = GF2Field.gf256Mul(s5, s4);
        short s7 = GF2Field.gf256Squ(s6);
        s7 = GF2Field.gf256Squ(s7);
        s7 = GF2Field.gf256Squ(s7);
        short s8 = GF2Field.gf256Mul(s7, s6);
        short s9 = GF2Field.gf256Squ(s8);
        return GF2Field.gf256Mul(s2, s9);
    }

    public static short addElem(short s, short s2) {
        return (short)(s ^ s2);
    }

    public static long addElem_64(long l, long l2) {
        return l ^ l2;
    }

    public static short invElem(short s) {
        return (short)(gfInvTable[s] & 0xFF);
    }

    public static long invElem_64(long l) {
        return GF2Field.gf256Inv_64(l);
    }

    public static short multElem(short s, short s2) {
        return (short)(gfMulTable[s][s2] & 0xFF);
    }

    public static long multElem_64(long l, long l2) {
        return GF2Field.gf256Mul_64(l, l2);
    }

    private static long gf4Mul2_64(long l) {
        long l2 = l & 0x5555555555555555L;
        long l3 = l & 0xAAAAAAAAAAAAAAAAL;
        return l3 ^ l2 << 1 ^ l3 >>> 1;
    }

    private static long gf4Mul_64(long l, long l2) {
        long l3 = (l << 1 & l2 ^ l2 << 1 & l) & 0xAAAAAAAAAAAAAAAAL;
        long l4 = l & l2;
        return l4 ^ l3 ^ (l4 & 0xAAAAAAAAAAAAAAAAL) >>> 1;
    }

    private static long gf4Squ_64(long l) {
        long l2 = l & 0xAAAAAAAAAAAAAAAAL;
        return l ^ l2 >>> 1;
    }

    private static long gf16Mul_64(long l, long l2) {
        long l3 = GF2Field.gf4Mul_64(l, l2);
        long l4 = l3 & 0x3333333333333333L;
        long l5 = l3 & 0xCCCCCCCCCCCCCCCCL;
        long l6 = (l << 2 ^ l) & 0xCCCCCCCCCCCCCCCCL ^ l5 >>> 2;
        long l7 = (l2 << 2 ^ l2) & 0xCCCCCCCCCCCCCCCCL ^ 0x2222222222222222L;
        long l8 = GF2Field.gf4Mul_64(l6, l7);
        return l8 ^ l4 << 2 ^ l4;
    }

    private static long gf16Squ_64(long l) {
        long l2 = GF2Field.gf4Squ_64(l);
        long l3 = GF2Field.gf4Mul2_64(l2 & 0xCCCCCCCCCCCCCCCCL);
        return l2 ^ l3 >>> 2;
    }

    private static long gf16Mul8_64(long l) {
        long l2 = l & 0x3333333333333333L;
        long l3 = l & 0xCCCCCCCCCCCCCCCCL;
        long l4 = l2 << 2 ^ l3 ^ l3 >>> 2;
        long l5 = GF2Field.gf4Mul2_64(l4);
        return l5 ^ l3 >>> 2;
    }

    private static long gf256Mul_64(long l, long l2) {
        long l3 = GF2Field.gf16Mul_64(l, l2);
        long l4 = l3 & 0xF0F0F0F0F0F0F0FL;
        long l5 = l3 & 0xF0F0F0F0F0F0F0F0L;
        long l6 = (l << 4 ^ l) & 0xF0F0F0F0F0F0F0F0L ^ l5 >>> 4;
        long l7 = (l2 << 4 ^ l2) & 0xF0F0F0F0F0F0F0F0L ^ 0x808080808080808L;
        long l8 = GF2Field.gf16Mul_64(l6, l7);
        return l8 ^ l4 << 4 ^ l4;
    }

    private static long gf256Squ_64(long l) {
        long l2 = GF2Field.gf16Squ_64(l);
        long l3 = l2 & 0xF0F0F0F0F0F0F0F0L;
        long l4 = GF2Field.gf16Mul8_64(l3);
        return l2 ^ l4 >>> 4;
    }

    private static long gf256Inv_64(long l) {
        long l2 = GF2Field.gf256Squ_64(l);
        long l3 = GF2Field.gf256Squ_64(l2);
        long l4 = GF2Field.gf256Squ_64(l3);
        long l5 = GF2Field.gf256Mul_64(l3, l2);
        long l6 = GF2Field.gf256Mul_64(l5, l4);
        long l7 = GF2Field.gf256Squ_64(l6);
        l7 = GF2Field.gf256Squ_64(l7);
        l7 = GF2Field.gf256Squ_64(l7);
        long l8 = GF2Field.gf256Mul_64(l7, l6);
        long l9 = GF2Field.gf256Squ_64(l8);
        return GF2Field.gf256Mul_64(l2, l9);
    }

    static {
        long l;
        int n;
        gfMulTable = new byte[256][256];
        gfInvTable = new byte[256];
        long l2 = 0x101010101010101L;
        for (n = 1; n <= 255; ++n) {
            l = 506097522914230528L;
            for (int i = 0; i < 256; i += 8) {
                long l3 = GF2Field.gf256Mul_64(l2, l);
                Pack.longToLittleEndian(l3, gfMulTable[n], i);
                l += 0x808080808080808L;
            }
            l2 += 0x101010101010101L;
        }
        l2 = 506097522914230528L;
        for (n = 0; n < 256; n += 8) {
            l = GF2Field.gf256Inv_64(l2);
            Pack.longToLittleEndian(l, gfInvTable, n);
            l2 += 0x808080808080808L;
        }
    }
}

