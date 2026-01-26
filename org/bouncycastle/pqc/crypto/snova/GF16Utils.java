/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.util.GF16;

class GF16Utils {
    private static final int GF16_MASK = 585;

    GF16Utils() {
    }

    static void encodeMergeInHalf(byte[] byArray, int n, byte[] byArray2) {
        int n2 = n + 1 >>> 1;
        int n3 = 0;
        while (n3 < n / 2) {
            byArray2[n3] = (byte)(byArray[n3] | byArray[n2] << 4);
            ++n3;
            ++n2;
        }
        if ((n & 1) == 1) {
            byArray2[n3] = byArray[n3];
        }
    }

    static void decodeMergeInHalf(byte[] byArray, byte[] byArray2, int n) {
        int n2 = n + 1 >>> 1;
        for (int i = 0; i < n2; ++i) {
            byArray2[i] = (byte)(byArray[i] & 0xF);
            byArray2[i + n2] = (byte)(byArray[i] >>> 4 & 0xF);
        }
    }

    static void gf16mTranMulMul(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7, byte[] byArray8, int n2) {
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        while (n3 < n2) {
            int n6;
            int n7;
            int n8;
            int n9;
            for (n9 = 0; n9 < n2; ++n9) {
                n8 = 0;
                n7 = 0;
                n6 = n + n9;
                int n10 = n3;
                while (n7 < n2) {
                    n8 = (byte)(n8 ^ GF16.mul(byArray[n6], byArray4[n10]));
                    ++n7;
                    n6 += n2;
                    n10 += n2;
                }
                byArray6[n9] = n8;
            }
            n9 = 0;
            n8 = 0;
            while (n9 < n2) {
                n7 = 0;
                for (n6 = 0; n6 < n2; ++n6) {
                    n7 = (byte)(n7 ^ GF16.mul(byArray2[n8 + n6], byArray6[n6]));
                }
                byArray7[n3 + n8] = n7;
                ++n9;
                n8 += n2;
            }
            for (n9 = 0; n9 < n2; ++n9) {
                byArray6[n9] = GF16.innerProduct(byArray5, n4, byArray, n + n9, n2);
            }
            for (n9 = 0; n9 < n2; ++n9) {
                byArray8[n5++] = GF16.innerProduct(byArray6, 0, byArray3, n9, n2);
            }
            ++n3;
            n4 += n2;
        }
    }

    static void gf16mMulMul(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, int n) {
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while (n2 < n) {
            int n5;
            for (n5 = 0; n5 < n; ++n5) {
                byArray4[n5] = GF16.innerProduct(byArray, n3, byArray2, n5, n);
            }
            for (n5 = 0; n5 < n; ++n5) {
                byArray5[n4++] = GF16.innerProduct(byArray4, 0, byArray3, n5, n);
            }
            ++n2;
            n3 += n;
        }
    }

    static void gf16mMul(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while (n2 < n) {
            for (int i = 0; i < n; ++i) {
                byArray3[n4++] = GF16.innerProduct(byArray, n3, byArray2, i, n);
            }
            ++n2;
            n3 += n;
        }
    }

    static void gf16mMulMulTo(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, int n) {
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while (n2 < n) {
            int n5;
            for (n5 = 0; n5 < n; ++n5) {
                byArray4[n5] = GF16.innerProduct(byArray, n3, byArray2, n5, n);
            }
            for (n5 = 0; n5 < n; ++n5) {
                int n6 = n4++;
                byArray5[n6] = (byte)(byArray5[n6] ^ GF16.innerProduct(byArray4, 0, byArray3, n5, n));
            }
            ++n2;
            n3 += n;
        }
    }

    static void gf16mMulTo(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while (n2 < n) {
            for (int i = 0; i < n; ++i) {
                int n5 = n4++;
                byArray3[n5] = (byte)(byArray3[n5] ^ GF16.innerProduct(byArray, n3, byArray2, i, n));
            }
            ++n2;
            n3 += n;
        }
    }

    static void gf16mMulToTo(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, int n) {
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while (n2 < n) {
            for (int i = 0; i < n; ++i) {
                int n5 = n4;
                byArray4[n5] = (byte)(byArray4[n5] ^ GF16.innerProduct(byArray, n3, byArray2, i, n));
                int n6 = n4++;
                byArray5[n6] = (byte)(byArray5[n6] ^ GF16.innerProduct(byArray2, n3, byArray3, i, n));
            }
            ++n2;
            n3 += n;
        }
    }

    static void gf16mMulTo(byte[] byArray, byte[] byArray2, byte[] byArray3, int n, int n2) {
        int n3 = 0;
        int n4 = 0;
        while (n3 < n2) {
            for (int i = 0; i < n2; ++i) {
                int n5 = n++;
                byArray3[n5] = (byte)(byArray3[n5] ^ GF16.innerProduct(byArray, n4, byArray2, i, n2));
            }
            ++n3;
            n4 += n2;
        }
    }

    static void gf16mMulTo(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, int n, int n2) {
        int n3 = 0;
        int n4 = 0;
        while (n3 < n2) {
            for (int i = 0; i < n2; ++i) {
                int n5 = n++;
                byArray5[n5] = (byte)(byArray5[n5] ^ (GF16.innerProduct(byArray, n4, byArray2, i, n2) ^ GF16.innerProduct(byArray3, n4, byArray4, i, n2)));
            }
            ++n3;
            n4 += n2;
        }
    }

    static void gf16mMulTo(byte[] byArray, byte[] byArray2, int n, byte[] byArray3, int n2, int n3) {
        int n4 = 0;
        int n5 = 0;
        while (n4 < n3) {
            for (int i = 0; i < n3; ++i) {
                int n6 = n2++;
                byArray3[n6] = (byte)(byArray3[n6] ^ GF16.innerProduct(byArray, n5, byArray2, n + i, n3));
            }
            ++n4;
            n5 += n3;
        }
    }

    static int gf16FromNibble(int n) {
        int n2 = n | n << 4;
        return n2 & 0x41 | n2 << 2 & 0x208;
    }

    static int ctGF16IsNotZero(byte by) {
        int n = by & 0xFF;
        return (n | n >>> 1 | n >>> 2 | n >>> 3) & 1;
    }

    private static int gf16Reduce(int n) {
        int n2 = n & 0x49249249;
        int n3 = n >>> 12;
        n2 ^= n3 ^ n3 << 3;
        n3 = n2 >>> 12;
        n2 ^= n3 ^ n3 << 3;
        n3 = n2 >>> 12;
        return (n2 ^= n3 ^ n3 << 3) & 0x249;
    }

    static byte gf16ToNibble(int n) {
        int n2 = GF16Utils.gf16Reduce(n);
        n2 |= n2 >>> 4;
        return (byte)(n2 & 5 | n2 >>> 2 & 0xA);
    }
}

