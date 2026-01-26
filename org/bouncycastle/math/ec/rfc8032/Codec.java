/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

abstract class Codec {
    Codec() {
    }

    static int decode16(byte[] byArray, int n) {
        int n2 = byArray[n] & 0xFF;
        return n2 |= (byArray[++n] & 0xFF) << 8;
    }

    static int decode24(byte[] byArray, int n) {
        int n2 = byArray[n] & 0xFF;
        n2 |= (byArray[++n] & 0xFF) << 8;
        return n2 |= (byArray[++n] & 0xFF) << 16;
    }

    static int decode32(byte[] byArray, int n) {
        int n2 = byArray[n] & 0xFF;
        n2 |= (byArray[++n] & 0xFF) << 8;
        n2 |= (byArray[++n] & 0xFF) << 16;
        return n2 |= byArray[++n] << 24;
    }

    static void decode32(byte[] byArray, int n, int[] nArray, int n2, int n3) {
        for (int i = 0; i < n3; ++i) {
            nArray[n2 + i] = Codec.decode32(byArray, n + i * 4);
        }
    }

    static void encode24(int n, byte[] byArray, int n2) {
        byArray[n2] = (byte)n;
        byArray[++n2] = (byte)(n >>> 8);
        byArray[++n2] = (byte)(n >>> 16);
    }

    static void encode32(int n, byte[] byArray, int n2) {
        byArray[n2] = (byte)n;
        byArray[++n2] = (byte)(n >>> 8);
        byArray[++n2] = (byte)(n >>> 16);
        byArray[++n2] = (byte)(n >>> 24);
    }

    static void encode32(int[] nArray, int n, int n2, byte[] byArray, int n3) {
        for (int i = 0; i < n2; ++i) {
            Codec.encode32(nArray[n + i], byArray, n3 + i * 4);
        }
    }

    static void encode56(long l, byte[] byArray, int n) {
        Codec.encode32((int)l, byArray, n);
        Codec.encode24((int)(l >>> 32), byArray, n + 4);
    }
}

