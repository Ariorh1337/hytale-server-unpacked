/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.util.Arrays;

public class Ed448Signer
implements Signer {
    private final Buffer buffer = new Buffer();
    private final byte[] context;
    private boolean forSigning;
    private Ed448PrivateKeyParameters privateKey;
    private Ed448PublicKeyParameters publicKey;

    public Ed448Signer(byte[] byArray) {
        if (null == byArray) {
            throw new NullPointerException("'context' cannot be null");
        }
        this.context = Arrays.clone(byArray);
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        this.forSigning = bl;
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom)cipherParameters).getParameters();
        }
        if (bl) {
            this.privateKey = (Ed448PrivateKeyParameters)cipherParameters;
            this.publicKey = null;
        } else {
            this.privateKey = null;
            this.publicKey = (Ed448PublicKeyParameters)cipherParameters;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("Ed448", 224, cipherParameters, bl));
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
            throw new IllegalStateException("Ed448Signer not initialised for signature generation.");
        }
        return this.buffer.generateSignature(this.privateKey, this.context);
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        if (this.forSigning || null == this.publicKey) {
            throw new IllegalStateException("Ed448Signer not initialised for verification");
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

        synchronized byte[] generateSignature(Ed448PrivateKeyParameters ed448PrivateKeyParameters, byte[] byArray) {
            byte[] byArray2 = new byte[114];
            ed448PrivateKeyParameters.sign(0, byArray, this.buf, 0, this.count, byArray2, 0);
            this.reset();
            return byArray2;
        }

        synchronized boolean verifySignature(Ed448PublicKeyParameters ed448PublicKeyParameters, byte[] byArray, byte[] byArray2) {
            if (114 != byArray2.length) {
                this.reset();
                return false;
            }
            boolean bl = ed448PublicKeyParameters.verify(0, byArray, this.buf, 0, this.count, byArray2, 0);
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

