/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.util.GF16;

class MapGroup1 {
    public final byte[][][][] p11;
    public final byte[][][][] p12;
    public final byte[][][][] p21;
    public final byte[][][] aAlpha;
    public final byte[][][] bAlpha;
    public final byte[][][] qAlpha1;
    public final byte[][][] qAlpha2;

    public MapGroup1(SnovaParameters snovaParameters) {
        int n = snovaParameters.getM();
        int n2 = snovaParameters.getV();
        int n3 = snovaParameters.getO();
        int n4 = snovaParameters.getAlpha();
        int n5 = snovaParameters.getLsq();
        this.p11 = new byte[n][n2][n2][n5];
        this.p12 = new byte[n][n2][n3][n5];
        this.p21 = new byte[n][n3][n2][n5];
        this.aAlpha = new byte[n][n4][n5];
        this.bAlpha = new byte[n][n4][n5];
        this.qAlpha1 = new byte[n][n4][n5];
        this.qAlpha2 = new byte[n][n4][n5];
    }

    void decode(byte[] byArray, int n, boolean bl) {
        int n2 = MapGroup1.decodeP(byArray, 0, this.p11, n);
        n2 += MapGroup1.decodeP(byArray, n2, this.p12, n - n2);
        n2 += MapGroup1.decodeP(byArray, n2, this.p21, n - n2);
        if (bl) {
            n2 += MapGroup1.decodeAlpha(byArray, n2, this.aAlpha, n - n2);
            n2 += MapGroup1.decodeAlpha(byArray, n2, this.bAlpha, n - n2);
            n2 += MapGroup1.decodeAlpha(byArray, n2, this.qAlpha1, n - n2);
            MapGroup1.decodeAlpha(byArray, n2, this.qAlpha2, n - n2);
        }
    }

    static int decodeP(byte[] byArray, int n, byte[][][][] byArray2, int n2) {
        int n3 = 0;
        for (int i = 0; i < byArray2.length; ++i) {
            n3 += MapGroup1.decodeAlpha(byArray, n + n3, byArray2[i], n2);
        }
        return n3;
    }

    private static int decodeAlpha(byte[] byArray, int n, byte[][][] byArray2, int n2) {
        int n3 = 0;
        for (int i = 0; i < byArray2.length; ++i) {
            n3 += MapGroup1.decodeArray(byArray, n + n3, byArray2[i], n2 - n3);
        }
        return n3;
    }

    static int decodeArray(byte[] byArray, int n, byte[][] byArray2, int n2) {
        int n3 = 0;
        for (int i = 0; i < byArray2.length; ++i) {
            int n4 = Math.min(byArray2[i].length, n2 << 1);
            GF16.decode(byArray, n + n3, byArray2[i], 0, n4);
            n4 = n4 + 1 >> 1;
            n3 += n4;
            n2 -= n4;
        }
        return n3;
    }

    void fill(byte[] byArray, boolean bl) {
        int n = MapGroup1.fillP(byArray, 0, this.p11, byArray.length);
        n += MapGroup1.fillP(byArray, n, this.p12, byArray.length - n);
        n += MapGroup1.fillP(byArray, n, this.p21, byArray.length - n);
        if (bl) {
            n += MapGroup1.fillAlpha(byArray, n, this.aAlpha, byArray.length - n);
            n += MapGroup1.fillAlpha(byArray, n, this.bAlpha, byArray.length - n);
            n += MapGroup1.fillAlpha(byArray, n, this.qAlpha1, byArray.length - n);
            MapGroup1.fillAlpha(byArray, n, this.qAlpha2, byArray.length - n);
        }
    }

    static int fillP(byte[] byArray, int n, byte[][][][] byArray2, int n2) {
        int n3 = 0;
        for (int i = 0; i < byArray2.length; ++i) {
            n3 += MapGroup1.fillAlpha(byArray, n + n3, byArray2[i], n2 - n3);
        }
        return n3;
    }

    static int fillAlpha(byte[] byArray, int n, byte[][][] byArray2, int n2) {
        int n3 = 0;
        for (int i = 0; i < byArray2.length; ++i) {
            for (int j = 0; j < byArray2[i].length; ++j) {
                int n4 = Math.min(byArray2[i][j].length, n2 - n3);
                System.arraycopy(byArray, n + n3, byArray2[i][j], 0, n4);
                n3 += n4;
            }
        }
        return n3;
    }
}

