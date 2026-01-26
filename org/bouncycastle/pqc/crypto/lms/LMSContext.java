/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.lms.LMOtsPrivateKey;
import org.bouncycastle.pqc.crypto.lms.LMOtsPublicKey;
import org.bouncycastle.pqc.crypto.lms.LMSSignedPubKey;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

public class LMSContext
implements Digest {
    private final byte[] C;
    private final LMOtsPrivateKey key;
    private final LMSigParameters sigParams;
    private final byte[][] path;
    private final LMOtsPublicKey publicKey;
    private final Object signature;
    private LMSSignedPubKey[] signedPubKeys;
    private volatile Digest digest;

    LMSContext(LMOtsPrivateKey lMOtsPrivateKey, LMSigParameters lMSigParameters, Digest digest, byte[] byArray, byte[][] byArray2) {
        this.key = lMOtsPrivateKey;
        this.sigParams = lMSigParameters;
        this.digest = digest;
        this.C = byArray;
        this.path = byArray2;
        this.publicKey = null;
        this.signature = null;
    }

    LMSContext(LMOtsPublicKey lMOtsPublicKey, Object object, Digest digest) {
        this.publicKey = lMOtsPublicKey;
        this.signature = object;
        this.digest = digest;
        this.C = null;
        this.key = null;
        this.sigParams = null;
        this.path = null;
    }

    byte[] getC() {
        return this.C;
    }

    byte[] getQ() {
        byte[] byArray = new byte[34];
        this.digest.doFinal(byArray, 0);
        this.digest = null;
        return byArray;
    }

    byte[][] getPath() {
        return this.path;
    }

    LMOtsPrivateKey getPrivateKey() {
        return this.key;
    }

    LMOtsPublicKey getPublicKey() {
        return this.publicKey;
    }

    LMSigParameters getSigParams() {
        return this.sigParams;
    }

    public Object getSignature() {
        return this.signature;
    }

    LMSSignedPubKey[] getSignedPubKeys() {
        return this.signedPubKeys;
    }

    LMSContext withSignedPublicKeys(LMSSignedPubKey[] lMSSignedPubKeyArray) {
        this.signedPubKeys = lMSSignedPubKeyArray;
        return this;
    }

    @Override
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName();
    }

    @Override
    public int getDigestSize() {
        return this.digest.getDigestSize();
    }

    @Override
    public void update(byte by) {
        this.digest.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.digest.update(byArray, n, n2);
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        return this.digest.doFinal(byArray, n);
    }

    @Override
    public void reset() {
        this.digest.reset();
    }
}

