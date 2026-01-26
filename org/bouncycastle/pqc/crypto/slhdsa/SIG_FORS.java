/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

class SIG_FORS {
    final byte[][] authPath;
    final byte[] sk;

    SIG_FORS(byte[] byArray, byte[][] byArray2) {
        this.authPath = byArray2;
        this.sk = byArray;
    }

    byte[] getSK() {
        return this.sk;
    }

    public byte[][] getAuthPath() {
        return this.authPath;
    }
}

