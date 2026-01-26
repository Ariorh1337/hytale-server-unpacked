/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;

public interface JWTClaimsSetVerifier<C extends SecurityContext> {
    public void verify(JWTClaimsSet var1, C var2) throws BadJWTException;
}

