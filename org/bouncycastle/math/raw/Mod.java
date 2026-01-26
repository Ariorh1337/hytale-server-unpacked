/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

import java.util.Random;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Integers;

public abstract class Mod {
    private static final int M30 = 0x3FFFFFFF;
    private static final long M32L = 0xFFFFFFFFL;

    public static void checkedModOddInverse(int[] nArray, int[] nArray2, int[] nArray3) {
        if (0 == Mod.modOddInverse(nArray, nArray2, nArray3)) {
            throw new ArithmeticException("Inverse does not exist.");
        }
    }

    public static void checkedModOddInverseVar(int[] nArray, int[] nArray2, int[] nArray3) {
        if (!Mod.modOddInverseVar(nArray, nArray2, nArray3)) {
            throw new ArithmeticException("Inverse does not exist.");
        }
    }

    public static int inverse32(int n) {
        int n2 = n;
        n2 *= 2 - n * n2;
        n2 *= 2 - n * n2;
        n2 *= 2 - n * n2;
        n2 *= 2 - n * n2;
        return n2;
    }

    public static int modOddInverse(int[] nArray, int[] nArray2, int[] nArray3) {
        int n;
        int n2 = nArray.length;
        int n3 = (n2 << 5) - Integers.numberOfLeadingZeros(nArray[n2 - 1]);
        int n4 = (n3 + 29) / 30;
        int[] nArray4 = new int[4];
        int[] nArray5 = new int[n4];
        int[] nArray6 = new int[n4];
        int[] nArray7 = new int[n4];
        int[] nArray8 = new int[n4];
        int[] nArray9 = new int[n4];
        nArray6[0] = 1;
        Mod.encode30(n3, nArray2, nArray8);
        Mod.encode30(n3, nArray, nArray9);
        System.arraycopy(nArray9, 0, nArray7, 0, n4);
        int n5 = 0;
        int n6 = Mod.inverse32(nArray9[0]);
        int n7 = Mod.getMaximumHDDivsteps(n3);
        for (n = 0; n < n7; n += 30) {
            n5 = Mod.hddivsteps30(n5, nArray7[0], nArray8[0], nArray4);
            Mod.updateDE30(n4, nArray5, nArray6, nArray4, n6, nArray9);
            Mod.updateFG30(n4, nArray7, nArray8, nArray4);
        }
        n = nArray7[n4 - 1] >> 31;
        Mod.cnegate30(n4, n, nArray7);
        Mod.cnormalize30(n4, n, nArray5, nArray9);
        Mod.decode30(n3, nArray5, nArray3);
        return Mod.equalTo(n4, nArray7, 1) & Mod.equalTo(n4, nArray8, 0);
    }

    public static boolean modOddInverseVar(int[] nArray, int[] nArray2, int[] nArray3) {
        int n = nArray.length;
        int n2 = (n << 5) - Integers.numberOfLeadingZeros(nArray[n - 1]);
        int n3 = (n2 + 29) / 30;
        int n4 = n2 - Nat.getBitLength(n, nArray2);
        int[] nArray4 = new int[4];
        int[] nArray5 = new int[n3];
        int[] nArray6 = new int[n3];
        int[] nArray7 = new int[n3];
        int[] nArray8 = new int[n3];
        int[] nArray9 = new int[n3];
        nArray6[0] = 1;
        Mod.encode30(n2, nArray2, nArray8);
        Mod.encode30(n2, nArray, nArray9);
        System.arraycopy(nArray9, 0, nArray7, 0, n3);
        int n5 = -n4;
        int n6 = n3;
        int n7 = n3;
        int n8 = Mod.inverse32(nArray9[0]);
        int n9 = Mod.getMaximumDivsteps(n2);
        int n10 = n4;
        while (!Mod.equalToVar(n7, nArray8, 0)) {
            if (n10 >= n9) {
                return false;
            }
            n10 += 30;
            n5 = Mod.divsteps30Var(n5, nArray7[0], nArray8[0], nArray4);
            Mod.updateDE30(n6, nArray5, nArray6, nArray4, n8, nArray9);
            Mod.updateFG30(n7, nArray7, nArray8, nArray4);
            n7 = Mod.trimFG30(n7, nArray7, nArray8);
        }
        int n11 = nArray7[n7 - 1] >> 31;
        int n12 = nArray5[n6 - 1] >> 31;
        if (n12 < 0) {
            n12 = Mod.add30(n6, nArray5, nArray9);
        }
        if (n11 < 0) {
            n12 = Mod.negate30(n6, nArray5);
            n11 = Mod.negate30(n7, nArray7);
        }
        if (!Mod.equalToVar(n7, nArray7, 1)) {
            return false;
        }
        if (n12 < 0) {
            n12 = Mod.add30(n6, nArray5, nArray9);
        }
        Mod.decode30(n2, nArray5, nArray3);
        return true;
    }

