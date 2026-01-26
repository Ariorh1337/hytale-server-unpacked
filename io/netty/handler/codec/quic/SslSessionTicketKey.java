/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import java.util.Arrays;

public final class SslSessionTicketKey {
    public static final int NAME_SIZE = 16;
    public static final int HMAC_KEY_SIZE = 16;
    public static final int AES_KEY_SIZE = 16;
    public static final int TICKET_KEY_SIZE = 48;
    final byte[] name;
    final byte[] hmacKey;
    final byte[] aesKey;

    public SslSessionTicketKey(byte[] name, byte[] hmacKey, byte[] aesKey) {
        if (name == null || name.length != 16) {
            throw new IllegalArgumentException("Length of name must be 16");
        }
        if (hmacKey == null || hmacKey.length != 16) {
            throw new IllegalArgumentException("Length of hmacKey must be 16");
        }
        if (aesKey == null || aesKey.length != 16) {
            throw new IllegalArgumentException("Length of aesKey must be 16");
        }
        this.name = (byte[])name.clone();
        this.hmacKey = (byte[])hmacKey.clone();
        this.aesKey = (byte[])aesKey.clone();
    }

    public byte[] name() {
        return (byte[])this.name.clone();
    }

    public byte[] hmacKey() {
        return (byte[])this.hmacKey.clone();
    }

    public byte[] aesKey() {
        return (byte[])this.aesKey.clone();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SslSessionTicketKey that = (SslSessionTicketKey)o;
        if (!Arrays.equals(this.name, that.name)) {
            return false;
        }
        if (!Arrays.equals(this.hmacKey, that.hmacKey)) {
            return false;
        }
        return Arrays.equals(this.aesKey, that.aesKey);
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.name);
        result = 31 * result + Arrays.hashCode(this.hmacKey);
        result = 31 * result + Arrays.hashCode(this.aesKey);
        return result;
    }

    public String toString() {
        return "SessionTicketKey{name=" + Arrays.toString(this.name) + ", hmacKey=" + Arrays.toString(this.hmacKey) + ", aesKey=" + Arrays.toString(this.aesKey) + '}';
    }
}

