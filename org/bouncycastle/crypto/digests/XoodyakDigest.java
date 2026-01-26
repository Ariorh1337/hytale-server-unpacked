/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.BufferBaseDigest;
import org.bouncycastle.crypto.engines.XoodyakEngine;
import org.bouncycastle.util.Arrays;

public class XoodyakDigest
extends BufferBaseDigest {
    private final byte[] state;
    private int phase;
    private static final int mode = 1;
    private static final int PhaseUp = 2;
    private static final int PhaseDown = 1;
    private static final int TAGLEN = 16;
    private int Cd;

    public XoodyakDigest() {
        super(BufferBaseDigest.ProcessingBufferType.Immediate, 16);
        this.DigestSize = 32;
        this.state = new byte[48];
        this.algorithmName = "Xoodyak Hash";
        this.reset();
    }

    @Override
    protected void processBytes(byte[] byArray, int n) {
        if (this.phase != 2) {
            XoodyakEngine.up(Friend.INSTANCE, 1, this.state, 0);
        }
        XoodyakEngine.down(Friend.INSTANCE, 1, this.state, byArray, n, this.BlockSize, this.Cd);
        this.phase = 1;
        this.Cd = 0;
    }

    @Override
    protected void finish(byte[] byArray, int n) {
        if (this.m_bufPos != 0) {
            if (this.phase != 2) {
                XoodyakEngine.up(Friend.INSTANCE, 1, this.state, 0);
            }
            XoodyakEngine.down(Friend.INSTANCE, 1, this.state, this.m_buf, 0, this.m_bufPos, this.Cd);
        }
        XoodyakEngine.up(Friend.INSTANCE, 1, this.state, 64);
        System.arraycopy(this.state, 0, byArray, n, 16);
        XoodyakEngine.down(Friend.INSTANCE, 1, this.state, null, 0, 0, 0);
        XoodyakEngine.up(Friend.INSTANCE, 1, this.state, 0);
        System.arraycopy(this.state, 0, byArray, n + 16, 16);
        this.phase = 1;
    }

    @Override
    public void reset() {
        super.reset();
        Arrays.fill(this.state, (byte)0);
        this.phase = 2;
        this.Cd = 3;
    }

    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }
    }
}

