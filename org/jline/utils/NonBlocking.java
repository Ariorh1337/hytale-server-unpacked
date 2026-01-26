/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingInputStreamImpl;
import org.jline.utils.NonBlockingPumpInputStream;
import org.jline.utils.NonBlockingPumpReader;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.NonBlockingReaderImpl;
import org.jline.utils.Timeout;

public class NonBlocking {
    public static NonBlockingPumpReader nonBlockingPumpReader() {
        return new NonBlockingPumpReader();
    }

    public static NonBlockingPumpReader nonBlockingPumpReader(int size) {
        return new NonBlockingPumpReader(size);
    }

    public static NonBlockingPumpInputStream nonBlockingPumpInputStream() {
        return new NonBlockingPumpInputStream();
    }

    public static NonBlockingPumpInputStream nonBlockingPumpInputStream(int size) {
        return new NonBlockingPumpInputStream(size);
    }

    public static NonBlockingInputStream nonBlockingStream(NonBlockingReader reader, Charset encoding) {
        return new NonBlockingReaderInputStream(reader, encoding);
    }

    public static NonBlockingInputStream nonBlocking(String name, InputStream inputStream) {
        if (inputStream instanceof NonBlockingInputStream) {
            return (NonBlockingInputStream)inputStream;
        }
        return new NonBlockingInputStreamImpl(name, inputStream);
    }

    public static NonBlockingReader nonBlocking(String name, Reader reader) {
        if (reader instanceof NonBlockingReader) {
            return (NonBlockingReader)reader;
        }
        return new NonBlockingReaderImpl(name, reader);
    }

    public static NonBlockingReader nonBlocking(String name, InputStream inputStream, Charset encoding) {
        return new NonBlockingInputStreamReader(NonBlocking.nonBlocking(name, inputStream), encoding);
    }

    private static class NonBlockingReaderInputStream
    extends NonBlockingInputStream {
        private final NonBlockingReader reader;
        private final CharsetEncoder encoder;
        private final ByteBuffer bytes;
        private final CharBuffer chars;

        private NonBlockingReaderInputStream(NonBlockingReader reader, Charset charset) {
            this.reader = reader;
            this.encoder = charset.newEncoder().onUnmappableCharacter(CodingErrorAction.REPLACE).onMalformedInput(CodingErrorAction.REPLACE);
            this.bytes = ByteBuffer.allocate(4);
            this.chars = CharBuffer.allocate(2);
            this.bytes.limit(0);
            this.chars.limit(0);
        }

        @Override
        public int available() {
            return (int)((float)this.reader.available() * this.encoder.averageBytesPerChar()) + this.bytes.remaining();
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }

        @Override
        public int read(long timeout, boolean isPeek) throws IOException {
            Timeout t = new Timeout(timeout);
            while (!this.bytes.hasRemaining() && !t.elapsed()) {
                int c = this.reader.read(t.timeout());
                if (c == -1) {
                    return -1;
                }
                if (c < 0) continue;
                if (!this.chars.hasRemaining()) {
                    this.chars.position(0);
                    this.chars.limit(0);
                }
                int l = this.chars.limit();
                this.chars.array()[this.chars.arrayOffset() + l] = (char)c;
                this.chars.limit(l + 1);
                this.bytes.clear();
                this.encoder.encode(this.chars, this.bytes, false);
                this.bytes.flip();
            }
            if (this.bytes.hasRemaining()) {
                if (isPeek) {
                    return this.bytes.get(this.bytes.position());
                }
                return this.bytes.get();
            }
            return -2;
        }
    }

    private static class NonBlockingInputStreamReader
    extends NonBlockingReader {
        private final NonBlockingInputStream input;
        private final CharsetDecoder decoder;
        private final ByteBuffer bytes;
        private final CharBuffer chars;

        public NonBlockingInputStreamReader(NonBlockingInputStream inputStream, Charset encoding) {
            this(inputStream, (encoding != null ? encoding : Charset.defaultCharset()).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
        }

        public NonBlockingInputStreamReader(NonBlockingInputStream input, CharsetDecoder decoder) {
            this.input = input;
            this.decoder = decoder;
            this.bytes = ByteBuffer.allocate(2048);
            this.chars = CharBuffer.allocate(1024);
            this.bytes.limit(0);
            this.chars.limit(0);
        }

        @Override
        protected int read(long timeout, boolean isPeek) throws IOException {
            Timeout t = new Timeout(timeout);
            while (!this.chars.hasRemaining() && !t.elapsed()) {
                int b = this.input.read(t.timeout());
                if (b == -1) {
                    return -1;
                }
                if (b < 0) continue;
                if (!this.bytes.hasRemaining()) {
                    this.bytes.position(0);
                    this.bytes.limit(0);
                }
                int l = this.bytes.limit();
                this.bytes.array()[this.bytes.arrayOffset() + l] = (byte)b;
                this.bytes.limit(l + 1);
                this.chars.clear();
                this.decoder.decode(this.bytes, this.chars, false);
                this.chars.flip();
            }
            if (this.chars.hasRemaining()) {
                if (isPeek) {
                    return this.chars.get(this.chars.position());
                }
                return this.chars.get();
            }
            return -2;
        }

        @Override
        public int readBuffered(char[] b, int off, int len, long timeout) throws IOException {
            int nb;
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || len < 0 || off + len < b.length) {
                throw new IllegalArgumentException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.chars.hasRemaining()) {
                int r = Math.min(len, this.chars.remaining());
                this.chars.get(b, off, r);
                return r;
            }
            Timeout t = new Timeout(timeout);
            while (!this.chars.hasRemaining() && !t.elapsed()) {
                if (!this.bytes.hasRemaining()) {
                    this.bytes.position(0);
                    this.bytes.limit(0);
                }
                if ((nb = this.input.readBuffered(this.bytes.array(), this.bytes.limit(), this.bytes.capacity() - this.bytes.limit(), t.timeout())) < 0) {
                    return nb;
                }
                this.bytes.limit(this.bytes.limit() + nb);
                this.chars.clear();
                this.decoder.decode(this.bytes, this.chars, false);
                this.chars.flip();
            }
            nb = Math.min(len, this.chars.remaining());
            this.chars.get(b, off, nb);
            return nb;
        }

        @Override
        public void shutdown() {
            this.input.shutdown();
        }

        @Override
        public void close() throws IOException {
            this.input.close();
        }
    }
}

