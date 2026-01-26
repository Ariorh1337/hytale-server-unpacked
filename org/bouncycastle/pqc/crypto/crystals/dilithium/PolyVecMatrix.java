/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;
import org.bouncycastle.pqc.crypto.crystals.dilithium.PolyVecK;
import org.bouncycastle.pqc.crypto.crystals.dilithium.PolyVecL;

class PolyVecMatrix {
    private final int dilithiumK;
    private final int dilithiumL;
    private final PolyVecL[] mat;

    public PolyVecMatrix(DilithiumEngine dilithiumEngine) {
        this.dilithiumK = dilithiumEngine.getDilithiumK();
        this.dilithiumL = dilithiumEngine.getDilithiumL();
        this.mat = new PolyVecL[this.dilithiumK];
        for (int i = 0; i < this.dilithiumK; ++i) {
            this.mat[i] = new PolyVecL(dilithiumEngine);
        }
    }

    public void pointwiseMontgomery(PolyVecK polyVecK, PolyVecL polyVecL) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            polyVecK.getVectorIndex(i).pointwiseAccountMontgomery(this.mat[i], polyVecL);
        }
    }

    public void expandMatrix(byte[] byArray) {
        for (int i = 0; i < this.dilithiumK; ++i) {
            for (int j = 0; j < this.dilithiumL; ++j) {
                this.mat[i].getVectorIndex(j).uniformBlocks(byArray, (short)((i << 8) + j));
            }
        }
    }

    private String addString() {
        String string = "[";
        for (int i = 0; i < this.dilithiumK; ++i) {
            string = string + "Outer Matrix " + i + " [";
            string = string + this.mat[i].toString();
            string = i == this.dilithiumK - 1 ? string + "]\n" : string + "],\n";
        }
        string = string + "]\n";
        return string;
    }

    public String toString(String string) {
        return string.concat(": \n" + this.addString());
    }
}

