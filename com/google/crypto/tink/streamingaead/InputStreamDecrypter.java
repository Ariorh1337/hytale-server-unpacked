/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.StreamingAead;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import javax.annotation.concurrent.GuardedBy;

final class InputStreamDecrypter
extends InputStream {
    @GuardedBy(value="this")
    boolean attemptedMatching = false;
    @GuardedBy(value="this")
    InputStream matchingStream = null;
    @GuardedBy(value="this")
    InputStream ciphertextStream;
    List<StreamingAead> primitives;
    byte[] associatedData;

    public InputStreamDecrypter(List<StreamingAead> primitives, InputStream ciphertextStream, byte[] associatedData) {
        this.primitives = primitives;
        this.ciphertextStream = ciphertextStream.markSupported() ? ciphertextStream : new BufferedInputStream(ciphertextStream);
        this.ciphertextStream.mark(Integer.MAX_VALUE);
        this.associatedData = (byte[])associatedData.clone();
    }

    @GuardedBy(value="this")
    private void rewind() throws IOException {
        this.ciphertextStream.reset();
    }

    @GuardedBy(value="this")
    private void disableRewinding() throws IOException {
        this.ciphertextStream.mark(0);
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    @GuardedBy(value="this")
    public synchronized int available() throws IOException {
        if (this.matchingStream == null) {
            return 0;
        }
        return this.matchingStream.available();
    }

    @Override
    @GuardedBy(value="this")
    public synchronized int read() throws IOException {
        byte[] oneByte = new byte[1];
        if (this.read(oneByte) == 1) {
            return oneByte[0] & 0xFF;
        }
        return -1;
    }

    @Override
    @GuardedBy(value="this")
    public synchronized int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    @GuardedBy(value="this")
    public synchronized int read(byte[] b, int offset, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (this.matchingStream != null) {
            return this.matchingStream.read(b, offset, len);
        }
        if (this.attemptedMatching) {
            throw new IOException("No matching key found for the ciphertext in the stream.");
        }
        this.attemptedMatching = true;
        for (StreamingAead streamingAead : this.primitives) {
            try {
                InputStream attemptedStream = streamingAead.newDecryptingStream(this.ciphertextStream, this.associatedData);
                int retValue = attemptedStream.read(b, offset, len);
                if (retValue == 0) {
                    throw new IOException("Could not read bytes from the ciphertext stream");
                }
                this.matchingStream = attemptedStream;
                this.disableRewinding();
                return retValue;
            }
            catch (IOException e) {
                this.rewind();
            }
            catch (GeneralSecurityException e) {
                this.rewind();
            }
        }
        throw new IOException("No matching key found for the ciphertext in the stream.");
    }

    @Override
    @GuardedBy(value="this")
    public synchronized void close() throws IOException {
        this.ciphertextStream.close();
    }
}

