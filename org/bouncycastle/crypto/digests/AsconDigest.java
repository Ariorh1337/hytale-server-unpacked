/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.AsconBaseDigest;
import org.bouncycastle.util.Pack;

public class AsconDigest
extends AsconBaseDigest {
    AsconParameters asconParameters;

    public AsconDigest(AsconParameters asconParameters) {
        this.asconParameters = asconParameters;
        switch (asconParameters.ordinal()) {
            case 0: {
                this.ASCON_PB_ROUNDS = 12;
                this.algorithmName = "Ascon-Hash";
                break;
            }
            case 1: {
                this.ASCON_PB_ROUNDS = 8;
                this.algorithmName = "Ascon-HashA";
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
            case 1: {
                this.p.set(92044056785660070L, 8326807761760157607L, 3371194088139667532L, -2956994353054992515L, -6828509670848688761L);
                break;
            }
            case 0: {
                this.p.set(-1255492011513352131L, -8380609354527731710L, -5437372128236807582L, 4834782570098516968L, 3787428097924915520L);
            }
        }
    }

    public static enum AsconParameters {
        AsconHash,
        AsconHashA;

    }
}

