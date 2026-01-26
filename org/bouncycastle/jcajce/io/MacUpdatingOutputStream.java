/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Mac;

class MacUpdatingOutputStream
extends OutputStream {
    private Mac mac;

    MacUpdatingOutputStream(Mac mac) {
        this.mac = mac;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        this.mac.update(byArray, n, n2);
    }

    @Override
    public void write(byte[] byArray) throws IOException {
        this.mac.update(byArray);
    }

    @Override
    public void write(int n) throws IOException {
        this.mac.update((byte)n);
    }
}

