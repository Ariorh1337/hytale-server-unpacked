/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.BaseJWEProvider;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;

public abstract class MultiCryptoProvider
extends BaseJWEProvider {
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    public static final Map<Integer, Set<JWEAlgorithm>> COMPATIBLE_ALGORITHMS;
    public static final Set<Curve> SUPPORTED_ELLIPTIC_CURVES;

    public Set<Curve> supportedEllipticCurves() {
        return SUPPORTED_ELLIPTIC_CURVES;
    }

    protected MultiCryptoProvider(SecretKey cek) throws KeyLengthException {
        super(SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS, cek);
    }

    static {
        SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
        LinkedHashSet<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.add(null);
        algs.add(JWEAlgorithm.A128KW);
        algs.add(JWEAlgorithm.A192KW);
        algs.add(JWEAlgorithm.A256KW);
        algs.add(JWEAlgorithm.A128GCMKW);
        algs.add(JWEAlgorithm.A192GCMKW);
        algs.add(JWEAlgorithm.A256GCMKW);
        algs.add(JWEAlgorithm.DIR);
        algs.add(JWEAlgorithm.ECDH_ES_A128KW);
        algs.add(JWEAlgorithm.ECDH_ES_A192KW);
        algs.add(JWEAlgorithm.ECDH_ES_A256KW);
        algs.add(JWEAlgorithm.RSA1_5);
        algs.add(JWEAlgorithm.RSA_OAEP);
        algs.add(JWEAlgorithm.RSA_OAEP_256);
        algs.add(JWEAlgorithm.RSA_OAEP_384);
        algs.add(JWEAlgorithm.RSA_OAEP_512);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
        HashMap algsMap = new HashMap();
        HashSet<JWEAlgorithm> bit128Algs = new HashSet<JWEAlgorithm>();
        HashSet<JWEAlgorithm> bit192Algs = new HashSet<JWEAlgorithm>();
        HashSet<JWEAlgorithm> bit256Algs = new HashSet<JWEAlgorithm>();
        bit128Algs.add(JWEAlgorithm.A128GCMKW);
        bit128Algs.add(JWEAlgorithm.A128KW);
        bit192Algs.add(JWEAlgorithm.A192GCMKW);
        bit192Algs.add(JWEAlgorithm.A192KW);
        bit256Algs.add(JWEAlgorithm.A256GCMKW);
        bit256Algs.add(JWEAlgorithm.A256KW);
        algsMap.put(128, Collections.unmodifiableSet(bit128Algs));
        algsMap.put(192, Collections.unmodifiableSet(bit192Algs));
        algsMap.put(256, Collections.unmodifiableSet(bit256Algs));
        COMPATIBLE_ALGORITHMS = Collections.unmodifiableMap(algsMap);
        LinkedHashSet<Curve> curves = new LinkedHashSet<Curve>();
        curves.add(Curve.P_256);
        curves.add(Curve.P_384);
        curves.add(Curve.P_521);
        curves.add(Curve.X25519);
        SUPPORTED_ELLIPTIC_CURVES = Collections.unmodifiableSet(curves);
    }
}

