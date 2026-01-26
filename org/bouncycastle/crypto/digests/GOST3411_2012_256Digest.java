/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.digests.GOST3411_2012Digest;
import org.bouncycastle.util.Memoable;

public final class GOST3411_2012_256Digest
extends GOST3411_2012Digest {
    private static final byte[] IV = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    public GOST3411_2012_256Digest(CryptoServicePurpose cryptoServicePurpose) {
        super(IV, cryptoServicePurpose);
    }

    public GOST3411_2012_256Digest() {
        super(IV, CryptoServicePurpose.ANY);
    }

    public GOST3411_2012_256Digest(GOST3411_2012_256Digest gOST3411_2012_256Digest) {
        super(IV, gOST3411_2012_256Digest.purpose);
        this.reset(gOST3411_2012_256Digest);
    }

    @Override
    public String getAlgorithmName() {
        return "GOST3411-2012-256";
    }

    @Override
    public int getDigestSize() {
        return 32;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        byte[] byArray2 = new byte[64];
        super.doFinal(byArray2, 0);
        System.arraycopy(byArray2, 32, byArray, n, 32);
        return 32;
    }

    @Override
    public Memoable copy() {
        return new GOST3411_2012_256Digest(this);
    }
}

