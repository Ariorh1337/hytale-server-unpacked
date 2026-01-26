/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ECDSAProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECParameterTable;
import com.nimbusds.jose.util.ByteUtils;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.spec.ECParameterSpec;
import java.util.Set;

public class ECDSA {
    public static JWSAlgorithm resolveAlgorithm(ECKey ecKey) throws JOSEException {
        ECParameterSpec ecParameterSpec = ecKey.getParams();
        return ECDSA.resolveAlgorithm(Curve.forECParameterSpec(ecParameterSpec));
    }

    public static JWSAlgorithm resolveAlgorithm(Curve curve) throws JOSEException {
        if (curve == null) {
            throw new JOSEException("The EC key curve is not supported, must be P-256, P-384 or P-521");
        }
        if (Curve.P_256.equals(curve)) {
            return JWSAlgorithm.ES256;
        }
        if (Curve.SECP256K1.equals(curve)) {
            return JWSAlgorithm.ES256K;
        }
        if (Curve.P_384.equals(curve)) {
            return JWSAlgorithm.ES384;
        }
        if (Curve.P_521.equals(curve)) {
            return JWSAlgorithm.ES512;
        }
        throw new JOSEException("Unexpected curve: " + curve);
    }

    public static Signature getSignerAndVerifier(JWSAlgorithm alg, Provider jcaProvider) throws JOSEException {
        String jcaAlg;
        if (alg.equals(JWSAlgorithm.ES256)) {
            jcaAlg = "SHA256withECDSA";
        } else if (alg.equals(JWSAlgorithm.ES256K)) {
            jcaAlg = "SHA256withECDSA";
        } else if (alg.equals(JWSAlgorithm.ES384)) {
            jcaAlg = "SHA384withECDSA";
        } else if (alg.equals(JWSAlgorithm.ES512)) {
            jcaAlg = "SHA512withECDSA";
        } else {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, ECDSAProvider.SUPPORTED_ALGORITHMS));
        }
        try {
            if (jcaProvider != null) {
                return Signature.getInstance(jcaAlg, jcaProvider);
            }
            return Signature.getInstance(jcaAlg);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Unsupported ECDSA algorithm: " + e.getMessage(), e);
        }
    }

    public static int getSignatureByteArrayLength(JWSAlgorithm alg) throws JOSEException {
        if (alg.equals(JWSAlgorithm.ES256)) {
            return 64;
        }
        if (alg.equals(JWSAlgorithm.ES256K)) {
            return 64;
        }
        if (alg.equals(JWSAlgorithm.ES384)) {
            return 96;
        }
        if (alg.equals(JWSAlgorithm.ES512)) {
            return 132;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, ECDSAProvider.SUPPORTED_ALGORITHMS));
    }

    public static byte[] transcodeSignatureToConcat(byte[] derSignature, int outputLength) throws JOSEException {
        int sLength;
        int j;
        int rLength;
        int i;
        int offset;
        if (derSignature.length < 8 || derSignature[0] != 48) {
            throw new JOSEException("Invalid ECDSA signature format");
        }
        if (derSignature[1] > 0) {
            offset = 2;
        } else if (derSignature[1] == -127) {
            offset = 3;
        } else {
            throw new JOSEException("Invalid ECDSA signature format");
        }
        for (i = rLength = derSignature[offset + 1]; i > 0 && derSignature[offset + 2 + rLength - i] == 0; --i) {
        }
        for (j = sLength = derSignature[offset + 2 + rLength + 1]; j > 0 && derSignature[offset + 2 + rLength + 2 + sLength - j] == 0; --j) {
        }
        int rawLen = Math.max(i, j);
        rawLen = Math.max(rawLen, outputLength / 2);
        if ((derSignature[offset - 1] & 0xFF) != derSignature.length - offset || (derSignature[offset - 1] & 0xFF) != 2 + rLength + 2 + sLength || derSignature[offset] != 2 || derSignature[offset + 2 + rLength] != 2) {
            throw new JOSEException("Invalid ECDSA signature format");
        }
        byte[] concatSignature = new byte[2 * rawLen];
        System.arraycopy(derSignature, offset + 2 + rLength - i, concatSignature, rawLen - i, i);
        System.arraycopy(derSignature, offset + 2 + rLength + 2 + sLength - j, concatSignature, 2 * rawLen - j, j);
        return concatSignature;
    }

    public static byte[] transcodeSignatureToDER(byte[] jwsSignature) throws JOSEException {
        try {
            int offset;
            byte[] derSignature;
            int len;
            int k;
            int rawLen;
            int i;
            for (i = rawLen = jwsSignature.length / 2; i > 0 && jwsSignature[rawLen - i] == 0; --i) {
            }
            int j = i;
            if (jwsSignature[rawLen - i] < 0) {
                ++j;
            }
            for (k = rawLen; k > 0 && jwsSignature[2 * rawLen - k] == 0; --k) {
            }
            int l = k;
            if (jwsSignature[2 * rawLen - k] < 0) {
                ++l;
            }
            if ((len = 2 + j + 2 + l) > 255) {
                throw new JOSEException("Invalid ECDSA signature format");
            }
            if (len < 128) {
                derSignature = new byte[4 + j + 2 + l];
                offset = 1;
            } else {
                derSignature = new byte[5 + j + 2 + l];
                derSignature[1] = -127;
                offset = 2;
            }
            derSignature[0] = 48;
            derSignature[offset++] = (byte)len;
            derSignature[offset++] = 2;
            derSignature[offset++] = (byte)j;
            System.arraycopy(jwsSignature, rawLen - i, derSignature, offset + j - i, i);
            offset += j;
            derSignature[offset++] = 2;
            derSignature[offset++] = (byte)l;
            System.arraycopy(jwsSignature, 2 * rawLen - k, derSignature, offset + l - k, k);
            return derSignature;
        }
        catch (Exception e) {
            if (e instanceof JOSEException) {
                throw e;
            }
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public static void ensureLegalSignature(byte[] jwsSignature, JWSAlgorithm jwsAlg) throws JOSEException {
        if (ByteUtils.isZeroFilled(jwsSignature)) {
            throw new JOSEException("Blank signature");
        }
        Set<Curve> matchingCurves = Curve.forJWSAlgorithm(jwsAlg);
        if (matchingCurves == null || matchingCurves.size() > 1) {
            throw new JOSEException("Unsupported JWS algorithm: " + jwsAlg);
        }
        Curve curve = matchingCurves.iterator().next();
        ECParameterSpec ecParameterSpec = ECParameterTable.get(curve);
        if (ecParameterSpec == null) {
            throw new JOSEException("Unsupported curve: " + curve);
        }
        int signatureLength = ECDSA.getSignatureByteArrayLength(jwsAlg);
        if (ECDSA.getSignatureByteArrayLength(jwsAlg) != jwsSignature.length) {
            throw new JOSEException("Illegal signature length");
        }
        int valueLength = signatureLength / 2;
        byte[] rBytes = ByteUtils.subArray(jwsSignature, 0, valueLength);
        BigInteger rValue = new BigInteger(1, rBytes);
        byte[] sBytes = ByteUtils.subArray(jwsSignature, valueLength, valueLength);
        BigInteger sValue = new BigInteger(1, sBytes);
        if (sValue.equals(BigInteger.ZERO) || rValue.equals(BigInteger.ZERO)) {
            throw new JOSEException("S and R must not be 0");
        }
        BigInteger N = ecParameterSpec.getOrder();
        if (N.compareTo(rValue) < 1 || N.compareTo(sValue) < 1) {
            throw new JOSEException("S and R must not exceed N");
        }
        if (rValue.mod(N).equals(BigInteger.ZERO) || sValue.mod(N).equals(BigInteger.ZERO)) {
            throw new JOSEException("R or S mod N != 0 check failed");
        }
    }

    private ECDSA() {
    }
}

