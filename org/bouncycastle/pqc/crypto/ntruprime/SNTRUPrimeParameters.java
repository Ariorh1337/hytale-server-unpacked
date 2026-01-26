/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.pqc.crypto.KEMParameters;

public class SNTRUPrimeParameters
implements KEMParameters {
    public static final SNTRUPrimeParameters sntrup653 = new SNTRUPrimeParameters("sntrup653", 653, 4621, 288, 994, 865, 994, 1518, 32);
    public static final SNTRUPrimeParameters sntrup761 = new SNTRUPrimeParameters("sntrup761", 761, 4591, 286, 1158, 1007, 1158, 1763, 32);
    public static final SNTRUPrimeParameters sntrup857 = new SNTRUPrimeParameters("sntrup857", 857, 5167, 322, 1322, 1152, 1322, 1999, 32);
    public static final SNTRUPrimeParameters sntrup953 = new SNTRUPrimeParameters("sntrup953", 953, 6343, 396, 1505, 1317, 1505, 2254, 32);
    public static final SNTRUPrimeParameters sntrup1013 = new SNTRUPrimeParameters("sntrup1013", 1013, 7177, 448, 1623, 1423, 1623, 2417, 32);
    public static final SNTRUPrimeParameters sntrup1277 = new SNTRUPrimeParameters("sntrup1277", 1277, 7879, 492, 2067, 1815, 2067, 3059, 32);
    private final String name;
    private final int p;
    private final int q;
    private final int w;
    private final int rqPolynomialBytes;
    private final int roundedPolynomialBytes;
    private final int publicKeyBytes;
    private final int privateKeyBytes;
    private final int sharedKeyBytes;

    private SNTRUPrimeParameters(String string, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        this.name = string;
        this.p = n;
        this.q = n2;
        this.w = n3;
        this.rqPolynomialBytes = n4;
        this.roundedPolynomialBytes = n5;
        this.publicKeyBytes = n6;
        this.privateKeyBytes = n7;
        this.sharedKeyBytes = n8;
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

    public int getPublicKeyBytes() {
        return this.publicKeyBytes;
    }

    public int getPrivateKeyBytes() {
        return this.privateKeyBytes;
    }

    public int getRqPolynomialBytes() {
        return this.rqPolynomialBytes;
    }

    public int getRoundedPolynomialBytes() {
        return this.roundedPolynomialBytes;
    }

    public int getSessionKeySize() {
        return this.sharedKeyBytes * 8;
    }
}

