/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.AsconXofBase;
import org.bouncycastle.util.Pack;

public class AsconXof128
extends AsconXofBase {
    public AsconXof128() {
        this.algorithmName = "Ascon-XOF-128";
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
        this.p.set(-2701369817892108309L, -3711838248891385495L, -1778763697082575311L, 1072114354614917324L, -2282070310009238562L);
    }
}

