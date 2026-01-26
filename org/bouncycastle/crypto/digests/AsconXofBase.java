/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.AsconBaseDigest;

abstract class AsconXofBase
extends AsconBaseDigest
implements Xof {
    private boolean m_squeezing;
    private final byte[] buffer;
    private int bytesInBuffer;

    AsconXofBase() {
        this.buffer = new byte[this.BlockSize];
    }

    @Override
    public void update(byte by) {
        this.ensureNoAbsorbWhileSqueezing(this.m_squeezing);
        super.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.ensureNoAbsorbWhileSqueezing(this.m_squeezing);
        super.update(byArray, n, n2);
    }

    @Override
    public int doOutput(byte[] byArray, int n, int n2) {
        int n3;
        int n4;
        this.ensureSufficientOutputBuffer(byArray, n, n2);
        int n5 = 0;
        if (this.bytesInBuffer != 0) {
            n4 = this.BlockSize - this.bytesInBuffer;
            n3 = Math.min(n2, this.bytesInBuffer);
            System.arraycopy(this.buffer, n4, byArray, n, n3);
            this.bytesInBuffer -= n3;
            n5 += n3;
        }
        if ((n4 = n2 - n5) >= this.BlockSize) {
            n3 = n4 - n4 % this.BlockSize;
            n5 += this.hash(byArray, n + n5, n3);
        }
        if (n5 < n2) {
            this.hash(this.buffer, 0, this.BlockSize);
            n3 = n2 - n5;
            System.arraycopy(this.buffer, 0, byArray, n + n5, n3);
            this.bytesInBuffer = this.buffer.length - n3;
            n5 += n3;
        }
        return n5;
    }

    @Override
    public int doFinal(byte[] byArray, int n, int n2) {
        int n3 = this.doOutput(byArray, n, n2);
        this.reset();
        return n3;
    }

    @Override
    public void reset() {
        this.m_squeezing = false;
        this.bytesInBuffer = 0;
        super.reset();
    }

    @Override
    protected void padAndAbsorb() {
        if (!this.m_squeezing) {
            this.m_squeezing = true;
            super.padAndAbsorb();
        } else {
            this.p.p(this.ASCON_PB_ROUNDS);
        }
    }

    private void ensureNoAbsorbWhileSqueezing(boolean bl) {
        if (bl) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
    }
}

