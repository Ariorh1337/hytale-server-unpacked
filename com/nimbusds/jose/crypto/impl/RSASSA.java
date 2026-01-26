/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.RSASSAProvider;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

public class RSASSA {
    public static Signature getSignerAndVerifier(JWSAlgorithm alg, Provider provider) throws JOSEException {
        Signature signature;
        if (alg.equals(JWSAlgorithm.RS256) && (signature = RSASSA.getSignerAndVerifier("SHA256withRSA", provider)) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.RS384) && (signature = RSASSA.getSignerAndVerifier("SHA384withRSA", provider)) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.RS512) && (signature = RSASSA.getSignerAndVerifier("SHA512withRSA", provider)) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS256) && (signature = RSASSA.getSignerAndVerifier("RSASSA-PSS", provider, new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1))) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS256) && (signature = RSASSA.getSignerAndVerifier("SHA256withRSA/PSS", provider)) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS256) && (signature = RSASSA.getSignerAndVerifier("SHA256withRSAandMGF1", provider)) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS384) && (signature = RSASSA.getSignerAndVerifier("RSASSA-PSS", provider, new PSSParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, 48, 1))) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS384) && (signature = RSASSA.getSignerAndVerifier("SHA384withRSA/PSS", provider)) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS384) && (signature = RSASSA.getSignerAndVerifier("SHA384withRSAandMGF1", provider)) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS512) && (signature = RSASSA.getSignerAndVerifier("RSASSA-PSS", provider, new PSSParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1))) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS512) && (signature = RSASSA.getSignerAndVerifier("SHA512withRSA/PSS", provider)) != null) {
            return signature;
        }
        if (alg.equals(JWSAlgorithm.PS512) && (signature = RSASSA.getSignerAndVerifier("SHA512withRSAandMGF1", provider)) != null) {
            return signature;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, RSASSAProvider.SUPPORTED_ALGORITHMS));
    }

    private static Signature getSignerAndVerifier(String jcaAlg, Provider provider) throws JOSEException {
        return RSASSA.getSignerAndVerifier(jcaAlg, provider, null);
    }

    private static Signature getSignerAndVerifier(String jcaAlg, Provider provider, PSSParameterSpec pssSpec) throws JOSEException {
        Signature signature;
        try {
            signature = provider != null ? Signature.getInstance(jcaAlg, provider) : Signature.getInstance(jcaAlg);
        }
        catch (NoSuchAlgorithmException ignore) {
            return null;
        }
        if (pssSpec != null) {
            try {
                signature.setParameter(pssSpec);
            }
            catch (InvalidAlgorithmParameterException e) {
                throw new JOSEException("Invalid RSASSA-PSS salt length parameter: " + e.getMessage(), e);
            }
        }
        return signature;
    }

    private RSASSA() {
    }
}

