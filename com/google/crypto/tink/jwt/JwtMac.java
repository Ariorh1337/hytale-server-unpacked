/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.jwt.JwtValidator;
import com.google.crypto.tink.jwt.RawJwt;
import com.google.crypto.tink.jwt.VerifiedJwt;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface JwtMac {
    public String computeMacAndEncode(RawJwt var1) throws GeneralSecurityException;

    public VerifiedJwt verifyMacAndDecode(String var1, JwtValidator var2) throws GeneralSecurityException;
}

