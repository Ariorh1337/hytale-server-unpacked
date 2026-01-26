/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io;

import java.io.OutputStream;

public class LimitedBuffer
extends OutputStream {
    private final byte[] buf;
    private int count;

    public LimitedBuffer(int n) {
        this.buf = new byte[n];
        this.count = 0;
    }

    public int copyTo(byte[] byArray, int n) {
        System.arraycopy(this.buf, 0, byArray, n, this.count);
        return this.count;
    }

    public int limit() {
        return this.buf.length;
    }

    public void reset() {
        this.count = 0;
    }

    public int size() {
        return this.count;
    }

    @Override
    public void write(int n) {
        this.buf[this.count++] = (byte)n;
    }

    @Override
    public void write(byte[] byArray) {
        System.arraycopy(byArray, 0, this.buf, this.count, byArray.length);
        this.count += byArray.length;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) {
        System.arraycopy(byArray, n, this.buf, this.count, n2);
        this.count += n2;
    }
}

