/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class Bits {
    Bits() {
    }

    static void readFully(InputStream inputStream, byte[] buffer) throws IOException {
        Bits.readFully(inputStream, buffer, 0, buffer.length);
    }

    static void readFully(InputStream inputStream, byte[] buffer, int offset, int length) throws IOException {
        if (buffer.length < length + offset) {
            throw new IllegalArgumentException("Buffer is too small");
        }
        int arrayOffset = offset;
        int bytesToRead = length;
        while (bytesToRead > 0) {
            int bytesRead = inputStream.read(buffer, arrayOffset, bytesToRead);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            bytesToRead -= bytesRead;
            arrayOffset += bytesRead;
        }
    }

    static int readInt(InputStream inputStream, byte[] buffer) throws IOException {
        Bits.readFully(inputStream, buffer, 0, 4);
        return Bits.readInt(buffer);
    }

    static int readInt(byte[] buffer) {
        return Bits.readInt(buffer, 0);
    }

    static int readInt(byte[] buffer, int offset) {
        int x = 0;
        x |= (0xFF & buffer[offset + 0]) << 0;
        x |= (0xFF & buffer[offset + 1]) << 8;
        x |= (0xFF & buffer[offset + 2]) << 16;
        return x |= (0xFF & buffer[offset + 3]) << 24;
    }

    static long readLong(InputStream inputStream) throws IOException {
        return Bits.readLong(inputStream, new byte[8]);
    }

    static long readLong(InputStream inputStream, byte[] buffer) throws IOException {
        Bits.readFully(inputStream, buffer, 0, 8);
        return Bits.readLong(buffer);
    }

    static long readLong(byte[] buffer) {
        return Bits.readLong(buffer, 0);
    }

    static long readLong(byte[] buffer, int offset) {
        long x = 0L;
        x |= (0xFFL & (long)buffer[offset + 0]) << 0;
        x |= (0xFFL & (long)buffer[offset + 1]) << 8;
        x |= (0xFFL & (long)buffer[offset + 2]) << 16;
        x |= (0xFFL & (long)buffer[offset + 3]) << 24;
        x |= (0xFFL & (long)buffer[offset + 4]) << 32;
        x |= (0xFFL & (long)buffer[offset + 5]) << 40;
        x |= (0xFFL & (long)buffer[offset + 6]) << 48;
        return x |= (0xFFL & (long)buffer[offset + 7]) << 56;
    }
}

