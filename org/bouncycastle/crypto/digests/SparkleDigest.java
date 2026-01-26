/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.BufferBaseDigest;
import org.bouncycastle.crypto.engines.SparkleEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class SparkleDigest
extends BufferBaseDigest {
    private static final int RATE_WORDS = 4;
    private final int[] state;
    private final int SPARKLE_STEPS_SLIM;
    private final int SPARKLE_STEPS_BIG;
    private final int STATE_WORDS;

    public SparkleDigest(SparkleParameters sparkleParameters) {
        super(BufferBaseDigest.ProcessingBufferType.Buffered, 16);
        switch (sparkleParameters.ordinal()) {
            case 0: {
                this.algorithmName = "ESCH-256";
                this.DigestSize = 32;
                this.SPARKLE_STEPS_SLIM = 7;
                this.SPARKLE_STEPS_BIG = 11;
                this.STATE_WORDS = 12;
                break;
            }
            case 1: {
                this.algorithmName = "ESCH-384";
                this.DigestSize = 48;
                this.SPARKLE_STEPS_SLIM = 8;
                this.SPARKLE_STEPS_BIG = 12;
                this.STATE_WORDS = 16;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid definition of SCHWAEMM instance");
            }
        }
        this.state = new int[this.STATE_WORDS];
    }

    @Override
    protected void processBytes(byte[] byArray, int n) {
        this.processBlock(byArray, n, this.SPARKLE_STEPS_SLIM);
    }

    @Override
    protected void finish(byte[] byArray, int n) {
        if (this.m_bufPos < this.BlockSize) {
            int n2 = (this.STATE_WORDS >> 1) - 1;
            this.state[n2] = this.state[n2] ^ 0x1000000;
            this.m_buf[this.m_bufPos++] = -128;
            Arrays.fill(this.m_buf, this.m_bufPos, this.BlockSize, (byte)0);
        } else {
            int n3 = (this.STATE_WORDS >> 1) - 1;
            this.state[n3] = this.state[n3] ^ 0x2000000;
        }
        this.processBlock(this.m_buf, 0, this.SPARKLE_STEPS_BIG);
        Pack.intToLittleEndian(this.state, 0, 4, byArray, n);
        if (this.STATE_WORDS == 16) {
            SparkleEngine.sparkle_opt16(Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
            Pack.intToLittleEndian(this.state, 0, 4, byArray, n + 16);
            SparkleEngine.sparkle_opt16(Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
            Pack.intToLittleEndian(this.state, 0, 4, byArray, n + 32);
        } else {
            SparkleEngine.sparkle_opt12(Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
            Pack.intToLittleEndian(this.state, 0, 4, byArray, n + 16);
        }
    }

    @Override
    public void reset() {
        super.reset();
        Arrays.fill(this.state, 0);
    }

    private void processBlock(byte[] byArray, int n, int n2) {
        int n3 = Pack.littleEndianToInt(byArray, n);
        int n4 = Pack.littleEndianToInt(byArray, n + 4);
        int n5 = Pack.littleEndianToInt(byArray, n + 8);
        int n6 = Pack.littleEndianToInt(byArray, n + 12);
        int n7 = SparkleDigest.ELL(n3 ^ n5);
        int n8 = SparkleDigest.ELL(n4 ^ n6);
        this.state[0] = this.state[0] ^ (n3 ^ n8);
        this.state[1] = this.state[1] ^ (n4 ^ n7);
        this.state[2] = this.state[2] ^ (n5 ^ n8);
        this.state[3] = this.state[3] ^ (n6 ^ n7);
        this.state[4] = this.state[4] ^ n8;
        this.state[5] = this.state[5] ^ n7;
        if (this.STATE_WORDS == 16) {
            this.state[6] = this.state[6] ^ n8;
            this.state[7] = this.state[7] ^ n7;
            SparkleEngine.sparkle_opt16(Friend.INSTANCE, this.state, n2);
        } else {
            SparkleEngine.sparkle_opt12(Friend.INSTANCE, this.state, n2);
        }
    }

    private static int ELL(int n) {
        return Integers.rotateRight(n, 16) ^ n & 0xFFFF;
    }

    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }
    }

    public static enum SparkleParameters {
        ESCH256,
        ESCH384;

    }
}

