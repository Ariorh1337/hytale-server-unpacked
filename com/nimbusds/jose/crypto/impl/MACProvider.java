/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.BaseJWSProvider;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public abstract class MACProvider
extends BaseJWSProvider {
    public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
    private final byte[] secret;
    private final SecretKey secretKey;

    public static Set<JWSAlgorithm> getCompatibleAlgorithms(int secretLength) {
        LinkedHashSet<JWSAlgorithm> hmacAlgs = new LinkedHashSet<JWSAlgorithm>();
        if (secretLength >= 256) {
            hmacAlgs.add(JWSAlgorithm.HS256);
        }
        if (secretLength >= 384) {
            hmacAlgs.add(JWSAlgorithm.HS384);
        }
        if (secretLength >= 512) {
            hmacAlgs.add(JWSAlgorithm.HS512);
        }
        return Collections.unmodifiableSet(hmacAlgs);
    }

    public static int getMinRequiredSecretLength(JWSAlgorithm alg) throws JOSEException {
        if (JWSAlgorithm.HS256.equals(alg)) {
            return 256;
        }
        if (JWSAlgorithm.HS384.equals(alg)) {
            return 384;
        }
        if (JWSAlgorithm.HS512.equals(alg)) {
            return 512;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, SUPPORTED_ALGORITHMS));
    }

    protected static String getJCAAlgorithmName(JWSAlgorithm alg) throws JOSEException {
        if (alg.equals(JWSAlgorithm.HS256)) {
            return "HMACSHA256";
        }
        if (alg.equals(JWSAlgorithm.HS384)) {
            return "HMACSHA384";
        }
        if (alg.equals(JWSAlgorithm.HS512)) {
            return "HMACSHA512";
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, SUPPORTED_ALGORITHMS));
    }

    protected MACProvider(byte[] secret) throws KeyLengthException {
        super(MACProvider.getCompatibleAlgorithms(ByteUtils.bitLength(secret.length)));
        if (ByteUtils.bitLength(secret) < 256) {
            throw new KeyLengthException("The secret length must be at least 256 bits");
        }
        this.secret = secret;
        this.secretKey = null;
    }

    protected MACProvider(SecretKey secretKey) throws KeyLengthException {
        super(secretKey.getEncoded() != null ? MACProvider.getCompatibleAlgorithms(ByteUtils.bitLength(secretKey.getEncoded())) : SUPPORTED_ALGORITHMS);
        if (secretKey.getEncoded() != null && ByteUtils.bitLength(secretKey.getEncoded()) < 256) {
            throw new KeyLengthException("The secret length must be at least 256 bits");
        }
        this.secretKey = secretKey;
        this.secret = null;
    }

    public SecretKey getSecretKey() {
        if (this.secretKey != null) {
            return this.secretKey;
        }
        if (this.secret != null) {
            return new SecretKeySpec(this.secret, "MAC");
        }
        throw new IllegalStateException("Unexpected state");
    }

    public byte[] getSecret() {
        if (this.secretKey != null) {
            return this.secretKey.getEncoded();
        }
        if (this.secret != null) {
            return this.secret;
        }
        throw new IllegalStateException("Unexpected state");
    }

    public String getSecretString() {
        byte[] secret = this.getSecret();
        if (secret == null) {
            return null;
        }
        return new String(secret, StandardCharset.UTF_8);
    }

    protected void ensureSecretLengthSatisfiesAlgorithm(JWSAlgorithm alg) throws JOSEException {
        if (this.getSecret() == null) {
            return;
        }
        int minRequiredBitLength = MACProvider.getMinRequiredSecretLength(alg);
        if (ByteUtils.bitLength(this.getSecret()) < minRequiredBitLength) {
            throw new KeyLengthException("The secret length for " + alg + " must be at least " + minRequiredBitLength + " bits");
        }
    }

    static {
        LinkedHashSet<JWSAlgorithm> algs = new LinkedHashSet<JWSAlgorithm>();
        algs.add(JWSAlgorithm.HS256);
        algs.add(JWSAlgorithm.HS384);
        algs.add(JWSAlgorithm.HS512);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
    }
}

