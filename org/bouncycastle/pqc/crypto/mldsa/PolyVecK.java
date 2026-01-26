/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.pqc.crypto.mldsa.MLDSAEngine;
import org.bouncycastle.pqc.crypto.mldsa.Poly;

class PolyVecK {
    private final Poly[] vec;

    PolyVecK(MLDSAEngine mLDSAEngine) {
        int n = mLDSAEngine.getDilithiumK();
        this.vec = new Poly[n];
        for (int i = 0; i < n; ++i) {
            this.vec[i] = new Poly(mLDSAEngine);
        }
    }

    Poly getVectorIndex(int n) {
        return this.vec[n];
    }

    void setVectorIndex(int n, Poly poly) {
        this.vec[n] = poly;
    }

    public void uniformEta(byte[] byArray, short s) {
        short s2 = s;
        for (int i = 0; i < this.vec.length; ++i) {
            short s3 = s2;
            s2 = (short)(s2 + 1);
            this.vec[i].uniformEta(byArray, s3);
        }
    }

    public void reduce() {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).reduce();
        }
    }

    public void invNttToMont() {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).invNttToMont();
        }
    }

    public void addPolyVecK(PolyVecK polyVecK) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).addPoly(polyVecK.getVectorIndex(i));
        }
    }

    public void conditionalAddQ() {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).conditionalAddQ();
        }
    }

    public void power2Round(PolyVecK polyVecK) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).power2Round(polyVecK.getVectorIndex(i));
        }
    }

    public void polyVecNtt() {
        for (int i = 0; i < this.vec.length; ++i) {
            this.vec[i].polyNtt();
        }
    }

    public void decompose(PolyVecK polyVecK) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).decompose(polyVecK.getVectorIndex(i));
        }
    }

    public void packW1(MLDSAEngine mLDSAEngine, byte[] byArray, int n) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).packW1(byArray, n + i * mLDSAEngine.getDilithiumPolyW1PackedBytes());
        }
    }

    public void pointwisePolyMontgomery(Poly poly, PolyVecK polyVecK) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).pointwiseMontgomery(poly, polyVecK.getVectorIndex(i));
        }
    }

    public void subtract(PolyVecK polyVecK) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).subtract(polyVecK.getVectorIndex(i));
        }
    }

    public boolean checkNorm(int n) {
        for (int i = 0; i < this.vec.length; ++i) {
            if (!this.getVectorIndex(i).checkNorm(n)) continue;
            return true;
        }
        return false;
    }

    public int makeHint(PolyVecK polyVecK, PolyVecK polyVecK2) {
        int n = 0;
        for (int i = 0; i < this.vec.length; ++i) {
            n += this.getVectorIndex(i).polyMakeHint(polyVecK.getVectorIndex(i), polyVecK2.getVectorIndex(i));
        }
        return n;
    }

    public void useHint(PolyVecK polyVecK, PolyVecK polyVecK2) {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).polyUseHint(polyVecK.getVectorIndex(i), polyVecK2.getVectorIndex(i));
        }
    }

    public void shiftLeft() {
        for (int i = 0; i < this.vec.length; ++i) {
            this.getVectorIndex(i).shiftLeft();
        }
    }

    public String toString() {
        String string = "[";
        for (int i = 0; i < this.vec.length; ++i) {
            string = string + i + " " + this.getVectorIndex(i).toString();
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

