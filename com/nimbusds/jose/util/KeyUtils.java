/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import javax.crypto.SecretKey;

public class KeyUtils {
    public static SecretKey toAESKey(final SecretKey secretKey) {
        if (secretKey == null || secretKey.getAlgorithm().equals("AES")) {
            return secretKey;
        }
        return new SecretKey(){

            @Override
            public String getAlgorithm() {
                return "AES";
            }

            @Override
            public String getFormat() {
                return secretKey.getFormat();
            }

            @Override
            public byte[] getEncoded() {
                return secretKey.getEncoded();
            }
        };
    }

    private KeyUtils() {
    }
}

