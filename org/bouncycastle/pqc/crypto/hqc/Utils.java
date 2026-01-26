/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Pack;

class Utils {
    Utils() {
    }

    static void fromLongArrayToByteArray(byte[] byArray, long[] lArray) {
        int n;
        int n2 = byArray.length / 8;
        for (n = 0; n != n2; ++n) {
            Pack.longToLittleEndian(lArray[n], byArray, n * 8);
        }
        if (byArray.length % 8 != 0) {
            n = n2 * 8;
            int n3 = 0;
            while (n < byArray.length) {
                byArray[n++] = (byte)(lArray[n2] >>> n3++ * 8);
            }
        }
    }

    static void fromLongArrayToByteArray(byte[] byArray, int n, int n2, long[] lArray) {
        int n3;
        int n4 = n2 >> 3;
        for (n3 = 0; n3 != n4; ++n3) {
            Pack.longToLittleEndian(lArray[n3], byArray, n);
            n += 8;
        }
        if ((n2 & 7) != 0) {
            n3 = 0;
            while (n < byArray.length) {
                byArray[n++] = (byte)(lArray[n4] >>> n3++ * 8);
            }
        }
    }

    static long bitMask(long l, long l2) {
        return (1L << (int)(l % l2)) - 1L;
    }

    static void fromByteArrayToLongArray(long[] lArray, byte[] byArray, int n, int n2) {
        byte[] byArray2 = byArray;
        if (n2 % 8 != 0) {
            byArray2 = new byte[(n2 + 7) / 8 * 8];
            System.arraycopy(byArray, n, byArray2, 0, n2);
            n = 0;
        }
        int n3 = Math.min(lArray.length, n2 + 7 >>> 3);
        for (int i = 0; i < n3; ++i) {
            lArray[i] = Pack.littleEndianToLong(byArray2, n);
            n += 8;
        }
    }

    static void fromByte32ArrayToLongArray(long[] lArray, int[] nArray) {
        for (int i = 0; i != nArray.length; i += 2) {
            lArray[i / 2] = (long)nArray[i] & 0xFFFFFFFFL;
            int n = i / 2;
            lArray[n] = lArray[n] | (long)nArray[i + 1] << 32;
        }
    }

    static void fromLongArrayToByte32Array(int[] nArray, long[] lArray) {
        for (int i = 0; i != lArray.length; ++i) {
            nArray[2 * i] = (int)lArray[i];
            nArray[2 * i + 1] = (int)(lArray[i] >> 32);
        }
    }

    static void copyBytes(int[] nArray, int n, int[] nArray2, int n2, int n3) {
        System.arraycopy(nArray, n, nArray2, n2, n3 / 2);
    }

    static int getByteSizeFromBitSize(int n) {
        return (n + 7) / 8;
    }

    static int getByte64SizeFromBitSize(int n) {
        return (n + 63) / 64;
    }

    static int toUnsigned8bits(int n) {
        return n & 0xFF;
    }

    static int toUnsigned16Bits(int n) {
        return n & 0xFFFF;
    }
}

