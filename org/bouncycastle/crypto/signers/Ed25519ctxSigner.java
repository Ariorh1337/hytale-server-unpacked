/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.util.Arrays;

public class Ed25519ctxSigner
implements Signer {
    private final Buffer buffer = new Buffer();
    private final byte[] context;
    private boolean forSigning;
    private Ed25519PrivateKeyParameters privateKey;
    private Ed25519PublicKeyParameters publicKey;

    public Ed25519ctxSigner(byte[] byArray) {
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
        this.buffer.write(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.buffer.write(byArray, n, n2);
    }

    @Override
    public byte[] generateSignature() {
        if (!this.forSigning || null == this.privateKey) {
            throw new IllegalStateException("Ed25519ctxSigner not initialised for signature generation.");
        }
        return this.buffer.generateSignature(this.privateKey, this.context);
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        if (this.forSigning || null == this.publicKey) {
            throw new IllegalStateException("Ed25519ctxSigner not initialised for verification");
        }
        return this.buffer.verifySignature(this.publicKey, this.context, byArray);
    }

    @Override
    public void reset() {
        this.buffer.reset();
    }

    private static final class Buffer
    extends ByteArrayOutputStream {
        private Buffer() {
        }

        synchronized byte[] generateSignature(Ed25519PrivateKeyParameters ed25519PrivateKeyParameters, byte[] byArray) {
            byte[] byArray2 = new byte[64];
            ed25519PrivateKeyParameters.sign(1, byArray, this.buf, 0, this.count, byArray2, 0);
            this.reset();
            return byArray2;
        }

        synchronized boolean verifySignature(Ed25519PublicKeyParameters ed25519PublicKeyParameters, byte[] byArray, byte[] byArray2) {
            if (64 != byArray2.length) {
                this.reset();
                return false;
            }
            boolean bl = ed25519PublicKeyParameters.verify(1, byArray, this.buf, 0, this.count, byArray2, 0);
            this.reset();
            return bl;
        }

        @Override
        public synchronized void reset() {
            Arrays.fill(this.buf, 0, this.count, (byte)0);
            this.count = 0;
        }
    }
}

