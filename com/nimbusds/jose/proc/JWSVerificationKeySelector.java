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
import com.nimbusds.jose.jwk.KeyConverter;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.AbstractJWKSelectorWithSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.Key;
import java.security.PublicKey;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class JWSVerificationKeySelector<C extends SecurityContext>
extends AbstractJWKSelectorWithSource<C>
implements JWSKeySelector<C> {
    private final Set<JWSAlgorithm> jwsAlgs;
    private final boolean singleJwsAlgConstructorWasCalled;

    public JWSVerificationKeySelector(JWSAlgorithm jwsAlg, JWKSource<C> jwkSource) {
        super(jwkSource);
        this.jwsAlgs = Collections.singleton(Objects.requireNonNull(jwsAlg));
        this.singleJwsAlgConstructorWasCalled = true;
    }

    public JWSVerificationKeySelector(Set<JWSAlgorithm> jwsAlgs, JWKSource<C> jwkSource) {
        super(jwkSource);
        if (jwsAlgs.isEmpty()) {
            throw new IllegalArgumentException("The JWS algorithms must not be empty");
        }
        this.jwsAlgs = Collections.unmodifiableSet(jwsAlgs);
        this.singleJwsAlgConstructorWasCalled = false;
    }

    public boolean isAllowed(JWSAlgorithm jwsAlg) {
        return this.jwsAlgs.contains(jwsAlg);
    }

    @Deprecated
    public JWSAlgorithm getExpectedJWSAlgorithm() {
        if (this.singleJwsAlgConstructorWasCalled) {
            return this.jwsAlgs.iterator().next();
        }
        throw new UnsupportedOperationException("Since this class was constructed with multiple algorithms, the behavior of this method is undefined.");
    }

    protected JWKMatcher createJWKMatcher(JWSHeader jwsHeader) {
        if (!this.isAllowed(jwsHeader.getAlgorithm())) {
            return null;
        }
        return JWKMatcher.forJWSHeader(jwsHeader);
    }

    @Override
    public List<Key> selectJWSKeys(JWSHeader jwsHeader, C context) throws KeySourceException {
        if (!this.jwsAlgs.contains(jwsHeader.getAlgorithm())) {
            return Collections.emptyList();
        }
        JWKMatcher jwkMatcher = this.createJWKMatcher(jwsHeader);
        if (jwkMatcher == null) {
            return Collections.emptyList();
        }
        List<JWK> jwkMatches = this.getJWKSource().get(new JWKSelector(jwkMatcher), context);
        LinkedList<Key> sanitizedKeyList = new LinkedList<Key>();
        for (Key key : KeyConverter.toJavaKeys(jwkMatches)) {
            if (!(key instanceof PublicKey) && !(key instanceof SecretKey)) continue;
            sanitizedKeyList.add(key);
        }
        return sanitizedKeyList;
    }
}

