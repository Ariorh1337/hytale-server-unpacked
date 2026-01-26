/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Poly;

class PolyVecL {
    Poly[] vec;
    private DilithiumEngine engine;
    private int mode;
    private int polyVecBytes;
    private int dilithiumL;
    private int dilithiumK;

    public PolyVecL(DilithiumEngine dilithiumEngine) {
        this.engine = dilithiumEngine;
        this.mode = dilithiumEngine.getDilithiumMode();
        this.dilithiumL = dilithiumEngine.getDilithiumL();
        this.dilithiumK = dilithiumEngine.getDilithiumK();
        this.vec = new Poly[this.dilithiumL];
        for (int i = 0; i < this.dilithiumL; ++i) {
            this.vec[i] = new Poly(dilithiumEngine);
        }
    }

    public PolyVecL() throws Exception {
        throw new Exception("Requires Parameter");
    }

    public Poly getVectorIndex(int n) {
        return this.vec[n];
    }

    public void expandMatrix(byte[] byArray, int n) {
        for (int i = 0; i < this.dilithiumL; ++i) {
            this.vec[i].uniformBlocks(byArray, (short)((n << 8) + i));
        }
    }

    public void uniformEta(byte[] byArray, short s) {
        short s2 = s;
        for (int i = 0; i < this.dilithiumL; ++i) {
            short s3 = s2;
            s2 = (short)(s2 + 1);
            this.getVectorIndex(i).uniformEta(byArray, s3);
        }
    }

    public void copyPolyVecL(PolyVecL polyVecL) {
        for (int i = 0; i < this.dilithiumL; ++i) {
            for (int j = 0; j < 256; ++j) {
                polyVecL.getVectorIndex(i).setCoeffIndex(j, this.getVectorIndex(i).getCoeffIndex(j));
            }
        }
    }

    public void polyVecNtt() {
        for (int i = 0; i < this.dilithiumL; ++i) {
            this.vec[i].polyNtt();
        }
    }

    public void uniformGamma1(byte[] byArray, short s) {
        for (int i = 0; i < this.dilithiumL; ++i) {
            this.getVectorIndex(i).uniformGamma1(byArray, (short)(this.dilithiumL * s + i));
        }
    }

    public void pointwisePolyMontgomery(Poly poly, PolyVecL polyVecL) {
        for (int i = 0; i < this.dilithiumL; ++i) {
            this.getVectorIndex(i).pointwiseMontgomery(poly, polyVecL.getVectorIndex(i));
        }
    }

    public void invNttToMont() {
        for (int i = 0; i < this.dilithiumL; ++i) {
            this.getVectorIndex(i).invNttToMont();
        }
    }

    public void addPolyVecL(PolyVecL polyVecL) {
        for (int i = 0; i < this.dilithiumL; ++i) {
            this.getVectorIndex(i).addPoly(polyVecL.getVectorIndex(i));
        }
    }

    public void reduce() {
        for (int i = 0; i < this.dilithiumL; ++i) {
            this.getVectorIndex(i).reduce();
        }
    }

    public boolean checkNorm(int n) {
        for (int i = 0; i < this.dilithiumL; ++i) {
            if (!this.getVectorIndex(i).checkNorm(n)) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        String string = "\n[";
        for (int i = 0; i < this.dilithiumL; ++i) {
            string = string + "Inner Matrix " + i + " " + this.getVectorIndex(i).toString();
            if (i == this.dilithiumL - 1) continue;
            string = string + ",\n";
        }
        string = string + "]";
        return string;
    }

    public String toString(String string) {
        return string + ": " + this.toString();
    }
}

