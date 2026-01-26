/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

class Reduce {
    Reduce() {
    }

    public static short montgomeryReduce(int n) {
        short s = (short)(n * 62209);
        int n2 = s * 3329;
        n2 = n - n2;
        return (short)(n2 >>= 16);
    }

    public static short barretReduce(short s) {
        long l = 0x4000000L;
        short s2 = (short)((l + 1664L) / 3329L);
        short s3 = (short)(s2 * s >> 26);
        s3 = (short)(s3 * 3329);
        return (short)(s - s3);
    }

    public static short conditionalSubQ(short s) {
        s = (short)(s - 3329);
        s = (short)(s + (s >> 15 & 0xD01));
        return s;
    }

    static int checkModulus(short s) {
        return s - 3329;
    }
}

