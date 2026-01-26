/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.LongDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA512Digest
extends LongDigest {
    private static final int DIGEST_LENGTH = 64;

    public SHA512Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public SHA512Digest(CryptoServicePurpose cryptoServicePurpose) {
        super(cryptoServicePurpose);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.reset();
    }

    public SHA512Digest(SHA512Digest sHA512Digest) {
        super(sHA512Digest);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    public SHA512Digest(byte[] byArray) {
        super(CryptoServicePurpose.values()[byArray[byArray.length - 1]]);
        this.restoreState(byArray);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-512";
    }

    @Override
    public int getDigestSize() {
        return 64;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        this.finish();
        Pack.longToBigEndian(this.H1, byArray, n);
        Pack.longToBigEndian(this.H2, byArray, n + 8);
        Pack.longToBigEndian(this.H3, byArray, n + 16);
        Pack.longToBigEndian(this.H4, byArray, n + 24);
        Pack.longToBigEndian(this.H5, byArray, n + 32);
        Pack.longToBigEndian(this.H6, byArray, n + 40);
        Pack.longToBigEndian(this.H7, byArray, n + 48);
        Pack.longToBigEndian(this.H8, byArray, n + 56);
        this.reset();
        return 64;
    }

    @Override
    public void reset() {
        super.reset();
        this.H1 = 7640891576956012808L;
        this.H2 = -4942790177534073029L;
        this.H3 = 4354685564936845355L;
        this.H4 = -6534734903238641935L;
        this.H5 = 5840696475078001361L;
        this.H6 = -7276294671716946913L;
        this.H7 = 2270897969802886507L;
        this.H8 = 6620516959819538809L;
    }

    @Override
    public Memoable copy() {
        return new SHA512Digest(this);
    }

    @Override
    public void reset(Memoable memoable) {
        SHA512Digest sHA512Digest = (SHA512Digest)memoable;
        this.copyIn(sHA512Digest);
    }

    @Override
    public byte[] getEncodedState() {
        byte[] byArray = new byte[this.getEncodedStateSize() + 1];
        super.populateState(byArray);
        byArray[byArray.length - 1] = (byte)this.purpose.ordinal();
        return byArray;
    }

    @Override
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, 256, this.purpose);
    }
}

