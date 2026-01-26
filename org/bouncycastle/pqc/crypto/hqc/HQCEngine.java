/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.pqc.crypto.hqc.GF2PolynomialCalculator;
import org.bouncycastle.pqc.crypto.hqc.ReedMuller;
import org.bouncycastle.pqc.crypto.hqc.ReedSolomon;
import org.bouncycastle.pqc.crypto.hqc.Shake256RandomGenerator;
import org.bouncycastle.pqc.crypto.hqc.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

class HQCEngine {
    private final int n;
    private final int n1;
    private final int k;
    private final int delta;
    private final int w;
    private final int wr;
    private final int g;
    private final int fft;
    private final int mulParam;
    private static final int SEED_BYTES = 32;
    private final int N_BYTE;
    private final int N_BYTE_64;
    private final int K_BYTE;
    private final int N1N2_BYTE_64;
    private final int N1N2_BYTE;
    private static final int SALT_SIZE_BYTES = 16;
    private final int[] generatorPoly;
    private final int N_MU;
    private final int pkSize;
    private final GF2PolynomialCalculator gf;
    private final long rejectionThreshold;

    public HQCEngine(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11, int[] nArray) {
        this.n = n;
        this.k = n4;
        this.delta = n6;
        this.w = n7;
        this.wr = n8;
        this.n1 = n2;
        this.generatorPoly = nArray;
        this.g = n5;
        this.fft = n9;
        this.N_MU = n10;
        this.pkSize = n11;
        this.mulParam = n3 >> 7;
        this.N_BYTE = Utils.getByteSizeFromBitSize(n);
        this.K_BYTE = n4;
        this.N_BYTE_64 = Utils.getByte64SizeFromBitSize(n);
        this.N1N2_BYTE_64 = Utils.getByte64SizeFromBitSize(n2 * n3);
        this.N1N2_BYTE = Utils.getByteSizeFromBitSize(n2 * n3);
        long l = (1L << (n & 0x3F)) - 1L;
        this.gf = new GF2PolynomialCalculator(this.N_BYTE_64, n, l);
        this.rejectionThreshold = 0x1000000L / (long)n * (long)n;
    }

    public void genKeyPair(byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
        byte[] byArray3 = new byte[32];
        byte[] byArray4 = new byte[64];
        long[] lArray = new long[this.N_BYTE_64];
        long[] lArray2 = new long[this.N_BYTE_64];
        long[] lArray3 = new long[this.N_BYTE_64];
        secureRandom.nextBytes(byArray3);
        Shake256RandomGenerator shake256RandomGenerator = new Shake256RandomGenerator(byArray3, 1);
        System.arraycopy(byArray3, 0, byArray2, this.pkSize + 32 + this.K_BYTE, 32);
        shake256RandomGenerator.nextBytes(byArray3);
        shake256RandomGenerator.nextBytes(byArray2, this.pkSize + 32, this.K_BYTE);
        HQCEngine.hashHI(byArray4, 512, byArray3, byArray3.length, (byte)2);
        shake256RandomGenerator.init(byArray4, 0, 32, (byte)1);
        this.vectSampleFixedWeight1(lArray2, shake256RandomGenerator, this.w);
        this.vectSampleFixedWeight1(lArray, shake256RandomGenerator, this.w);
        System.arraycopy(byArray4, 32, byArray, 0, 32);
        shake256RandomGenerator.init(byArray4, 32, 32, (byte)1);
        this.vectSetRandom(shake256RandomGenerator, lArray3);
        this.gf.vectMul(lArray3, lArray2, lArray3);
        Longs.xorTo(this.N_BYTE_64, lArray, 0, lArray3, 0);
        Utils.fromLongArrayToByteArray(byArray, 32, byArray.length - 32, lArray3);
        System.arraycopy(byArray4, 0, byArray2, this.pkSize, 32);
        System.arraycopy(byArray, 0, byArray2, 0, this.pkSize);
        Arrays.clear(byArray4);
        Arrays.clear(lArray);
        Arrays.clear(lArray2);
        Arrays.clear(lArray3);
    }

    public void encaps(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, SecureRandom secureRandom) {
        byte[] byArray6 = new byte[this.K_BYTE];
        byte[] byArray7 = new byte[32];
        long[] lArray = new long[this.N_BYTE_64];
        long[] lArray2 = new long[this.N1N2_BYTE_64];
        secureRandom.nextBytes(byArray6);
        secureRandom.nextBytes(byArray5);
        HQCEngine.hashHI(byArray7, 256, byArray4, byArray4.length, (byte)1);
        this.hashGJ(byArray3, 512, byArray7, byArray6, 0, byArray6.length, byArray5, 0, 16, (byte)0);
        this.pkeEncrypt(lArray, lArray2, byArray4, byArray6, byArray3, 32);
        Utils.fromLongArrayToByteArray(byArray, lArray);
        Utils.fromLongArrayToByteArray(byArray2, lArray2);
        Arrays.clear(lArray);
        Arrays.clear(lArray2);
        Arrays.clear(byArray6);
        Arrays.clear(byArray7);
    }

