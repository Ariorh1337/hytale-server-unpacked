/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.pqc.crypto.KEMParameters;

public class NTRULPRimeParameters
implements KEMParameters {
    public static final NTRULPRimeParameters ntrulpr653 = new NTRULPRimeParameters("ntrulpr653", 653, 4621, 252, 289, 2175, 113, 2031, 290, 865, 897, 1125, 32);
    public static final NTRULPRimeParameters ntrulpr761 = new NTRULPRimeParameters("ntrulpr761", 761, 4591, 250, 292, 2156, 114, 2007, 287, 1007, 1039, 1294, 32);
    public static final NTRULPRimeParameters ntrulpr857 = new NTRULPRimeParameters("ntrulpr857", 857, 5167, 281, 329, 2433, 101, 2265, 324, 1152, 1184, 1463, 32);
    public static final NTRULPRimeParameters ntrulpr953 = new NTRULPRimeParameters("ntrulpr953", 953, 6343, 345, 404, 2997, 82, 2798, 400, 1317, 1349, 1652, 32);
    public static final NTRULPRimeParameters ntrulpr1013 = new NTRULPRimeParameters("ntrulpr1013", 1013, 7177, 392, 450, 3367, 73, 3143, 449, 1423, 1455, 1773, 32);
    public static final NTRULPRimeParameters ntrulpr1277 = new NTRULPRimeParameters("ntrulpr1277", 1277, 7879, 429, 502, 3724, 66, 3469, 496, 1815, 1847, 2231, 32);
    private final String name;
    private final int p;
    private final int q;
    private final int w;
    private final int delta;
    private final int tau0;
    private final int tau1;
    private final int tau2;
    private final int tau3;
    private final int roundedPolynomialBytes;
    private final int publicKeyBytes;
    private final int privateKeyBytes;
    private final int sharedKeyBytes;

    private NTRULPRimeParameters(String string, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11, int n12) {
        this.name = string;
        this.p = n;
        this.q = n2;
        this.w = n3;
        this.delta = n4;
        this.tau0 = n5;
        this.tau1 = n6;
        this.tau2 = n7;
        this.tau3 = n8;
        this.roundedPolynomialBytes = n9;
        this.publicKeyBytes = n10;
        this.privateKeyBytes = n11;
        this.sharedKeyBytes = n12;
    }

    public String getName() {
        return this.name;
    }

    public int getP() {
        return this.p;
    }

    public int getQ() {
        return this.q;
    }

    public int getW() {
        return this.w;
    }

    public int getDelta() {
        return this.delta;
    }

    public int getTau0() {
        return this.tau0;
    }

    public int getTau1() {
        return this.tau1;
    }

    public int getTau2() {
        return this.tau2;
    }

    public int getTau3() {
        return this.tau3;
    }

    public int getPublicKeyBytes() {
        return this.publicKeyBytes;
    }

    public int getPrivateKeyBytes() {
        return this.privateKeyBytes;
    }

    public int getRoundedPolynomialBytes() {
        return this.roundedPolynomialBytes;
    }

    public int getSessionKeySize() {
        return this.sharedKeyBytes * 8;
    }
}

