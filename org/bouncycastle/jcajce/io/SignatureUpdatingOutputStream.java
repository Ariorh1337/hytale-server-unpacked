/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.util.Exceptions;

class SignatureUpdatingOutputStream
extends OutputStream {
    private Signature sig;

    SignatureUpdatingOutputStream(Signature signature) {
        this.sig = signature;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        try {
            this.sig.update(byArray, n, n2);
        }
        catch (SignatureException signatureException) {
            throw Exceptions.ioException(signatureException.getMessage(), signatureException);
        }
    }

    @Override
    public void write(byte[] byArray) throws IOException {
        try {
            this.sig.update(byArray);
        }
        catch (SignatureException signatureException) {
            throw Exceptions.ioException(signatureException.getMessage(), signatureException);
        }
    }

    @Override
    public void write(int n) throws IOException {
        try {
            this.sig.update((byte)n);
        }
        catch (SignatureException signatureException) {
            throw Exceptions.ioException(signatureException.getMessage(), signatureException);
        }
    }
}