    public int decaps(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        long[] lArray = new long[this.N_BYTE_64];
        long[] lArray2 = new long[this.N_BYTE_64];
        long[] lArray3 = new long[this.N_BYTE_64];
        long[] lArray4 = new long[this.N_BYTE_64];
        byte[] byArray4 = new byte[32];
        byte[] byArray5 = new byte[64];
        byte[] byArray6 = new byte[this.k];
        byte[] byArray7 = new byte[32];
        byte[] byArray8 = new byte[this.n1];
        Shake256RandomGenerator shake256RandomGenerator = new Shake256RandomGenerator(byArray3, this.pkSize, 32, 1);
        this.vectSampleFixedWeight1(lArray4, shake256RandomGenerator, this.w);
        Utils.fromByteArrayToLongArray(lArray, byArray2, 0, this.N_BYTE);
        Utils.fromByteArrayToLongArray(lArray2, byArray2, this.N_BYTE, this.N1N2_BYTE);
        this.gf.vectMul(lArray3, lArray4, lArray);
        this.vectTruncate(lArray3);
        Longs.xorTo(this.N_BYTE_64, lArray2, 0, lArray3, 0);
        ReedMuller.decode(byArray8, lArray3, this.n1, this.mulParam);
        ReedSolomon.decode(byArray6, byArray8, this.n1, this.fft, this.delta, this.k, this.g);
        int n = 0;
        HQCEngine.hashHI(byArray4, 256, byArray3, this.pkSize, (byte)1);
        this.hashGJ(byArray5, 512, byArray4, byArray6, 0, byArray6.length, byArray2, this.N_BYTE + this.N1N2_BYTE, 16, (byte)0);
        System.arraycopy(byArray5, 0, byArray, 0, 32);
        Arrays.fill(lArray4, 0L);
        this.pkeEncrypt(lArray3, lArray4, byArray3, byArray6, byArray5, 32);
        this.hashGJ(byArray7, 256, byArray4, byArray3, this.pkSize + 32, this.K_BYTE, byArray2, 0, byArray2.length, (byte)3);
        if (!Arrays.constantTimeAreEqual(this.N_BYTE_64, lArray, 0, lArray3, 0)) {
            n = 1;
        }
        if (!Arrays.constantTimeAreEqual(this.N_BYTE_64, lArray2, 0, lArray4, 0)) {
            n = 1;
        }
        --n;
        for (int i = 0; i < this.K_BYTE; ++i) {
            byArray[i] = (byte)((byArray[i] & n ^ byArray7[i] & ~n) & 0xFF);
        }
        Arrays.clear(lArray);
        Arrays.clear(lArray2);
        Arrays.clear(lArray3);
        Arrays.clear(lArray4);
        Arrays.clear(byArray4);
        Arrays.clear(byArray5);
        Arrays.clear(byArray6);
        Arrays.clear(byArray7);
        Arrays.clear(byArray8);
        return -n;
    }

    private void pkeEncrypt(long[] lArray, long[] lArray2, byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        long[] lArray3 = new long[this.N_BYTE_64];
        long[] lArray4 = new long[this.N_BYTE_64];
        byte[] byArray4 = new byte[this.n1];
        ReedSolomon.encode(byArray4, byArray2, this.n1, this.k, this.g, this.generatorPoly);
        ReedMuller.encode(lArray2, byArray4, this.n1, this.mulParam);
        Shake256RandomGenerator shake256RandomGenerator = new Shake256RandomGenerator(byArray, 0, 32, 1);
        this.vectSetRandom(shake256RandomGenerator, lArray4);
        shake256RandomGenerator.init(byArray3, n, 32, (byte)1);
        this.vectSampleFixedWeights2(shake256RandomGenerator, lArray3, this.wr);
        this.gf.vectMul(lArray, lArray3, lArray4);
        Utils.fromByteArrayToLongArray(lArray4, byArray, 32, this.pkSize - 32);
        this.gf.vectMul(lArray4, lArray3, lArray4);
        this.vectSampleFixedWeights2(shake256RandomGenerator, lArray3, this.wr);
        Longs.xorTo(this.N_BYTE_64, lArray3, 0, lArray4, 0);
        this.vectTruncate(lArray4);
        Longs.xorTo(this.N1N2_BYTE_64, lArray4, 0, lArray2, 0);
        this.vectSampleFixedWeights2(shake256RandomGenerator, lArray4, this.wr);
        Longs.xorTo(this.N_BYTE_64, lArray4, 0, lArray, 0);
        Arrays.clear(lArray3);
        Arrays.clear(lArray4);
        Arrays.clear(byArray4);
    }

