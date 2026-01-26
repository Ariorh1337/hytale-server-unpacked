/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;

@Deprecated
public interface JWKSetCache {
    public void put(JWKSet var1);

    public JWKSet get();

    public boolean requiresRefresh();
}

