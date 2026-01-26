/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.AsconBaseDigest;
import org.bouncycastle.crypto.digests.BufferBaseDigest;
import org.bouncycastle.crypto.engines.AsconPermutationFriend;
import org.bouncycastle.util.Pack;

public class ISAPDigest
extends BufferBaseDigest {
    private final AsconPermutationFriend.AsconPermutation p = AsconPermutationFriend.getAsconPermutation(Friend.access$000());

    public ISAPDigest() {
        super(BufferBaseDigest.ProcessingBufferType.Immediate, 8);
        this.DigestSize = 32;
        this.algorithmName = "ISAP Hash";
        this.reset();
    }

    @Override
    protected void processBytes(byte[] byArray, int n) {
        this.p.x0 ^= Pack.bigEndianToLong(byArray, n);
        this.p.p(12);
    }

    @Override
    protected void finish(byte[] byArray, int n) {
        this.p.x0 ^= 128L << (7 - this.m_bufPos << 3);
        while (this.m_bufPos > 0) {
            this.p.x0 ^= ((long)this.m_buf[--this.m_bufPos] & 0xFFL) << (7 - this.m_bufPos << 3);
        }
        for (int i = 0; i < 4; ++i) {
            this.p.p(12);
            Pack.longToBigEndian(this.p.x0, byArray, n);
            n += 8;
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.p.set(-1255492011513352131L, -8380609354527731710L, -5437372128236807582L, 4834782570098516968L, 3787428097924915520L);
    }

    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }

        static Friend getFriend(AsconBaseDigest.Friend friend) {
            if (null == friend) {
                throw new NullPointerException("This method is only for use by AsconBaseDigest");
            }
            return INSTANCE;
        }

        static /* synthetic */ Friend access$000() {
            return INSTANCE;
        }
    }
}

