/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

public class MayoParameters {
    public static final MayoParameters mayo1 = new MayoParameters("MAYO_1", 86, 78, 5, 8, 78, 81, 10, 39, 312, 39, 40, 120159, 24336, 24, 1420, 454, new int[]{8, 1, 1, 0}, 24, 32, 24);
    public static final MayoParameters mayo2 = new MayoParameters("MAYO_2", 81, 64, 4, 17, 64, 69, 4, 32, 544, 32, 34, 66560, 34816, 24, 4912, 186, new int[]{8, 0, 2, 8}, 24, 32, 24);
    public static final MayoParameters mayo3 = new MayoParameters("MAYO_3", 118, 108, 7, 10, 108, 111, 11, 54, 540, 54, 55, 317844, 58320, 32, 2986, 681, new int[]{8, 0, 1, 7}, 32, 48, 32);
    public static final MayoParameters mayo5 = new MayoParameters("MAYO_5", 154, 142, 9, 12, 142, 145, 12, 71, 852, 71, 72, 720863, 120984, 40, 5554, 964, new int[]{4, 0, 8, 1}, 40, 64, 40);
    private final String name;
    private final int n;
    private final int m;
    private final int mVecLimbs;
    private final int o;
    private final int v;
    private final int ACols;
    private final int k;
    private final int mBytes;
    private final int OBytes;
    private final int vBytes;
    private final int rBytes;
    private final int P1Bytes;
    private final int P2Bytes;
    private final int cskBytes;
    private final int cpkBytes;
    private final int sigBytes;
    private final int[] fTail;
    private final int saltBytes;
    private final int digestBytes;
    private static final int pkSeedBytes = 16;
    private final int skSeedBytes;

    private MayoParameters(String string, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11, int n12, int n13, int n14, int n15, int n16, int[] nArray, int n17, int n18, int n19) {
        this.name = string;
        this.n = n;
        this.m = n2;
        this.mVecLimbs = n3;
        this.o = n4;
        this.v = n5;
        this.ACols = n6;
        this.k = n7;
        this.mBytes = n8;
        this.OBytes = n9;
        this.vBytes = n10;
        this.rBytes = n11;
        this.P1Bytes = n12;
        this.P2Bytes = n13;
        this.cskBytes = n14;
        this.cpkBytes = n15;
        this.sigBytes = n16;
        this.fTail = nArray;
        this.saltBytes = n17;
        this.digestBytes = n18;
        this.skSeedBytes = n19;
    }

    public String getName() {
        return this.name;
    }

    public int getN() {
        return this.n;
    }

    public int getM() {
        return this.m;
    }

    public int getMVecLimbs() {
        return this.mVecLimbs;
    }

    public int getO() {
        return this.o;
    }

    public int getV() {
        return this.v;
    }

    public int getACols() {
        return this.ACols;
    }

    public int getK() {
        return this.k;
    }

    public int getMBytes() {
        return this.mBytes;
    }

    public int getOBytes() {
        return this.OBytes;
    }

    public int getVBytes() {
        return this.vBytes;
    }

    public int getRBytes() {
        return this.rBytes;
    }

    public int getP1Bytes() {
        return this.P1Bytes;
    }

    public int getP2Bytes() {
        return this.P2Bytes;
    }

    public int getCskBytes() {
        return this.cskBytes;
    }

    public int getCpkBytes() {
        return this.cpkBytes;
    }

    public int getSigBytes() {
        return this.sigBytes;
    }

    public int[] getFTail() {
        return this.fTail;
    }

    public int getSaltBytes() {
        return this.saltBytes;
    }

    public int getDigestBytes() {
        return this.digestBytes;
    }

    public int getPkSeedBytes() {
        return 16;
    }

    public int getSkSeedBytes() {
        return this.skSeedBytes;
    }

    public int getP1Limbs() {
        return (this.v * (this.v + 1) >> 1) * this.mVecLimbs;
    }

    public int getP2Limbs() {
        return this.v * this.o * this.mVecLimbs;
    }

    public int getP3Limbs() {
        return (this.o * (this.o + 1) >> 1) * this.mVecLimbs;
    }
}

