/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

class SK {
    final byte[] seed;
    final byte[] prf;

    SK(byte[] byArray, byte[] byArray2) {
        this.seed = byArray;
        this.prf = byArray2;
    }
}

