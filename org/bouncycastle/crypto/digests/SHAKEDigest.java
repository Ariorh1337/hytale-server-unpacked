/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Memoable;

public class SHAKEDigest
extends KeccakDigest
implements Xof,
SavableDigest {
    private static int checkBitLength(int n) {
        switch (n) {
            case 128: 
            case 256: {
                return n;
            }
        }
        throw new IllegalArgumentException("'bitStrength' " + n + " not supported for SHAKE");
    }

    public SHAKEDigest() {
        this(128);
    }

    public SHAKEDigest(CryptoServicePurpose cryptoServicePurpose) {
        this(128, cryptoServicePurpose);
    }

    public SHAKEDigest(int n) {
        super(SHAKEDigest.checkBitLength(n), CryptoServicePurpose.ANY);
    }

    public SHAKEDigest(int n, CryptoServicePurpose cryptoServicePurpose) {
        super(SHAKEDigest.checkBitLength(n), cryptoServicePurpose);
    }

    public SHAKEDigest(SHAKEDigest sHAKEDigest) {
        super(sHAKEDigest);
    }

    public SHAKEDigest(byte[] byArray) {
        super(byArray);
    }

    @Override
    public String getAlgorithmName() {
        return "SHAKE" + this.fixedOutputLength;
    }

    @Override
    public int getDigestSize() {
        return this.fixedOutputLength / 4;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        return this.doFinal(byArray, n, this.getDigestSize());
    }

    @Override
    public int doFinal(byte[] byArray, int n, int n2) {
        int n3 = this.doOutput(byArray, n, n2);
        this.reset();
        return n3;
    }

    @Override
    public int doOutput(byte[] byArray, int n, int n2) {
        if (!this.squeezing) {
            this.absorbBits(15, 4);
        }
        this.squeeze(byArray, n, (long)n2 * 8L);
        return n2;
    }

    @Override
    protected int doFinal(byte[] byArray, int n, byte by, int n2) {
        return this.doFinal(byArray, n, this.getDigestSize(), by, n2);
    }

    protected int doFinal(byte[] byArray, int n, int n2, byte by, int n3) {
        if (n3 < 0 || n3 > 7) {
            throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
        }
        int n4 = by & (1 << n3) - 1 | 15 << n3;
        int n5 = n3 + 4;
        if (n5 >= 8) {
            this.absorb((byte)n4);
            n5 -= 8;
            n4 >>>= 8;
        }
        if (n5 > 0) {
            this.absorbBits(n4, n5);
        }
        this.squeeze(byArray, n, (long)n2 * 8L);
        this.reset();
        return n2;
    }

    @Override
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, this.purpose);
    }

    @Override
    public byte[] getEncodedState() {
        byte[] byArray = new byte[this.state.length * 8 + this.dataQueue.length + 12 + 2];
        super.getEncodedState(byArray);
        return byArray;
    }

    @Override
    public Memoable copy() {
        return new SHAKEDigest(this);
    }

    @Override
    public void reset(Memoable memoable) {
        SHAKEDigest sHAKEDigest = (SHAKEDigest)memoable;
        this.copyIn(sHAKEDigest);
    }
}

