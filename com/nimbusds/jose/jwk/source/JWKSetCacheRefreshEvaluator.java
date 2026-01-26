/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ForceRefreshJWKSetCacheEvaluator;
import com.nimbusds.jose.jwk.source.NoRefreshJWKSetCacheEvaluator;
import com.nimbusds.jose.jwk.source.ReferenceComparisonRefreshJWKSetEvaluator;

public abstract class JWKSetCacheRefreshEvaluator {
    public static JWKSetCacheRefreshEvaluator forceRefresh() {
        return ForceRefreshJWKSetCacheEvaluator.getInstance();
    }

    public static JWKSetCacheRefreshEvaluator noRefresh() {
        return NoRefreshJWKSetCacheEvaluator.getInstance();
    }

    public static JWKSetCacheRefreshEvaluator referenceComparison(JWKSet jwtSet) {
        return new ReferenceComparisonRefreshJWKSetEvaluator(jwtSet);
    }

    public abstract boolean requiresRefresh(JWKSet var1);
}

