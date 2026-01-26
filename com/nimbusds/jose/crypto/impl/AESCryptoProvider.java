/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.BaseJWEProvider;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.util.ByteUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;

public abstract class AESCryptoProvider
extends BaseJWEProvider {
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    public static final Map<Integer, Set<JWEAlgorithm>> COMPATIBLE_ALGORITHMS;
    private final SecretKey kek;

    private static Set<JWEAlgorithm> getCompatibleJWEAlgorithms(int kekLength) throws KeyLengthException {
        Set<JWEAlgorithm> algs = COMPATIBLE_ALGORITHMS.get(kekLength);
        if (algs == null) {
            throw new KeyLengthException("The Key Encryption Key length must be 128 bits (16 bytes), 192 bits (24 bytes) or 256 bits (32 bytes)");
        }
        return algs;
    }

    protected AESCryptoProvider(SecretKey kek, SecretKey cek) throws KeyLengthException {
        super(AESCryptoProvider.getCompatibleJWEAlgorithms(ByteUtils.bitLength(kek.getEncoded())), ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS, cek);
        this.kek = kek;
    }

    public SecretKey getKey() {
        return this.kek;
    }

    static {
        SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
        LinkedHashSet<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.add(JWEAlgorithm.A128KW);
        algs.add(JWEAlgorithm.A192KW);
        algs.add(JWEAlgorithm.A256KW);
        algs.add(JWEAlgorithm.A128GCMKW);
        algs.add(JWEAlgorithm.A192GCMKW);
        algs.add(JWEAlgorithm.A256GCMKW);
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
    }
}

