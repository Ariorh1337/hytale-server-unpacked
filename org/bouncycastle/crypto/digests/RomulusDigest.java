/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.BufferBaseDigest;
import org.bouncycastle.crypto.engines.RomulusEngine;
import org.bouncycastle.util.Arrays;

public class RomulusDigest
extends BufferBaseDigest {
    private final byte[] h = new byte[16];
    private final byte[] g = new byte[16];

    public RomulusDigest() {
        super(BufferBaseDigest.ProcessingBufferType.Immediate, 32);
        this.DigestSize = 32;
        this.algorithmName = "Romulus Hash";
    }

    @Override
    protected void processBytes(byte[] byArray, int n) {
        RomulusEngine.hirose_128_128_256(Friend.INSTANCE, this.h, this.g, byArray, n);
    }

    @Override
    protected void finish(byte[] byArray, int n) {
        Arrays.fill(this.m_buf, this.m_bufPos, 31, (byte)0);
        this.m_buf[31] = (byte)(this.m_bufPos & 0x1F);
        this.h[0] = (byte)(this.h[0] ^ 2);
        RomulusEngine.hirose_128_128_256(Friend.INSTANCE, this.h, this.g, this.m_buf, 0);
        System.arraycopy(this.h, 0, byArray, n, 16);
        System.arraycopy(this.g, 0, byArray, 16 + n, 16);
    }

    @Override
    public void reset() {
        super.reset();
        Arrays.clear(this.h);
        Arrays.clear(this.g);
    }

    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }
    }
}

