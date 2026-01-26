/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.jline.utils.NonBlockingReader;

public class NonBlockingPumpReader
extends NonBlockingReader {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private final char[] buffer;
    private int read;
    private int write;
    private int count;
    final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    private final Writer writer;
    private boolean closed;

    public NonBlockingPumpReader() {
        this(4096);
    }

    public NonBlockingPumpReader(int bufferSize) {
        this.buffer = new char[bufferSize];
        this.writer = new NbpWriter();
        this.lock = new ReentrantLock();
        this.notEmpty = this.lock.newCondition();
        this.notFull = this.lock.newCondition();
    }

    public Writer getWriter() {
        return this.writer;
    }

    @Override
    public boolean ready() {
        return this.available() > 0;
    }

    @Override
    public int available() {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = this.count;
            return n;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int read(long timeout, boolean isPeek) throws IOException {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (!this.closed && this.count == 0) {
                try {
                    if (timeout > 0L) {
                        this.notEmpty.await(timeout, TimeUnit.MILLISECONDS);
                    } else {
                        this.notEmpty.await();
                    }
                }
                catch (InterruptedException e) {
                    throw (IOException)new InterruptedIOException().initCause(e);
                }
            }
            if (this.closed) {
                int e = -1;
                return e;
            }
            if (this.count == 0) {
                int e = -2;
                return e;
            }
            if (isPeek) {
                char e = this.buffer[this.read];
                return e;
            }
            char res = this.buffer[this.read];
            if (++this.read == this.buffer.length) {
                this.read = 0;
            }
            --this.count;
            this.notFull.signal();
            char c = res;
            return c;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int readBuffered(char[] b, int off, int len, long timeout) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len < b.length) {
            throw new IllegalArgumentException();
        }
        if (len == 0) {
            return 0;
        }
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (!this.closed && this.count == 0) {
                try {
                    if (timeout > 0L) {
                        if (!this.notEmpty.await(timeout, TimeUnit.MILLISECONDS)) {
                            throw new IOException("Timeout reading");
                        }
                    } else {
                        this.notEmpty.await();
                    }
                }
                catch (InterruptedException e) {
                    throw (IOException)new InterruptedIOException().initCause(e);
                }
            }
            if (this.closed) {
                int e = -1;
                return e;
            }
            if (this.count == 0) {
                int e = -2;
                return e;
            }
            int r = Math.min(len, this.count);
            for (int i = 0; i < r; ++i) {
                b[off + i] = this.buffer[this.read++];
                if (this.read != this.buffer.length) continue;
                this.read = 0;
            }
            this.count -= r;
            this.notFull.signal();
            int n = r;
            return n;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            ReentrantLock lock = this.lock;
            lock.lock();
            try {
                while (len > 0) {
                    if (!this.closed && this.count == this.buffer.length) {
                        try {
                            this.notFull.await();
                        }
                        catch (InterruptedException e) {
                            throw (IOException)new InterruptedIOException().initCause(e);
                        }
                    }
                    if (this.closed) {
                        throw new IOException("Closed");
                    }
                    while (len > 0 && this.count < this.buffer.length) {
                        this.buffer[this.write++] = cbuf[off++];
                        ++this.count;
                        --len;
                        if (this.write != this.buffer.length) continue;
                        this.write = 0;
                    }
                    this.notEmpty.signal();
                }
            }
            finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void close() throws IOException {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.closed = true;
            this.notEmpty.signalAll();
            this.notFull.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    private class NbpWriter
    extends Writer {
        private NbpWriter() {
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            NonBlockingPumpReader.this.write(cbuf, off, len);
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
            NonBlockingPumpReader.this.close();
        }
    }
}

