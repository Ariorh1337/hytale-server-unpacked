/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.util.Pack;

class Utils {
    Utils() {
    }

    static void store_gf(byte[] byArray, int n, short s) {
        byArray[n + 0] = (byte)(s & 0xFF);
        byArray[n + 1] = (byte)(s >> 8);
    }

    static short load_gf(byte[] byArray, int n, int n2) {
        return (short)(Pack.littleEndianToShort(byArray, n) & n2);
    }

    static int load4(byte[] byArray, int n) {
        return Pack.littleEndianToInt(byArray, n);
    }

    static void store8(byte[] byArray, int n, long l) {
        byArray[n + 0] = (byte)(l >> 0 & 0xFFL);
        byArray[n + 1] = (byte)(l >> 8 & 0xFFL);
        byArray[n + 2] = (byte)(l >> 16 & 0xFFL);
        byArray[n + 3] = (byte)(l >> 24 & 0xFFL);
        byArray[n + 4] = (byte)(l >> 32 & 0xFFL);
        byArray[n + 5] = (byte)(l >> 40 & 0xFFL);
        byArray[n + 6] = (byte)(l >> 48 & 0xFFL);
        byArray[n + 7] = (byte)(l >> 56 & 0xFFL);
    }

    static long load8(byte[] byArray, int n) {
        return Pack.littleEndianToLong(byArray, n);
    }

    static short bitrev(short s, int n) {
        s = (short)((s & 0xFF) << 8 | (s & 0xFF00) >> 8);
        s = (short)((s & 0xF0F) << 4 | (s & 0xF0F0) >> 4);
        s = (short)((s & 0x3333) << 2 | (s & 0xCCCC) >> 2);
        s = (short)((s & 0x5555) << 1 | (s & 0xAAAA) >> 1);
        if (n == 12) {
            return (short)(s >> 4);
        }
        return (short)(s >> 3);
    }
}

