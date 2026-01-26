/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class BigIntegerEncoding {
    public static byte[] toBigEndianBytes(BigInteger n) {
        if (n.signum() == -1) {
            throw new IllegalArgumentException("n must not be negative");
        }
        return n.toByteArray();
    }

    public static byte[] toUnsignedBigEndianBytes(BigInteger n) {
        if (n.signum() == -1) {
            throw new IllegalArgumentException("n must not be negative");
        }
        byte[] twosComplement = n.toByteArray();
        if (twosComplement[0] == 0) {
            return Arrays.copyOfRange(twosComplement, 1, twosComplement.length);
        }
        return twosComplement;
    }

    public static byte[] toBigEndianBytesOfFixedLength(BigInteger n, int length) throws GeneralSecurityException {
        if (n.signum() == -1) {
            throw new IllegalArgumentException("integer must be nonnegative");
        }
        byte[] b = n.toByteArray();
        if (b.length == length) {
            return b;
        }
        if (b.length > length + 1) {
            throw new GeneralSecurityException("integer too large");
        }
        if (b.length == length + 1) {
            if (b[0] == 0) {
                return Arrays.copyOfRange(b, 1, b.length);
            }
            throw new GeneralSecurityException("integer too large");
        }
        byte[] res = new byte[length];
        System.arraycopy(b, 0, res, length - b.length, b.length);
        return res;
    }

    public static BigInteger fromUnsignedBigEndianBytes(byte[] bytes) {
        return new BigInteger(1, bytes);
    }

    private BigIntegerEncoding() {
    }
}

