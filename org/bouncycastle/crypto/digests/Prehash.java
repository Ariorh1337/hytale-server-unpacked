/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.io.LimitedBuffer;

public class Prehash
implements Digest {
    private final String algorithmName;
    private final LimitedBuffer buf;

    public static Prehash forDigest(Digest digest) {
        return new Prehash(digest);
    }

    private Prehash(Digest digest) {
        this.algorithmName = digest.getAlgorithmName();
        this.buf = new LimitedBuffer(digest.getDigestSize());
    }

    @Override
    public String getAlgorithmName() {
        return this.algorithmName;
    }

    @Override
    public int getDigestSize() {
        return this.buf.limit();
    }

    @Override
    public void update(byte by) {
        this.buf.write(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.buf.write(byArray, n, n2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int doFinal(byte[] byArray, int n) {
        try {
            if (this.getDigestSize() != this.buf.size()) {
                throw new IllegalStateException("Incorrect prehash size");
            }
            int n2 = this.buf.copyTo(byArray, n);
            return n2;
        }
        finally {
            this.reset();
        }
    }

    @Override
    public void reset() {
        this.buf.reset();
    }
}

