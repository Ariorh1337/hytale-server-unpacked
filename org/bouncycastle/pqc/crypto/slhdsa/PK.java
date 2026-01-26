/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

class PK {
    final byte[] seed;
    final byte[] root;

    PK(byte[] byArray, byte[] byArray2) {
        this.seed = byArray;
        this.root = byArray2;
    }
}

