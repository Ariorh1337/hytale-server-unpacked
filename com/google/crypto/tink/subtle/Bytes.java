/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

public final class Bytes {
    public static final boolean equal(byte[] x, byte[] y) {
        return MessageDigest.isEqual(x, y);
    }

    public static byte[] concat(byte[] ... chunks) throws GeneralSecurityException {
        int length = 0;
        for (byte[] chunk : chunks) {
            if (length > Integer.MAX_VALUE - chunk.length) {
                throw new GeneralSecurityException("exceeded size limit");
            }
            length += chunk.length;
        }
        byte[] res = new byte[length];
        int pos = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, res, pos, chunk.length);
            pos += chunk.length;
        }
        return res;
    }

    public static final byte[] xor(byte[] x, int offsetX, byte[] y, int offsetY, int len) {
        if (len < 0 || x.length - len < offsetX || y.length - len < offsetY) {
            throw new IllegalArgumentException("That combination of buffers, offsets and length to xor result in out-of-bond accesses.");
        }
        byte[] res = new byte[len];
        for (int i = 0; i < len; ++i) {
            res[i] = (byte)(x[i + offsetX] ^ y[i + offsetY]);
        }
        return res;
    }

    public static final void xor(ByteBuffer output, ByteBuffer x, ByteBuffer y, int len) {
        if (len < 0 || x.remaining() < len || y.remaining() < len || output.remaining() < len) {
            throw new IllegalArgumentException("That combination of buffers, offsets and length to xor result in out-of-bond accesses.");
        }
        for (int i = 0; i < len; ++i) {
            output.put((byte)(x.get() ^ y.get()));
        }
    }

    public static final byte[] xor(byte[] x, byte[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("The lengths of x and y should match.");
        }
        return Bytes.xor(x, 0, y, 0, x.length);
    }

    public static final byte[] xorEnd(byte[] a, byte[] b) {
        if (a.length < b.length) {
            throw new IllegalArgumentException("xorEnd requires a.length >= b.length");
        }
        int paddingLength = a.length - b.length;
        byte[] res = Arrays.copyOf(a, a.length);
        for (int i = 0; i < b.length; ++i) {
            int n = paddingLength + i;
            res[n] = (byte)(res[n] ^ b[i]);
        }
        return res;
    }

    public static byte[] intToByteArray(int capacity, int value) {
        if (capacity > 4 || capacity < 0) {
            throw new IllegalArgumentException("capacity must be between 0 and 4");
        }
        if (value < 0 || capacity < 4 && value >= 1 << 8 * capacity) {
            throw new IllegalArgumentException("value too large");
        }
        byte[] result = new byte[capacity];
        for (int i = 0; i < capacity; ++i) {
            result[i] = (byte)(value >> 8 * i & 0xFF);
        }
        return result;
    }

    public static int byteArrayToInt(byte[] bytes) {
        return Bytes.byteArrayToInt(bytes, bytes.length);
    }

    public static int byteArrayToInt(byte[] bytes, int length) {
        return Bytes.byteArrayToInt(bytes, 0, length);
    }

    public static int byteArrayToInt(byte[] bytes, int offset, int length) {
        if (length > 4 || length < 0) {
            throw new IllegalArgumentException("length must be between 0 and 4");
        }
        if (offset < 0 || offset + length > bytes.length) {
            throw new IllegalArgumentException("offset and length are out of bounds");
        }
        int value = 0;
        for (int i = 0; i < length; ++i) {
            value += (bytes[i + offset] & 0xFF) << i * 8;
        }
        return value;
    }

    private Bytes() {
    }
}

