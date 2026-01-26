/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64Codec;
import com.nimbusds.jose.util.BigIntegerUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.math.BigInteger;

@Immutable
public class Base64URL
extends Base64 {
    public Base64URL(String base64URL) {
        super(base64URL);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Base64URL && this.toString().equals(object.toString());
    }

    public static Base64URL from(String base64URL) {
        if (base64URL == null) {
            return null;
        }
        return new Base64URL(base64URL);
    }

    public static Base64URL encode(byte[] bytes) {
        return new Base64URL(Base64Codec.encodeToString(bytes, true));
    }

    public static Base64URL encode(BigInteger bigInt) {
        return Base64URL.encode(BigIntegerUtils.toBytesUnsigned(bigInt));
    }

    public static Base64URL encode(String text) {
        return Base64URL.encode(text.getBytes(StandardCharset.UTF_8));
    }
}

