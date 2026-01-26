/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.AsconBaseDigest;
import org.bouncycastle.util.Pack;

public class AsconHash256
extends AsconBaseDigest {
    public AsconHash256() {
        this.algorithmName = "Ascon-Hash256";
        this.reset();
    }

    @Override
    protected long pad(int n) {
        return 1L << (n << 3);
    }

    @Override
    protected long loadBytes(byte[] byArray, int n) {
        return Pack.littleEndianToLong(byArray, n);
    }

    @Override
    protected long loadBytes(byte[] byArray, int n, int n2) {
        return Pack.littleEndianToLong(byArray, n, n2);
    }

    @Override
    protected void setBytes(long l, byte[] byArray, int n) {
        Pack.longToLittleEndian(l, byArray, n);
    }

    @Override
    protected void setBytes(long l, byte[] byArray, int n, int n2) {
        Pack.longToLittleEndian(l, byArray, n, n2);
    }

    @Override
    public void reset() {
        super.reset();
        this.p.set(-7269279749984954751L, 5459383224871899602L, -5880230600644446182L, 4359436768738168243L, 1899470422303676269L);
    }
}

