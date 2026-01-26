/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public class Bytes {
    public static final int BYTES = 1;
    public static final int SIZE = 8;

    public static void xor(int n, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        for (int i = 0; i < n; ++i) {
            byArray3[i] = (byte)(byArray[i] ^ byArray2[i]);
        }
    }

    public static void xor(int n, byte[] byArray, int n2, byte[] byArray2, byte[] byArray3, int n3) {
        for (int i = 0; i < n; ++i) {
            byArray3[n3++] = (byte)(byArray[n2++] ^ byArray2[i]);
        }
    }

    public static void xor(int n, byte[] byArray, int n2, byte[] byArray2, int n3, byte[] byArray3, int n4) {
        for (int i = 0; i < n; ++i) {
            byArray3[n4 + i] = (byte)(byArray[n2 + i] ^ byArray2[n3 + i]);
        }
    }

    public static void xor(int n, byte[] byArray, byte[] byArray2, byte[] byArray3, int n2) {
        for (int i = 0; i < n; ++i) {
            byArray3[n2++] = (byte)(byArray[i] ^ byArray2[i]);
        }
    }

    public static void xor(int n, byte[] byArray, byte[] byArray2, int n2, byte[] byArray3, int n3) {
        for (int i = 0; i < n; ++i) {
            byArray3[n3++] = (byte)(byArray[i] ^ byArray2[n2++]);
        }
    }

    public static void xorTo(int n, byte[] byArray, byte[] byArray2) {
        for (int i = 0; i < n; ++i) {
            int n2 = i;
            byArray2[n2] = (byte)(byArray2[n2] ^ byArray[i]);
        }
    }

    public static void xorTo(int n, byte[] byArray, int n2, byte[] byArray2) {
        int n3 = 0;
        while (n3 < n) {
            int n4 = n3++;
            byArray2[n4] = (byte)(byArray2[n4] ^ byArray[n2++]);
        }
    }

    public static void xorTo(int n, byte[] byArray, int n2, byte[] byArray2, int n3) {
        for (int i = 0; i < n; ++i) {
            int n4 = n3 + i;
            byArray2[n4] = (byte)(byArray2[n4] ^ byArray[n2 + i]);
        }
    }
}

