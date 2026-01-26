/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.BufferBaseDigest;
import org.bouncycastle.crypto.engines.PhotonBeetleEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

public class PhotonBeetleDigest
extends BufferBaseDigest {
    private final byte[] state;
    private static final int SQUEEZE_RATE_INBYTES = 16;
    private static final int D = 8;
    private int blockCount;

    public PhotonBeetleDigest() {
        super(BufferBaseDigest.ProcessingBufferType.Buffered, 4);
        this.DigestSize = 32;
        this.state = new byte[this.DigestSize];
        this.algorithmName = "Photon-Beetle Hash";
        this.blockCount = 0;
    }

    @Override
    protected void processBytes(byte[] byArray, int n) {
        if (this.blockCount < 4) {
            System.arraycopy(byArray, n, this.state, this.blockCount << 2, this.BlockSize);
        } else {
            PhotonBeetleEngine.photonPermutation(Friend.INSTANCE, this.state);
            Bytes.xorTo(this.BlockSize, byArray, n, this.state);
        }
        ++this.blockCount;
    }

    @Override
    protected void finish(byte[] byArray, int n) {
        int n2 = 5;
        if (this.m_bufPos == 0 && this.blockCount == 0) {
            int n3 = this.DigestSize - 1;
            this.state[n3] = (byte)(this.state[n3] ^ 1 << n2);
        } else if (this.blockCount < 4) {
            System.arraycopy(this.m_buf, 0, this.state, this.blockCount << 2, this.m_bufPos);
            int n4 = (this.blockCount << 2) + this.m_bufPos;
            this.state[n4] = (byte)(this.state[n4] ^ 1);
            int n5 = this.DigestSize - 1;
            this.state[n5] = (byte)(this.state[n5] ^ 1 << n2);
        } else if (this.blockCount == 4 && this.m_bufPos == 0) {
            int n6 = this.DigestSize - 1;
            this.state[n6] = (byte)(this.state[n6] ^ 2 << n2);
        } else {
            PhotonBeetleEngine.photonPermutation(Friend.INSTANCE, this.state);
            Bytes.xorTo(this.m_bufPos, this.m_buf, this.state);
            if (this.m_bufPos < this.BlockSize) {
                int n7 = this.m_bufPos;
                this.state[n7] = (byte)(this.state[n7] ^ 1);
            }
            int n8 = this.DigestSize - 1;
            this.state[n8] = (byte)(this.state[n8] ^ (this.m_bufPos % this.BlockSize == 0 ? 1 : 2) << n2);
        }
        PhotonBeetleEngine.photonPermutation(Friend.INSTANCE, this.state);
        System.arraycopy(this.state, 0, byArray, n, 16);
        PhotonBeetleEngine.photonPermutation(Friend.INSTANCE, this.state);
        System.arraycopy(this.state, 0, byArray, n + 16, 16);
    }

    @Override
    public void reset() {
        super.reset();
        Arrays.fill(this.state, (byte)0);
        this.blockCount = 0;
    }

    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }
    }
}

