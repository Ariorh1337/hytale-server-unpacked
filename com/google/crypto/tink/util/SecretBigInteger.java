/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.util;

import com.google.crypto.tink.SecretKeyAccess;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.MessageDigest;

@Immutable
public final class SecretBigInteger {
    private final BigInteger value;

    private SecretBigInteger(BigInteger value) {
        this.value = value;
    }

    public static SecretBigInteger fromBigInteger(BigInteger value, SecretKeyAccess access) {
        if (access == null) {
            throw new NullPointerException("SecretKeyAccess required");
        }
        return new SecretBigInteger(value);
    }

    public BigInteger getBigInteger(SecretKeyAccess access) {
        if (access == null) {
            throw new NullPointerException("SecretKeyAccess required");
        }
        return this.value;
    }

    public boolean equalsSecretBigInteger(SecretBigInteger other) {
        byte[] myArray = this.value.toByteArray();
        byte[] otherArray = other.value.toByteArray();
        return MessageDigest.isEqual(myArray, otherArray);
    }
}

