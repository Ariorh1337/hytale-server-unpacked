/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class WriterOutputStream
extends OutputStream {
    private final Writer out;
    private final CharsetDecoder decoder;
    private final ByteBuffer decoderIn = ByteBuffer.allocate(256);
    private final CharBuffer decoderOut = CharBuffer.allocate(128);

    public WriterOutputStream(Writer out, Charset charset) {
        this(out, charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
    }

    public WriterOutputStream(Writer out, CharsetDecoder decoder) {
        this.out = out;
        this.decoder = decoder;
    }

    @Override
    public void write(int b) throws IOException {
        this.write(new byte[]{(byte)b}, 0, 1);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            int c = Math.min(len, this.decoderIn.remaining());
            this.decoderIn.put(b, off, c);
            this.processInput(false);
            len -= c;
            off += c;
        }
        this.flush();
    }

    @Override
    public void flush() throws IOException {
        this.flushOutput();
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.processInput(true);
        this.flush();
        this.out.close();
    }

    private void processInput(boolean endOfInput) throws IOException {
        CoderResult coderResult;
        this.decoderIn.flip();
        while ((coderResult = this.decoder.decode(this.decoderIn, this.decoderOut, endOfInput)).isOverflow()) {
            this.flushOutput();
        }
        if (!coderResult.isUnderflow()) {
            throw new IOException("Unexpected coder result");
        }
        this.decoderIn.compact();
    }

    private void flushOutput() throws IOException {
        if (this.decoderOut.position() > 0) {
            this.out.write(this.decoderOut.array(), 0, this.decoderOut.position());
            this.decoderOut.rewind();
        }
    }
}

