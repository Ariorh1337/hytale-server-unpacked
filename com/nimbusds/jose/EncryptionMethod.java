/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.AlgorithmFamily;
import com.nimbusds.jose.Requirement;
import com.nimbusds.jose.shaded.jcip.Immutable;

@Immutable
public final class EncryptionMethod
extends Algorithm {
    private static final long serialVersionUID = 1L;
    private final int cekBitLength;
    public static final EncryptionMethod A128CBC_HS256 = new EncryptionMethod("A128CBC-HS256", Requirement.REQUIRED, 256);
    public static final EncryptionMethod A192CBC_HS384 = new EncryptionMethod("A192CBC-HS384", Requirement.OPTIONAL, 384);
    public static final EncryptionMethod A256CBC_HS512 = new EncryptionMethod("A256CBC-HS512", Requirement.REQUIRED, 512);
    public static final EncryptionMethod A128CBC_HS256_DEPRECATED = new EncryptionMethod("A128CBC+HS256", Requirement.OPTIONAL, 256);
    public static final EncryptionMethod A256CBC_HS512_DEPRECATED = new EncryptionMethod("A256CBC+HS512", Requirement.OPTIONAL, 512);
    public static final EncryptionMethod A128GCM = new EncryptionMethod("A128GCM", Requirement.RECOMMENDED, 128);
    public static final EncryptionMethod A192GCM = new EncryptionMethod("A192GCM", Requirement.OPTIONAL, 192);
    public static final EncryptionMethod A256GCM = new EncryptionMethod("A256GCM", Requirement.RECOMMENDED, 256);
    public static final EncryptionMethod XC20P = new EncryptionMethod("XC20P", Requirement.OPTIONAL, 256);

    public EncryptionMethod(String name, Requirement req, int cekBitLength) {
        super(name, req);
        this.cekBitLength = cekBitLength;
    }

    public EncryptionMethod(String name, Requirement req) {
        this(name, req, 0);
    }

    public EncryptionMethod(String name) {
        this(name, null, 0);
    }

    public int cekBitLength() {
        return this.cekBitLength;
    }

    public static EncryptionMethod parse(String s) {
        if (s.equals(A128CBC_HS256.getName())) {
            return A128CBC_HS256;
        }
        if (s.equals(A192CBC_HS384.getName())) {
            return A192CBC_HS384;
        }
        if (s.equals(A256CBC_HS512.getName())) {
            return A256CBC_HS512;
        }
        if (s.equals(A128GCM.getName())) {
            return A128GCM;
        }
        if (s.equals(A192GCM.getName())) {
            return A192GCM;
        }
        if (s.equals(A256GCM.getName())) {
            return A256GCM;
        }
        if (s.equals(A128CBC_HS256_DEPRECATED.getName())) {
            return A128CBC_HS256_DEPRECATED;
        }
        if (s.equals(A256CBC_HS512_DEPRECATED.getName())) {
            return A256CBC_HS512_DEPRECATED;
        }
        if (s.equals(XC20P.getName())) {
            return XC20P;
        }
        return new EncryptionMethod(s);
    }

    public static final class Family
    extends AlgorithmFamily<EncryptionMethod> {
        private static final long serialVersionUID = 1L;
        public static final Family AES_CBC_HMAC_SHA = new Family(A128CBC_HS256, A192CBC_HS384, A256CBC_HS512);
        public static final Family AES_GCM = new Family(A128GCM, A192GCM, A256GCM);

        public Family(EncryptionMethod ... encs) {
            super((Algorithm[])encs);
        }
    }
}

