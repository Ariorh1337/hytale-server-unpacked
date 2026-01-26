/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObjectJSON;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.X25519Decrypter;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.BaseJWEProvider;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.crypto.impl.MultiCryptoProvider;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ThreadSafe
public class MultiDecrypter
extends MultiCryptoProvider
implements JWEDecrypter,
CriticalHeaderParamsAware {
    private final JWK jwk;
    private final String kid;
    private final URI x5u;
    private final Base64URL x5t;
    private final Base64URL x5t256;
    private final List<Base64> x5c;
    private final Base64URL thumbprint;
    private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

    public MultiDecrypter(JWK jwk) throws JOSEException, KeyLengthException {
        this(jwk, null);
    }

    public MultiDecrypter(JWK jwk, Set<String> defCritHeaders) throws JOSEException, KeyLengthException {
        super(null);
        if (jwk == null) {
            throw new IllegalArgumentException("The private key (JWK) must not be null");
        }
        this.jwk = jwk;
        this.kid = jwk.getKeyID();
        this.x5c = jwk.getX509CertChain();
        this.x5u = jwk.getX509CertURL();
        this.x5t = jwk.getX509CertThumbprint();
        this.x5t256 = jwk.getX509CertSHA256Thumbprint();
        this.thumbprint = jwk.computeThumbprint();
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
    }

    @Override
    public Set<String> getProcessedCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }

    @Override
    public Set<String> getDeferredCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }

    private boolean jwkMatched(JWEHeader recipientHeader) throws JOSEException {
        if (this.thumbprint.toString().equals(recipientHeader.getKeyID())) {
            return true;
        }
        JWK rjwk = recipientHeader.getJWK();
        if (rjwk != null && this.thumbprint.equals(rjwk.computeThumbprint())) {
            return true;
        }
        if (this.x5u != null && this.x5u.equals(recipientHeader.getX509CertURL())) {
            return true;
        }
        if (this.x5t != null && this.x5t.equals(recipientHeader.getX509CertThumbprint())) {
            return true;
        }
        if (this.x5t256 != null && this.x5t256.equals(recipientHeader.getX509CertSHA256Thumbprint())) {
            return true;
        }
        List rx5c = recipientHeader.getX509CertChain();
        if (this.x5c != null && rx5c != null && this.x5c.containsAll(rx5c) && rx5c.containsAll(this.x5c)) {
            return true;
        }
        return this.kid != null && this.kid.equals(recipientHeader.getKeyID());
    }

    @Deprecated
    public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) throws JOSEException {
        return this.decrypt(header, encryptedKey, iv, cipherText, authTag, AAD.compute(header));
    }

    @Override
    public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag, byte[] aad) throws JOSEException {
        BaseJWEProvider decrypter;
        if (iv == null) {
            throw new JOSEException("Unexpected present JWE initialization vector (IV)");
        }
        if (authTag == null) {
            throw new JOSEException("Missing JWE authentication tag");
        }
        if (aad == null) {
            throw new JOSEException("Missing JWE additional authenticated data (AAD)");
        }
        KeyType kty = this.jwk.getKeyType();
        Set<String> defCritHeaders = this.critPolicy.getDeferredCriticalHeaderParams();
        JWEObjectJSON.Recipient recipient = null;
        JWEHeader recipientHeader = null;
        try {
            for (Object recipientMap : JSONObjectUtils.getJSONArray(JSONObjectUtils.parse(encryptedKey.decodeToString()), "recipients")) {
                try {
                    recipient = JWEObjectJSON.Recipient.parse((Map)recipientMap);
                    recipientHeader = (JWEHeader)header.join(recipient.getUnprotectedHeader());
                }
                catch (Exception e) {
                    throw new JOSEException(e.getMessage());
                }
                if (!this.jwkMatched(recipientHeader)) {
                    recipientHeader = null;
                    continue;
                }
                break;
            }
        }
        catch (Exception e) {
            recipientHeader = header;
            recipient = new JWEObjectJSON.Recipient(null, encryptedKey);
        }
        if (recipientHeader == null) {
            throw new JOSEException("No recipient found");
        }
        JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(recipientHeader);
        this.critPolicy.ensureHeaderPasses(recipientHeader);
        if (KeyType.RSA.equals(kty) && RSADecrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
            decrypter = new RSADecrypter(this.jwk.toRSAKey().toRSAPrivateKey(), defCritHeaders);
        } else if (KeyType.EC.equals(kty) && ECDHDecrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
            decrypter = new ECDHDecrypter(this.jwk.toECKey().toECPrivateKey(), defCritHeaders);
        } else if (KeyType.OCT.equals(kty) && AESDecrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
            decrypter = new AESDecrypter(this.jwk.toOctetSequenceKey().toSecretKey("AES"), defCritHeaders);
        } else if (KeyType.OCT.equals(kty) && DirectDecrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
            decrypter = new DirectDecrypter(this.jwk.toOctetSequenceKey().toSecretKey("AES"), defCritHeaders);
        } else if (KeyType.OKP.equals(kty) && X25519Decrypter.SUPPORTED_ALGORITHMS.contains(alg)) {
            decrypter = new X25519Decrypter(this.jwk.toOctetKeyPair(), defCritHeaders);
        } else {
            throw new JOSEException("Unsupported algorithm");
        }
        return decrypter.decrypt(recipientHeader, recipient.getEncryptedKey(), iv, cipherText, authTag, aad);
    }
}

