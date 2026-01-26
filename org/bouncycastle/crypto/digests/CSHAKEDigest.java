/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;

public class CSHAKEDigest
extends SHAKEDigest {
    private static final byte[] padding = new byte[100];
    private byte[] diff;

    public CSHAKEDigest(int n, byte[] byArray, byte[] byArray2) {
        this(n, CryptoServicePurpose.ANY, byArray, byArray2);
    }

    public CSHAKEDigest(int n, CryptoServicePurpose cryptoServicePurpose, byte[] byArray, byte[] byArray2) {
        super(n, cryptoServicePurpose);
        if (!(byArray != null && byArray.length != 0 || byArray2 != null && byArray2.length != 0)) {
            this.diff = null;
        } else {
            this.diff = Arrays.concatenate(XofUtils.leftEncode(this.rate / 8), this.encodeString(byArray), this.encodeString(byArray2));
            this.diffPadAndAbsorb();
        }
    }

    public CSHAKEDigest(CSHAKEDigest cSHAKEDigest) {
        super(cSHAKEDigest);
        this.diff = Arrays.clone(cSHAKEDigest.diff);
    }

    public CSHAKEDigest(byte[] byArray) {
        super(byArray);
        int n = this.state.length * 8 + this.dataQueue.length + 12 + 2;
        if (byArray.length != n) {
            this.diff = new byte[byArray.length - n];
            System.arraycopy(byArray, n, this.diff, 0, this.diff.length);
        } else {
            this.diff = null;
        }
    }

    private void copyIn(CSHAKEDigest cSHAKEDigest) {
        super.copyIn(cSHAKEDigest);
        this.diff = Arrays.clone(cSHAKEDigest.diff);
    }

    private void diffPadAndAbsorb() {
        int n = this.rate / 8;
        this.absorb(this.diff, 0, this.diff.length);
        int n2 = this.diff.length % n;
        if (n2 != 0) {
            int n3;
            for (n3 = n - n2; n3 > padding.length; n3 -= padding.length) {
                this.absorb(padding, 0, padding.length);
            }
            this.absorb(padding, 0, n3);
        }
    }

    private byte[] encodeString(byte[] byArray) {
        if (byArray == null || byArray.length == 0) {
            return XofUtils.leftEncode(0L);
        }
        return Arrays.concatenate(XofUtils.leftEncode((long)byArray.length * 8L), byArray);
    }

    @Override
    public String getAlgorithmName() {
        return "CSHAKE" + this.fixedOutputLength;
    }

    @Override
    public int doOutput(byte[] byArray, int n, int n2) {
        if (this.diff != null) {
            if (!this.squeezing) {
                this.absorbBits(0, 2);
            }
            this.squeeze(byArray, n, (long)n2 * 8L);
            return n2;
        }
        return super.doOutput(byArray, n, n2);
    }

    @Override
    public void reset() {
        super.reset();
        if (this.diff != null) {
            this.diffPadAndAbsorb();
        }
    }

    @Override
    public byte[] getEncodedState() {
        byte[] byArray;
        int n = this.state.length * 8 + this.dataQueue.length + 12 + 2;
        if (this.diff == null) {
            byArray = new byte[n];
            super.getEncodedState(byArray);
        } else {
            byArray = new byte[n + this.diff.length];
            super.getEncodedState(byArray);
            System.arraycopy(this.diff, 0, byArray, n, this.diff.length);
        }
        return byArray;
    }

    @Override
    public Memoable copy() {
        return new CSHAKEDigest(this);
    }

    @Override
    public void reset(Memoable memoable) {
        CSHAKEDigest cSHAKEDigest = (CSHAKEDigest)memoable;
        this.copyIn(cSHAKEDigest);
    }
}

