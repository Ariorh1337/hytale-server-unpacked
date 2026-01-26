/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

class FalconCodec {
    static final byte[] max_fg_bits = new byte[]{0, 8, 8, 8, 8, 8, 7, 7, 6, 6, 5};
    static final byte[] max_FG_bits = new byte[]{0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};

    FalconCodec() {
    }

    static int modq_encode(byte[] byArray, int n, short[] sArray, int n2) {
        int n3;
        int n4 = 1 << n2;
        for (n3 = 0; n3 < n4; ++n3) {
            if ((sArray[n3] & 0xFFFF) < 12289) continue;
            return 0;
        }
        int n5 = n4 * 14 + 7 >> 3;
        if (byArray == null) {
            return n5;
        }
        if (n5 > n) {
            return 0;
        }
        int n6 = 1;
        int n7 = 0;
        int n8 = 0;
        for (n3 = 0; n3 < n4; ++n3) {
            n7 = n7 << 14 | sArray[n3] & 0xFFFF;
            n8 += 14;
            while (n8 >= 8) {
                byArray[n6++] = (byte)(n7 >> (n8 -= 8));
            }
        }
        if (n8 > 0) {
            byArray[n6] = (byte)(n7 << 8 - n8);
        }
        return n5;
    }

    static int modq_decode(short[] sArray, int n, byte[] byArray, int n2) {
        int n3 = 1 << n;
        int n4 = n3 * 14 + 7 >> 3;
        if (n4 > n2) {
            return 0;
        }
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        while (n8 < n3) {
            n6 = n6 << 8 | byArray[n5++] & 0xFF;
            if ((n7 += 8) < 14) continue;
            int n9 = n6 >>> (n7 -= 14) & 0x3FFF;
            if (n9 >= 12289) {
                return 0;
            }
            sArray[n8] = (short)n9;
            ++n8;
        }
        if ((n6 & (1 << n7) - 1) != 0) {
            return 0;
        }
        return n4;
    }

    static int trim_i8_encode(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) {
        int n5;
        int n6 = 1 << n3;
        int n7 = (1 << n4 - 1) - 1;
        int n8 = -n7;
        for (n5 = 0; n5 < n6; ++n5) {
            if (byArray2[n5] >= n8 && byArray2[n5] <= n7) continue;
            return 0;
        }
        int n9 = n6 * n4 + 7 >> 3;
        if (byArray == null) {
            return n9;
        }
        if (n9 > n2) {
            return 0;
        }
        int n10 = n;
        int n11 = 0;
        int n12 = 0;
        int n13 = (1 << n4) - 1;
        for (n5 = 0; n5 < n6; ++n5) {
            n11 = n11 << n4 | byArray2[n5] & 0xFFFF & n13;
            n12 += n4;
            while (n12 >= 8) {
                byArray[n10++] = (byte)(n11 >>> (n12 -= 8));
            }
        }
        if (n12 > 0) {
            byArray[n10] = (byte)(n11 << 8 - n12);
        }
        return n9;
    }

    static int trim_i8_decode(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) {
        int n5 = 1 << n;
        int n6 = n5 * n2 + 7 >> 3;
        if (n6 > n4) {
            return 0;
        }
        int n7 = n3;
        int n8 = 0;
        int n9 = 0;
        int n10 = 0;
        int n11 = (1 << n2) - 1;
        int n12 = 1 << n2 - 1;
        while (n8 < n5) {
            n9 = n9 << 8 | byArray2[n7++] & 0xFF;
            n10 += 8;
            while (n10 >= n2 && n8 < n5) {
                int n13 = n9 >>> (n10 -= n2) & n11;
                if ((n13 |= -(n13 & n12)) == -n12) {
                    return 0;
                }
                byArray[n8] = (byte)n13;
                ++n8;
            }
        }
        if ((n9 & (1 << n10) - 1) != 0) {
            return 0;
        }
        return n6;
    }

    static int comp_encode(byte[] byArray, int n, short[] sArray, int n2) {
        int n3;
        int n4 = 1 << n2;
        int n5 = 0;
        for (n3 = 0; n3 < n4; ++n3) {
            if (sArray[n3] >= -2047 && sArray[n3] <= 2047) continue;
            return 0;
        }
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        for (n3 = 0; n3 < n4; ++n3) {
            n6 <<= 1;
            int n9 = sArray[n3];
            if (n9 < 0) {
                n9 = -n9;
                n6 |= 1;
            }
            int n10 = n9;
            n6 <<= 7;
            n6 |= n10 & 0x7F;
            n7 += 8;
            n6 <<= (n10 >>>= 7) + 1;
            n6 |= 1;
            n7 += n10 + 1;
            while (n7 >= 8) {
                n7 -= 8;
                if (byArray != null) {
                    if (n8 >= n) {
                        return 0;
                    }
                    byArray[n5 + n8] = (byte)(n6 >>> n7);
                }
                ++n8;
            }
        }
        if (n7 > 0) {
            if (byArray != null) {
                if (n8 >= n) {
                    return 0;
                }
                byArray[n5 + n8] = (byte)(n6 << 8 - n7);
            }
            ++n8;
        }
        return n8;
    }

    static int comp_decode(short[] sArray, int n, byte[] byArray, int n2) {
        int n3 = 1 << n;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        for (int i = 0; i < n3; ++i) {
            int n8;
            int n9;
            block7: {
                if (n7 >= n2) {
                    return 0;
                }
                n5 = n5 << 8 | byArray[n4 + n7] & 0xFF;
                ++n7;
                int n10 = n5 >>> n6;
                n9 = n10 & 0x80;
                n8 = n10 & 0x7F;
                do {
                    if (n6 == 0) {
                        if (n7 >= n2) {
                            return 0;
                        }
                        n5 = n5 << 8 | byArray[n4 + n7] & 0xFF;
                        ++n7;
                        n6 = 8;
                    }
                    if ((n5 >>> --n6 & 1) != 0) break block7;
                } while ((n8 += 128) <= 2047);
                return 0;
            }
            if (n9 != 0 && n8 == 0) {
                return 0;
            }
            sArray[i] = (short)(n9 != 0 ? -n8 : n8);
        }
        if ((n5 & (1 << n6) - 1) != 0) {
            return 0;
        }
        return n7;
    }
}

