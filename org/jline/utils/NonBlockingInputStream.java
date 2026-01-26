/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import org.jline.utils.Timeout;

public abstract class NonBlockingInputStream
extends InputStream {
    public static final int EOF = -1;
    public static final int READ_EXPIRED = -2;

    @Override
    public int read() throws IOException {
        return this.read(0L, false);
    }

    public int peek(long timeout) throws IOException {
        return this.read(timeout, true);
    }

    public int read(long timeout) throws IOException {
        return this.read(timeout, false);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int c = this.read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;
        return 1;
    }

    public int readBuffered(byte[] b) throws IOException {
        return this.readBuffered(b, 0L);
    }

    public int readBuffered(byte[] b, long timeout) throws IOException {
        return this.readBuffered(b, 0, b.length, timeout);
    }

    public int readBuffered(byte[] b, int off, int len, long timeout) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len < b.length) {
            throw new IllegalArgumentException();
        }
        if (len == 0) {
            return 0;
        }
        Timeout t = new Timeout(timeout);
        int nb = 0;
        while (!t.elapsed()) {
            int r = this.read(nb > 0 ? 1L : t.timeout());
            if (r < 0) {
                return nb > 0 ? nb : r;
            }
            b[off + nb++] = (byte)r;
            if (nb < len && !t.isInfinite()) continue;
            break;
        }
        return nb;
    }

    public void shutdown() {
    }

    public abstract int read(long var1, boolean var3) throws IOException;
}