    private int barrettReduce(int n) {
        long l = (long)n * (long)this.N_MU >>> 32;
        int n2 = n - (int)(l * (long)this.n);
        n2 -= -(n2 - this.n >>> 31 ^ 1) & this.n;
        return n2;
    }

    private void generateRandomSupport(int[] nArray, int n, Shake256RandomGenerator shake256RandomGenerator) {
        int n2 = 3 * n;
        byte[] byArray = new byte[n2];
        int n3 = n2;
        int n4 = 0;
        while (n4 < n) {
            int n5;
            if (n3 == n2) {
                shake256RandomGenerator.xofGetBytes(byArray, n2);
                n3 = 0;
            }
            if ((long)(n5 = (byArray[n3++] & 0xFF) << 16 | (byArray[n3++] & 0xFF) << 8 | byArray[n3++] & 0xFF) >= this.rejectionThreshold) continue;
            n5 = this.barrettReduce(n5);
            boolean bl = false;
            for (int i = 0; i < n4; ++i) {
                if (nArray[i] != n5) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            nArray[n4++] = n5;
        }
    }

    private void writeSupportToVector(long[] lArray, int[] nArray, int n) {
        int n2;
        int[] nArray2 = new int[this.wr];
        long[] lArray2 = new long[this.wr];
        for (n2 = 0; n2 < n; ++n2) {
            nArray2[n2] = nArray[n2] >>> 6;
            lArray2[n2] = 1L << (nArray[n2] & 0x3F);
        }
        for (n2 = 0; n2 < lArray.length; ++n2) {
            long l = 0L;
            for (int i = 0; i < n; ++i) {
                int n3 = n2 - nArray2[i];
                l |= lArray2[i] & (long)(-(1 ^ (n3 | -n3) >>> 31));
            }
            lArray[n2] = l;
        }
    }

    public void vectSampleFixedWeight1(long[] lArray, Shake256RandomGenerator shake256RandomGenerator, int n) {
        int[] nArray = new int[this.wr];
        this.generateRandomSupport(nArray, n, shake256RandomGenerator);
        this.writeSupportToVector(lArray, nArray, n);
    }

    private static void hashHI(byte[] byArray, int n, byte[] byArray2, int n2, byte by) {
        SHA3Digest sHA3Digest = new SHA3Digest(n);
        sHA3Digest.update(byArray2, 0, n2);
        sHA3Digest.update(by);
        sHA3Digest.doFinal(byArray, 0);
    }

    private void hashGJ(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, int n2, int n3, byte[] byArray4, int n4, int n5, byte by) {
        SHA3Digest sHA3Digest = new SHA3Digest(n);
        sHA3Digest.update(byArray2, 0, byArray2.length);
        sHA3Digest.update(byArray3, n2, n3);
        sHA3Digest.update(byArray4, n4, n5);
        sHA3Digest.update(by);
        sHA3Digest.doFinal(byArray, 0);
    }

    private void vectSetRandom(Shake256RandomGenerator shake256RandomGenerator, long[] lArray) {
        byte[] byArray = new byte[lArray.length << 3];
        shake256RandomGenerator.xofGetBytes(byArray, this.N_BYTE);
        Pack.littleEndianToLong(byArray, 0, lArray);
        int n = this.N_BYTE_64 - 1;
        lArray[n] = lArray[n] & Utils.bitMask(this.n, 64L);
    }

    private void vectSampleFixedWeights2(Shake256RandomGenerator shake256RandomGenerator, long[] lArray, int n) {
        int n2;
        int[] nArray = new int[this.wr];
        byte[] byArray = new byte[this.wr << 2];
        shake256RandomGenerator.xofGetBytes(byArray, byArray.length);
        Pack.littleEndianToInt(byArray, 0, nArray);
        for (n2 = 0; n2 < n; ++n2) {
            nArray[n2] = n2 + (int)(((long)nArray[n2] & 0xFFFFFFFFL) * (long)(this.n - n2) >> 32);
        }
        n2 = n - 1;
        while (n2-- > 0) {
            int n3 = 0;
            for (int i = n2 + 1; i < n; ++i) {
                n3 |= HQCEngine.compareU32(nArray[i], nArray[n2]);
            }
            n3 = -n3;
            nArray[n2] = n3 & n2 ^ ~n3 & nArray[n2];
        }
        this.writeSupportToVector(lArray, nArray, n);
    }

    private static int compareU32(int n, int n2) {
        return 1 ^ (n - n2 | n2 - n) >>> 31;
    }

    private void vectTruncate(long[] lArray) {
        Arrays.fill(lArray, this.N1N2_BYTE_64, this.n + 63 >> 6, 0L);
    }
}

