/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;

public class NonMemoableDigest
implements ExtendedDigest {
    private ExtendedDigest baseDigest;

    public NonMemoableDigest(ExtendedDigest extendedDigest) {
        if (extendedDigest == null) {
            throw new IllegalArgumentException("baseDigest must not be null");
        }
        this.baseDigest = extendedDigest;
    }

    @Override
    public String getAlgorithmName() {
        return this.baseDigest.getAlgorithmName();
    }

    @Override
    public int getDigestSize() {
        return this.baseDigest.getDigestSize();
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
        return this.baseDigest.doFinal(byArray, n);
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

