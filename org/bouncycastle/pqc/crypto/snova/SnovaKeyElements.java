/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.pqc.crypto.snova.MapGroup1;
import org.bouncycastle.pqc.crypto.snova.MapGroup2;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;

class SnovaKeyElements {
    public final MapGroup1 map1;
    public final byte[][][] T12;
    public final MapGroup2 map2;

    public SnovaKeyElements(SnovaParameters snovaParameters) {
        int n = snovaParameters.getO();
        int n2 = snovaParameters.getV();
        int n3 = snovaParameters.getLsq();
        this.map1 = new MapGroup1(snovaParameters);
        this.T12 = new byte[n2][n][n3];
        this.map2 = new MapGroup2(snovaParameters);
    }

    static int copy3d(byte[][][] byArray, byte[] byArray2, int n) {
        for (int i = 0; i < byArray.length; ++i) {
            for (int j = 0; j < byArray[i].length; ++j) {
                System.arraycopy(byArray[i][j], 0, byArray2, n, byArray[i][j].length);
                n += byArray[i][j].length;
            }
        }
        return n;
    }

    static int copy4d(byte[][][][] byArray, byte[] byArray2, int n) {
        for (int i = 0; i < byArray.length; ++i) {
            n = SnovaKeyElements.copy3d(byArray[i], byArray2, n);
        }
        return n;
    }

    static int copy3d(byte[] byArray, int n, byte[][][] byArray2) {
        for (int i = 0; i < byArray2.length; ++i) {
            for (int j = 0; j < byArray2[i].length; ++j) {
                System.arraycopy(byArray, n, byArray2[i][j], 0, byArray2[i][j].length);
                n += byArray2[i][j].length;
            }
        }
        return n;
    }

    static int copy4d(byte[] byArray, int n, byte[][][][] byArray2) {
        for (int i = 0; i < byArray2.length; ++i) {
            for (int j = 0; j < byArray2[i].length; ++j) {
                for (int k = 0; k < byArray2[i][j].length; ++k) {
                    System.arraycopy(byArray, n, byArray2[i][j][k], 0, byArray2[i][j][k].length);
                    n += byArray2[i][j][k].length;
                }
            }
        }
        return n;
    }
}

