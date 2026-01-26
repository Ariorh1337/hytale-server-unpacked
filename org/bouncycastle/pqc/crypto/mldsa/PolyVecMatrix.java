/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.pqc.crypto.mldsa.MLDSAEngine;
import org.bouncycastle.pqc.crypto.mldsa.PolyVecK;
import org.bouncycastle.pqc.crypto.mldsa.PolyVecL;

class PolyVecMatrix {
    private final PolyVecL[] matrix;

    PolyVecMatrix(MLDSAEngine mLDSAEngine) {
        int n = mLDSAEngine.getDilithiumK();
        this.matrix = new PolyVecL[n];
        for (int i = 0; i < n; ++i) {
            this.matrix[i] = new PolyVecL(mLDSAEngine);
        }
    }

    public void pointwiseMontgomery(PolyVecK polyVecK, PolyVecL polyVecL) {
        for (int i = 0; i < this.matrix.length; ++i) {
            polyVecK.getVectorIndex(i).pointwiseAccountMontgomery(this.matrix[i], polyVecL);
        }
    }

    public void expandMatrix(byte[] byArray) {
        for (int i = 0; i < this.matrix.length; ++i) {
            this.matrix[i].uniformBlocks(byArray, i << 8);
        }
    }

    private String addString() {
        String string = "[";
        for (int i = 0; i < this.matrix.length; ++i) {
            string = string + "Outer Matrix " + i + " [";
            string = string + this.matrix[i].toString();
            string = i == this.matrix.length - 1 ? string + "]\n" : string + "],\n";
        }
        string = string + "]\n";
        return string;
    }

    public String toString(String string) {
        return string.concat(": \n" + this.addString());
    }
}

