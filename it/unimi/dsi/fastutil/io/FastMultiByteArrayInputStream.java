/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.io;

import it.unimi.dsi.fastutil.io.MeasurableInputStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FastMultiByteArrayInputStream
extends MeasurableInputStream
implements RepositionableStream {
    public static final int SLICE_BITS = 30;
    public static final int SLICE_SIZE = 0x40000000;
    public static final int SLICE_MASK = 0x3FFFFFFF;
    public byte[][] array;
    public byte[] current;
    public long length;
    private long position;

    public FastMultiByteArrayInputStream(MeasurableInputStream is) throws IOException {
        this(is, is.length());
    }

    public FastMultiByteArrayInputStream(InputStream is, long size) throws IOException {
        this.length = size;
        this.array = new byte[(int)((size + 0x40000000L - 1L) / 0x40000000L) + 1][];
        for (int i = 0; i < this.array.length - 1; ++i) {
            this.array[i] = new byte[size >= 0x40000000L ? 0x40000000 : (int)size];
            if (FastMultiByteArrayInputStream.read(is, this.array[i]) != (long)this.array[i].length) {
                throw new EOFException();
            }
            size -= (long)this.array[i].length;
        }
        this.current = this.array[0];
    }

    private static long read(InputStream is, byte[] a) throws IOException {
        int result;
        if (a.length == 0) {
            return 0L;
        }
        int read = 0;
        do {
            if ((result = is.read(a, read, Math.min(a.length - read, 0x100000))) >= 0) continue;
            return read;
        } while ((read += result) < a.length);
        return read;
    }

    public FastMultiByteArrayInputStream(FastMultiByteArrayInputStream is) {
        this.array = is.array;
        this.length = is.length;
        this.current = this.array[0];
    }

    public FastMultiByteArrayInputStream(byte[] array) {
        if (array.length == 0) {
            this.array = new byte[1][];
        } else {
            this.array = new byte[2][];
            this.array[0] = array;
            this.length = array.length;
            this.current = array;
        }
    }

    @Override
    public int available() {
        return (int)Math.min(Integer.MAX_VALUE, this.length - this.position);
    }

    @Override
    public long skip(long n) {
        if (n > this.length - this.position) {
            n = this.length - this.position;
        }
        this.position += n;
        this.updateCurrent();
        return n;
    }

    @Override
    public int read() {
        int disp;
        if (this.length == this.position) {
            return -1;
        }
        if ((disp = (int)(this.position++ & 0x3FFFFFFFL)) == 0) {
            this.updateCurrent();
        }
        return this.current[disp] & 0xFF;
    }

    @Override
    public int read(byte[] b, int offset, int length) {
        int n;
        long remaining = this.length - this.position;
        if (remaining == 0L) {
            return length == 0 ? 0 : -1;
        }
        int m = n = (int)Math.min((long)length, remaining);
        while (true) {
            int disp;
            if ((disp = (int)(this.position & 0x3FFFFFFFL)) == 0) {
                this.updateCurrent();
            }
            int res = Math.min(n, this.current.length - disp);
            System.arraycopy(this.current, disp, b, offset, res);
            this.position += (long)res;
            if ((n -= res) == 0) {
                return m;
            }
            offset += res;
        }
    }

    private void updateCurrent() {
        this.current = this.array[(int)(this.position >>> 30)];
    }

    @Override
    public long position() {
        return this.position;
    }

    @Override
    public void position(long newPosition) {
        this.position = Math.min(newPosition, this.length);
        this.updateCurrent();
    }

    @Override
    public long length() throws IOException {
        return this.length;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int dummy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }
}

