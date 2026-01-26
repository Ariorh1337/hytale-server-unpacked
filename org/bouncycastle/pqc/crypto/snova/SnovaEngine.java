/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.pqc.crypto.snova.GF16Utils;
import org.bouncycastle.pqc.crypto.snova.MapGroup1;
import org.bouncycastle.pqc.crypto.snova.MapGroup2;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyElements;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.GF16;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

class SnovaEngine {
    private static final Map<Integer, byte[]> fixedAbqSet = new HashMap<Integer, byte[]>();
    private static final Map<Integer, byte[][]> sSet = new HashMap<Integer, byte[][]>();
    private static final Map<Integer, int[][]> xSSet = new HashMap<Integer, int[][]>();
    private final SnovaParameters params;
    private final int l;
    private final int lsq;
    private final int m;
    private final int v;
    private final int o;
    private final int alpha;
    private final int n;
    final byte[][] S;
    final int[][] xS;

    public SnovaEngine(SnovaParameters snovaParameters) {
        int n;
        int n2;
        this.params = snovaParameters;
        this.l = snovaParameters.getL();
        this.lsq = snovaParameters.getLsq();
        this.m = snovaParameters.getM();
        this.v = snovaParameters.getV();
        this.o = snovaParameters.getO();
        this.alpha = snovaParameters.getAlpha();
        this.n = snovaParameters.getN();
        if (!xSSet.containsKey(Integers.valueOf(this.l))) {
            byte[][] byArray = new byte[this.l][this.lsq];
            int[][] nArray = new int[this.l][this.lsq];
            this.be_aI(byArray[0], 0, (byte)1);
            this.beTheS(byArray[1]);
            for (n2 = 2; n2 < this.l; ++n2) {
                GF16Utils.gf16mMul(byArray[n2 - 1], byArray[1], byArray[n2], this.l);
            }
            for (n2 = 0; n2 < this.l; ++n2) {
                for (n = 0; n < this.lsq; ++n) {
                    nArray[n2][n] = GF16Utils.gf16FromNibble(byArray[n2][n]);
                }
            }
            sSet.put(Integers.valueOf(this.l), byArray);
            xSSet.put(Integers.valueOf(this.l), nArray);
        }
        this.S = sSet.get(Integers.valueOf(this.l));
        this.xS = xSSet.get(Integers.valueOf(this.l));
        if (this.l < 4 && !fixedAbqSet.containsKey(Integers.valueOf(this.o))) {
            int n3 = this.alpha * this.l;
            int n4 = n3 * this.l;
            n2 = this.o * n3;
            n = this.o * n4;
            byte[] byArray = new byte[n << 2];
            byte[] byArray2 = new byte[n + n2];
            byte[] byArray3 = new byte[n2 << 2];
            byte[] byArray4 = "SNOVA_ABQ".getBytes();
            SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
            sHAKEDigest.update(byArray4, 0, byArray4.length);
            sHAKEDigest.doFinal(byArray2, 0, byArray2.length);
            GF16.decode(byArray2, byArray, n << 1);
            GF16.decode(byArray2, n4, byArray3, 0, n2 << 1);
            int n5 = 0;
            int n6 = 0;
            int n7 = 0;
            while (n5 < this.o) {
                int n8 = 0;
                int n9 = n7;
                int n10 = n6;
                while (n8 < this.alpha) {
                    this.makeInvertibleByAddingAS(byArray, n10);
                    this.makeInvertibleByAddingAS(byArray, n + n10);
                    this.genAFqS(byArray3, n9, byArray, (n << 1) + n10);
                    this.genAFqS(byArray3, n2 + n9, byArray, (n << 1) + n + n10);
                    ++n8;
                    n9 += this.l;
                    n10 += this.lsq;
                }
                ++n5;
                n6 += n4;
                n7 += n3;
            }
            fixedAbqSet.put(Integers.valueOf(this.o), byArray);
        }
    }

    private void beTheS(byte[] byArray) {
        int n = 0;
        int n2 = 0;
        while (n < this.l) {
            for (int i = 0; i < this.l; ++i) {
                int n3 = 8 - (n + i);
                byArray[n2 + i] = (byte)(n3 & 0xF);
            }
            ++n;
            n2 += this.l;
        }
        if (this.l == 5) {
            byArray[24] = 9;
        }
    }

    private void be_aI(byte[] byArray, int n, byte by) {
        int n2 = this.l + 1;
        int n3 = 0;
        while (n3 < this.l) {
            byArray[n] = by;
            ++n3;
            n += n2;
        }
    }

