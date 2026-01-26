/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64Codec;
import com.nimbusds.jose.util.BigIntegerUtils;
import com.nimbusds.jose.util.JSONStringUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Immutable
public class Base64
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String value;

    public Base64(String base64) {
        this.value = Objects.requireNonNull(base64);
    }

    public byte[] decode() {
        return Base64Codec.decode(this.value);
    }

    public BigInteger decodeToBigInteger() {
        return new BigInteger(1, this.decode());
    }

    public String decodeToString() {
        return new String(this.decode(), StandardCharset.UTF_8);
    }

    public String toJSONString() {
        return JSONStringUtils.toJSONString(this.value);
    }

    public String toString() {
        return this.value;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof Base64 && this.toString().equals(object.toString());
    }

    public static Base64 from(String base64) {
        if (base64 == null) {
            return null;
        }
        return new Base64(base64);
    }

    public static Base64 encode(byte[] bytes) {
        return new Base64(Base64Codec.encodeToString(bytes, false));
    }

    public static Base64 encode(BigInteger bigInt) {
        return Base64.encode(BigIntegerUtils.toBytesUnsigned(bigInt));
    }

    public static Base64 encode(String text) {
        return Base64.encode(text.getBytes(StandardCharset.UTF_8));
    }
}

