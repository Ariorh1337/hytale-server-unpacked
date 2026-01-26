/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;

class DigestUpdatingOutputStream
extends OutputStream {
    private MessageDigest digest;

    DigestUpdatingOutputStream(MessageDigest messageDigest) {
        this.digest = messageDigest;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        this.digest.update(byArray, n, n2);
    }

    @Override
    public void write(byte[] byArray) throws IOException {
        this.digest.update(byArray);
    }

    @Override
    public void write(int n) throws IOException {
        this.digest.update((byte)n);
    }
}

