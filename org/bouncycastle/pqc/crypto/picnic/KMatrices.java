/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

class KMatrices {
    private int nmatrices;
    private int rows;
    private int columns;
    private int[] data;

    public KMatrices(int n, int n2, int n3, int[] nArray) {
        this.nmatrices = n;
        this.rows = n2;
        this.columns = n3;
        this.data = nArray;
    }

    public int getNmatrices() {
        return this.nmatrices;
    }

    public int getSize() {
        return this.rows * this.columns;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public int[] getData() {
        return this.data;
    }
}

