/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.pqc.crypto.picnic.KMatrices;

class KMatricesWithPointer
extends KMatrices {
    private int matrixPointer = 0;

    public KMatricesWithPointer(KMatrices kMatrices) {
        super(kMatrices.getNmatrices(), kMatrices.getRows(), kMatrices.getColumns(), kMatrices.getData());
    }

    public int getMatrixPointer() {
        return this.matrixPointer;
    }

    public void setMatrixPointer(int n) {
        this.matrixPointer = n;
    }
}