    public static int modOddIsCoprime(int[] nArray, int[] nArray2) {
        int n;
        int n2 = nArray.length;
        int n3 = (n2 << 5) - Integers.numberOfLeadingZeros(nArray[n2 - 1]);
        int n4 = (n3 + 29) / 30;
        int[] nArray3 = new int[4];
        int[] nArray4 = new int[n4];
        int[] nArray5 = new int[n4];
        int[] nArray6 = new int[n4];
        Mod.encode30(n3, nArray2, nArray5);
        Mod.encode30(n3, nArray, nArray6);
        System.arraycopy(nArray6, 0, nArray4, 0, n4);
        int n5 = 0;
        int n6 = Mod.getMaximumHDDivsteps(n3);
        for (n = 0; n < n6; n += 30) {
            n5 = Mod.hddivsteps30(n5, nArray4[0], nArray5[0], nArray3);
            Mod.updateFG30(n4, nArray4, nArray5, nArray3);
        }
        n = nArray4[n4 - 1] >> 31;
        Mod.cnegate30(n4, n, nArray4);
        return Mod.equalTo(n4, nArray4, 1) & Mod.equalTo(n4, nArray5, 0);
    }

    public static boolean modOddIsCoprimeVar(int[] nArray, int[] nArray2) {
        int n = nArray.length;
        int n2 = (n << 5) - Integers.numberOfLeadingZeros(nArray[n - 1]);
        int n3 = (n2 + 29) / 30;
        int n4 = n2 - Nat.getBitLength(n, nArray2);
        int[] nArray3 = new int[4];
        int[] nArray4 = new int[n3];
        int[] nArray5 = new int[n3];
        int[] nArray6 = new int[n3];
        Mod.encode30(n2, nArray2, nArray5);
        Mod.encode30(n2, nArray, nArray6);
        System.arraycopy(nArray6, 0, nArray4, 0, n3);
        int n5 = -n4;
        int n6 = n3;
        int n7 = Mod.getMaximumDivsteps(n2);
        int n8 = n4;
        while (!Mod.equalToVar(n6, nArray5, 0)) {
            if (n8 >= n7) {
                return false;
            }
            n8 += 30;
            n5 = Mod.divsteps30Var(n5, nArray4[0], nArray5[0], nArray3);
            Mod.updateFG30(n6, nArray4, nArray5, nArray3);
            n6 = Mod.trimFG30(n6, nArray4, nArray5);
        }
        int n9 = nArray4[n6 - 1] >> 31;
        if (n9 < 0) {
            n9 = Mod.negate30(n6, nArray4);
        }
        return Mod.equalToVar(n6, nArray4, 1);
    }

    public static int[] random(int[] nArray) {
        int n = nArray.length;
        Random random = new Random();
        int[] nArray2 = Nat.create(n);
        int n2 = nArray[n - 1];
        n2 |= n2 >>> 1;
        n2 |= n2 >>> 2;
        n2 |= n2 >>> 4;
        n2 |= n2 >>> 8;
        n2 |= n2 >>> 16;
        do {
            for (int i = 0; i != n; ++i) {
                nArray2[i] = random.nextInt();
            }
            int n3 = n - 1;
            nArray2[n3] = nArray2[n3] & n2;
        } while (Nat.gte(n, nArray2, nArray));
        return nArray2;
    }

    private static int add30(int n, int[] nArray, int[] nArray2) {
        int n2 = 0;
        int n3 = n - 1;
        for (int i = 0; i < n3; ++i) {
            nArray[i] = (n2 += nArray[i] + nArray2[i]) & 0x3FFFFFFF;
            n2 >>= 30;
        }
        nArray[n3] = n2 += nArray[n3] + nArray2[n3];
        return n2 >>= 30;
    }

    private static void cnegate30(int n, int n2, int[] nArray) {
        int n3 = 0;
        int n4 = n - 1;
        for (int i = 0; i < n4; ++i) {
            nArray[i] = (n3 += (nArray[i] ^ n2) - n2) & 0x3FFFFFFF;
            n3 >>= 30;
        }
        nArray[n4] = n3 += (nArray[n4] ^ n2) - n2;
    }

