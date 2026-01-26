/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

class OWCPAKeyPair {
    public final byte[] publicKey;
    public final byte[] privateKey;

    public OWCPAKeyPair(byte[] byArray, byte[] byArray2) {
        this.publicKey = byArray;
        this.privateKey = byArray2;
    }
}

