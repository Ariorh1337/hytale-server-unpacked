/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.factories;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.impl.BaseJWSProvider;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.produce.JWSSignerFactory;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultJWSSignerFactory
implements JWSSignerFactory {
    private final JCAContext jcaContext = new JCAContext();
    public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;

    @Override
    public Set<JWSAlgorithm> supportedJWSAlgorithms() {
        return SUPPORTED_ALGORITHMS;
    }

    @Override
    public JCAContext getJCAContext() {
        return this.jcaContext;
    }

    @Override
    public JWSSigner createJWSSigner(JWK key) throws JOSEException {
        BaseJWSProvider signer;
        if (!key.isPrivate()) {
            throw JWKException.expectedPrivate();
        }
        if (key.getKeyUse() != null && !KeyUse.SIGNATURE.equals(key.getKeyUse())) {
            throw new JWKException("The JWK use must be sig (signature) or unspecified");
        }
        if (key instanceof OctetSequenceKey) {
            signer = new MACSigner((OctetSequenceKey)key);
        } else if (key instanceof RSAKey) {
            signer = new RSASSASigner((RSAKey)key);
        } else if (key instanceof ECKey && ECDSASigner.SUPPORTED_CURVES.contains(((ECKey)key).getCurve())) {
            signer = new ECDSASigner((ECKey)key);
        } else if (key instanceof OctetKeyPair && Ed25519Signer.SUPPORTED_CURVES.contains(((OctetKeyPair)key).getCurve())) {
            signer = new Ed25519Signer((OctetKeyPair)key);
        } else {
            throw new JOSEException("Unsupported JWK type and / or curve");
        }
        ((JCAContext)signer.getJCAContext()).setSecureRandom(this.jcaContext.getSecureRandom());
        ((JCAContext)signer.getJCAContext()).setProvider(this.jcaContext.getProvider());
        return signer;
    }

    @Override
    public JWSSigner createJWSSigner(JWK key, JWSAlgorithm alg) throws JOSEException {
        BaseJWSProvider signer;
        if (!key.isPrivate()) {
            throw JWKException.expectedPrivate();
        }
        if (key.getKeyUse() != null && !KeyUse.SIGNATURE.equals(key.getKeyUse())) {
            throw new JWKException("The JWK use must be sig (signature) or unspecified");
        }
        if (MACSigner.SUPPORTED_ALGORITHMS.contains(alg) && key instanceof OctetSequenceKey) {
            signer = new MACSigner((OctetSequenceKey)key);
        } else if (RSASSASigner.SUPPORTED_ALGORITHMS.contains(alg) && key instanceof RSAKey) {
            signer = new RSASSASigner((RSAKey)key);
        } else if (ECDSASigner.SUPPORTED_ALGORITHMS.contains(alg) && key instanceof ECKey && ECDSASigner.SUPPORTED_CURVES.contains(((ECKey)key).getCurve())) {
            signer = new ECDSASigner((ECKey)key);
        } else if (Ed25519Signer.SUPPORTED_ALGORITHMS.contains(alg) && key instanceof OctetKeyPair && Ed25519Signer.SUPPORTED_CURVES.contains(((OctetKeyPair)key).getCurve())) {
            signer = new Ed25519Signer((OctetKeyPair)key);
        } else {
            throw new JOSEException("Unsupported JWK type, JWK curve and / or JWS algorithm");
        }
        ((JCAContext)signer.getJCAContext()).setSecureRandom(this.jcaContext.getSecureRandom());
        ((JCAContext)signer.getJCAContext()).setProvider(this.jcaContext.getProvider());
        return signer;
    }

    static {
        LinkedHashSet algs = new LinkedHashSet();
        algs.addAll(MACSigner.SUPPORTED_ALGORITHMS);
        algs.addAll(RSASSASigner.SUPPORTED_ALGORITHMS);
        algs.addAll(ECDSASigner.SUPPORTED_ALGORITHMS);
        algs.addAll(Ed25519Signer.SUPPORTED_ALGORITHMS);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
    }
}

