/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public class GF16 {
    private static final byte[] F_STAR;
    private static final byte[] MT4B;
    private static final byte[] INV4B;

    static byte mt(int n, int n2) {
        return MT4B[n << 4 ^ n2];
    }

    public static byte mul(byte by, byte by2) {
        return MT4B[by << 4 | by2];
    }

    public static int mul(int n, int n2) {
        return MT4B[n << 4 | n2];
    }

    public static byte inv(byte by) {
        return INV4B[by & 0xF];
    }

    public static void decode(byte[] byArray, byte[] byArray2, int n) {
        int n2;
        int n3 = 0;
        int n4 = n >> 1;
        for (n2 = 0; n2 < n4; ++n2) {
            byArray2[n3++] = (byte)(byArray[n2] & 0xF);
            byArray2[n3++] = (byte)(byArray[n2] >>> 4 & 0xF);
        }
        if ((n & 1) == 1) {
            byArray2[n3] = (byte)(byArray[n2] & 0xF);
        }
    }

    public static void decode(byte[] byArray, int n, byte[] byArray2, int n2, int n3) {
        int n4 = n3 >> 1;
        for (int i = 0; i < n4; ++i) {
            byArray2[n2++] = (byte)(byArray[n] & 0xF);
            byArray2[n2++] = (byte)(byArray[n++] >>> 4 & 0xF);
        }
        if ((n3 & 1) == 1) {
            byArray2[n2] = (byte)(byArray[n] & 0xF);
        }
    }

    public static void encode(byte[] byArray, byte[] byArray2, int n) {
        int n2 = 0;
        int n3 = n >> 1;
        for (int i = 0; i < n3; ++i) {
            int n4 = byArray[n2++] & 0xF;
            int n5 = (byArray[n2++] & 0xF) << 4;
            byArray2[i] = (byte)(n4 | n5);
        }
        if ((n & 1) == 1) {
            byArray2[i] = (byte)(byArray[n2] & 0xF);
        }
    }

    public static void encode(byte[] byArray, byte[] byArray2, int n, int n2) {
        int n3 = 0;
        int n4 = n2 >> 1;
        for (int i = 0; i < n4; ++i) {
            int n5 = byArray[n3++] & 0xF;
            int n6 = (byArray[n3++] & 0xF) << 4;
            byArray2[n++] = (byte)(n5 | n6);
        }
        if ((n2 & 1) == 1) {
            byArray2[n] = (byte)(byArray[n3] & 0xF);
        }
    }

    public static byte innerProduct(byte[] byArray, int n, byte[] byArray2, int n2, int n3) {
        byte by = 0;
        int n4 = 0;
        while (n4 < n3) {
            by = (byte)(by ^ GF16.mul(byArray[n++], byArray2[n2]));
            ++n4;
            n2 += n3;
        }
        return by;
    }

    static {
        int n;
        int n2;
        F_STAR = new byte[]{1, 2, 4, 8, 3, 6, 12, 11, 5, 10, 7, 14, 15, 13, 9};
        MT4B = new byte[256];
        INV4B = new byte[16];
        for (n2 = 0; n2 < 15; ++n2) {
            for (n = 0; n < 15; ++n) {
                GF16.MT4B[GF16.F_STAR[n2] << 4 ^ GF16.F_STAR[n]] = F_STAR[(n2 + n) % 15];
            }
        }
        n2 = F_STAR[1];
        n = F_STAR[14];
        int n3 = 1;
        int n4 = 1;
        GF16.INV4B[1] = 1;
        for (int i = 0; i < 14; ++i) {
            n3 = GF16.mt(n3, n2);
            n4 = GF16.mt(n4, n);
            GF16.INV4B[n3] = (byte)n4;
        }
    }
}

