/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class HMAC {
    public static Mac getInitMac(SecretKey secretKey, Provider provider) throws JOSEException {
        return HMAC.getInitMac(secretKey.getAlgorithm(), secretKey, provider);
    }

    public static Mac getInitMac(String alg, SecretKey secretKey, Provider provider) throws JOSEException {
        Mac mac;
        try {
            mac = provider != null ? Mac.getInstance(alg, provider) : Mac.getInstance(alg);
            mac.init(secretKey);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Unsupported HMAC algorithm: " + e.getMessage(), e);
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("Invalid HMAC key: " + e.getMessage(), e);
        }
        return mac;
    }

    @Deprecated
    public static byte[] compute(String alg, byte[] secret, byte[] message, Provider provider) throws JOSEException {
        return HMAC.compute(alg, new SecretKeySpec(secret, alg), message, provider);
    }

    public static byte[] compute(String alg, SecretKey secretKey, byte[] message, Provider provider) throws JOSEException {
        Mac mac = HMAC.getInitMac(alg, secretKey, provider);
        mac.update(message);
        return mac.doFinal();
    }

    public static byte[] compute(SecretKey secretKey, byte[] message, Provider provider) throws JOSEException {
        return HMAC.compute(secretKey.getAlgorithm(), secretKey, message, provider);
    }
}

