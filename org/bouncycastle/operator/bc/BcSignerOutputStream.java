/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;

public class BcSignerOutputStream
extends OutputStream {
    private Signer sig;

    BcSignerOutputStream(Signer signer) {
        this.sig = signer;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        this.sig.update(byArray, n, n2);
    }

    @Override
    public void write(byte[] byArray) throws IOException {
        this.sig.update(byArray, 0, byArray.length);
    }

    @Override
    public void write(int n) throws IOException {
        this.sig.update((byte)n);
    }

    byte[] getSignature() throws CryptoException {
        return this.sig.generateSignature();
    }

    boolean verify(byte[] byArray) {
        return this.sig.verifySignature(byArray);
    }
}

