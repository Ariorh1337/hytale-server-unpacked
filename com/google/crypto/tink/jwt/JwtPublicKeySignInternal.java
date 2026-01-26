/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.jwt.RawJwt;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Immutable
public interface JwtPublicKeySignInternal {
    public String signAndEncodeWithKid(RawJwt var1, Optional<String> var2) throws GeneralSecurityException;
}

