/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.digests.GOST3411_2012Digest;
import org.bouncycastle.util.Memoable;

public class GOST3411_2012_512Digest
extends GOST3411_2012Digest {
    private static final byte[] IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public GOST3411_2012_512Digest(CryptoServicePurpose cryptoServicePurpose) {
        super(IV, cryptoServicePurpose);
    }

    public GOST3411_2012_512Digest() {
        super(IV, CryptoServicePurpose.ANY);
    }

    public GOST3411_2012_512Digest(GOST3411_2012_512Digest gOST3411_2012_512Digest) {
        super(IV, gOST3411_2012_512Digest.purpose);
        this.reset(gOST3411_2012_512Digest);
    }

    @Override
    public String getAlgorithmName() {
        return "GOST3411-2012-512";
    }

    @Override
    public int getDigestSize() {
        return 64;
    }

    @Override
    public Memoable copy() {
        return new GOST3411_2012_512Digest(this);
    }
}

