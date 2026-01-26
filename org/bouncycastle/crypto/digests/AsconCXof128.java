/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.digests.AsconXofBase;
import org.bouncycastle.util.Pack;

public class AsconCXof128
extends AsconXofBase {
    private final long z0;
    private final long z1;
    private final long z2;
    private final long z3;
    private final long z4;

    public AsconCXof128() {
        this(new byte[0], 0, 0);
    }

    public AsconCXof128(byte[] byArray) {
        this(byArray, 0, byArray.length);
    }

    public AsconCXof128(byte[] byArray, int n, int n2) {
        this.algorithmName = "Ascon-CXOF128";
        this.ensureSufficientInputBuffer(byArray, n, n2);
        if (n2 > 256) {
            throw new DataLengthException("customized string is too long");
        }
        this.initState(byArray, n, n2);
        this.z0 = this.p.x0;
        this.z1 = this.p.x1;
        this.z2 = this.p.x2;
        this.z3 = this.p.x3;
        this.z4 = this.p.x4;
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
        this.p.set(this.z0, this.z1, this.z2, this.z3, this.z4);
    }

    private void initState(byte[] byArray, int n, int n2) {
        if (n2 == 0) {
            this.p.set(5768210384618244584L, 6623958265790276749L, 4252419465292010770L, 1238191464582506891L, 56353695744608240L);
        } else {
            this.p.set(7445901275803737603L, 4886737088792722364L, -1616759365661982283L, 3076320316797452470L, -8124743304765850554L);
            this.p.x0 ^= (long)n2 << 3;
            this.p.p(12);
            this.update(byArray, n, n2);
            this.padAndAbsorb();
        }
        super.reset();
    }
}

