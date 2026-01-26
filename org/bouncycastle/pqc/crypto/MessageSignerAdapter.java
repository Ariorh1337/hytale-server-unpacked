/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;

public final class MessageSignerAdapter
implements Signer {
    private final Buffer buffer = new Buffer();
    private final MessageSigner messageSigner;

    public MessageSignerAdapter(MessageSigner messageSigner) {
        if (messageSigner == null) {
            throw new NullPointerException("'messageSigner' cannot be null");
        }
        this.messageSigner = messageSigner;
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        this.messageSigner.init(bl, cipherParameters);
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
        return this.messageSigner.generateSignature(this.getMessage());
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        return this.messageSigner.verifySignature(this.getMessage(), byArray);
    }

    @Override
    public void reset() {
        this.buffer.reset();
    }

    private byte[] getMessage() {
        try {
            byte[] byArray = this.buffer.toByteArray();
            return byArray;
        }
        finally {
            this.reset();
        }
    }

    private static final class Buffer
    extends ByteArrayOutputStream {
        private Buffer() {
        }

        @Override
        public synchronized void reset() {
            Arrays.fill(this.buf, 0, this.count, (byte)0);
            this.count = 0;
        }
    }
}

