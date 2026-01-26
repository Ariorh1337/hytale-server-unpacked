/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.pqc.crypto.picnic.KMatricesWithPointer;
import org.bouncycastle.pqc.crypto.picnic.PicnicEngine;
import org.bouncycastle.pqc.crypto.picnic.Utils;
import org.bouncycastle.util.Pack;

class Tape {
    byte[][] tapes;
    int pos;
    int nTapes;
    private PicnicEngine engine;

    public Tape(PicnicEngine picnicEngine) {
        this.engine = picnicEngine;
        this.tapes = new byte[picnicEngine.numMPCParties][2 * picnicEngine.andSizeBytes];
        this.pos = 0;
        this.nTapes = picnicEngine.numMPCParties;
    }

    protected void setAuxBits(byte[] byArray) {
        int n = this.engine.numMPCParties - 1;
        int n2 = 0;
        int n3 = this.engine.stateSizeBits;
        for (int i = 0; i < this.engine.numRounds; ++i) {
            for (int j = 0; j < n3; ++j) {
                Utils.setBit(this.tapes[n], n3 + n3 * 2 * i + j, Utils.getBit(byArray, n2++));
            }
        }
    }

    protected void computeAuxTape(byte[] byArray) {
        int[] nArray = new int[16];
        int[] nArray2 = new int[16];
        int[] nArray3 = new int[16];
        int[] nArray4 = new int[16];
        int[] nArray5 = new int[16];
        nArray5[this.engine.stateSizeWords - 1] = 0;
        this.tapesToParityBits(nArray5, this.engine.stateSizeBits);
        KMatricesWithPointer kMatricesWithPointer = this.engine.lowmcConstants.KMatrixInv(this.engine);
        this.engine.matrix_mul(nArray4, nArray5, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
        if (byArray != null) {
            Pack.intToLittleEndian(nArray4, 0, this.engine.stateSizeWords, byArray, 0);
        }
        for (int i = this.engine.numRounds; i > 0; --i) {
            kMatricesWithPointer = this.engine.lowmcConstants.KMatrix(this.engine, i);
            this.engine.matrix_mul(nArray, nArray4, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            this.engine.xor_array(nArray2, nArray2, nArray, 0);
            kMatricesWithPointer = this.engine.lowmcConstants.LMatrixInv(this.engine, i - 1);
            this.engine.matrix_mul(nArray3, nArray2, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            if (i == 1) {
                System.arraycopy(nArray5, 0, nArray2, 0, nArray5.length);
            } else {
                this.pos = this.engine.stateSizeBits * 2 * (i - 1);
                this.tapesToParityBits(nArray2, this.engine.stateSizeBits);
            }
            this.pos = this.engine.stateSizeBits * 2 * (i - 1) + this.engine.stateSizeBits;
            this.engine.aux_mpc_sbox(nArray2, nArray3, this);
        }
        this.pos = 0;
    }

    private void tapesToParityBits(int[] nArray, int n) {
        for (int i = 0; i < n; ++i) {
            Utils.setBitInWordArray(nArray, i, Utils.parity16(this.tapesToWord()));
        }
    }

    protected int tapesToWord() {
        int n = 0;
        int n2 = this.pos >>> 3;
        int n3 = this.pos & 7 ^ 7;
        int n4 = 1 << n3;
        n |= (this.tapes[0][n2] & n4) << 7;
        n |= (this.tapes[1][n2] & n4) << 6;
        n |= (this.tapes[2][n2] & n4) << 5;
        n |= (this.tapes[3][n2] & n4) << 4;
        n |= (this.tapes[4][n2] & n4) << 3;
        n |= (this.tapes[5][n2] & n4) << 2;
        n |= (this.tapes[6][n2] & n4) << 1;
        n |= (this.tapes[7][n2] & n4) << 0;
        n |= (this.tapes[8][n2] & n4) << 15;
        n |= (this.tapes[9][n2] & n4) << 14;
        n |= (this.tapes[10][n2] & n4) << 13;
        n |= (this.tapes[11][n2] & n4) << 12;
        n |= (this.tapes[12][n2] & n4) << 11;
        n |= (this.tapes[13][n2] & n4) << 10;
        n |= (this.tapes[14][n2] & n4) << 9;
        ++this.pos;
        return (n |= (this.tapes[15][n2] & n4) << 8) >>> n3;
    }
}

