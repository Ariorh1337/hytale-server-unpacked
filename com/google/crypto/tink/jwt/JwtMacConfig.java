/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.jwt.JwtHmacKeyManager;
import com.google.crypto.tink.jwt.JwtMacWrapper;
import java.security.GeneralSecurityException;

public final class JwtMacConfig {
    public static final String JWT_HMAC_TYPE_URL = JwtHmacKeyManager.getKeyType();

    public static void register() throws GeneralSecurityException {
        JwtHmacKeyManager.register(true);
        JwtMacWrapper.register();
    }

    private JwtMacConfig() {
    }
}