    private static void cnormalize30(int n, int n2, int[] nArray, int[] nArray2) {
        int n3;
        int n4;
        int n5 = n - 1;
        int n6 = 0;
        int n7 = nArray[n5] >> 31;
        for (n4 = 0; n4 < n5; ++n4) {
            n3 = nArray[n4] + (nArray2[n4] & n7);
            n3 = (n3 ^ n2) - n2;
            nArray[n4] = (n6 += n3) & 0x3FFFFFFF;
            n6 >>= 30;
        }
        n4 = nArray[n5] + (nArray2[n5] & n7);
        n4 = (n4 ^ n2) - n2;
        nArray[n5] = n6 += n4;
        n6 = 0;
        n7 = nArray[n5] >> 31;
        for (n4 = 0; n4 < n5; ++n4) {
            n3 = nArray[n4] + (nArray2[n4] & n7);
            nArray[n4] = (n6 += n3) & 0x3FFFFFFF;
            n6 >>= 30;
        }
        n4 = nArray[n5] + (nArray2[n5] & n7);
        nArray[n5] = n6 += n4;
    }

    private static void decode30(int n, int[] nArray, int[] nArray2) {
        int n2 = 0;
        long l = 0L;
        int n3 = 0;
        int n4 = 0;
        while (n > 0) {
            while (n2 < Math.min(32, n)) {
                l |= (long)nArray[n3++] << n2;
                n2 += 30;
            }
            nArray2[n4++] = (int)l;
            l >>>= 32;
            n2 -= 32;
            n -= 32;
        }
    }

    private static int divsteps30Var(int n, int n2, int n3, int[] nArray) {
        int n4 = 1;
        int n5 = 0;
        int n6 = 0;
        int n7 = 1;
        int n8 = n2;
        int n9 = n3;
        int n10 = 30;
        while (true) {
            int n11;
            int n12;
            int n13;
            int n14 = Integers.numberOfTrailingZeros(n9 | -1 << n10);
            n9 >>= n14;
            n4 <<= n14;
            n5 <<= n14;
            n -= n14;
            if ((n10 -= n14) <= 0) break;
            if (n <= 0) {
                n = 2 - n;
                int n15 = n8;
                n8 = n9;
                n9 = -n15;
                int n16 = n4;
                n4 = n6;
                n6 = -n16;
                int n17 = n5;
                n5 = n7;
                n7 = -n17;
                n13 = n > n10 ? n10 : n;
                n12 = -1 >>> 32 - n13 & 0x3F;
                n11 = n8 * n9 * (n8 * n8 - 2) & n12;
            } else {
                n13 = n > n10 ? n10 : n;
                n12 = -1 >>> 32 - n13 & 0xF;
                n11 = n8 + ((n8 + 1 & 4) << 1);
                n11 = n11 * -n9 & n12;
            }
            n9 += n8 * n11;
            n6 += n4 * n11;
            n7 += n5 * n11;
        }
        nArray[0] = n4;
        nArray[1] = n5;
        nArray[2] = n6;
        nArray[3] = n7;
        return n;
    }

    private static void encode30(int n, int[] nArray, int[] nArray2) {
        int n2 = 0;
        long l = 0L;
        int n3 = 0;
        int n4 = 0;
        while (n > 0) {
            if (n2 < Math.min(30, n)) {
                l |= ((long)nArray[n3++] & 0xFFFFFFFFL) << n2;
                n2 += 32;
            }
            nArray2[n4++] = (int)l & 0x3FFFFFFF;
            l >>>= 30;
            n2 -= 30;
            n -= 30;
        }
    }

    private static int equalTo(int n, int[] nArray, int n2) {
        int n3 = nArray[0] ^ n2;
        for (int i = 1; i < n; ++i) {
            n3 |= nArray[i];
        }
        n3 = n3 >>> 1 | n3 & 1;
        return n3 - 1 >> 31;
    }

    private static boolean equalToVar(int n, int[] nArray, int n2) {
        int n3 = nArray[0] ^ n2;
        if (n3 != 0) {
            return false;
        }
        for (int i = 1; i < n; ++i) {
            n3 |= nArray[i];
        }
        return n3 == 0;
    }

    private static int getMaximumDivsteps(int n) {
        return (int)(188898L * (long)n + (long)(n < 46 ? 308405 : 181188) >>> 16);
    }

    private static int getMaximumHDDivsteps(int n) {
        return (int)(150964L * (long)n + 99243L >>> 16);
    }

