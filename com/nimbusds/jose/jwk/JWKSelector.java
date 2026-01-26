/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.shaded.jcip.Immutable;
import java.util.LinkedList;
import java.util.List;

@Immutable
public final class JWKSelector {
    private final JWKMatcher matcher;

    public JWKSelector(JWKMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("The JWK matcher must not be null");
        }
        this.matcher = matcher;
    }

    public JWKMatcher getMatcher() {
        return this.matcher;
    }

    public List<JWK> select(JWKSet jwkSet) {
        LinkedList<JWK> selectedKeys = new LinkedList<JWK>();
        if (jwkSet == null) {
            return selectedKeys;
        }
        for (JWK key : jwkSet.getKeys()) {
            if (!this.matcher.matches(key)) continue;
            selectedKeys.add(key);
        }
        return selectedKeys;
    }
}

