/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class Blake2bpDigest
implements ExtendedDigest {
    private int bufferPos = 0;
    private int keyLength = 0;
    private int digestLength;
    private int fanout;
    private int depth;
    private int nodeOffset = 0;
    private long innerHashLength;
    private Blake2bDigest[] S = new Blake2bDigest[4];
    private Blake2bDigest root;
    private byte[] buffer = null;
    private byte[] salt = null;
    private byte[] param = null;
    private byte[] key = null;
    private final int BLAKE2B_BLOCKBYTES = 128;
    private final int BLAKE2B_KEYBYTES = 64;
    private final int BLAKE2B_OUTBYTES = 64;
    private final int PARALLELISM_DEGREE = 4;
    private final byte[] singleByte = new byte[1];

    public Blake2bpDigest(byte[] byArray) {
        this.param = new byte[64];
        this.buffer = new byte[512];
        this.init(byArray);
    }

    @Override
    public String getAlgorithmName() {
        return "BLAKE2bp";
    }

    @Override
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override
    public void update(byte by) {
        this.singleByte[0] = by;
        this.update(this.singleByte, 0, 1);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        int n3;
        int n4 = this.bufferPos;
        int n5 = 1024 - n4;
        if (n4 != 0 && n2 >= n5) {
            System.arraycopy(byArray, n, this.buffer, n4, n5);
            for (n3 = 0; n3 < 4; ++n3) {
                this.S[n3].update(this.buffer, n3 * 128, 128);
            }
            n += n5;
            n2 -= n5;
            n4 = 0;
        }
        for (n3 = 0; n3 < 4; ++n3) {
            int n6 = n;
            n6 += n3 * 128;
            for (int i = n2; i >= 512; i -= 512) {
                this.S[n3].update(byArray, n6, 128);
                n6 += 512;
            }
        }
        n += n2 - n2 % 512;
        if ((n2 %= 512) > 0) {
            System.arraycopy(byArray, n, this.buffer, n4, n2);
        }
        this.bufferPos = n4 + n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        int n2;
        byte[][] byArray2 = new byte[4][64];
        int n3 = 0;
        for (n2 = 0; n2 < 4; ++n2) {
            if (this.bufferPos > n2 * 128) {
                n3 = this.bufferPos - n2 * 128;
                if (n3 > 128) {
                    n3 = 128;
                }
                this.S[n2].update(this.buffer, n2 * 128, n3);
            }
            this.S[n2].doFinal(byArray2[n2], 0);
        }
        for (n2 = 0; n2 < 4; ++n2) {
            this.root.update(byArray2[n2], 0, 64);
        }
        n2 = this.root.doFinal(byArray, n);
        this.reset();
        return n2;
    }

    @Override
    public void reset() {
        this.bufferPos = 0;
        this.digestLength = 64;
        this.root.reset();
        for (int i = 0; i < 4; ++i) {
            this.S[i].reset();
        }
        this.root.setAsLastNode();
        this.S[3].setAsLastNode();
        if (this.key != null) {
            byte[] byArray = new byte[128];
            System.arraycopy(this.key, 0, byArray, 0, this.keyLength);
            for (int i = 0; i < 4; ++i) {
                this.S[i].update(byArray, 0, 128);
            }
        }
    }

    @Override
    public int getByteLength() {
        return 0;
    }

    private void init(byte[] byArray) {
        if (byArray != null && byArray.length > 0) {
            this.keyLength = byArray.length;
            if (this.keyLength > 64) {
                throw new IllegalArgumentException("Keys > 64 bytes are not supported");
            }
            this.key = Arrays.clone(byArray);
        }
        this.bufferPos = 0;
        this.digestLength = 64;
        this.fanout = 4;
        this.depth = 2;
        this.innerHashLength = 64L;
        this.param[0] = (byte)this.digestLength;
        this.param[1] = (byte)this.keyLength;
        this.param[2] = (byte)this.fanout;
        this.param[3] = (byte)this.depth;
        this.param[16] = 1;
        this.param[17] = (byte)this.innerHashLength;
        this.root = new Blake2bDigest(null, this.param);
        Pack.intToLittleEndian(this.nodeOffset, this.param, 8);
        this.param[16] = 0;
        for (int i = 0; i < 4; ++i) {
            Pack.intToLittleEndian(i, this.param, 8);
            this.S[i] = new Blake2bDigest(null, this.param);
        }
        this.root.setAsLastNode();
        this.S[3].setAsLastNode();
        if (byArray != null && this.keyLength > 0) {
            byte[] byArray2 = new byte[128];
            System.arraycopy(byArray, 0, byArray2, 0, this.keyLength);
            for (int i = 0; i < 4; ++i) {
                this.S[i].update(byArray2, 0, 128);
            }
        }
    }
}

