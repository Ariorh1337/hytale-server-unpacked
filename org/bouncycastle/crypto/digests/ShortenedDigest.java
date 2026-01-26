/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;

public class ShortenedDigest
implements ExtendedDigest {
    private ExtendedDigest baseDigest;
    private int length;

    public ShortenedDigest(ExtendedDigest extendedDigest, int n) {
        if (extendedDigest == null) {
            throw new IllegalArgumentException("baseDigest must not be null");
        }
        if (n > extendedDigest.getDigestSize()) {
            throw new IllegalArgumentException("baseDigest output not large enough to support length");
        }
        this.baseDigest = extendedDigest;
        this.length = n;
    }

    @Override
    public String getAlgorithmName() {
        return this.baseDigest.getAlgorithmName() + "(" + this.length * 8 + ")";
    }

    @Override
    public int getDigestSize() {
        return this.length;
    }

    @Override
    public void update(byte by) {
        this.baseDigest.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.baseDigest.update(byArray, n, n2);
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        byte[] byArray2 = new byte[this.baseDigest.getDigestSize()];
        this.baseDigest.doFinal(byArray2, 0);
        System.arraycopy(byArray2, 0, byArray, n, this.length);
        return this.length;
    }

    @Override
    public void reset() {
        this.baseDigest.reset();
    }

    @Override
    public int getByteLength() {
        return this.baseDigest.getByteLength();
    }
}

