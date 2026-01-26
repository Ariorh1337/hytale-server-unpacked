/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.util.Memoable;

public class SHA3Digest
extends KeccakDigest
implements SavableDigest {
    private static int checkBitLength(int n) {
        switch (n) {
            case 224: 
            case 256: 
            case 384: 
            case 512: {
                return n;
            }
        }
        throw new IllegalArgumentException("'bitLength' " + n + " not supported for SHA-3");
    }

    public SHA3Digest() {
        this(256, CryptoServicePurpose.ANY);
    }

    public SHA3Digest(CryptoServicePurpose cryptoServicePurpose) {
        this(256, cryptoServicePurpose);
    }

    public SHA3Digest(int n) {
        super(SHA3Digest.checkBitLength(n), CryptoServicePurpose.ANY);
    }

    public SHA3Digest(int n, CryptoServicePurpose cryptoServicePurpose) {
        super(SHA3Digest.checkBitLength(n), cryptoServicePurpose);
    }

    public SHA3Digest(byte[] byArray) {
        super(byArray);
    }

    public SHA3Digest(SHA3Digest sHA3Digest) {
        super(sHA3Digest);
    }

    @Override
    public String getAlgorithmName() {
        return "SHA3-" + this.fixedOutputLength;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        this.absorbBits(2, 2);
        return super.doFinal(byArray, n);
    }

    @Override
    protected int doFinal(byte[] byArray, int n, byte by, int n2) {
        if (n2 < 0 || n2 > 7) {
            throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
        }
        int n3 = by & (1 << n2) - 1 | 2 << n2;
        int n4 = n2 + 2;
        if (n4 >= 8) {
            this.absorb((byte)n3);
            n4 -= 8;
            n3 >>>= 8;
        }
        return super.doFinal(byArray, n, (byte)n3, n4);
    }

    @Override
    public byte[] getEncodedState() {
        byte[] byArray = new byte[this.state.length * 8 + this.dataQueue.length + 12 + 2];
        super.getEncodedState(byArray);
        return byArray;
    }

    @Override
    public Memoable copy() {
        return new SHA3Digest(this);
    }

    @Override
    public void reset(Memoable memoable) {
        SHA3Digest sHA3Digest = (SHA3Digest)memoable;
        this.copyIn(sHA3Digest);
    }
}

