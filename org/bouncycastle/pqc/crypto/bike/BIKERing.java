/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

class BIKERing {
    private static final int PERMUTATION_CUTOFF = 64;
    private final int bits;
    private final int size;
    private final int sizeExt;
    private final Map<Integer, Integer> halfPowers = new HashMap<Integer, Integer>();

    BIKERing(int n) {
        if ((n & 0xFFFF0001) != 1) {
            throw new IllegalArgumentException();
        }
        this.bits = n;
        this.size = n + 63 >>> 6;
        this.sizeExt = this.size * 2;
        BIKERing.generateHalfPowersInv(this.halfPowers, n);
    }

    void add(long[] lArray, long[] lArray2, long[] lArray3) {
        for (int i = 0; i < this.size; ++i) {
            lArray3[i] = lArray[i] ^ lArray2[i];
        }
    }

    void addTo(long[] lArray, long[] lArray2) {
        for (int i = 0; i < this.size; ++i) {
            int n = i;
            lArray2[n] = lArray2[n] ^ lArray[i];
        }
    }

    void copy(long[] lArray, long[] lArray2) {
        for (int i = 0; i < this.size; ++i) {
            lArray2[i] = lArray[i];
        }
    }

    long[] create() {
        return new long[this.size];
    }

    long[] createExt() {
        return new long[this.sizeExt];
    }

    void decodeBytes(byte[] byArray, long[] lArray) {
        int n = this.bits & 0x3F;
        Pack.littleEndianToLong(byArray, 0, lArray, 0, this.size - 1);
        byte[] byArray2 = new byte[8];
        System.arraycopy(byArray, this.size - 1 << 3, byArray2, 0, n + 7 >>> 3);
        lArray[this.size - 1] = Pack.littleEndianToLong(byArray2, 0);
    }

    byte[] encodeBitsTransposed(long[] lArray) {
        byte[] byArray = new byte[this.bits];
        byArray[0] = (byte)(lArray[0] & 1L);
        for (int i = 1; i < this.bits; ++i) {
            byArray[this.bits - i] = (byte)(lArray[i >>> 6] >>> (i & 0x3F) & 1L);
        }
        return byArray;
    }

    void encodeBytes(long[] lArray, byte[] byArray) {
        int n = this.bits & 0x3F;
        Pack.longToLittleEndian(lArray, 0, this.size - 1, byArray, 0);
        byte[] byArray2 = new byte[8];
        Pack.longToLittleEndian(lArray[this.size - 1], byArray2, 0);
        System.arraycopy(byArray2, 0, byArray, this.size - 1 << 3, n + 7 >>> 3);
    }

    void inv(long[] lArray, long[] lArray2) {
        long[] lArray3 = this.create();
        long[] lArray4 = this.create();
        long[] lArray5 = this.create();
        this.copy(lArray, lArray3);
        this.copy(lArray, lArray5);
        int n = this.bits - 2;
        int n2 = 32 - Integers.numberOfLeadingZeros(n);
        for (int i = 1; i < n2; ++i) {
            this.squareN(lArray3, 1 << i - 1, lArray4);
            this.multiply(lArray3, lArray4, lArray3);
            if ((n & 1 << i) == 0) continue;
            int n3 = n & (1 << i) - 1;
            this.squareN(lArray3, n3, lArray4);
            this.multiply(lArray5, lArray4, lArray5);
        }
        this.square(lArray5, lArray2);
    }

    void multiply(long[] lArray, long[] lArray2, long[] lArray3) {
        long[] lArray4 = this.createExt();
        this.implMultiplyAcc(lArray, lArray2, lArray4);
        this.reduce(lArray4, lArray3);
    }

    void reduce(long[] lArray, long[] lArray2) {
        int n = this.bits & 0x3F;
        int n2 = 64 - n;
        long l = -1L >>> n2;
        Nat.shiftUpBits64(this.size, lArray, this.size, n2, lArray[this.size - 1], lArray2, 0);
        this.addTo(lArray, lArray2);
        int n3 = this.size - 1;
        lArray2[n3] = lArray2[n3] & l;
    }

    int getSize() {
        return this.size;
    }

    int getSizeExt() {
        return this.sizeExt;
    }

    void square(long[] lArray, long[] lArray2) {
        long[] lArray3 = this.createExt();
        this.implSquare(lArray, lArray3);
        this.reduce(lArray3, lArray2);
    }

    void squareN(long[] lArray, int n, long[] lArray2) {
        if (n >= 64) {
            this.implPermute(lArray, n, lArray2);
            return;
        }
        long[] lArray3 = this.createExt();
        this.implSquare(lArray, lArray3);
        this.reduce(lArray3, lArray2);
        while (--n > 0) {
            this.implSquare(lArray2, lArray3);
            this.reduce(lArray3, lArray2);
        }
    }

    private static int implModAdd(int n, int n2, int n3) {
        int n4 = n2 + n3 - n;
        return n4 + (n4 >> 31 & n);
    }

    protected void implMultiplyAcc(long[] lArray, long[] lArray2, long[] lArray3) {
        int n;
        long[] lArray4 = new long[16];
        for (int i = 0; i < this.size; ++i) {
            BIKERing.implMulwAcc(lArray4, lArray[i], lArray2[i], lArray3, i << 1);
        }
        long l = lArray3[0];
        long l2 = lArray3[1];
        for (int i = 1; i < this.size; ++i) {
            lArray3[i] = (l ^= lArray3[i << 1]) ^ l2;
            l2 ^= lArray3[(i << 1) + 1];
        }
        long l3 = l ^ l2;
        for (n = 0; n < this.size; ++n) {
            lArray3[this.size + n] = lArray3[n] ^ l3;
        }
        n = this.size - 1;
        for (int i = 1; i < n * 2; ++i) {
            int n2 = Math.min(n, i);
            for (int j = i - n2; j < n2; ++j, --n2) {
                BIKERing.implMulwAcc(lArray4, lArray[j] ^ lArray[n2], lArray2[j] ^ lArray2[n2], lArray3, i);
            }
        }
    }

