/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.subtle.NonceBasedStreamingAead;
import com.google.crypto.tink.subtle.StreamSegmentDecrypter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.security.GeneralSecurityException;
import java.util.Arrays;

class StreamingAeadDecryptingChannel
implements ReadableByteChannel {
    private static final int PLAINTEXT_SEGMENT_EXTRA_SIZE = 16;
    private ReadableByteChannel ciphertextChannel;
    private ByteBuffer ciphertextSegment;
    private ByteBuffer plaintextSegment;
    private ByteBuffer header;
    private boolean headerRead;
    private boolean endOfCiphertext;
    private boolean endOfPlaintext;
    private boolean definedState;
    private final byte[] associatedData;
    private int segmentNr;
    private final StreamSegmentDecrypter decrypter;
    private final int ciphertextSegmentSize;
    private final int firstCiphertextSegmentSize;

    public StreamingAeadDecryptingChannel(NonceBasedStreamingAead streamAead, ReadableByteChannel ciphertextChannel, byte[] associatedData) throws GeneralSecurityException, IOException {
        this.decrypter = streamAead.newStreamSegmentDecrypter();
        this.ciphertextChannel = ciphertextChannel;
        this.header = ByteBuffer.allocate(streamAead.getHeaderLength());
        this.associatedData = Arrays.copyOf(associatedData, associatedData.length);
        this.ciphertextSegmentSize = streamAead.getCiphertextSegmentSize();
        this.ciphertextSegment = ByteBuffer.allocate(this.ciphertextSegmentSize + 1);
        this.ciphertextSegment.limit(0);
        this.firstCiphertextSegmentSize = this.ciphertextSegmentSize - streamAead.getCiphertextOffset();
        this.plaintextSegment = ByteBuffer.allocate(streamAead.getPlaintextSegmentSize() + 16);
        this.plaintextSegment.limit(0);
        this.headerRead = false;
        this.endOfCiphertext = false;
        this.endOfPlaintext = false;
        this.segmentNr = 0;
        this.definedState = true;
    }

    private void readSomeCiphertext(ByteBuffer buffer) throws IOException {
        int read;
        while ((read = this.ciphertextChannel.read(buffer)) > 0 && buffer.remaining() > 0) {
        }
        if (read < -1) {
            throw new IOException("Unexpected return value from ciphertextChannel.read");
        }
        if (read == -1) {
            this.endOfCiphertext = true;
        }
    }

    private boolean tryReadHeader() throws IOException {
        if (this.endOfCiphertext) {
            throw new IOException("Ciphertext is too short");
        }
        this.readSomeCiphertext(this.header);
        if (this.header.remaining() > 0) {
            return false;
        }
        this.header.flip();
        try {
            this.decrypter.init(this.header, this.associatedData);
            this.headerRead = true;
        }
        catch (GeneralSecurityException ex) {
            this.setUndefinedState();
            throw new IOException(ex);
        }
        return true;
    }

    private void setUndefinedState() {
        this.definedState = false;
        this.plaintextSegment.limit(0);
    }

    private boolean tryLoadSegment() throws IOException {
        if (!this.endOfCiphertext) {
            this.readSomeCiphertext(this.ciphertextSegment);
        }
        if (this.ciphertextSegment.remaining() > 0 && !this.endOfCiphertext) {
            return false;
        }
        byte lastByte = 0;
        if (!this.endOfCiphertext) {
            lastByte = this.ciphertextSegment.get(this.ciphertextSegment.position() - 1);
            this.ciphertextSegment.position(this.ciphertextSegment.position() - 1);
        }
        this.ciphertextSegment.flip();
        this.plaintextSegment.clear();
        try {
            this.decrypter.decryptSegment(this.ciphertextSegment, this.segmentNr, this.endOfCiphertext, this.plaintextSegment);
        }
        catch (GeneralSecurityException ex) {
            this.setUndefinedState();
            throw new IOException(ex.getMessage() + "\n" + this.toString() + "\nsegmentNr:" + this.segmentNr + " endOfCiphertext:" + this.endOfCiphertext, ex);
        }
        ++this.segmentNr;
        this.plaintextSegment.flip();
        this.ciphertextSegment.clear();
        if (!this.endOfCiphertext) {
            this.ciphertextSegment.clear();
            this.ciphertextSegment.limit(this.ciphertextSegmentSize + 1);
            this.ciphertextSegment.put(lastByte);
        }
        return true;
    }

    @Override
    public synchronized int read(ByteBuffer dst) throws IOException {
        int bytesRead;
        if (!this.definedState) {
            throw new IOException("This StreamingAeadDecryptingChannel is in an undefined state");
        }
        if (!this.headerRead) {
            if (!this.tryReadHeader()) {
                return 0;
            }
            this.ciphertextSegment.clear();
            this.ciphertextSegment.limit(this.firstCiphertextSegmentSize + 1);
        }
        if (this.endOfPlaintext) {
            return -1;
        }
        int startPosition = dst.position();
        while (dst.remaining() > 0) {
            if (this.plaintextSegment.remaining() == 0) {
                if (this.endOfCiphertext) {
                    this.endOfPlaintext = true;
                    break;
                }
                if (!this.tryLoadSegment()) break;
            }
            if (this.plaintextSegment.remaining() <= dst.remaining()) {
                dst.put(this.plaintextSegment);
                continue;
            }
            int sliceSize = dst.remaining();
            ByteBuffer slice = this.plaintextSegment.duplicate();
            slice.limit(slice.position() + sliceSize);
            dst.put(slice);
            this.plaintextSegment.position(this.plaintextSegment.position() + sliceSize);
        }
        if ((bytesRead = dst.position() - startPosition) == 0 && this.endOfPlaintext) {
            return -1;
        }
        return bytesRead;
    }

    @Override
    public synchronized void close() throws IOException {
        this.ciphertextChannel.close();
    }

    @Override
    public synchronized boolean isOpen() {
        return this.ciphertextChannel.isOpen();
    }

    public synchronized String toString() {
        StringBuilder res = new StringBuilder();
        res.append("StreamingAeadDecryptingChannel").append("\nsegmentNr:").append(this.segmentNr).append("\nciphertextSegmentSize:").append(this.ciphertextSegmentSize).append("\nheaderRead:").append(this.headerRead).append("\nendOfCiphertext:").append(this.endOfCiphertext).append("\nendOfPlaintext:").append(this.endOfPlaintext).append("\ndefinedState:").append(this.definedState).append("\nHeader").append(" position:").append(this.header.position()).append(" limit:").append(this.header.position()).append("\nciphertextSgement").append(" position:").append(this.ciphertextSegment.position()).append(" limit:").append(this.ciphertextSegment.limit()).append("\nplaintextSegment").append(" position:").append(this.plaintextSegment.position()).append(" limit:").append(this.plaintextSegment.limit());
        return res.toString();
    }
}

