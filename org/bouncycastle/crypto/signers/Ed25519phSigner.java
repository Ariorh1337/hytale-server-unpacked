/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;

public class Ed25519phSigner
implements Signer {
    private final Digest prehash = Ed25519.createPrehash();
    private final byte[] context;
    private boolean forSigning;
    private Ed25519PrivateKeyParameters privateKey;
    private Ed25519PublicKeyParameters publicKey;

    public Ed25519phSigner(byte[] byArray) {
        if (null == byArray) {
            throw new NullPointerException("'context' cannot be null");
        }
        this.context = Arrays.clone(byArray);
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        this.forSigning = bl;
        if (bl) {
            this.privateKey = (Ed25519PrivateKeyParameters)cipherParameters;
            this.publicKey = null;
        } else {
            this.privateKey = null;
            this.publicKey = (Ed25519PublicKeyParameters)cipherParameters;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("Ed25519", 128, cipherParameters, bl));
        this.reset();
    }

    @Override
    public void update(byte by) {
        this.prehash.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.prehash.update(byArray, n, n2);
    }

    @Override
    public byte[] generateSignature() {
        if (!this.forSigning || null == this.privateKey) {
            throw new IllegalStateException("Ed25519phSigner not initialised for signature generation.");
        }
        byte[] byArray = new byte[64];
        if (64 != this.prehash.doFinal(byArray, 0)) {
            throw new IllegalStateException("Prehash digest failed");
        }
        byte[] byArray2 = new byte[64];
        this.privateKey.sign(2, this.context, byArray, 0, 64, byArray2, 0);
        return byArray2;
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        if (this.forSigning || null == this.publicKey) {
            throw new IllegalStateException("Ed25519phSigner not initialised for verification");
        }
        if (64 != byArray.length) {
            this.prehash.reset();
            return false;
        }
        byte[] byArray2 = new byte[64];
        if (64 != this.prehash.doFinal(byArray2, 0)) {
            throw new IllegalStateException("Prehash digest failed");
        }
        return this.publicKey.verify(2, this.context, byArray2, 0, 64, byArray, 0);
    }

    @Override
    public void reset() {
        this.prehash.reset();
    }
}

