/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSetCacheRefreshEvaluator;

class ForceRefreshJWKSetCacheEvaluator
extends JWKSetCacheRefreshEvaluator {
    private static final ForceRefreshJWKSetCacheEvaluator INSTANCE = new ForceRefreshJWKSetCacheEvaluator();

    public static ForceRefreshJWKSetCacheEvaluator getInstance() {
        return INSTANCE;
    }

    private ForceRefreshJWKSetCacheEvaluator() {
    }

    @Override
    public boolean requiresRefresh(JWKSet jwkSet) {
        return true;
    }

    public boolean equals(Object obj) {
        return obj instanceof ForceRefreshJWKSetCacheEvaluator;
    }

    public int hashCode() {
        return 0;
    }
}

