/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWKSecurityContext;
import java.util.List;

public class JWKSecurityContextJWKSet
implements JWKSource<JWKSecurityContext> {
    @Override
    public List<JWK> get(JWKSelector jwkSelector, JWKSecurityContext context) throws KeySourceException {
        return jwkSelector.select(new JWKSet(context.getKeys()));
    }
}