    private void genAFqSCT(byte[] byArray, int n, byte[] byArray2) {
        int[] nArray = new int[this.lsq];
        int n2 = this.l + 1;
        int n3 = GF16Utils.gf16FromNibble(byArray[n]);
        int n4 = 0;
        int n5 = 0;
        while (n4 < this.l) {
            nArray[n5] = n3;
            ++n4;
            n5 += n2;
        }
        for (n4 = 1; n4 < this.l - 1; ++n4) {
            n3 = GF16Utils.gf16FromNibble(byArray[n + n4]);
            for (n5 = 0; n5 < this.lsq; ++n5) {
                int n6 = n5;
                nArray[n6] = nArray[n6] ^ n3 * this.xS[n4][n5];
            }
        }
        n4 = GF16Utils.ctGF16IsNotZero(byArray[n + this.l - 1]);
        n5 = n4 * byArray[n + this.l - 1] + (1 - n4) * (15 + GF16Utils.ctGF16IsNotZero(byArray[n]) - byArray[n]);
        n3 = GF16Utils.gf16FromNibble((byte)n5);
        for (int i = 0; i < this.lsq; ++i) {
            int n7 = i;
            nArray[n7] = nArray[n7] ^ n3 * this.xS[this.l - 1][i];
            byArray2[i] = GF16Utils.gf16ToNibble(nArray[i]);
        }
        Arrays.fill(nArray, 0);
    }

    private void makeInvertibleByAddingAS(byte[] byArray, int n) {
        if (this.gf16Determinant(byArray, n) != 0) {
            return;
        }
        for (int i = 1; i < 16; ++i) {
            this.generateASMatrixTo(byArray, n, (byte)i);
            if (this.gf16Determinant(byArray, n) == 0) continue;
            return;
        }
    }

    private byte gf16Determinant(byte[] byArray, int n) {
        switch (this.l) {
            case 2: {
                return this.determinant2x2(byArray, n);
            }
            case 3: {
                return this.determinant3x3(byArray, n);
            }
            case 4: {
                return this.determinant4x4(byArray, n);
            }
            case 5: {
                return this.determinant5x5(byArray, n);
            }
        }
        throw new IllegalStateException();
    }

    private byte determinant2x2(byte[] byArray, int n) {
        return (byte)(GF16.mul(byArray[n], byArray[n + 3]) ^ GF16.mul(byArray[n + 1], byArray[n + 2]));
    }

    private byte determinant3x3(byte[] byArray, int n) {
        byte by = byArray[n++];
        byte by2 = byArray[n++];
        byte by3 = byArray[n++];
        byte by4 = byArray[n++];
        byte by5 = byArray[n++];
        byte by6 = byArray[n++];
        byte by7 = byArray[n++];
        byte by8 = byArray[n++];
        byte by9 = byArray[n];
        return (byte)(GF16.mul((int)by, GF16.mul(by5, by9) ^ GF16.mul(by6, by8)) ^ GF16.mul((int)by2, GF16.mul(by4, by9) ^ GF16.mul(by6, by7)) ^ GF16.mul((int)by3, GF16.mul(by4, by8) ^ GF16.mul(by5, by7)));
    }

    private byte determinant4x4(byte[] byArray, int n) {
        byte by = byArray[n++];
        byte by2 = byArray[n++];
        byte by3 = byArray[n++];
        byte by4 = byArray[n++];
        byte by5 = byArray[n++];
        byte by6 = byArray[n++];
        byte by7 = byArray[n++];
        byte by8 = byArray[n++];
        byte by9 = byArray[n++];
        byte by10 = byArray[n++];
        byte by11 = byArray[n++];
        byte by12 = byArray[n++];
        byte by13 = byArray[n++];
        byte by14 = byArray[n++];
        byte by15 = byArray[n++];
        byte by16 = byArray[n];
        byte by17 = (byte)(GF16.mul(by11, by16) ^ GF16.mul(by12, by15));
        byte by18 = (byte)(GF16.mul(by10, by16) ^ GF16.mul(by12, by14));
        byte by19 = (byte)(GF16.mul(by10, by15) ^ GF16.mul(by11, by14));
        byte by20 = (byte)(GF16.mul(by9, by16) ^ GF16.mul(by12, by13));
        byte by21 = (byte)(GF16.mul(by9, by15) ^ GF16.mul(by11, by13));
        byte by22 = (byte)(GF16.mul(by9, by14) ^ GF16.mul(by10, by13));
        return (byte)(GF16.mul((int)by, GF16.mul(by6, by17) ^ GF16.mul(by7, by18) ^ GF16.mul(by8, by19)) ^ GF16.mul((int)by2, GF16.mul(by5, by17) ^ GF16.mul(by7, by20) ^ GF16.mul(by8, by21)) ^ GF16.mul((int)by3, GF16.mul(by5, by18) ^ GF16.mul(by6, by20) ^ GF16.mul(by8, by22)) ^ GF16.mul((int)by4, GF16.mul(by5, by19) ^ GF16.mul(by6, by21) ^ GF16.mul(by7, by22)));
    }

