/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.digests.BufferBaseDigest;
import org.bouncycastle.crypto.digests.ISAPDigest;
import org.bouncycastle.crypto.engines.AsconPermutationFriend;

abstract class AsconBaseDigest
extends BufferBaseDigest {
    AsconPermutationFriend.AsconPermutation p = AsconPermutationFriend.getAsconPermutation(ISAPDigest.Friend.getFriend(Friend.access$000()));
    protected int ASCON_PB_ROUNDS = 12;

    protected AsconBaseDigest() {
        super(BufferBaseDigest.ProcessingBufferType.Immediate, 8);
        this.DigestSize = 32;
    }

    protected abstract long pad(int var1);

    protected abstract long loadBytes(byte[] var1, int var2);

    protected abstract long loadBytes(byte[] var1, int var2, int var3);

    protected abstract void setBytes(long var1, byte[] var3, int var4);

    protected abstract void setBytes(long var1, byte[] var3, int var4, int var5);

    @Override
    protected void processBytes(byte[] byArray, int n) {
        this.p.x0 ^= this.loadBytes(byArray, n);
        this.p.p(this.ASCON_PB_ROUNDS);
    }

    @Override
    protected void finish(byte[] byArray, int n) {
        this.padAndAbsorb();
        this.squeeze(byArray, n, this.DigestSize);
    }

    protected void padAndAbsorb() {
        this.p.x0 ^= this.loadBytes(this.m_buf, 0, this.m_bufPos) ^ this.pad(this.m_bufPos);
        this.p.p(12);
    }

    protected void squeeze(byte[] byArray, int n, int n2) {
        while (n2 > this.BlockSize) {
            this.setBytes(this.p.x0, byArray, n);
            this.p.p(this.ASCON_PB_ROUNDS);
            n += this.BlockSize;
            n2 -= this.BlockSize;
        }
        this.setBytes(this.p.x0, byArray, n, n2);
    }

    protected int hash(byte[] byArray, int n, int n2) {
        this.ensureSufficientOutputBuffer(byArray, n, n2);
        this.padAndAbsorb();
        this.squeeze(byArray, n, n2);
        return n2;
    }

    protected void ensureSufficientOutputBuffer(byte[] byArray, int n, int n2) {
        if (n + n2 > byArray.length) {
            throw new OutputLengthException("output buffer is too short");
        }
    }

    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }

        static /* synthetic */ Friend access$000() {
            return INSTANCE;
        }
    }
}