    private static int hddivsteps30(int n, int n2, int n3, int[] nArray) {
        int n4 = 0x40000000;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0x40000000;
        int n8 = n2;
        int n9 = n3;
        for (int i = 0; i < 30; ++i) {
            int n10 = n >> 31;
            int n11 = -(n9 & 1);
            int n12 = n8 ^ n10;
            int n13 = n4 ^ n10;
            int n14 = n5 ^ n10;
            int n15 = n11 & ~n10;
            n = (n ^ n15) + 1;
            n8 += (n9 -= n12 & n11) & n15;
            n4 += (n6 -= n13 & n11) & n15;
            n5 += (n7 -= n14 & n11) & n15;
            n9 >>= 1;
            n6 >>= 1;
            n7 >>= 1;
        }
        nArray[0] = n4;
        nArray[1] = n5;
        nArray[2] = n6;
        nArray[3] = n7;
        return n;
    }

    private static int negate30(int n, int[] nArray) {
        int n2 = 0;
        int n3 = n - 1;
        for (int i = 0; i < n3; ++i) {
            nArray[i] = (n2 -= nArray[i]) & 0x3FFFFFFF;
            n2 >>= 30;
        }
        nArray[n3] = n2 -= nArray[n3];
        return n2 >>= 30;
    }

    private static int trimFG30(int n, int[] nArray, int[] nArray2) {
        int n2 = nArray[n - 1];
        int n3 = nArray2[n - 1];
        int n4 = n - 2 >> 31;
        n4 |= n2 ^ n2 >> 31;
        if ((n4 |= n3 ^ n3 >> 31) == 0) {
            int n5 = n - 2;
            nArray[n5] = nArray[n5] | n2 << 30;
            int n6 = n - 2;
            nArray2[n6] = nArray2[n6] | n3 << 30;
            --n;
        }
        return n;
    }

    private static void updateDE30(int n, int[] nArray, int[] nArray2, int[] nArray3, int n2, int[] nArray4) {
        int n3 = nArray3[0];
        int n4 = nArray3[1];
        int n5 = nArray3[2];
        int n6 = nArray3[3];
        int n7 = nArray[n - 1] >> 31;
        int n8 = nArray2[n - 1] >> 31;
        int n9 = (n3 & n7) + (n4 & n8);
        int n10 = (n5 & n7) + (n6 & n8);
        int n11 = nArray4[0];
        int n12 = nArray[0];
        int n13 = nArray2[0];
        long l = (long)n3 * (long)n12 + (long)n4 * (long)n13;
        long l2 = (long)n5 * (long)n12 + (long)n6 * (long)n13;
        n9 -= n2 * (int)l + n9 & 0x3FFFFFFF;
        n10 -= n2 * (int)l2 + n10 & 0x3FFFFFFF;
        l += (long)n11 * (long)n9;
        l2 += (long)n11 * (long)n10;
        l >>= 30;
        l2 >>= 30;
        for (int i = 1; i < n; ++i) {
            n11 = nArray4[i];
            n12 = nArray[i];
            n13 = nArray2[i];
            nArray[i - 1] = (int)(l += (long)n3 * (long)n12 + (long)n4 * (long)n13 + (long)n11 * (long)n9) & 0x3FFFFFFF;
            l >>= 30;
            nArray2[i - 1] = (int)(l2 += (long)n5 * (long)n12 + (long)n6 * (long)n13 + (long)n11 * (long)n10) & 0x3FFFFFFF;
            l2 >>= 30;
        }
        nArray[n - 1] = (int)l;
        nArray2[n - 1] = (int)l2;
    }

    private static void updateFG30(int n, int[] nArray, int[] nArray2, int[] nArray3) {
        int n2 = nArray3[0];
        int n3 = nArray3[1];
        int n4 = nArray3[2];
        int n5 = nArray3[3];
        int n6 = nArray[0];
        int n7 = nArray2[0];
        long l = (long)n2 * (long)n6 + (long)n3 * (long)n7;
        long l2 = (long)n4 * (long)n6 + (long)n5 * (long)n7;
        l >>= 30;
        l2 >>= 30;
        for (int i = 1; i < n; ++i) {
            n6 = nArray[i];
            n7 = nArray2[i];
            nArray[i - 1] = (int)(l += (long)n2 * (long)n6 + (long)n3 * (long)n7) & 0x3FFFFFFF;
            l >>= 30;
            nArray2[i - 1] = (int)(l2 += (long)n4 * (long)n6 + (long)n5 * (long)n7) & 0x3FFFFFFF;
            l2 >>= 30;
        }
        nArray[n - 1] = (int)l;
        nArray2[n - 1] = (int)l2;
    }
}