    private byte determinant5x5(byte[] byArray, int n) {
        byte by = byArray[n++];
        byte by2 = byArray[n++];
        byte by3 = byArray[n++];
        byte by4 = byArray[n++];
        byte by5 = byArray[n++];
        byte by6 = byArray[n++];
        byte by7 = byArray[n++];
        byte by8 = byArray[n++];
        byte by9 = byArray[n++];
        byte by10 = byArray[n++];
        byte by11 = byArray[n++];
        byte by12 = byArray[n++];
        byte by13 = byArray[n++];
        byte by14 = byArray[n++];
        byte by15 = byArray[n++];
        byte by16 = byArray[n++];
        byte by17 = byArray[n++];
        byte by18 = byArray[n++];
        byte by19 = byArray[n++];
        byte by20 = byArray[n++];
        byte by21 = byArray[n++];
        byte by22 = byArray[n++];
        byte by23 = byArray[n++];
        byte by24 = byArray[n++];
        byte by25 = byArray[n];
        byte by26 = (byte)(GF16.mul(by6, by12) ^ GF16.mul(by7, by11));
        byte by27 = (byte)(GF16.mul(by6, by13) ^ GF16.mul(by8, by11));
        byte by28 = (byte)(GF16.mul(by6, by14) ^ GF16.mul(by9, by11));
        byte by29 = (byte)(GF16.mul(by6, by15) ^ GF16.mul(by10, by11));
        byte by30 = (byte)(GF16.mul(by7, by13) ^ GF16.mul(by8, by12));
        byte by31 = (byte)(GF16.mul(by7, by14) ^ GF16.mul(by9, by12));
        byte by32 = (byte)(GF16.mul(by7, by15) ^ GF16.mul(by10, by12));
        byte by33 = (byte)(GF16.mul(by8, by14) ^ GF16.mul(by9, by13));
        byte by34 = (byte)(GF16.mul(by8, by15) ^ GF16.mul(by10, by13));
        byte by35 = (byte)(GF16.mul(by9, by15) ^ GF16.mul(by10, by14));
        byte by36 = (byte)GF16.mul(GF16.mul(by, by30) ^ GF16.mul(by2, by27) ^ GF16.mul(by3, by26), GF16.mul(by19, by25) ^ GF16.mul(by20, by24));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by, by31) ^ GF16.mul(by2, by28) ^ GF16.mul(by4, by26), GF16.mul(by18, by25) ^ GF16.mul(by20, by23)));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by, by32) ^ GF16.mul(by2, by29) ^ GF16.mul(by5, by26), GF16.mul(by18, by24) ^ GF16.mul(by19, by23)));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by, by33) ^ GF16.mul(by3, by28) ^ GF16.mul(by4, by27), GF16.mul(by17, by25) ^ GF16.mul(by20, by22)));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by, by34) ^ GF16.mul(by3, by29) ^ GF16.mul(by5, by27), GF16.mul(by17, by24) ^ GF16.mul(by19, by22)));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by, by35) ^ GF16.mul(by4, by29) ^ GF16.mul(by5, by28), GF16.mul(by17, by23) ^ GF16.mul(by18, by22)));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by2, by33) ^ GF16.mul(by3, by31) ^ GF16.mul(by4, by30), GF16.mul(by16, by25) ^ GF16.mul(by20, by21)));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by2, by34) ^ GF16.mul(by3, by32) ^ GF16.mul(by5, by30), GF16.mul(by16, by24) ^ GF16.mul(by19, by21)));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by2, by35) ^ GF16.mul(by4, by32) ^ GF16.mul(by5, by31), GF16.mul(by16, by23) ^ GF16.mul(by18, by21)));
        by36 = (byte)(by36 ^ GF16.mul(GF16.mul(by3, by35) ^ GF16.mul(by4, by34) ^ GF16.mul(by5, by33), GF16.mul(by16, by22) ^ GF16.mul(by17, by21)));
        return by36;
    }

    private void generateASMatrixTo(byte[] byArray, int n, byte by) {
        int n2 = 0;
        int n3 = n;
        while (n2 < this.l) {
            for (int i = 0; i < this.l; ++i) {
                int n4 = 8 - (n2 + i);
                if (this.l == 5 && n2 == 4 && i == 4) {
                    n4 = 9;
                }
                int n5 = n3 + i;
                byArray[n5] = (byte)(byArray[n5] ^ GF16.mul((byte)n4, by));
            }
            ++n2;
            n3 += this.l;
        }
    }

    private void genAFqS(byte[] byArray, int n, byte[] byArray2, int n2) {
        byte by;
        this.be_aI(byArray2, n2, byArray[n]);
        for (by = 1; by < this.l - 1; ++by) {
            this.gf16mScaleTo(this.S[by], byArray[n + by], byArray2, n2);
        }
        by = (byte)(byArray[n + this.l - 1] != 0 ? byArray[n + this.l - 1] : 16 - (byArray[n] + (byArray[n] == 0 ? (byte)1 : 0)));
        this.gf16mScaleTo(this.S[this.l - 1], by, byArray2, n2);
    }

    private void gf16mScaleTo(byte[] byArray, byte by, byte[] byArray2, int n) {
        int n2 = 0;
        int n3 = 0;
        while (n2 < this.l) {
            for (int i = 0; i < this.l; ++i) {
                int n4 = n3 + i + n;
                byArray2[n4] = (byte)(byArray2[n4] ^ GF16.mul(byArray[n3 + i], by));
            }
            ++n2;
            n3 += this.l;
        }
    }

    private void genF(MapGroup2 mapGroup2, MapGroup1 mapGroup1, byte[][][] byArray) {
        SnovaEngine.copy4DMatrix(mapGroup1.p11, mapGroup2.f11, this.m, this.v, this.v, this.lsq);
        SnovaEngine.copy4DMatrix(mapGroup1.p12, mapGroup2.f12, this.m, this.v, this.o, this.lsq);
        SnovaEngine.copy4DMatrix(mapGroup1.p21, mapGroup2.f21, this.m, this.o, this.v, this.lsq);
        for (int i = 0; i < this.m; ++i) {
            for (int j = 0; j < this.v; ++j) {
                for (int k = 0; k < this.o; ++k) {
                    for (int i2 = 0; i2 < this.v; ++i2) {
                        GF16Utils.gf16mMulToTo(mapGroup1.p11[i][j][i2], byArray[i2][k], mapGroup1.p11[i][i2][j], mapGroup2.f12[i][j][k], mapGroup2.f21[i][k][j], this.l);
                    }
                }
            }
        }
    }

    private static void copy4DMatrix(byte[][][][] byArray, byte[][][][] byArray2, int n, int n2, int n3, int n4) {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                for (int k = 0; k < n3; ++k) {
                    System.arraycopy(byArray[i][j][k], 0, byArray2[i][j][k], 0, n4);
                }
            }
        }
    }

    public void genP22(byte[] byArray, int n, byte[][][] byArray2, byte[][][][] byArray3, byte[][][][] byArray4) {
        int n2 = this.o * this.lsq;
        int n3 = n2 * this.o;
        byte[] byArray5 = new byte[this.m * n3];
        int n4 = 0;
        int n5 = 0;
        while (n4 < this.m) {
            int n6 = 0;
            int n7 = n5;
            while (n6 < this.o) {
                int n8 = 0;
                int n9 = n7;
                while (n8 < this.o) {
                    for (int i = 0; i < this.v; ++i) {
                        GF16Utils.gf16mMulTo(byArray2[i][n6], byArray4[n4][i][n8], byArray3[n4][n6][i], byArray2[i][n8], byArray5, n9, this.l);
                    }
                    ++n8;
                    n9 += this.lsq;
                }
                ++n6;
                n7 += n2;
            }
            ++n4;
            n5 += n3;
        }
        GF16.encode(byArray5, byArray, n, byArray5.length);
    }

    private void genSeedsAndT12(byte[][][] byArray, byte[] byArray2) {
        int n = this.v * this.o * this.l;
        int n2 = n + 1 >>> 1;
        byte[] byArray3 = new byte[n2];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray2, 0, byArray2.length);
        sHAKEDigest.doFinal(byArray3, 0, byArray3.length);
        byte[] byArray4 = new byte[n];
        GF16.decode(byArray3, byArray4, n);
        int n3 = 0;
        for (int i = 0; i < this.v; ++i) {
            for (int j = 0; j < this.o; ++j) {
                this.genAFqSCT(byArray4, n3, byArray[i][j]);
                n3 += this.l;
            }
        }
    }

    public void genABQP(MapGroup1 mapGroup1, byte[] byArray) {
        Object object;
        int n;
        int n2;
        int n3 = this.lsq * (2 * this.m * this.alpha + this.m * (this.n * this.n - this.m * this.m)) + this.l * 2 * this.m * this.alpha;
        byte[] byArray2 = new byte[this.m * this.alpha * this.l << 1];
        byte[] byArray3 = new byte[n3 + 1 >> 1];
        if (this.params.isPkExpandShake()) {
            long l = 0L;
            n2 = 0;
            n = byArray3.length;
            byte[] byArray4 = new byte[8];
            SHAKEDigest sHAKEDigest = new SHAKEDigest(128);
            while (n > 0) {
                sHAKEDigest.update(byArray, 0, byArray.length);
                Pack.longToLittleEndian(l, byArray4, 0);
                sHAKEDigest.update(byArray4, 0, 8);
                int n4 = Math.min(n, 168);
                sHAKEDigest.doFinal(byArray3, n2, n4);
                n2 += n4;
                n -= n4;
                ++l;
            }
        } else {
            byte[] byArray5 = new byte[16];
            object = SICBlockCipher.newInstance(AESEngine.newInstance());
            object.init(true, new ParametersWithIV(new KeyParameter(byArray), byArray5));
            n2 = object.getBlockSize();
            byte[] byArray6 = new byte[n2];
            int n5 = 0;
            while (n5 + n2 <= byArray3.length) {
                object.processBlock(byArray6, 0, byArray3, n5);
                n5 += n2;
            }
            if (n5 < byArray3.length) {
                object.processBlock(byArray6, 0, byArray6, 0);
                int n6 = byArray3.length - n5;
                System.arraycopy(byArray6, 0, byArray3, n5, n6);
            }
        }
        if ((this.lsq & 1) == 0) {
            mapGroup1.decode(byArray3, n3 - byArray2.length >> 1, this.l >= 4);
        } else {
            byte[] byArray7 = new byte[n3 - byArray2.length];
            GF16.decode(byArray3, byArray7, byArray7.length);
            mapGroup1.fill(byArray7, this.l >= 4);
        }
        if (this.l >= 4) {
            GF16.decode(byArray3, n3 - byArray2.length >> 1, byArray2, 0, byArray2.length);
            int n7 = 0;
            int n8 = this.m * this.alpha * this.l;
            for (n2 = 0; n2 < this.m; ++n2) {
                for (n = 0; n < this.alpha; ++n) {
                    this.makeInvertibleByAddingAS(mapGroup1.aAlpha[n2][n], 0);
                    this.makeInvertibleByAddingAS(mapGroup1.bAlpha[n2][n], 0);
                    this.genAFqS(byArray2, n7, mapGroup1.qAlpha1[n2][n], 0);
                    this.genAFqS(byArray2, n8, mapGroup1.qAlpha2[n2][n], 0);
                    n7 += this.l;
                    n8 += this.l;
                }
            }
        } else {
            int n9 = this.o * this.alpha * this.lsq;
            object = fixedAbqSet.get(Integers.valueOf(this.o));
            MapGroup1.fillAlpha((byte[])object, 0, mapGroup1.aAlpha, this.m * n9);
            MapGroup1.fillAlpha((byte[])object, n9, mapGroup1.bAlpha, (this.m - 1) * n9);
            MapGroup1.fillAlpha((byte[])object, n9 * 2, mapGroup1.qAlpha1, (this.m - 2) * n9);
            MapGroup1.fillAlpha((byte[])object, n9 * 3, mapGroup1.qAlpha2, (this.m - 3) * n9);
        }
    }

    public void genMap1T12Map2(SnovaKeyElements snovaKeyElements, byte[] byArray, byte[] byArray2) {
        this.genSeedsAndT12(snovaKeyElements.T12, byArray2);
        this.genABQP(snovaKeyElements.map1, byArray);
        this.genF(snovaKeyElements.map2, snovaKeyElements.map1, snovaKeyElements.T12);
    }
}

