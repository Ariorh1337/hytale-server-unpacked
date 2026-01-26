/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.params.Blake3Parameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class Blake3Mac
implements Mac {
    private final Blake3Digest theDigest;

    public Blake3Mac(Blake3Digest blake3Digest) {
        this.theDigest = blake3Digest;
    }

    @Override
    public String getAlgorithmName() {
        return this.theDigest.getAlgorithmName() + "Mac";
    }

    @Override
    public void init(CipherParameters cipherParameters) {
        CipherParameters cipherParameters2 = cipherParameters;
        if (cipherParameters2 instanceof KeyParameter) {
            cipherParameters2 = Blake3Parameters.key(((KeyParameter)cipherParameters2).getKey());
        }
        if (!(cipherParameters2 instanceof Blake3Parameters)) {
            throw new IllegalArgumentException("Invalid parameter passed to Blake3Mac init - " + cipherParameters.getClass().getName());
        }
        Blake3Parameters blake3Parameters = (Blake3Parameters)cipherParameters2;
        if (blake3Parameters.getKey() == null) {
            throw new IllegalArgumentException("Blake3Mac requires a key parameter.");
        }
        this.theDigest.init(blake3Parameters);
    }

    @Override
    public int getMacSize() {
        return this.theDigest.getDigestSize();
    }

    @Override
    public void update(byte by) {
        this.theDigest.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.theDigest.update(byArray, n, n2);
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        return this.theDigest.doFinal(byArray, n);
    }

    @Override
    public void reset() {
        this.theDigest.reset();
    }
}

