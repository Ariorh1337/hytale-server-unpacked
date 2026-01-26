/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.jwt.JwtValidator;
import com.google.crypto.tink.jwt.VerifiedJwt;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Immutable
public interface JwtPublicKeyVerifyInternal {
    public VerifiedJwt verifyAndDecodeWithKid(String var1, JwtValidator var2, Optional<String> var3) throws GeneralSecurityException;
}

