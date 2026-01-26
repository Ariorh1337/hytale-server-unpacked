/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.KeyException;
import com.nimbusds.jose.jwk.JWK;

public class JWKException
extends KeyException {
    public JWKException(String message) {
        super(message);
    }

    public static JWKException expectedClass(Class<? extends JWK> expectedJWKClass) {
        return new JWKException("Invalid JWK: Must be an instance of " + expectedJWKClass);
    }

    public static JWKException expectedPrivate() {
        return new JWKException("Expected private JWK but none available");
    }
}

