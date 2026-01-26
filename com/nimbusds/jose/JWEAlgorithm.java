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
public final class JWEAlgorithm
extends Algorithm {
    private static final long serialVersionUID = 1L;
    @Deprecated
    public static final JWEAlgorithm RSA1_5 = new JWEAlgorithm("RSA1_5", Requirement.REQUIRED);
    @Deprecated
    public static final JWEAlgorithm RSA_OAEP = new JWEAlgorithm("RSA-OAEP", Requirement.OPTIONAL);
    public static final JWEAlgorithm RSA_OAEP_256 = new JWEAlgorithm("RSA-OAEP-256", Requirement.OPTIONAL);
    public static final JWEAlgorithm RSA_OAEP_384 = new JWEAlgorithm("RSA-OAEP-384", Requirement.OPTIONAL);
    public static final JWEAlgorithm RSA_OAEP_512 = new JWEAlgorithm("RSA-OAEP-512", Requirement.OPTIONAL);
    public static final JWEAlgorithm A128KW = new JWEAlgorithm("A128KW", Requirement.RECOMMENDED);
    public static final JWEAlgorithm A192KW = new JWEAlgorithm("A192KW", Requirement.OPTIONAL);
    public static final JWEAlgorithm A256KW = new JWEAlgorithm("A256KW", Requirement.RECOMMENDED);
    public static final JWEAlgorithm DIR = new JWEAlgorithm("dir", Requirement.RECOMMENDED);
    public static final JWEAlgorithm ECDH_ES = new JWEAlgorithm("ECDH-ES", Requirement.RECOMMENDED);
    public static final JWEAlgorithm ECDH_ES_A128KW = new JWEAlgorithm("ECDH-ES+A128KW", Requirement.RECOMMENDED);
    public static final JWEAlgorithm ECDH_ES_A192KW = new JWEAlgorithm("ECDH-ES+A192KW", Requirement.OPTIONAL);
    public static final JWEAlgorithm ECDH_ES_A256KW = new JWEAlgorithm("ECDH-ES+A256KW", Requirement.RECOMMENDED);
    public static final JWEAlgorithm ECDH_1PU = new JWEAlgorithm("ECDH-1PU", Requirement.OPTIONAL);
    public static final JWEAlgorithm ECDH_1PU_A128KW = new JWEAlgorithm("ECDH-1PU+A128KW", Requirement.OPTIONAL);
    public static final JWEAlgorithm ECDH_1PU_A192KW = new JWEAlgorithm("ECDH-1PU+A192KW", Requirement.OPTIONAL);
    public static final JWEAlgorithm ECDH_1PU_A256KW = new JWEAlgorithm("ECDH-1PU+A256KW", Requirement.OPTIONAL);
    public static final JWEAlgorithm A128GCMKW = new JWEAlgorithm("A128GCMKW", Requirement.OPTIONAL);
    public static final JWEAlgorithm A192GCMKW = new JWEAlgorithm("A192GCMKW", Requirement.OPTIONAL);
    public static final JWEAlgorithm A256GCMKW = new JWEAlgorithm("A256GCMKW", Requirement.OPTIONAL);
    public static final JWEAlgorithm PBES2_HS256_A128KW = new JWEAlgorithm("PBES2-HS256+A128KW", Requirement.OPTIONAL);
    public static final JWEAlgorithm PBES2_HS384_A192KW = new JWEAlgorithm("PBES2-HS384+A192KW", Requirement.OPTIONAL);
    public static final JWEAlgorithm PBES2_HS512_A256KW = new JWEAlgorithm("PBES2-HS512+A256KW", Requirement.OPTIONAL);

    public JWEAlgorithm(String name, Requirement req) {
        super(name, req);
    }

    public JWEAlgorithm(String name) {
        super(name, null);
    }

    public static JWEAlgorithm parse(String s) {
        if (s.equals(RSA1_5.getName())) {
            return RSA1_5;
        }
        if (s.equals(RSA_OAEP.getName())) {
            return RSA_OAEP;
        }
        if (s.equals(RSA_OAEP_256.getName())) {
            return RSA_OAEP_256;
        }
        if (s.equals(RSA_OAEP_384.getName())) {
            return RSA_OAEP_384;
        }
        if (s.equals(RSA_OAEP_512.getName())) {
            return RSA_OAEP_512;
        }
        if (s.equals(A128KW.getName())) {
            return A128KW;
        }
        if (s.equals(A192KW.getName())) {
            return A192KW;
        }
        if (s.equals(A256KW.getName())) {
            return A256KW;
        }
        if (s.equals(DIR.getName())) {
            return DIR;
        }
        if (s.equals(ECDH_ES.getName())) {
            return ECDH_ES;
        }
        if (s.equals(ECDH_ES_A128KW.getName())) {
            return ECDH_ES_A128KW;
        }
        if (s.equals(ECDH_ES_A192KW.getName())) {
            return ECDH_ES_A192KW;
        }
        if (s.equals(ECDH_ES_A256KW.getName())) {
            return ECDH_ES_A256KW;
        }
        if (s.equals(ECDH_1PU.getName())) {
            return ECDH_1PU;
        }
        if (s.equals(ECDH_1PU_A128KW.getName())) {
            return ECDH_1PU_A128KW;
        }
        if (s.equals(ECDH_1PU_A192KW.getName())) {
            return ECDH_1PU_A192KW;
        }
        if (s.equals(ECDH_1PU_A256KW.getName())) {
            return ECDH_1PU_A256KW;
        }
        if (s.equals(A128GCMKW.getName())) {
            return A128GCMKW;
        }
        if (s.equals(A192GCMKW.getName())) {
            return A192GCMKW;
        }
        if (s.equals(A256GCMKW.getName())) {
            return A256GCMKW;
        }
        if (s.equals(PBES2_HS256_A128KW.getName())) {
            return PBES2_HS256_A128KW;
        }
        if (s.equals(PBES2_HS384_A192KW.getName())) {
            return PBES2_HS384_A192KW;
        }
        if (s.equals(PBES2_HS512_A256KW.getName())) {
            return PBES2_HS512_A256KW;
        }
        return new JWEAlgorithm(s);
    }

    public static final class Family
    extends AlgorithmFamily<JWEAlgorithm> {
        private static final long serialVersionUID = 1L;
        public static final Family RSA = new Family(RSA1_5, RSA_OAEP, RSA_OAEP_256, RSA_OAEP_384, RSA_OAEP_512);
        public static final Family AES_KW = new Family(A128KW, A192KW, A256KW);
        public static final Family ECDH_ES = new Family(ECDH_ES, ECDH_ES_A128KW, ECDH_ES_A192KW, ECDH_ES_A256KW);
        public static final Family ECDH_1PU = new Family(ECDH_1PU, ECDH_1PU_A128KW, ECDH_1PU_A192KW, ECDH_1PU_A256KW);
        public static final Family AES_GCM_KW = new Family(A128GCMKW, A192GCMKW, A256GCMKW);
        public static final Family PBES2 = new Family(PBES2_HS256_A128KW, PBES2_HS384_A192KW, PBES2_HS512_A256KW);
        public static final Family ASYMMETRIC = new Family(ArrayUtils.concat(RSA.toArray(new JWEAlgorithm[0]), new JWEAlgorithm[][]{ECDH_ES.toArray(new JWEAlgorithm[0])}));
        public static final Family SYMMETRIC = new Family(ArrayUtils.concat(AES_KW.toArray(new JWEAlgorithm[0]), AES_GCM_KW.toArray(new JWEAlgorithm[0]), {DIR}));

        public Family(JWEAlgorithm ... algs) {
            super((Algorithm[])algs);
        }
    }
}