    private void implPermute(long[] lArray, int n, long[] lArray2) {
        int n2 = this.bits;
        int n3 = this.halfPowers.get(Integers.valueOf(n));
        int n4 = BIKERing.implModAdd(n2, n3, n3);
        int n5 = BIKERing.implModAdd(n2, n4, n4);
        int n6 = BIKERing.implModAdd(n2, n5, n5);
        int n7 = n2 - n6;
        int n8 = BIKERing.implModAdd(n2, n7, n3);
        int n9 = BIKERing.implModAdd(n2, n7, n4);
        int n10 = BIKERing.implModAdd(n2, n8, n4);
        int n11 = BIKERing.implModAdd(n2, n7, n5);
        int n12 = BIKERing.implModAdd(n2, n8, n5);
        int n13 = BIKERing.implModAdd(n2, n9, n5);
        int n14 = BIKERing.implModAdd(n2, n10, n5);
        for (int i = 0; i < this.size; ++i) {
            long l = 0L;
            for (int j = 0; j < 64; j += 8) {
                n7 = BIKERing.implModAdd(n2, n7, n6);
                n8 = BIKERing.implModAdd(n2, n8, n6);
                n9 = BIKERing.implModAdd(n2, n9, n6);
                n10 = BIKERing.implModAdd(n2, n10, n6);
                n11 = BIKERing.implModAdd(n2, n11, n6);
                n12 = BIKERing.implModAdd(n2, n12, n6);
                n13 = BIKERing.implModAdd(n2, n13, n6);
                n14 = BIKERing.implModAdd(n2, n14, n6);
                l |= (lArray[n7 >>> 6] >>> n7 & 1L) << j + 0;
                l |= (lArray[n8 >>> 6] >>> n8 & 1L) << j + 1;
                l |= (lArray[n9 >>> 6] >>> n9 & 1L) << j + 2;
                l |= (lArray[n10 >>> 6] >>> n10 & 1L) << j + 3;
                l |= (lArray[n11 >>> 6] >>> n11 & 1L) << j + 4;
                l |= (lArray[n12 >>> 6] >>> n12 & 1L) << j + 5;
                l |= (lArray[n13 >>> 6] >>> n13 & 1L) << j + 6;
                l |= (lArray[n14 >>> 6] >>> n14 & 1L) << j + 7;
            }
            lArray2[i] = l;
        }
        int n15 = this.size - 1;
        lArray2[n15] = lArray2[n15] & -1L >>> -n2;
    }

    private static int generateHalfPower(int n, int n2, int n3) {
        int n4;
        int n5;
        int n6 = 1;
        for (n5 = n3; n5 >= 32; n5 -= 32) {
            n4 = n2 * n6;
            long l = ((long)n4 & 0xFFFFFFFFL) * (long)n;
            long l2 = l + (long)n6;
            n6 = (int)(l2 >>> 32);
        }
        if (n5 > 0) {
            n4 = -1 >>> -n5;
            int n7 = n2 * n6 & n4;
            long l = ((long)n7 & 0xFFFFFFFFL) * (long)n;
            long l3 = l + (long)n6;
            n6 = (int)(l3 >>> n5);
        }
        return n6;
    }

    private static void generateHalfPowersInv(Map<Integer, Integer> map, int n) {
        int n2 = n - 2;
        int n3 = 32 - Integers.numberOfLeadingZeros(n2);
        int n4 = Mod.inverse32(-n);
        for (int i = 1; i < n3; ++i) {
            int n5;
            int n6 = 1 << i - 1;
            if (n6 >= 64 && !map.containsKey(Integers.valueOf(n6))) {
                map.put(Integers.valueOf(n6), Integers.valueOf(BIKERing.generateHalfPower(n, n4, n6)));
            }
            if ((n2 & 1 << i) == 0 || (n5 = n2 & (1 << i) - 1) < 64 || map.containsKey(Integers.valueOf(n5))) continue;
            map.put(Integers.valueOf(n5), Integers.valueOf(BIKERing.generateHalfPower(n, n4, n5)));
        }
    }

    private static void implMulwAcc(long[] lArray, long l, long l2, long[] lArray2, int n) {
        int n2;
        lArray[1] = l2;
        for (n2 = 2; n2 < 16; n2 += 2) {
            lArray[n2] = lArray[n2 >>> 1] << 1;
            lArray[n2 + 1] = lArray[n2] ^ l2;
        }
        n2 = (int)l;
        long l3 = 0L;
        long l4 = lArray[n2 & 0xF] ^ lArray[n2 >>> 4 & 0xF] << 4;
        int n3 = 56;
        do {
            n2 = (int)(l >>> n3);
            long l5 = lArray[n2 & 0xF] ^ lArray[n2 >>> 4 & 0xF] << 4;
            l4 ^= l5 << n3;
            l3 ^= l5 >>> -n3;
        } while ((n3 -= 8) > 0);
        for (int i = 0; i < 7; ++i) {
            l = (l & 0xFEFEFEFEFEFEFEFEL) >>> 1;
            l3 ^= l & l2 << i >> 63;
        }
        int n4 = n;
        lArray2[n4] = lArray2[n4] ^ l4;
        int n5 = n + 1;
        lArray2[n5] = lArray2[n5] ^ l3;
    }

    private void implSquare(long[] lArray, long[] lArray2) {
        Interleave.expand64To128(lArray, 0, this.size, lArray2, 0);
    }
}

