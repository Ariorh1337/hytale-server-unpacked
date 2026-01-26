/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import org.jline.utils.ClosedException;

public class InputStreamReader
extends Reader {
    private InputStream in;
    private static final int BUFFER_SIZE = 4;
    private boolean endOfInput = false;
    CharsetDecoder decoder;
    ByteBuffer bytes = ByteBuffer.allocate(4);
    char pending = (char)65535;

    public InputStreamReader(InputStream in) {
        super(in);
        this.in = in;
        this.decoder = Charset.defaultCharset().newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        this.bytes.limit(0);
    }

    public InputStreamReader(InputStream in, String enc) throws UnsupportedEncodingException {
        super(in);
        if (enc == null) {
            throw new NullPointerException();
        }
        this.in = in;
        try {
            this.decoder = Charset.forName(enc).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        }
        catch (IllegalArgumentException e) {
            throw (UnsupportedEncodingException)new UnsupportedEncodingException(enc).initCause(e);
        }
        this.bytes.limit(0);
    }

    public InputStreamReader(InputStream in, CharsetDecoder dec) {
        super(in);
        dec.averageCharsPerByte();
        this.in = in;
        this.decoder = dec;
        this.bytes.limit(0);
    }

    public InputStreamReader(InputStream in, Charset charset) {
        super(in);
        this.in = in;
        this.decoder = charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        this.bytes.limit(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            this.decoder = null;
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
        }
    }

    public String getEncoding() {
        if (!this.isOpen()) {
            return null;
        }
        return this.decoder.charset().name();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            if (!this.isOpen()) {
                throw new ClosedException("InputStreamReader is closed.");
            }
            if (this.pending != '\uffff') {
                char c = this.pending;
                this.pending = (char)65535;
                return c;
            }
            char[] buf = new char[2];
            int nb = this.read(buf, 0, 2);
            if (nb == 2) {
                this.pending = buf[1];
            }
            if (nb > 0) {
                return buf[0];
            }
            return -1;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read(char[] buf, int offset, int length) throws IOException {
        Object object = this.lock;
        synchronized (object) {
            boolean needInput;
            if (!this.isOpen()) {
                throw new IOException("InputStreamReader is closed.");
            }
            if (offset < 0 || offset > buf.length - length || length < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (length == 0) {
                return 0;
            }
            CharBuffer out = CharBuffer.wrap(buf, offset, length);
            CoderResult result = CoderResult.UNDERFLOW;
            boolean bl = needInput = !this.bytes.hasRemaining();
            while (out.position() == offset) {
                if (needInput) {
                    try {
                        if (this.in.available() == 0 && out.position() > offset) {
                            break;
                        }
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    int off = this.bytes.arrayOffset() + this.bytes.limit();
                    int was_red = this.in.read(this.bytes.array(), off, 1);
                    if (was_red == -1) {
                        this.endOfInput = true;
                        break;
                    }
                    if (was_red == 0) break;
                    this.bytes.limit(this.bytes.limit() + was_red);
                }
                if (!(result = this.decoder.decode(this.bytes, out, false)).isUnderflow()) break;
                if (this.bytes.limit() == this.bytes.capacity()) {
                    this.bytes.compact();
                    this.bytes.limit(this.bytes.position());
                    this.bytes.position(0);
                }
                needInput = true;
            }
            if (result == CoderResult.UNDERFLOW && this.endOfInput) {
                result = this.decoder.decode(this.bytes, out, true);
                this.decoder.flush(out);
                this.decoder.reset();
            }
            if (result.isMalformed()) {
                throw new MalformedInputException(result.length());
            }
            if (result.isUnmappable()) {
                throw new UnmappableCharacterException(result.length());
            }
            return out.position() - offset == 0 ? -1 : out.position() - offset;
        }
    }

    private boolean isOpen() {
        return this.in != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean ready() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            if (this.in == null) {
                throw new IOException("InputStreamReader is closed.");
            }
            try {
                return this.bytes.hasRemaining() || this.in.available() > 0;
            }
            catch (IOException e) {
                return false;
            }
        }
    }
}

