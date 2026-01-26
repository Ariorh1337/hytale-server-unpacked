/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.crypto.engines.AsconPermutationFriend;

abstract class AsconBaseEngine
extends AEADBaseEngine {
    protected int nr;
    protected long K0;
    protected long K1;
    protected long N0;
    protected long N1;
    protected long ASCON_IV;
    AsconPermutationFriend.AsconPermutation p = new AsconPermutationFriend.AsconPermutation();
    protected long dsep;

    AsconBaseEngine() {
    }

    protected abstract long pad(int var1);

    protected abstract long loadBytes(byte[] var1, int var2);

    protected abstract void setBytes(long var1, byte[] var3, int var4);

    protected abstract void ascon_aeadinit();

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        switch (this.m_state.ord) {
            case 2: 
            case 6: {
                this.processFinalAAD();
                this.p.p(this.nr);
                break;
            }
        }
        this.p.x4 ^= this.dsep;
        this.m_aadPos = 0;
        this.m_state = state;
    }

    protected abstract void processFinalDecrypt(byte[] var1, int var2, byte[] var3, int var4);

    protected abstract void processFinalEncrypt(byte[] var1, int var2, byte[] var3, int var4);

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
        this.p.x0 ^= this.loadBytes(byArray, n);
        if (this.BlockSize == 16) {
            this.p.x1 ^= this.loadBytes(byArray, 8 + n);
        }
        this.p.p(this.nr);
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        if (this.forEncryption) {
            this.processFinalEncrypt(this.m_buf, this.m_bufPos, byArray, n);
        } else {
            this.processFinalDecrypt(this.m_buf, this.m_bufPos, byArray, n);
        }
        this.setBytes(this.p.x3, this.mac, 0);
        this.setBytes(this.p.x4, this.mac, 8);
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        long l = this.loadBytes(byArray, n);
        this.setBytes(this.p.x0 ^ l, byArray2, n2);
        this.p.x0 = l;
        if (this.BlockSize == 16) {
            long l2 = this.loadBytes(byArray, n + 8);
            this.setBytes(this.p.x1 ^ l2, byArray2, n2 + 8);
            this.p.x1 = l2;
        }
        this.p.p(this.nr);
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.p.x0 ^= this.loadBytes(byArray, n);
        this.setBytes(this.p.x0, byArray2, n2);
        if (this.BlockSize == 16) {
            this.p.x1 ^= this.loadBytes(byArray, n + 8);
            this.setBytes(this.p.x1, byArray2, n2 + 8);
        }
        this.p.p(this.nr);
    }

    @Override
    protected void reset(boolean bl) {
        super.reset(bl);
        this.ascon_aeadinit();
    }

    public abstract String getAlgorithmVersion();
}

