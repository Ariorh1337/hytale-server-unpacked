/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.pqc.crypto.mldsa.MLDSAEngine;
import org.bouncycastle.pqc.crypto.mldsa.Poly;

class PolyVecL {
    private final Poly[] vec;

    PolyVecL(MLDSAEngine mLDSAEngine) {
        int n = mLDSAEngine.getDilithiumL();
        this.vec = new Poly[n];
        for (int i = 0; i < n; ++i) {
            this.vec[i] = new Poly(mLDSAEngine);
        }
    }

    public PolyVecL() throws Exception {
        throw new Exception("Requires Parameter");
    }

    public Poly getVectorIndex(int n) {
        return this.vec[n];
    }

    void uniformBlocks(byte[] byArray, int n) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.vec[i].uniformBlocks(byArray, (short)(n + i));
        }
    }

    public void uniformEta(byte[] byArray, short s) {
        short s2 = s;
        for (int i = 0; i < this.vec.length; ++i) {
            short s3 = s2;
            s2 = (short)(s2 + 1);
            this.getVectorIndex(i).uniformEta(byArray, s3);
        }
    }

    void copyTo(PolyVecL polyVecL) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.vec[i].copyTo(polyVecL.vec[i]);
        }
    }

    public void polyVecNtt() {
        for (int i = 0; i < this.vec.length; ++i) {
            this.vec[i].polyNtt();
        }
    }

    public void uniformGamma1(byte[] byArray, short s) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).uniformGamma1(byArray, (short)(this.vec.length * s + i));
        }
    }

    public void pointwisePolyMontgomery(Poly poly, PolyVecL polyVecL) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).pointwiseMontgomery(poly, polyVecL.getVectorIndex(i));
        }
    }

    public void invNttToMont() {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).invNttToMont();
        }
    }

    public void addPolyVecL(PolyVecL polyVecL) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).addPoly(polyVecL.getVectorIndex(i));
        }
    }

    public void reduce() {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).reduce();
        }
    }

    public boolean checkNorm(int n) {
        for (int i = 0; i < this.vec.length; ++i) {
            if (!this.getVectorIndex(i).checkNorm(n)) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        String string = "\n[";
        for (int i = 0; i < this.vec.length; ++i) {
            string = string + "Inner Matrix " + i + " " + this.getVectorIndex(i).toString();
            if (i == this.vec.length - 1) continue;
            string = string + ",\n";
        }
        string = string + "]";
        return string;
    }

    public String toString(String string) {
        return string + ": " + this.toString();
    }
}

