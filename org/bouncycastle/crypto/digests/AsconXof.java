/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.AsconXofBase;
import org.bouncycastle.util.Pack;

public class AsconXof
extends AsconXofBase {
    AsconParameters asconParameters;

    public AsconXof(AsconParameters asconParameters) {
        this.BlockSize = 8;
        this.asconParameters = asconParameters;
        switch (asconParameters.ordinal()) {
            case 0: {
                this.ASCON_PB_ROUNDS = 12;
                this.algorithmName = "Ascon-Xof";
                break;
            }
            case 1: {
                this.ASCON_PB_ROUNDS = 8;
                this.algorithmName = "Ascon-XofA";
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid parameter settings for Ascon Hash");
            }
        }
        this.reset();
    }

    @Override
    protected long pad(int n) {
        return 128L << 56 - (n << 3);
    }

    @Override
    protected long loadBytes(byte[] byArray, int n) {
        return Pack.bigEndianToLong(byArray, n);
    }

    @Override
    protected long loadBytes(byte[] byArray, int n, int n2) {
        return Pack.bigEndianToLong(byArray, n, n2);
    }

    @Override
    protected void setBytes(long l, byte[] byArray, int n) {
        Pack.longToBigEndian(l, byArray, n);
    }

    @Override
    protected void setBytes(long l, byte[] byArray, int n, int n2) {
        Pack.longToBigEndian(l, byArray, n, n2);
    }

    @Override
    public void reset() {
        super.reset();
        switch (this.asconParameters.ordinal()) {
            case 0: {
                this.p.set(-5368810569253202922L, 3121280575360345120L, 7395939140700676632L, 6533890155656471820L, 5710016986865767350L);
                break;
            }
            case 1: {
                this.p.set(4940560291654768690L, -3635129828240960206L, -597534922722107095L, 2623493988082852443L, -6283826724160825537L);
            }
        }
    }

    public static enum AsconParameters {
        AsconXof,
        AsconXofA;

    }
}

