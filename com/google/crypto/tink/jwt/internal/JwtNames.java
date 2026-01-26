/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt.internal;

public final class JwtNames {
    public static final String CLAIM_ISSUER = "iss";
    public static final String CLAIM_SUBJECT = "sub";
    public static final String CLAIM_AUDIENCE = "aud";
    public static final String CLAIM_EXPIRATION = "exp";
    public static final String CLAIM_NOT_BEFORE = "nbf";
    public static final String CLAIM_ISSUED_AT = "iat";
    public static final String CLAIM_JWT_ID = "jti";
    public static final String HEADER_ALGORITHM = "alg";
    public static final String HEADER_KEY_ID = "kid";
    public static final String HEADER_TYPE = "typ";
    public static final String HEADER_CRITICAL = "crit";

    public static void validate(String name) {
        if (JwtNames.isRegisteredName(name)) {
            throw new IllegalArgumentException(String.format("claim '%s' is invalid because it's a registered name; use the corresponding setter method.", name));
        }
    }

    public static boolean isRegisteredName(String name) {
        return name.equals(CLAIM_ISSUER) || name.equals(CLAIM_SUBJECT) || name.equals(CLAIM_AUDIENCE) || name.equals(CLAIM_EXPIRATION) || name.equals(CLAIM_NOT_BEFORE) || name.equals(CLAIM_ISSUED_AT) || name.equals(CLAIM_JWT_ID);
    }

    private JwtNames() {
    }
}

