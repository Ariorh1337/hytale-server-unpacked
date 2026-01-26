/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.bike.BIKERing;
import org.bouncycastle.pqc.crypto.bike.BIKEUtils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

class BIKEEngine {
    private int r;
    private int w;
    private int hw;
    private int t;
    private int nbIter;
    private int tau;
    private final BIKERing bikeRing;
    private int L_BYTE;
    private int R_BYTE;
    private int R2_BYTE;

    public BIKEEngine(int n, int n2, int n3, int n4, int n5, int n6) {
        this.r = n;
        this.w = n2;
        this.t = n3;
        this.nbIter = n5;
        this.tau = n6;
        this.hw = this.w / 2;
        this.L_BYTE = n4 / 8;
        this.R_BYTE = n + 7 >>> 3;
        this.R2_BYTE = 2 * n + 7 >>> 3;
        this.bikeRing = new BIKERing(n);
    }

    public int getSessionKeySize() {
        return this.L_BYTE;
    }

    private byte[] functionH(byte[] byArray) {
        byte[] byArray2 = new byte[2 * this.R_BYTE];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray, 0, byArray.length);
        BIKEUtils.generateRandomByteArray(byArray2, 2 * this.r, this.t, sHAKEDigest);
        return byArray2;
    }

    private void functionL(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        byte[] byArray4 = new byte[48];
        SHA3Digest sHA3Digest = new SHA3Digest(384);
        sHA3Digest.update(byArray, 0, byArray.length);
        sHA3Digest.update(byArray2, 0, byArray2.length);
        sHA3Digest.doFinal(byArray4, 0);
        System.arraycopy(byArray4, 0, byArray3, 0, this.L_BYTE);
    }

    private void functionK(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        byte[] byArray5 = new byte[48];
        SHA3Digest sHA3Digest = new SHA3Digest(384);
        sHA3Digest.update(byArray, 0, byArray.length);
        sHA3Digest.update(byArray2, 0, byArray2.length);
        sHA3Digest.update(byArray3, 0, byArray3.length);
        sHA3Digest.doFinal(byArray5, 0);
        System.arraycopy(byArray5, 0, byArray4, 0, this.L_BYTE);
    }

    public void genKeyPair(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, SecureRandom secureRandom) {
        byte[] byArray5 = new byte[64];
        secureRandom.nextBytes(byArray5);
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray5, 0, this.L_BYTE);
        BIKEUtils.generateRandomByteArray(byArray, this.r, this.hw, sHAKEDigest);
        BIKEUtils.generateRandomByteArray(byArray2, this.r, this.hw, sHAKEDigest);
        long[] lArray = this.bikeRing.create();
        long[] lArray2 = this.bikeRing.create();
        this.bikeRing.decodeBytes(byArray, lArray);
        this.bikeRing.decodeBytes(byArray2, lArray2);
        long[] lArray3 = this.bikeRing.create();
        this.bikeRing.inv(lArray, lArray3);
        this.bikeRing.multiply(lArray3, lArray2, lArray3);
        this.bikeRing.encodeBytes(lArray3, byArray4);
        System.arraycopy(byArray5, this.L_BYTE, byArray3, 0, byArray3.length);
    }

    public void encaps(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, SecureRandom secureRandom) {
        byte[] byArray5 = new byte[this.L_BYTE];
        secureRandom.nextBytes(byArray5);
        byte[] byArray6 = this.functionH(byArray5);
        byte[] byArray7 = new byte[this.R_BYTE];
        byte[] byArray8 = new byte[this.R_BYTE];
        this.splitEBytes(byArray6, byArray7, byArray8);
        long[] lArray = this.bikeRing.create();
        long[] lArray2 = this.bikeRing.create();
        this.bikeRing.decodeBytes(byArray7, lArray);
        this.bikeRing.decodeBytes(byArray8, lArray2);
        long[] lArray3 = this.bikeRing.create();
        this.bikeRing.decodeBytes(byArray4, lArray3);
        this.bikeRing.multiply(lArray3, lArray2, lArray3);
        this.bikeRing.add(lArray3, lArray, lArray3);
        this.bikeRing.encodeBytes(lArray3, byArray);
        this.functionL(byArray7, byArray8, byArray2);
        Bytes.xorTo(this.L_BYTE, byArray5, byArray2);
        this.functionK(byArray5, byArray, byArray2, byArray3);
    }

    public void decaps(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6) {
        int[] nArray = new int[this.hw];
        int[] nArray2 = new int[this.hw];
        this.convertToCompact(nArray, byArray2);
        this.convertToCompact(nArray2, byArray3);
        byte[] byArray7 = this.computeSyndrome(byArray5, byArray2);
        byte[] byArray8 = this.BGFDecoder(byArray7, nArray, nArray2);
        byte[] byArray9 = new byte[2 * this.R_BYTE];
        BIKEUtils.fromBitArrayToByteArray(byArray9, byArray8, 0, 2 * this.r);
        byte[] byArray10 = new byte[this.R_BYTE];
        byte[] byArray11 = new byte[this.R_BYTE];
        this.splitEBytes(byArray9, byArray10, byArray11);
        byte[] byArray12 = new byte[this.L_BYTE];
        this.functionL(byArray10, byArray11, byArray12);
        Bytes.xorTo(this.L_BYTE, byArray6, byArray12);
        byte[] byArray13 = this.functionH(byArray12);
        if (Arrays.areEqual(byArray9, 0, this.R2_BYTE, byArray13, 0, this.R2_BYTE)) {
            this.functionK(byArray12, byArray5, byArray6, byArray);
        } else {
            this.functionK(byArray4, byArray5, byArray6, byArray);
        }
    }

    private byte[] computeSyndrome(byte[] byArray, byte[] byArray2) {
        long[] lArray = this.bikeRing.create();
        long[] lArray2 = this.bikeRing.create();
        this.bikeRing.decodeBytes(byArray, lArray);
        this.bikeRing.decodeBytes(byArray2, lArray2);
        this.bikeRing.multiply(lArray, lArray2, lArray);
        return this.bikeRing.encodeBitsTransposed(lArray);
    }

    private byte[] BGFDecoder(byte[] byArray, int[] nArray, int[] nArray2) {
        byte[] byArray2 = new byte[2 * this.r];
        int[] nArray3 = this.getColumnFromCompactVersion(nArray);
        int[] nArray4 = this.getColumnFromCompactVersion(nArray2);
        byte[] byArray3 = new byte[2 * this.r];
        byte[] byArray4 = new byte[this.r];
        byte[] byArray5 = new byte[2 * this.r];
        int n = this.threshold(BIKEUtils.getHammingWeight(byArray), this.r);
        this.BFIter(byArray, byArray2, n, nArray, nArray2, nArray3, nArray4, byArray3, byArray5, byArray4);
        this.BFMaskedIter(byArray, byArray2, byArray3, (this.hw + 1) / 2 + 1, nArray, nArray2, nArray3, nArray4);
        this.BFMaskedIter(byArray, byArray2, byArray5, (this.hw + 1) / 2 + 1, nArray, nArray2, nArray3, nArray4);
        for (int i = 1; i < this.nbIter; ++i) {
            Arrays.fill(byArray3, (byte)0);
            n = this.threshold(BIKEUtils.getHammingWeight(byArray), this.r);
            this.BFIter2(byArray, byArray2, n, nArray, nArray2, nArray3, nArray4, byArray4);
        }
        if (BIKEUtils.getHammingWeight(byArray) == 0) {
            return byArray2;
        }
        return null;
    }

    private void BFIter(byte[] byArray, byte[] byArray2, int n, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4, byte[] byArray3, byte[] byArray4, byte[] byArray5) {
        int n2;
        this.ctrAll(nArray3, byArray, byArray5);
        int n3 = byArray5[0] & 0xFF;
        int n4 = (n3 - n >> 31) + 1;
        int n5 = (n3 - (n - this.tau) >> 31) + 1;
        byArray2[0] = (byte)(byArray2[0] ^ (byte)n4);
        byArray3[0] = (byte)n4;
        byArray4[0] = (byte)n5;
        for (n3 = 1; n3 < this.r; ++n3) {
            n4 = byArray5[n3] & 0xFF;
            n5 = (n4 - n >> 31) + 1;
            n2 = (n4 - (n - this.tau) >> 31) + 1;
            int n6 = this.r - n3;
            byArray2[n6] = (byte)(byArray2[n6] ^ (byte)n5);
            byArray3[n3] = (byte)n5;
            byArray4[n3] = (byte)n2;
        }
        this.ctrAll(nArray4, byArray, byArray5);
        n3 = byArray5[0] & 0xFF;
        n4 = (n3 - n >> 31) + 1;
        n5 = (n3 - (n - this.tau) >> 31) + 1;
        int n7 = this.r;
        byArray2[n7] = (byte)(byArray2[n7] ^ (byte)n4);
        byArray3[this.r] = (byte)n4;
        byArray4[this.r] = (byte)n5;
        for (n3 = 1; n3 < this.r; ++n3) {
            n4 = byArray5[n3] & 0xFF;
            n5 = (n4 - n >> 31) + 1;
            n2 = (n4 - (n - this.tau) >> 31) + 1;
            int n8 = this.r + this.r - n3;
            byArray2[n8] = (byte)(byArray2[n8] ^ (byte)n5);
            byArray3[this.r + n3] = (byte)n5;
            byArray4[this.r + n3] = (byte)n2;
        }
        for (n3 = 0; n3 < 2 * this.r; ++n3) {
            this.recomputeSyndrome(byArray, n3, nArray, nArray2, byArray3[n3] != 0);
        }
    }

    private void BFIter2(byte[] byArray, byte[] byArray2, int n, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4, byte[] byArray3) {
        int n2;
        int[] nArray5 = new int[2 * this.r];
        this.ctrAll(nArray3, byArray, byArray3);
        int n3 = byArray3[0] & 0xFF;
        int n4 = (n3 - n >> 31) + 1;
        byArray2[0] = (byte)(byArray2[0] ^ (byte)n4);
        nArray5[0] = n4;
        for (n3 = 1; n3 < this.r; ++n3) {
            n4 = byArray3[n3] & 0xFF;
            n2 = (n4 - n >> 31) + 1;
            int n5 = this.r - n3;
            byArray2[n5] = (byte)(byArray2[n5] ^ (byte)n2);
            nArray5[n3] = n2;
        }
        this.ctrAll(nArray4, byArray, byArray3);
        n3 = byArray3[0] & 0xFF;
        n4 = (n3 - n >> 31) + 1;
        int n6 = this.r;
        byArray2[n6] = (byte)(byArray2[n6] ^ (byte)n4);
        nArray5[this.r] = n4;
        for (n3 = 1; n3 < this.r; ++n3) {
            n4 = byArray3[n3] & 0xFF;
            n2 = (n4 - n >> 31) + 1;
            int n7 = this.r + this.r - n3;
            byArray2[n7] = (byte)(byArray2[n7] ^ (byte)n2);
            nArray5[this.r + n3] = n2;
        }
        for (n3 = 0; n3 < 2 * this.r; ++n3) {
            this.recomputeSyndrome(byArray, n3, nArray, nArray2, nArray5[n3] == 1);
        }
    }

    private void BFMaskedIter(byte[] byArray, byte[] byArray2, byte[] byArray3, int n, int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4) {
        boolean bl;
        int n2;
        int[] nArray5 = new int[2 * this.r];
        for (n2 = 0; n2 < this.r; ++n2) {
            if (byArray3[n2] != 1) continue;
            bl = this.ctr(nArray3, byArray, n2) >= n;
            this.updateNewErrorIndex(byArray2, n2, bl);
            nArray5[n2] = bl ? 1 : 0;
        }
        for (n2 = 0; n2 < this.r; ++n2) {
            if (byArray3[this.r + n2] != 1) continue;
            bl = this.ctr(nArray4, byArray, n2) >= n;
            this.updateNewErrorIndex(byArray2, this.r + n2, bl);
            nArray5[this.r + n2] = bl ? 1 : 0;
        }
        for (n2 = 0; n2 < 2 * this.r; ++n2) {
            this.recomputeSyndrome(byArray, n2, nArray, nArray2, nArray5[n2] == 1);
        }
    }

    private int threshold(int n, int n2) {
        switch (n2) {
            case 12323: {
                return BIKEEngine.thresholdFromParameters(n, 0.0069722, 13.53, 36);
            }
            case 24659: {
                return BIKEEngine.thresholdFromParameters(n, 0.005265, 15.2588, 52);
            }
            case 40973: {
                return BIKEEngine.thresholdFromParameters(n, 0.00402312, 17.8785, 69);
            }
        }
        throw new IllegalArgumentException();
    }

    private static int thresholdFromParameters(int n, double d, double d2, int n2) {
        return Math.max(n2, (int)Math.floor(d * (double)n + d2));
    }

    private int ctr(int[] nArray, byte[] byArray, int n) {
        int n2;
        int n3;
        int n4 = 0;
        int n5 = this.hw - 4;
        for (n3 = 0; n3 <= n5; n3 += 4) {
            n2 = nArray[n3 + 0] + n - this.r;
            int n6 = nArray[n3 + 1] + n - this.r;
            int n7 = nArray[n3 + 2] + n - this.r;
            int n8 = nArray[n3 + 3] + n - this.r;
            n2 += n2 >> 31 & this.r;
            n6 += n6 >> 31 & this.r;
            n7 += n7 >> 31 & this.r;
            n8 += n8 >> 31 & this.r;
            n4 += byArray[n2] & 0xFF;
            n4 += byArray[n6] & 0xFF;
            n4 += byArray[n7] & 0xFF;
            n4 += byArray[n8] & 0xFF;
        }
        while (n3 < this.hw) {
            n2 = nArray[n3] + n - this.r;
            n2 += n2 >> 31 & this.r;
            n4 += byArray[n2] & 0xFF;
            ++n3;
        }
        return n4;
    }

    private void ctrAll(int[] nArray, byte[] byArray, byte[] byArray2) {
        int n = nArray[0];
        int n2 = this.r - n;
        System.arraycopy(byArray, n, byArray2, 0, n2);
        System.arraycopy(byArray, 0, byArray2, n2, n);
        for (n = 1; n < this.hw; ++n) {
            int n3;
            n2 = nArray[n];
            int n4 = this.r - n2;
            int n5 = n4 - 4;
            for (n3 = 0; n3 <= n5; n3 += 4) {
                int n6 = n3 + 0;
                byArray2[n6] = (byte)(byArray2[n6] + (byArray[n2 + n3 + 0] & 0xFF));
                int n7 = n3 + 1;
                byArray2[n7] = (byte)(byArray2[n7] + (byArray[n2 + n3 + 1] & 0xFF));
                int n8 = n3 + 2;
                byArray2[n8] = (byte)(byArray2[n8] + (byArray[n2 + n3 + 2] & 0xFF));
                int n9 = n3 + 3;
                byArray2[n9] = (byte)(byArray2[n9] + (byArray[n2 + n3 + 3] & 0xFF));
            }
            while (n3 < n4) {
                int n10 = n3;
                byArray2[n10] = (byte)(byArray2[n10] + (byArray[n2 + n3] & 0xFF));
                ++n3;
            }
            int n11 = this.r - 4;
            for (n5 = n4; n5 <= n11; n5 += 4) {
                int n12 = n5 + 0;
                byArray2[n12] = (byte)(byArray2[n12] + (byArray[n5 + 0 - n4] & 0xFF));
                int n13 = n5 + 1;
                byArray2[n13] = (byte)(byArray2[n13] + (byArray[n5 + 1 - n4] & 0xFF));
                int n14 = n5 + 2;
                byArray2[n14] = (byte)(byArray2[n14] + (byArray[n5 + 2 - n4] & 0xFF));
                int n15 = n5 + 3;
                byArray2[n15] = (byte)(byArray2[n15] + (byArray[n5 + 3 - n4] & 0xFF));
            }
            while (n5 < this.r) {
                int n16 = n5;
                byArray2[n16] = (byte)(byArray2[n16] + (byArray[n5 - n4] & 0xFF));
                ++n5;
            }
        }
    }

    private void convertToCompact(int[] nArray, byte[] byArray) {
        int n = 0;
        for (int i = 0; i < this.R_BYTE; ++i) {
            for (int j = 0; j < 8 && i * 8 + j != this.r; ++j) {
                int n2 = byArray[i] >> j & 1;
                nArray[n] = i * 8 + j & -n2 | nArray[n] & ~(-n2);
                n = (n + n2) % this.hw;
            }
        }
    }

    private int[] getColumnFromCompactVersion(int[] nArray) {
        int[] nArray2 = new int[this.hw];
        if (nArray[0] == 0) {
            nArray2[0] = 0;
            for (int i = 1; i < this.hw; ++i) {
                nArray2[i] = this.r - nArray[this.hw - i];
            }
        } else {
            for (int i = 0; i < this.hw; ++i) {
                nArray2[i] = this.r - nArray[this.hw - 1 - i];
            }
        }
        return nArray2;
    }

    private void recomputeSyndrome(byte[] byArray, int n, int[] nArray, int[] nArray2, boolean bl) {
        byte by;
        byte by2 = by = bl ? (byte)1 : 0;
        if (n < this.r) {
            for (int i = 0; i < this.hw; ++i) {
                if (nArray[i] <= n) {
                    int n2 = n - nArray[i];
                    byArray[n2] = (byte)(byArray[n2] ^ by);
                    continue;
                }
                int n3 = this.r + n - nArray[i];
                byArray[n3] = (byte)(byArray[n3] ^ by);
            }
        } else {
            for (int i = 0; i < this.hw; ++i) {
                if (nArray2[i] <= n - this.r) {
                    int n4 = n - this.r - nArray2[i];
                    byArray[n4] = (byte)(byArray[n4] ^ by);
                    continue;
                }
                int n5 = this.r - nArray2[i] + (n - this.r);
                byArray[n5] = (byte)(byArray[n5] ^ by);
            }
        }
    }

    private void splitEBytes(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n = this.r & 7;
        System.arraycopy(byArray, 0, byArray2, 0, this.R_BYTE - 1);
        byte by = byArray[this.R_BYTE - 1];
        byte by2 = (byte)(-1 << n);
        byArray2[this.R_BYTE - 1] = (byte)(by & ~by2);
        byte by3 = (byte)(by & by2);
        for (int i = 0; i < this.R_BYTE; ++i) {
            byte by4 = byArray[this.R_BYTE + i];
            byArray3[i] = (byte)(by4 << 8 - n | (by3 & 0xFF) >>> n);
            by3 = by4;
        }
    }

    private void updateNewErrorIndex(byte[] byArray, int n, boolean bl) {
        int n2 = n;
        if (n != 0 && n != this.r) {
            n2 = n > this.r ? 2 * this.r - n + this.r : this.r - n;
        }
        int n3 = n2;
        byArray[n3] = (byte)(byArray[n3] ^ (bl ? (byte)1 : 0));
    }
}

