/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.util;

import com.google.crypto.tink.subtle.Hex;
import com.google.errorprone.annotations.Immutable;
import java.util.Arrays;

@Immutable
public final class Bytes {
    private final byte[] data;

    public static Bytes copyFrom(byte[] data) {
        if (data == null) {
            throw new NullPointerException("data must be non-null");
        }
        return Bytes.copyFrom(data, 0, data.length);
    }

    public static Bytes copyFrom(byte[] data, int start, int len) {
        if (data == null) {
            throw new NullPointerException("data must be non-null");
        }
        if (start + len > data.length) {
            len = data.length - start;
        }
        return new Bytes(data, start, len);
    }

    public byte[] toByteArray() {
        byte[] result = new byte[this.data.length];
        System.arraycopy(this.data, 0, result, 0, this.data.length);
        return result;
    }

    public int size() {
        return this.data.length;
    }

    private Bytes(byte[] buf, int start, int len) {
        this.data = new byte[len];
        System.arraycopy(buf, start, this.data, 0, len);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Bytes)) {
            return false;
        }
        Bytes other = (Bytes)o;
        return Arrays.equals(other.data, this.data);
    }

    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    public String toString() {
        return "Bytes(" + Hex.encode(this.data) + ")";
    }
}

