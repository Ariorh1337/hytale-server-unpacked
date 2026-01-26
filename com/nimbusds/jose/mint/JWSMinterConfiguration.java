/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.mint;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.produce.JWSSignerFactory;

public interface JWSMinterConfiguration<C extends SecurityContext> {
    public JWKSource<C> getJWKSource();

    public void setJWKSource(JWKSource<C> var1);

    public JWSSignerFactory getJWSSignerFactory();

    public void setJWSSignerFactory(JWSSignerFactory var1);
}

