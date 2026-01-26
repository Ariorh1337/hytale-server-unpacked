/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.util.Integers;

abstract class ScalarUtil {
    private static final long M = 0xFFFFFFFFL;

    ScalarUtil() {
    }

    static void addShifted_NP(int n, int n2, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4) {
        long l = 0L;
        long l2 = 0L;
        if (n2 == 0) {
            for (int i = 0; i <= n; ++i) {
                int n3 = nArray3[i];
                l2 += (long)nArray[i] & 0xFFFFFFFFL;
                l2 += (long)n3 & 0xFFFFFFFFL;
                l += (long)n3 & 0xFFFFFFFFL;
                n3 = (int)(l += (long)nArray2[i] & 0xFFFFFFFFL);
                l >>>= 32;
                nArray3[i] = n3;
                nArray[i] = (int)(l2 += (long)n3 & 0xFFFFFFFFL);
                l2 >>>= 32;
            }
        } else if (n2 < 32) {
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            for (int i = 0; i <= n; ++i) {
                int n7 = nArray3[i];
                int n8 = n7 << n2 | n4 >>> -n2;
                n4 = n7;
                l2 += (long)nArray[i] & 0xFFFFFFFFL;
                l2 += (long)n8 & 0xFFFFFFFFL;
                int n9 = nArray2[i];
                int n10 = n9 << n2 | n6 >>> -n2;
                n6 = n9;
                l += (long)n7 & 0xFFFFFFFFL;
                n7 = (int)(l += (long)n10 & 0xFFFFFFFFL);
                l >>>= 32;
                nArray3[i] = n7;
                int n11 = n7 << n2 | n5 >>> -n2;
                n5 = n7;
                nArray[i] = (int)(l2 += (long)n11 & 0xFFFFFFFFL);
                l2 >>>= 32;
            }
        } else {
            System.arraycopy(nArray3, 0, nArray4, 0, n);
            int n12 = n2 >>> 5;
            int n13 = n2 & 0x1F;
            if (n13 == 0) {
                for (int i = n12; i <= n; ++i) {
                    l2 += (long)nArray[i] & 0xFFFFFFFFL;
                    l2 += (long)nArray4[i - n12] & 0xFFFFFFFFL;
                    l += (long)nArray3[i] & 0xFFFFFFFFL;
                    nArray3[i] = (int)(l += (long)nArray2[i - n12] & 0xFFFFFFFFL);
                    l >>>= 32;
                    nArray[i] = (int)(l2 += (long)nArray3[i - n12] & 0xFFFFFFFFL);
                    l2 >>>= 32;
                }
            } else {
                int n14 = 0;
                int n15 = 0;
                int n16 = 0;
                for (int i = n12; i <= n; ++i) {
                    int n17 = nArray4[i - n12];
                    int n18 = n17 << n13 | n14 >>> -n13;
                    n14 = n17;
                    l2 += (long)nArray[i] & 0xFFFFFFFFL;
                    l2 += (long)n18 & 0xFFFFFFFFL;
                    int n19 = nArray2[i - n12];
                    int n20 = n19 << n13 | n16 >>> -n13;
                    n16 = n19;
                    l += (long)nArray3[i] & 0xFFFFFFFFL;
                    nArray3[i] = (int)(l += (long)n20 & 0xFFFFFFFFL);
                    l >>>= 32;
                    int n21 = nArray3[i - n12];
                    int n22 = n21 << n13 | n15 >>> -n13;
                    n15 = n21;
                    nArray[i] = (int)(l2 += (long)n22 & 0xFFFFFFFFL);
                    l2 >>>= 32;
                }
            }
        }
    }

    static void addShifted_UV(int n, int n2, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4) {
        int n3 = n2 >>> 5;
        int n4 = n2 & 0x1F;
        long l = 0L;
        long l2 = 0L;
        if (n4 == 0) {
            for (int i = n3; i <= n; ++i) {
                l += (long)nArray[i] & 0xFFFFFFFFL;
                l2 += (long)nArray2[i] & 0xFFFFFFFFL;
                nArray[i] = (int)(l += (long)nArray3[i - n3] & 0xFFFFFFFFL);
                l >>>= 32;
                nArray2[i] = (int)(l2 += (long)nArray4[i - n3] & 0xFFFFFFFFL);
                l2 >>>= 32;
            }
        } else {
            int n5 = 0;
            int n6 = 0;
            for (int i = n3; i <= n; ++i) {
                int n7 = nArray3[i - n3];
                int n8 = nArray4[i - n3];
                int n9 = n7 << n4 | n5 >>> -n4;
                int n10 = n8 << n4 | n6 >>> -n4;
                n5 = n7;
                n6 = n8;
                l += (long)nArray[i] & 0xFFFFFFFFL;
                l2 += (long)nArray2[i] & 0xFFFFFFFFL;
                nArray[i] = (int)(l += (long)n9 & 0xFFFFFFFFL);
                l >>>= 32;
                nArray2[i] = (int)(l2 += (long)n10 & 0xFFFFFFFFL);
                l2 >>>= 32;
            }
        }
    }

    static int getBitLength(int n, int[] nArray) {
        int n2;
        int n3 = nArray[n2] >> 31;
        for (n2 = n; n2 > 0 && nArray[n2] == n3; --n2) {
        }
        return n2 * 32 + 32 - Integers.numberOfLeadingZeros(nArray[n2] ^ n3);
    }

