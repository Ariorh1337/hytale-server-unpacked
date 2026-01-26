/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ConcatKDF;
import com.nimbusds.jose.crypto.impl.ECDH;
import com.nimbusds.jose.crypto.impl.ECDHCryptoProvider;
import com.nimbusds.jose.crypto.utils.ECChecks;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Objects;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ECDH1PU {
    public static ECDH.AlgorithmMode resolveAlgorithmMode(JWEAlgorithm alg) throws JOSEException {
        Objects.requireNonNull(alg, "The parameter \"alg\" must not be null");
        if (alg.equals(JWEAlgorithm.ECDH_1PU)) {
            return ECDH.AlgorithmMode.DIRECT;
        }
        if (alg.equals(JWEAlgorithm.ECDH_1PU_A128KW) || alg.equals(JWEAlgorithm.ECDH_1PU_A192KW) || alg.equals(JWEAlgorithm.ECDH_1PU_A256KW)) {
            return ECDH.AlgorithmMode.KW;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, ECDHCryptoProvider.SUPPORTED_ALGORITHMS));
    }

    public static int sharedKeyLength(JWEAlgorithm alg, EncryptionMethod enc) throws JOSEException {
        Objects.requireNonNull(alg, "The parameter \"alg\" must not be null");
        Objects.requireNonNull(enc, "The parameter \"enc\" must not be null");
        if (alg.equals(JWEAlgorithm.ECDH_1PU)) {
            int length = enc.cekBitLength();
            if (length == 0) {
                throw new JOSEException("Unsupported JWE encryption method " + enc);
            }
            return length;
        }
        if (alg.equals(JWEAlgorithm.ECDH_1PU_A128KW)) {
            return 128;
        }
        if (alg.equals(JWEAlgorithm.ECDH_1PU_A192KW)) {
            return 192;
        }
        if (alg.equals(JWEAlgorithm.ECDH_1PU_A256KW)) {
            return 256;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, ECDHCryptoProvider.SUPPORTED_ALGORITHMS));
    }

    public static SecretKey deriveSharedKey(JWEHeader header, SecretKey Z, ConcatKDF concatKDF) throws JOSEException {
        String algID;
        Objects.requireNonNull(header, "The parameter \"header\" must not be null");
        Objects.requireNonNull(Z, "The parameter \"Z\" must not be null");
        Objects.requireNonNull(concatKDF, "The parameter \"concatKDF\" must not be null");
        int sharedKeyLength = ECDH1PU.sharedKeyLength(header.getAlgorithm(), header.getEncryptionMethod());
        ECDH.AlgorithmMode algMode = ECDH1PU.resolveAlgorithmMode(header.getAlgorithm());
        if (algMode == ECDH.AlgorithmMode.DIRECT) {
            algID = header.getEncryptionMethod().getName();
        } else if (algMode == ECDH.AlgorithmMode.KW) {
            algID = header.getAlgorithm().getName();
        } else {
            throw new JOSEException("Unsupported JWE ECDH algorithm mode: " + (Object)((Object)algMode));
        }
        return concatKDF.deriveKey(Z, sharedKeyLength, ConcatKDF.encodeDataWithLength(algID.getBytes(StandardCharsets.US_ASCII)), ConcatKDF.encodeDataWithLength(header.getAgreementPartyUInfo()), ConcatKDF.encodeDataWithLength(header.getAgreementPartyVInfo()), ConcatKDF.encodeIntData(sharedKeyLength), ConcatKDF.encodeNoData());
    }

    public static SecretKey deriveSharedKey(JWEHeader header, SecretKey Z, Base64URL tag, ConcatKDF concatKDF) throws JOSEException {
        String algID;
        Objects.requireNonNull(header, "The parameter \"header\" must not be null");
        Objects.requireNonNull(Z, "The parameter \"Z\" must not be null");
        Objects.requireNonNull(tag, "The parameter \"tag\" must not be null");
        Objects.requireNonNull(concatKDF, "The parameter \"concatKDF\" must not be null");
        int sharedKeyLength = ECDH1PU.sharedKeyLength(header.getAlgorithm(), header.getEncryptionMethod());
        ECDH.AlgorithmMode algMode = ECDH1PU.resolveAlgorithmMode(header.getAlgorithm());
        if (algMode == ECDH.AlgorithmMode.DIRECT) {
            algID = header.getEncryptionMethod().getName();
        } else if (algMode == ECDH.AlgorithmMode.KW) {
            algID = header.getAlgorithm().getName();
        } else {
            throw new JOSEException("Unsupported JWE ECDH algorithm mode: " + (Object)((Object)algMode));
        }
        return concatKDF.deriveKey(Z, sharedKeyLength, ConcatKDF.encodeDataWithLength(algID.getBytes(StandardCharsets.US_ASCII)), ConcatKDF.encodeDataWithLength(header.getAgreementPartyUInfo()), ConcatKDF.encodeDataWithLength(header.getAgreementPartyVInfo()), ConcatKDF.encodeIntData(sharedKeyLength), ConcatKDF.encodeNoData(), ConcatKDF.encodeDataWithLength(tag));
    }

    public static SecretKey deriveZ(SecretKey Ze, SecretKey Zs) {
        Objects.requireNonNull(Ze, "The parameter \"Ze\" must not be null");
        Objects.requireNonNull(Zs, "The parameter \"Zs\" must not be null");
        byte[] encodedKey = ByteUtils.concat(Ze.getEncoded(), Zs.getEncoded());
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    public static SecretKey deriveSenderZ(ECPrivateKey privateKey, ECPublicKey publicKey, ECPrivateKey epk, Provider provider) throws JOSEException {
        ECDH1PU.validateSameCurve(privateKey, publicKey);
        ECDH1PU.validateSameCurve(epk, publicKey);
        SecretKey Ze = ECDH.deriveSharedSecret(publicKey, epk, provider);
        SecretKey Zs = ECDH.deriveSharedSecret(publicKey, privateKey, provider);
        return ECDH1PU.deriveZ(Ze, Zs);
    }

    public static SecretKey deriveSenderZ(OctetKeyPair privateKey, OctetKeyPair publicKey, OctetKeyPair epk) throws JOSEException {
        ECDH1PU.validateSameCurve(privateKey, publicKey);
        ECDH1PU.validateSameCurve(epk, publicKey);
        SecretKey Ze = ECDH.deriveSharedSecret(publicKey, epk);
        SecretKey Zs = ECDH.deriveSharedSecret(publicKey, privateKey);
        return ECDH1PU.deriveZ(Ze, Zs);
    }

    public static SecretKey deriveRecipientZ(ECPrivateKey privateKey, ECPublicKey publicKey, ECPublicKey epk, Provider provider) throws JOSEException {
        ECDH1PU.validateSameCurve(privateKey, publicKey);
        ECDH1PU.validateSameCurve(privateKey, epk);
        SecretKey Ze = ECDH.deriveSharedSecret(epk, privateKey, provider);
        SecretKey Zs = ECDH.deriveSharedSecret(publicKey, privateKey, provider);
        return ECDH1PU.deriveZ(Ze, Zs);
    }

    public static SecretKey deriveRecipientZ(OctetKeyPair privateKey, OctetKeyPair publicKey, OctetKeyPair epk) throws JOSEException {
        ECDH1PU.validateSameCurve(privateKey, publicKey);
        ECDH1PU.validateSameCurve(privateKey, epk);
        SecretKey Ze = ECDH.deriveSharedSecret(epk, privateKey);
        SecretKey Zs = ECDH.deriveSharedSecret(publicKey, privateKey);
        return ECDH1PU.deriveZ(Ze, Zs);
    }

    public static void validateSameCurve(ECPrivateKey privateKey, ECPublicKey publicKey) throws JOSEException {
        Objects.requireNonNull(privateKey, "The parameter \"privateKey\" must not be null");
        Objects.requireNonNull(publicKey, "The parameter \"publicKey\" must not be null");
        if (!privateKey.getParams().getCurve().equals(publicKey.getParams().getCurve())) {
            throw new JOSEException("Curve of public key does not match curve of private key");
        }
        if (!ECChecks.isPointOnCurve(publicKey, privateKey)) {
            throw new JOSEException("Invalid public EC key: Point(s) not on the expected curve");
        }
    }

    public static void validateSameCurve(OctetKeyPair privateKey, OctetKeyPair publicKey) throws JOSEException {
        Objects.requireNonNull(privateKey, "The parameter \"privateKey\" must not be null");
        Objects.requireNonNull(publicKey, "The parameter \"publicKey\" must not be null");
        if (!privateKey.isPrivate()) {
            throw new JOSEException("OKP private key should be a private key");
        }
        if (publicKey.isPrivate()) {
            throw new JOSEException("OKP public key should not be a private key");
        }
        if (!publicKey.getCurve().equals(Curve.X25519)) {
            throw new JOSEException("Only supports OctetKeyPairs with crv=X25519");
        }
        if (!privateKey.getCurve().equals(publicKey.getCurve())) {
            throw new JOSEException("Curve of public key does not match curve of private key");
        }
    }

    private ECDH1PU() {
    }
}

