/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.jline.utils.ClosedException;

public class PumpReader
extends Reader {
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private final CharBuffer readBuffer;
    private final CharBuffer writeBuffer;
    private final Writer writer;
    private boolean closed;

    public PumpReader() {
        this(4096);
    }

    public PumpReader(int bufferSize) {
        char[] buf = new char[Math.max(bufferSize, 2)];
        this.readBuffer = CharBuffer.wrap(buf);
        this.writeBuffer = CharBuffer.wrap(buf);
        this.writer = new Writer(this);
        this.readBuffer.limit(0);
    }

    public java.io.Writer getWriter() {
        return this.writer;
    }

    public java.io.InputStream createInputStream(Charset charset) {
        return new InputStream(this, charset);
    }

    private boolean waitForMoreInput() throws InterruptedIOException {
        if (!this.writeBuffer.hasRemaining()) {
            throw new AssertionError((Object)"No space in write buffer");
        }
        int oldRemaining = this.readBuffer.remaining();
        do {
            if (this.closed) {
                return false;
            }
            this.notifyAll();
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
        } while (this.readBuffer.remaining() <= oldRemaining);
        return true;
    }

    private boolean wait(CharBuffer buffer) throws InterruptedIOException {
        while (!buffer.hasRemaining()) {
            if (this.closed) {
                return false;
            }
            this.notifyAll();
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
        }
        return true;
    }

    private boolean waitForInput() throws InterruptedIOException {
        return this.wait(this.readBuffer);
    }

    private void waitForBufferSpace() throws InterruptedIOException, ClosedException {
        if (!this.wait(this.writeBuffer) || this.closed) {
            throw new ClosedException();
        }
    }

    private static boolean rewind(CharBuffer buffer, CharBuffer other) {
        if (buffer.position() > other.position()) {
            other.limit(buffer.position());
        }
        if (buffer.position() == buffer.capacity()) {
            buffer.rewind();
            buffer.limit(other.position());
            return true;
        }
        return false;
    }

    private boolean rewindReadBuffer() {
        boolean rw = PumpReader.rewind(this.readBuffer, this.writeBuffer) && this.readBuffer.hasRemaining();
        this.notifyAll();
        return rw;
    }

    private void rewindWriteBuffer() {
        PumpReader.rewind(this.writeBuffer, this.readBuffer);
        this.notifyAll();
    }

    @Override
    public synchronized boolean ready() {
        return this.readBuffer.hasRemaining();
    }

    public synchronized int available() {
        int count = this.readBuffer.remaining();
        if (this.writeBuffer.position() < this.readBuffer.position()) {
            count += this.writeBuffer.position();
        }
        return count;
    }

    @Override
    public synchronized int read() throws IOException {
        if (!this.waitForInput()) {
            return -1;
        }
        char b = this.readBuffer.get();
        this.rewindReadBuffer();
        return b;
    }

    private int copyFromBuffer(char[] cbuf, int off, int len) {
        len = Math.min(len, this.readBuffer.remaining());
        this.readBuffer.get(cbuf, off, len);
        return len;
    }

    @Override
    public synchronized int read(char[] cbuf, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (!this.waitForInput()) {
            return -1;
        }
        int count = this.copyFromBuffer(cbuf, off, len);
        if (this.rewindReadBuffer() && count < len) {
            count += this.copyFromBuffer(cbuf, off + count, len - count);
            this.rewindReadBuffer();
        }
        return count;
    }

    @Override
    public synchronized int read(CharBuffer target) throws IOException {
        if (!target.hasRemaining()) {
            return 0;
        }
        if (!this.waitForInput()) {
            return -1;
        }
        int count = this.readBuffer.read(target);
        if (this.rewindReadBuffer() && target.hasRemaining()) {
            count += this.readBuffer.read(target);
            this.rewindReadBuffer();
        }
        return count;
    }

    private void encodeBytes(CharsetEncoder encoder, ByteBuffer output) throws IOException {
        int oldPos = output.position();
        CoderResult result = encoder.encode(this.readBuffer, output, false);
        int encodedCount = output.position() - oldPos;
        if (result.isUnderflow()) {
            boolean hasMoreInput = this.rewindReadBuffer();
            boolean reachedEndOfInput = false;
            if (encodedCount == 0 && !hasMoreInput) {
                boolean bl = reachedEndOfInput = !this.waitForMoreInput();
            }
            if ((result = encoder.encode(this.readBuffer, output, reachedEndOfInput)).isError()) {
                result.throwException();
            }
            if (!reachedEndOfInput && output.position() - oldPos == 0) {
                throw new AssertionError((Object)"Failed to encode any chars");
            }
            this.rewindReadBuffer();
        } else if (result.isOverflow()) {
            if (encodedCount == 0) {
                throw new AssertionError((Object)"Output buffer has not enough space");
            }
        } else {
            result.throwException();
        }
    }

    synchronized int readBytes(CharsetEncoder encoder, byte[] b, int off, int len) throws IOException {
        if (!this.waitForInput()) {
            return 0;
        }
        ByteBuffer output = ByteBuffer.wrap(b, off, len);
        this.encodeBytes(encoder, output);
        return output.position() - off;
    }

    synchronized void readBytes(CharsetEncoder encoder, ByteBuffer output) throws IOException {
        if (!this.waitForInput()) {
            return;
        }
        this.encodeBytes(encoder, output);
    }

    synchronized void write(char c) throws IOException {
        this.waitForBufferSpace();
        this.writeBuffer.put(c);
        this.rewindWriteBuffer();
    }

    synchronized void write(char[] cbuf, int off, int len) throws IOException {
        while (len > 0) {
            this.waitForBufferSpace();
            int count = Math.min(len, this.writeBuffer.remaining());
            this.writeBuffer.put(cbuf, off, count);
            off += count;
            len -= count;
            this.rewindWriteBuffer();
        }
    }

    synchronized void write(String str, int off, int len) throws IOException {
        char[] buf = this.writeBuffer.array();
        while (len > 0) {
            this.waitForBufferSpace();
            int count = Math.min(len, this.writeBuffer.remaining());
            str.getChars(off, off + count, buf, this.writeBuffer.position());
            this.writeBuffer.position(this.writeBuffer.position() + count);
            off += count;
            len -= count;
            this.rewindWriteBuffer();
        }
    }

    synchronized void flush() {
        if (this.readBuffer.hasRemaining()) {
            this.notifyAll();
        }
    }

    @Override
    public synchronized void close() throws IOException {
        this.closed = true;
        this.notifyAll();
    }

    private static class Writer
    extends java.io.Writer {
        private final PumpReader reader;

        private Writer(PumpReader reader) {
            this.reader = reader;
        }

        @Override
        public void write(int c) throws IOException {
            this.reader.write((char)c);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            this.reader.write(cbuf, off, len);
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            this.reader.write(str, off, len);
        }

        @Override
        public void flush() throws IOException {
            this.reader.flush();
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }
    }

    private static class InputStream
    extends java.io.InputStream {
        private final PumpReader reader;
        private final CharsetEncoder encoder;
        private final ByteBuffer buffer;

        private InputStream(PumpReader reader, Charset charset) {
            this.reader = reader;
            this.encoder = charset.newEncoder().onUnmappableCharacter(CodingErrorAction.REPLACE).onMalformedInput(CodingErrorAction.REPLACE);
            this.buffer = ByteBuffer.allocate((int)Math.ceil(this.encoder.maxBytesPerChar() * 2.0f));
            this.buffer.limit(0);
        }

        @Override
        public int available() throws IOException {
            return (int)((double)this.reader.available() * (double)this.encoder.averageBytesPerChar()) + this.buffer.remaining();
        }

        @Override
        public int read() throws IOException {
            if (!this.buffer.hasRemaining() && !this.readUsingBuffer()) {
                return -1;
            }
            return this.buffer.get() & 0xFF;
        }

        private boolean readUsingBuffer() throws IOException {
            this.buffer.clear();
            this.reader.readBytes(this.encoder, this.buffer);
            this.buffer.flip();
            return this.buffer.hasRemaining();
        }

        private int copyFromBuffer(byte[] b, int off, int len) {
            len = Math.min(len, this.buffer.remaining());
            this.buffer.get(b, off, len);
            return len;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int read;
            if (len == 0) {
                return 0;
            }
            if (this.buffer.hasRemaining()) {
                read = this.copyFromBuffer(b, off, len);
                if (read == len) {
                    return len;
                }
                off += read;
                len -= read;
            } else {
                read = 0;
            }
            if (len >= this.buffer.capacity()) {
                read += this.reader.readBytes(this.encoder, b, off, len);
            } else if (this.readUsingBuffer()) {
                read += this.copyFromBuffer(b, off, len);
            }
            return read == 0 ? -1 : read;
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }
    }
}

