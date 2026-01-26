/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.crypto.digests.SHAKEDigest;

class Shake256RandomGenerator {
    private final SHAKEDigest digest = new SHAKEDigest(256);

    public Shake256RandomGenerator(byte[] byArray, byte by) {
        this.digest.update(byArray, 0, byArray.length);
        this.digest.update(by);
    }

    public Shake256RandomGenerator(byte[] byArray, int n, int n2, byte by) {
        this.digest.update(byArray, n, n2);
        this.digest.update(by);
    }

    public void init(byte[] byArray, int n, int n2, byte by) {
        this.digest.reset();
        this.digest.update(byArray, n, n2);
        this.digest.update(by);
    }

    public void nextBytes(byte[] byArray) {
        this.digest.doOutput(byArray, 0, byArray.length);
    }

    public void nextBytes(byte[] byArray, int n, int n2) {
        this.digest.doOutput(byArray, n, n2);
    }

    public void xofGetBytes(byte[] byArray, int n) {
        int n2 = n & 7;
        int n3 = n - n2;
        this.digest.doOutput(byArray, 0, n3);
        if (n2 != 0) {
            byte[] byArray2 = new byte[8];
            this.digest.doOutput(byArray2, 0, 8);
            System.arraycopy(byArray2, 0, byArray, n3, n2);
        }
    }
}

