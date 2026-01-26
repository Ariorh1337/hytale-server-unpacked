/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.pqc.crypto.mlkem.Poly;

final class CBD {
    CBD() {
    }

    public static void mlkemCBD(Poly poly, byte[] byArray, int n) {
        switch (n) {
            case 3: {
                for (int i = 0; i < 64; ++i) {
                    long l = CBD.convertByteTo24BitUnsignedInt(byArray, 3 * i);
                    long l2 = l & 0x249249L;
                    l2 += l >> 1 & 0x249249L;
                    l2 += l >> 2 & 0x249249L;
                    for (int j = 0; j < 4; ++j) {
                        short s = (short)(l2 >> 6 * j + 0 & 7L);
                        short s2 = (short)(l2 >> 6 * j + 3 & 7L);
                        poly.setCoeffIndex(4 * i + j, (short)(s - s2));
                    }
                }
                break;
            }
            default: {
                for (int i = 0; i < 32; ++i) {
                    long l = CBD.convertByteTo32BitUnsignedInt(byArray, 4 * i);
                    long l3 = l & 0x55555555L;
                    l3 += l >> 1 & 0x55555555L;
                    for (int j = 0; j < 8; ++j) {
                        short s = (short)(l3 >> 4 * j + 0 & 3L);
                        short s3 = (short)(l3 >> 4 * j + n & 3L);
                        poly.setCoeffIndex(8 * i + j, (short)(s - s3));
                    }
                }
            }
        }
    }

    private static long convertByteTo32BitUnsignedInt(byte[] byArray, int n) {
        long l = byArray[n] & 0xFF;
        l |= (long)(byArray[n + 1] & 0xFF) << 8;
        l |= (long)(byArray[n + 2] & 0xFF) << 16;
        return l |= (long)(byArray[n + 3] & 0xFF) << 24;
    }

    private static long convertByteTo24BitUnsignedInt(byte[] byArray, int n) {
        long l = byArray[n] & 0xFF;
        l |= (long)(byArray[n + 1] & 0xFF) << 8;
        return l |= (long)(byArray[n + 2] & 0xFF) << 16;
    }
}