    static int getBitLengthPositive(int n, int[] nArray) {
        int n2;
        for (n2 = n; n2 > 0 && nArray[n2] == 0; --n2) {
        }
        return n2 * 32 + 32 - Integers.numberOfLeadingZeros(nArray[n2]);
    }

    static boolean lessThan(int n, int[] nArray, int[] nArray2) {
        int n2 = n;
        do {
            int n3;
            int n4;
            if ((n4 = nArray[n2] + Integer.MIN_VALUE) < (n3 = nArray2[n2] + Integer.MIN_VALUE)) {
                return true;
            }
            if (n4 <= n3) continue;
            return false;
        } while (--n2 >= 0);
        return false;
    }

    static void subShifted_NP(int n, int n2, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4) {
        long l = 0L;
        long l2 = 0L;
        if (n2 == 0) {
            for (int i = 0; i <= n; ++i) {
                int n3 = nArray3[i];
                l2 += (long)nArray[i] & 0xFFFFFFFFL;
                l2 -= (long)n3 & 0xFFFFFFFFL;
                l += (long)n3 & 0xFFFFFFFFL;
                n3 = (int)(l -= (long)nArray2[i] & 0xFFFFFFFFL);
                l >>= 32;
                nArray3[i] = n3;
                nArray[i] = (int)(l2 -= (long)n3 & 0xFFFFFFFFL);
                l2 >>= 32;
            }
        } else if (n2 < 32) {
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            for (int i = 0; i <= n; ++i) {
                int n7 = nArray3[i];
                int n8 = n7 << n2 | n4 >>> -n2;
                n4 = n7;
                l2 += (long)nArray[i] & 0xFFFFFFFFL;
                l2 -= (long)n8 & 0xFFFFFFFFL;
                int n9 = nArray2[i];
                int n10 = n9 << n2 | n6 >>> -n2;
                n6 = n9;
                l += (long)n7 & 0xFFFFFFFFL;
                n7 = (int)(l -= (long)n10 & 0xFFFFFFFFL);
                l >>= 32;
                nArray3[i] = n7;
                int n11 = n7 << n2 | n5 >>> -n2;
                n5 = n7;
                nArray[i] = (int)(l2 -= (long)n11 & 0xFFFFFFFFL);
                l2 >>= 32;
            }
        } else {
            System.arraycopy(nArray3, 0, nArray4, 0, n);
            int n12 = n2 >>> 5;
            int n13 = n2 & 0x1F;
            if (n13 == 0) {
                for (int i = n12; i <= n; ++i) {
                    l2 += (long)nArray[i] & 0xFFFFFFFFL;
                    l2 -= (long)nArray4[i - n12] & 0xFFFFFFFFL;
                    l += (long)nArray3[i] & 0xFFFFFFFFL;
                    nArray3[i] = (int)(l -= (long)nArray2[i - n12] & 0xFFFFFFFFL);
                    l >>= 32;
                    nArray[i] = (int)(l2 -= (long)nArray3[i - n12] & 0xFFFFFFFFL);
                    l2 >>= 32;
                }
            } else {
                int n14 = 0;
                int n15 = 0;
                int n16 = 0;
                for (int i = n12; i <= n; ++i) {
                    int n17 = nArray4[i - n12];
                    int n18 = n17 << n13 | n14 >>> -n13;
                    n14 = n17;
                    l2 += (long)nArray[i] & 0xFFFFFFFFL;
                    l2 -= (long)n18 & 0xFFFFFFFFL;
                    int n19 = nArray2[i - n12];
                    int n20 = n19 << n13 | n16 >>> -n13;
                    n16 = n19;
                    l += (long)nArray3[i] & 0xFFFFFFFFL;
                    nArray3[i] = (int)(l -= (long)n20 & 0xFFFFFFFFL);
                    l >>= 32;
                    int n21 = nArray3[i - n12];
                    int n22 = n21 << n13 | n15 >>> -n13;
                    n15 = n21;
                    nArray[i] = (int)(l2 -= (long)n22 & 0xFFFFFFFFL);
                    l2 >>= 32;
                }
            }
        }
    }

    static void subShifted_UV(int n, int n2, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4) {
        int n3 = n2 >>> 5;
        int n4 = n2 & 0x1F;
        long l = 0L;
        long l2 = 0L;
        if (n4 == 0) {
            for (int i = n3; i <= n; ++i) {
                l += (long)nArray[i] & 0xFFFFFFFFL;
                l2 += (long)nArray2[i] & 0xFFFFFFFFL;
                nArray[i] = (int)(l -= (long)nArray3[i - n3] & 0xFFFFFFFFL);
                l >>= 32;
                nArray2[i] = (int)(l2 -= (long)nArray4[i - n3] & 0xFFFFFFFFL);
                l2 >>= 32;
            }
        } else {
            int n5 = 0;
            int n6 = 0;
            for (int i = n3; i <= n; ++i) {
                int n7 = nArray3[i - n3];
                int n8 = nArray4[i - n3];
                int n9 = n7 << n4 | n5 >>> -n4;
                int n10 = n8 << n4 | n6 >>> -n4;
                n5 = n7;
                n6 = n8;
                l += (long)nArray[i] & 0xFFFFFFFFL;
                l2 += (long)nArray2[i] & 0xFFFFFFFFL;
                nArray[i] = (int)(l -= (long)n9 & 0xFFFFFFFFL);
                l >>= 32;
                nArray2[i] = (int)(l2 -= (long)n10 & 0xFFFFFFFFL);
                l2 >>= 32;
            }
        }
    }
}

