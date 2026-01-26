/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

class SIG_XMSS {
    final byte[] sig;
    final byte[][] auth;

    public SIG_XMSS(byte[] byArray, byte[][] byArray2) {
        this.sig = byArray;
        this.auth = byArray2;
    }

    public byte[] getWOTSSig() {
        return this.sig;
    }

    public byte[][] getXMSSAUTH() {
        return this.auth;
    }
}

