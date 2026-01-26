/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSetCacheRefreshEvaluator;
import java.util.Objects;

class ReferenceComparisonRefreshJWKSetEvaluator
extends JWKSetCacheRefreshEvaluator {
    private final JWKSet jwkSet;

    public ReferenceComparisonRefreshJWKSetEvaluator(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @Override
    public boolean requiresRefresh(JWKSet jwkSet) {
        return jwkSet == this.jwkSet;
    }

    public int hashCode() {
        return Objects.hash(this.jwkSet);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ReferenceComparisonRefreshJWKSetEvaluator other = (ReferenceComparisonRefreshJWKSetEvaluator)obj;
        return Objects.equals(this.jwkSet, other.jwkSet);
    }
}

