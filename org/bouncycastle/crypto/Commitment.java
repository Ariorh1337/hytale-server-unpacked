/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

public class Commitment {
    private final byte[] secret;
    private final byte[] commitment;

    public Commitment(byte[] byArray, byte[] byArray2) {
        this.secret = byArray;
        this.commitment = byArray2;
    }

    public byte[] getSecret() {
        return this.secret;
    }

    public byte[] getCommitment() {
        return this.commitment;
    }
}

