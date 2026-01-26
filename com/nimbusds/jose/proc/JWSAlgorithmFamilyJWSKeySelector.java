/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.AbstractJWKSelectorWithSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import java.net.URL;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JWSAlgorithmFamilyJWSKeySelector<C extends SecurityContext>
extends AbstractJWKSelectorWithSource<C>
implements JWSKeySelector<C> {
    private final Map<JWSAlgorithm, JWSKeySelector<C>> selectors = new HashMap<JWSAlgorithm, JWSKeySelector<C>>();

    public JWSAlgorithmFamilyJWSKeySelector(JWSAlgorithm.Family jwsAlgFamily, JWKSource<C> jwkSource) {
        super(jwkSource);
        for (JWSAlgorithm jwsAlg : jwsAlgFamily) {
            this.selectors.put(jwsAlg, new JWSVerificationKeySelector<C>(jwsAlg, jwkSource));
        }
    }

    @Override
    public List<? extends Key> selectJWSKeys(JWSHeader header, C context) throws KeySourceException {
        JWSKeySelector<C> selector = this.selectors.get(header.getAlgorithm());
        if (selector == null) {
            return Collections.emptyList();
        }
        return selector.selectJWSKeys(header, context);
    }

    public static <C extends SecurityContext> JWSAlgorithmFamilyJWSKeySelector<C> fromJWKSetURL(URL jwkSetURL) throws KeySourceException {
        RemoteJWKSet jwkSource = new RemoteJWKSet(jwkSetURL);
        return JWSAlgorithmFamilyJWSKeySelector.fromJWKSource(jwkSource);
    }

    public static <C extends SecurityContext> JWSAlgorithmFamilyJWSKeySelector<C> fromJWKSource(JWKSource<C> jwkSource) throws KeySourceException {
        JWKMatcher jwkMatcher = new JWKMatcher.Builder().publicOnly(true).keyUses(KeyUse.SIGNATURE, null).keyTypes(KeyType.RSA, KeyType.EC).build();
        List<JWK> jwks = jwkSource.get(new JWKSelector(jwkMatcher), null);
        for (JWK jwk : jwks) {
            if (KeyType.RSA.equals(jwk.getKeyType())) {
                return new JWSAlgorithmFamilyJWSKeySelector<C>(JWSAlgorithm.Family.RSA, jwkSource);
            }
            if (!KeyType.EC.equals(jwk.getKeyType())) continue;
            return new JWSAlgorithmFamilyJWSKeySelector<C>(JWSAlgorithm.Family.EC, jwkSource);
        }
        throw new KeySourceException("Couldn't retrieve JWKs");
    }
}

