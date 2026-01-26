/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.source.JWKSetUnavailableException;

public class JWKSetParseException
extends JWKSetUnavailableException {
    private static final long serialVersionUID = 1L;

    public JWKSetParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

