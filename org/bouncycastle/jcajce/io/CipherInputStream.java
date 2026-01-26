/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;

public class CipherInputStream
extends FilterInputStream {
    private final Cipher cipher;
    private final byte[] inputBuffer = new byte[512];
    private boolean finalized = false;
    private boolean nextChunkCalled = false;
    private byte[] buf;
    private int maxBuf;
    private int bufOff;

    public CipherInputStream(InputStream inputStream, Cipher cipher) {
        super(inputStream);
        this.cipher = cipher;
    }

    private int nextChunk() throws IOException {
        if (this.finalized) {
            return -1;
        }
        this.nextChunkCalled = true;
        this.bufOff = 0;
        this.maxBuf = 0;
        while (this.maxBuf == 0) {
            int n = this.in.read(this.inputBuffer);
            if (n == -1) {
                this.buf = this.finaliseCipher();
                if (this.buf == null || this.buf.length == 0) {
                    return -1;
                }
                this.maxBuf = this.buf.length;
                return this.maxBuf;
            }
            this.buf = this.cipher.update(this.inputBuffer, 0, n);
            if (this.buf == null) continue;
            this.maxBuf = this.buf.length;
        }
        return this.maxBuf;
    }

    private byte[] finaliseCipher() throws InvalidCipherTextIOException, IOException {
        try {
            if (!this.nextChunkCalled) {
                this.nextChunk();
            }
            if (!this.finalized) {
                this.finalized = true;
                return this.cipher.doFinal();
            }
            return null;
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw new InvalidCipherTextIOException("Error finalising cipher", generalSecurityException);
        }
    }

    @Override
    public int read() throws IOException {
        if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
            return -1;
        }
        return this.buf[this.bufOff++] & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
            return -1;
        }
        int n3 = Math.min(n2, this.available());
        System.arraycopy(this.buf, this.bufOff, byArray, n, n3);
        this.bufOff += n3;
        return n3;
    }

    @Override
    public long skip(long l) throws IOException {
        if (l <= 0L) {
            return 0L;
        }
        int n = (int)Math.min(l, (long)this.available());
        this.bufOff += n;
        return n;
    }

    @Override
    public int available() throws IOException {
        return this.maxBuf - this.bufOff;
    }

    @Override
    public void close() throws IOException {
        try {
            this.in.close();
        }
        finally {
            if (!this.finalized) {
                this.finaliseCipher();
            }
        }
        this.bufOff = 0;
        this.maxBuf = 0;
    }

    @Override
    public void mark(int n) {
    }

    @Override
    public void reset() throws IOException {
    }

    @Override
    public boolean markSupported() {
        return false;
    }
}

