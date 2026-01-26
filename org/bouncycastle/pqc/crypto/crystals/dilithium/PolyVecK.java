/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Poly;

class PolyVecK {
    Poly[] vec;
    private DilithiumEngine engine;
    private int mode;
    private int polyVecBytes;
    private int dilithiumK;
    private int dilithiumL;

    public PolyVecK(DilithiumEngine dilithiumEngine) {
        this.engine = dilithiumEngine;
        this.mode = dilithiumEngine.getDilithiumMode();
        this.dilithiumK = dilithiumEngine.getDilithiumK();
        this.dilithiumL = dilithiumEngine.getDilithiumL();
        this.vec = new Poly[this.dilithiumK];
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.vec[i] = new Poly(dilithiumEngine);
        }
    }

    public PolyVecK() throws Exception {
        throw new Exception("Requires Parameter");
    }

    public Poly getVectorIndex(int n) {
        return this.vec[n];
    }

    public void setVectorIndex(int n, Poly poly) {
        this.vec[n] = poly;
    }

    public void uniformEta(byte[] byArray, short s) {
        short s2 = s;
        for (int i = 0; i < this.dilithiumK; ++i) {
            short s3 = s2;
            s2 = (short)(s2 + 1);
            this.getVectorIndex(i).uniformEta(byArray, s3);
        }
    }

    public void reduce() {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).reduce();
        }
    }

    public void invNttToMont() {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).invNttToMont();
        }
    }

    public void addPolyVecK(PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).addPoly(polyVecK.getVectorIndex(i));
        }
    }

    public void conditionalAddQ() {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).conditionalAddQ();
        }
    }

    public void power2Round(PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).power2Round(polyVecK.getVectorIndex(i));
        }
    }

    public void polyVecNtt() {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.vec[i].polyNtt();
        }
    }

    public void decompose(PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).decompose(polyVecK.getVectorIndex(i));
        }
    }

    public byte[] packW1() {
        byte[] byArray = new byte[this.dilithiumK * this.engine.getDilithiumPolyW1PackedBytes()];
        for (int i = 0; i < this.dilithiumK; ++i) {
            System.arraycopy(this.getVectorIndex(i).w1Pack(), 0, byArray, i * this.engine.getDilithiumPolyW1PackedBytes(), this.engine.getDilithiumPolyW1PackedBytes());
        }
        return byArray;
    }

    public void pointwisePolyMontgomery(Poly poly, PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).pointwiseMontgomery(poly, polyVecK.getVectorIndex(i));
        }
    }

    public void subtract(PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).subtract(polyVecK.getVectorIndex(i));
        }
    }

    public boolean checkNorm(int n) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            if (!this.getVectorIndex(i).checkNorm(n)) continue;
            return true;
        }
        return false;
    }

    public int makeHint(PolyVecK polyVecK, PolyVecK polyVecK2) {
        int n = 0;
        for (int i = 0; i < this.dilithiumK; ++i) {
            n += this.getVectorIndex(i).polyMakeHint(polyVecK.getVectorIndex(i), polyVecK2.getVectorIndex(i));
        }
        return n;
    }

    public void useHint(PolyVecK polyVecK, PolyVecK polyVecK2) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).polyUseHint(polyVecK.getVectorIndex(i), polyVecK2.getVectorIndex(i));
        }
    }

    public void shiftLeft() {
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.getVectorIndex(i).shiftLeft();
        }
    }

    public String toString() {
        String string = "[";
        for (int i = 0; i < this.dilithiumK; ++i) {
            string = string + i + " " + this.getVectorIndex(i).toString();
            if (i == this.dilithiumK - 1) continue;
            string = string + ",\n";
        }
        string = string + "]";
        return string;
    }

    public String toString(String string) {
        return string + ": " + this.toString();
    }
}

