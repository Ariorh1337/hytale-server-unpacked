/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.JOSEProcessorConfiguration;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;

public interface JWTProcessorConfiguration<C extends SecurityContext>
extends JOSEProcessorConfiguration<C> {
    public JWTClaimsSetAwareJWSKeySelector<C> getJWTClaimsSetAwareJWSKeySelector();

    public void setJWTClaimsSetAwareJWSKeySelector(JWTClaimsSetAwareJWSKeySelector<C> var1);

    public JWTClaimsSetVerifier<C> getJWTClaimsSetVerifier();

    public void setJWTClaimsSetVerifier(JWTClaimsSetVerifier<C> var1);
}

