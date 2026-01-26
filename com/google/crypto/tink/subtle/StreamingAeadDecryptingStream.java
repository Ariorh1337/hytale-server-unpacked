/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.subtle.NonceBasedStreamingAead;
import com.google.crypto.tink.subtle.StreamSegmentDecrypter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;

class StreamingAeadDecryptingStream
extends FilterInputStream {
    private static final int PLAINTEXT_SEGMENT_EXTRA_SIZE = 16;
    private final ByteBuffer ciphertextSegment;
    private final ByteBuffer plaintextSegment;
    private final int headerLength;
    private boolean headerRead;
    private boolean endOfCiphertext;
    private boolean endOfPlaintext;
    private boolean decryptionErrorOccured;
    private final byte[] aad;
    private int segmentNr;
    private final StreamSegmentDecrypter decrypter;
    private final int ciphertextSegmentSize;
    private final int firstCiphertextSegmentSize;

    private static Buffer toBuffer(ByteBuffer b) {
        return b;
    }

    public StreamingAeadDecryptingStream(NonceBasedStreamingAead streamAead, InputStream ciphertextStream, byte[] associatedData) throws GeneralSecurityException, IOException {
        super(ciphertextStream);
        this.decrypter = streamAead.newStreamSegmentDecrypter();
        this.headerLength = streamAead.getHeaderLength();
        this.aad = Arrays.copyOf(associatedData, associatedData.length);
        this.ciphertextSegmentSize = streamAead.getCiphertextSegmentSize();
        this.ciphertextSegment = ByteBuffer.allocate(this.ciphertextSegmentSize + 1);
        StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).limit(0);
        this.firstCiphertextSegmentSize = this.ciphertextSegmentSize - streamAead.getCiphertextOffset();
        this.plaintextSegment = ByteBuffer.allocate(streamAead.getPlaintextSegmentSize() + 16);
        StreamingAeadDecryptingStream.toBuffer(this.plaintextSegment).limit(0);
        this.headerRead = false;
        this.endOfCiphertext = false;
        this.endOfPlaintext = false;
        this.segmentNr = 0;
        this.decryptionErrorOccured = false;
    }

    private void readHeader() throws IOException {
        if (this.headerRead) {
            this.setDecryptionErrorOccured();
            throw new IOException("Decryption failed.");
        }
        ByteBuffer header = ByteBuffer.allocate(this.headerLength);
        while (header.remaining() > 0) {
            int read = this.in.read(header.array(), StreamingAeadDecryptingStream.toBuffer(header).position(), header.remaining());
            if (read < 0) {
                this.setDecryptionErrorOccured();
                throw new IOException("Ciphertext is too short");
            }
            if (read == 0) {
                throw new IOException("Could not read bytes from the ciphertext stream");
            }
            StreamingAeadDecryptingStream.toBuffer(header).position(StreamingAeadDecryptingStream.toBuffer(header).position() + read);
        }
        StreamingAeadDecryptingStream.toBuffer(header).flip();
        try {
            this.decrypter.init(header, this.aad);
        }
        catch (GeneralSecurityException ex) {
            throw new IOException(ex);
        }
        this.headerRead = true;
    }

    private void setDecryptionErrorOccured() {
        this.decryptionErrorOccured = true;
        StreamingAeadDecryptingStream.toBuffer(this.plaintextSegment).limit(0);
    }

    private void loadSegment() throws IOException {
        while (!this.endOfCiphertext && this.ciphertextSegment.remaining() > 0) {
            int read = this.in.read(this.ciphertextSegment.array(), this.ciphertextSegment.position(), this.ciphertextSegment.remaining());
            if (read > 0) {
                StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).position(StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).position() + read);
                continue;
            }
            if (read == -1) {
                this.endOfCiphertext = true;
                continue;
            }
            if (read == 0) {
                throw new IOException("Could not read bytes from the ciphertext stream");
            }
            throw new IOException("Unexpected return value from in.read");
        }
        byte lastByte = 0;
        if (!this.endOfCiphertext) {
            lastByte = this.ciphertextSegment.get(StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).position() - 1);
            StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).position(StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).position() - 1);
        }
        StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).flip();
        StreamingAeadDecryptingStream.toBuffer(this.plaintextSegment).clear();
        try {
            this.decrypter.decryptSegment(this.ciphertextSegment, this.segmentNr, this.endOfCiphertext, this.plaintextSegment);
        }
        catch (GeneralSecurityException ex) {
            this.setDecryptionErrorOccured();
            throw new IOException(ex.getMessage() + "\n" + this.toString() + "\nsegmentNr:" + this.segmentNr + " endOfCiphertext:" + this.endOfCiphertext, ex);
        }
        ++this.segmentNr;
        StreamingAeadDecryptingStream.toBuffer(this.plaintextSegment).flip();
        StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).clear();
        if (!this.endOfCiphertext) {
            StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).clear();
            StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).limit(this.ciphertextSegmentSize + 1);
            this.ciphertextSegment.put(lastByte);
        }
    }

    @Override
    public int read() throws IOException {
        byte[] oneByte = new byte[1];
        int ret = this.read(oneByte, 0, 1);
        if (ret == 1) {
            return oneByte[0] & 0xFF;
        }
        if (ret == -1) {
            return ret;
        }
        throw new IOException("Reading failed");
    }

    @Override
    public int read(byte[] dst) throws IOException {
        return this.read(dst, 0, dst.length);
    }

    @Override
    public synchronized int read(byte[] dst, int offset, int length) throws IOException {
        int bytesRead;
        int sliceSize;
        if (this.decryptionErrorOccured) {
            throw new IOException("Decryption failed.");
        }
        if (!this.headerRead) {
            this.readHeader();
            StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).clear();
            StreamingAeadDecryptingStream.toBuffer(this.ciphertextSegment).limit(this.firstCiphertextSegmentSize + 1);
        }
        if (this.endOfPlaintext) {
            return -1;
        }
        for (bytesRead = 0; bytesRead < length; bytesRead += sliceSize) {
            if (this.plaintextSegment.remaining() == 0) {
                if (this.endOfCiphertext) {
                    this.endOfPlaintext = true;
                    break;
                }
                this.loadSegment();
            }
            sliceSize = Math.min(this.plaintextSegment.remaining(), length - bytesRead);
            this.plaintextSegment.get(dst, bytesRead + offset, sliceSize);
        }
        if (bytesRead == 0 && this.endOfPlaintext) {
            return -1;
        }
        return bytesRead;
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
    }

    @Override
    public synchronized int available() {
        return this.plaintextSegment.remaining();
    }

    @Override
    public synchronized void mark(int readlimit) {
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public long skip(long n) throws IOException {
        int bytesRead;
        long remaining;
        long maxSkipBufferSize = this.ciphertextSegmentSize;
        if (n <= 0L) {
            return 0L;
        }
        int size = (int)Math.min(maxSkipBufferSize, remaining);
        byte[] skipBuffer = new byte[size];
        for (remaining = n; remaining > 0L && (bytesRead = this.read(skipBuffer, 0, (int)Math.min((long)size, remaining))) > 0; remaining -= (long)bytesRead) {
        }
        return n - remaining;
    }

    public synchronized String toString() {
        StringBuilder res = new StringBuilder();
        res.append("StreamingAeadDecryptingStream").append("\nsegmentNr:").append(this.segmentNr).append("\nciphertextSegmentSize:").append(this.ciphertextSegmentSize).append("\nheaderRead:").append(this.headerRead).append("\nendOfCiphertext:").append(this.endOfCiphertext).append("\nendOfPlaintext:").append(this.endOfPlaintext).append("\ndecryptionErrorOccured:").append(this.decryptionErrorOccured).append("\nciphertextSgement").append(" position:").append(this.ciphertextSegment.position()).append(" limit:").append(this.ciphertextSegment.limit()).append("\nplaintextSegment").append(" position:").append(this.plaintextSegment.position()).append(" limit:").append(this.plaintextSegment.limit());
        return res.toString();
    }
}

