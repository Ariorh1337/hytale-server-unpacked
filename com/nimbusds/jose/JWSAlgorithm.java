/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.AlgorithmFamily;
import com.nimbusds.jose.Requirement;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.ArrayUtils;

@Immutable
public final class JWSAlgorithm
extends Algorithm {
    private static final long serialVersionUID = 1L;
    public static final JWSAlgorithm HS256 = new JWSAlgorithm("HS256", Requirement.REQUIRED);
    public static final JWSAlgorithm HS384 = new JWSAlgorithm("HS384", Requirement.OPTIONAL);
    public static final JWSAlgorithm HS512 = new JWSAlgorithm("HS512", Requirement.OPTIONAL);
    public static final JWSAlgorithm RS256 = new JWSAlgorithm("RS256", Requirement.RECOMMENDED);
    public static final JWSAlgorithm RS384 = new JWSAlgorithm("RS384", Requirement.OPTIONAL);
    public static final JWSAlgorithm RS512 = new JWSAlgorithm("RS512", Requirement.OPTIONAL);
    public static final JWSAlgorithm ES256 = new JWSAlgorithm("ES256", Requirement.RECOMMENDED);
    public static final JWSAlgorithm ES256K = new JWSAlgorithm("ES256K", Requirement.OPTIONAL);
    public static final JWSAlgorithm ES384 = new JWSAlgorithm("ES384", Requirement.OPTIONAL);
    public static final JWSAlgorithm ES512 = new JWSAlgorithm("ES512", Requirement.OPTIONAL);
    public static final JWSAlgorithm PS256 = new JWSAlgorithm("PS256", Requirement.OPTIONAL);
    public static final JWSAlgorithm PS384 = new JWSAlgorithm("PS384", Requirement.OPTIONAL);
    public static final JWSAlgorithm PS512 = new JWSAlgorithm("PS512", Requirement.OPTIONAL);
    public static final JWSAlgorithm EdDSA = new JWSAlgorithm("EdDSA", Requirement.OPTIONAL);
    public static final JWSAlgorithm Ed25519 = new JWSAlgorithm("Ed25519", Requirement.OPTIONAL);
    public static final JWSAlgorithm Ed448 = new JWSAlgorithm("Ed448", Requirement.OPTIONAL);

    public JWSAlgorithm(String name, Requirement req) {
        super(name, req);
    }

    public JWSAlgorithm(String name) {
        super(name, null);
    }

    public static JWSAlgorithm parse(String s) {
        if (s.equals(HS256.getName())) {
            return HS256;
        }
        if (s.equals(HS384.getName())) {
            return HS384;
        }
        if (s.equals(HS512.getName())) {
            return HS512;
        }
        if (s.equals(RS256.getName())) {
            return RS256;
        }
        if (s.equals(RS384.getName())) {
            return RS384;
        }
        if (s.equals(RS512.getName())) {
            return RS512;
        }
        if (s.equals(ES256.getName())) {
            return ES256;
        }
        if (s.equals(ES256K.getName())) {
            return ES256K;
        }
        if (s.equals(ES384.getName())) {
            return ES384;
        }
        if (s.equals(ES512.getName())) {
            return ES512;
        }
        if (s.equals(PS256.getName())) {
            return PS256;
        }
        if (s.equals(PS384.getName())) {
            return PS384;
        }
        if (s.equals(PS512.getName())) {
            return PS512;
        }
        if (s.equals(EdDSA.getName())) {
            return EdDSA;
        }
        if (s.equals(Ed25519.getName())) {
            return Ed25519;
        }
        if (s.equals(Ed448.getName())) {
            return Ed448;
        }
        return new JWSAlgorithm(s);
    }

    public static final class Family
    extends AlgorithmFamily<JWSAlgorithm> {
        private static final long serialVersionUID = 1L;
        public static final Family HMAC_SHA = new Family(HS256, HS384, HS512);
        public static final Family RSA = new Family(RS256, RS384, RS512, PS256, PS384, PS512);
        public static final Family EC = new Family(ES256, ES256K, ES384, ES512);
        public static final Family ED = new Family(EdDSA, Ed25519, Ed448);
        public static final Family SIGNATURE = new Family(ArrayUtils.concat(RSA.toArray(new JWSAlgorithm[0]), EC.toArray(new JWSAlgorithm[0]), ED.toArray(new JWSAlgorithm[0])));

        public Family(JWSAlgorithm ... algs) {
            super((Algorithm[])algs);
        }
    }
}

