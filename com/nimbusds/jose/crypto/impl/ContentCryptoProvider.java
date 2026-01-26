/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AESCBC;
import com.nimbusds.jose.crypto.impl.AESGCM;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import com.nimbusds.jose.crypto.impl.DeflateHelper;
import com.nimbusds.jose.crypto.impl.XC20P;
import com.nimbusds.jose.jca.JWEJCAContext;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.Container;
import com.nimbusds.jose.util.IntegerOverflowException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ContentCryptoProvider {
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    public static final Map<Integer, Set<EncryptionMethod>> COMPATIBLE_ENCRYPTION_METHODS;

    public static SecretKey generateCEK(EncryptionMethod enc, SecureRandom randomGen) throws JOSEException {
        if (!SUPPORTED_ENCRYPTION_METHODS.contains(enc)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedEncryptionMethod(enc, SUPPORTED_ENCRYPTION_METHODS));
        }
        byte[] cekMaterial = new byte[ByteUtils.byteLength(enc.cekBitLength())];
        randomGen.nextBytes(cekMaterial);
        return new SecretKeySpec(cekMaterial, "AES");
    }

    private static void checkCEKLength(SecretKey cek, EncryptionMethod enc) throws KeyLengthException {
        int cekBitLength;
        try {
            cekBitLength = ByteUtils.safeBitLength(cek.getEncoded());
        }
        catch (IntegerOverflowException e) {
            throw new KeyLengthException("The Content Encryption Key (CEK) is too long: " + e.getMessage());
        }
        if (cekBitLength == 0) {
            return;
        }
        if (enc.cekBitLength() != cekBitLength) {
            throw new KeyLengthException("The Content Encryption Key (CEK) length for " + enc + " must be " + enc.cekBitLength() + " bits");
        }
    }

    public static JWECryptoParts encrypt(JWEHeader header, byte[] clearText, SecretKey cek, Base64URL encryptedKey, JWEJCAContext jcaProvider) throws JOSEException {
        return ContentCryptoProvider.encrypt(header, clearText, null, cek, encryptedKey, jcaProvider);
    }

    public static JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad, SecretKey cek, Base64URL encryptedKey, JWEJCAContext jcaProvider) throws JOSEException {
        AuthenticatedCipherText authCipherText;
        byte[] iv;
        if (aad == null) {
            return ContentCryptoProvider.encrypt(header, clearText, AAD.compute(header), cek, encryptedKey, jcaProvider);
        }
        ContentCryptoProvider.checkCEKLength(cek, header.getEncryptionMethod());
        byte[] plainText = DeflateHelper.applyCompression(header, clearText);
        if (header.getEncryptionMethod().equals(EncryptionMethod.A128CBC_HS256) || header.getEncryptionMethod().equals(EncryptionMethod.A192CBC_HS384) || header.getEncryptionMethod().equals(EncryptionMethod.A256CBC_HS512)) {
            iv = AESCBC.generateIV(jcaProvider.getSecureRandom());
            authCipherText = AESCBC.encryptAuthenticated(cek, iv, plainText, aad, jcaProvider.getContentEncryptionProvider(), jcaProvider.getMACProvider());
        } else if (header.getEncryptionMethod().equals(EncryptionMethod.A128GCM) || header.getEncryptionMethod().equals(EncryptionMethod.A192GCM) || header.getEncryptionMethod().equals(EncryptionMethod.A256GCM)) {
            Container<byte[]> ivContainer = new Container<byte[]>(AESGCM.generateIV(jcaProvider.getSecureRandom()));
            authCipherText = AESGCM.encrypt(cek, ivContainer, plainText, aad, jcaProvider.getContentEncryptionProvider());
            iv = ivContainer.get();
        } else if (header.getEncryptionMethod().equals(EncryptionMethod.A128CBC_HS256_DEPRECATED) || header.getEncryptionMethod().equals(EncryptionMethod.A256CBC_HS512_DEPRECATED)) {
            iv = AESCBC.generateIV(jcaProvider.getSecureRandom());
            authCipherText = AESCBC.encryptWithConcatKDF(header, cek, encryptedKey, iv, plainText, jcaProvider.getContentEncryptionProvider(), jcaProvider.getMACProvider());
        } else if (header.getEncryptionMethod().equals(EncryptionMethod.XC20P)) {
            Container<Object> ivContainer = new Container<Object>(null);
            authCipherText = XC20P.encryptAuthenticated(cek, ivContainer, plainText, aad);
            iv = ivContainer.get();
        } else {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedEncryptionMethod(header.getEncryptionMethod(), SUPPORTED_ENCRYPTION_METHODS));
        }
        return new JWECryptoParts(header, encryptedKey, Base64URL.encode(iv), Base64URL.encode(authCipherText.getCipherText()), Base64URL.encode(authCipherText.getAuthenticationTag()));
    }

    public static byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag, SecretKey cek, JWEJCAContext jcaProvider) throws JOSEException {
        return ContentCryptoProvider.decrypt(header, null, encryptedKey, iv, cipherText, authTag, cek, jcaProvider);
    }

    public static byte[] decrypt(JWEHeader header, byte[] aad, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag, SecretKey cek, JWEJCAContext jcaProvider) throws JOSEException {
        byte[] plainText;
        if (aad == null) {
            return ContentCryptoProvider.decrypt(header, AAD.compute(header), encryptedKey, iv, cipherText, authTag, cek, jcaProvider);
        }
        ContentCryptoProvider.checkCEKLength(cek, header.getEncryptionMethod());
        if (header.getEncryptionMethod().equals(EncryptionMethod.A128CBC_HS256) || header.getEncryptionMethod().equals(EncryptionMethod.A192CBC_HS384) || header.getEncryptionMethod().equals(EncryptionMethod.A256CBC_HS512)) {
            plainText = AESCBC.decryptAuthenticated(cek, iv.decode(), cipherText.decode(), aad, authTag.decode(), jcaProvider.getContentEncryptionProvider(), jcaProvider.getMACProvider());
        } else if (header.getEncryptionMethod().equals(EncryptionMethod.A128GCM) || header.getEncryptionMethod().equals(EncryptionMethod.A192GCM) || header.getEncryptionMethod().equals(EncryptionMethod.A256GCM)) {
            plainText = AESGCM.decrypt(cek, iv.decode(), cipherText.decode(), aad, authTag.decode(), jcaProvider.getContentEncryptionProvider());
        } else if (header.getEncryptionMethod().equals(EncryptionMethod.A128CBC_HS256_DEPRECATED) || header.getEncryptionMethod().equals(EncryptionMethod.A256CBC_HS512_DEPRECATED)) {
            plainText = AESCBC.decryptWithConcatKDF(header, cek, encryptedKey, iv, cipherText, authTag, jcaProvider.getContentEncryptionProvider(), jcaProvider.getMACProvider());
        } else if (header.getEncryptionMethod().equals(EncryptionMethod.XC20P)) {
            plainText = XC20P.decryptAuthenticated(cek, iv.decode(), cipherText.decode(), aad, authTag.decode());
        } else {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedEncryptionMethod(header.getEncryptionMethod(), SUPPORTED_ENCRYPTION_METHODS));
        }
        return DeflateHelper.applyDecompression(header, plainText);
    }

    static {
        LinkedHashSet<EncryptionMethod> methods = new LinkedHashSet<EncryptionMethod>();
        methods.add(EncryptionMethod.A128CBC_HS256);
        methods.add(EncryptionMethod.A192CBC_HS384);
        methods.add(EncryptionMethod.A256CBC_HS512);
        methods.add(EncryptionMethod.A128GCM);
        methods.add(EncryptionMethod.A192GCM);
        methods.add(EncryptionMethod.A256GCM);
        methods.add(EncryptionMethod.A128CBC_HS256_DEPRECATED);
        methods.add(EncryptionMethod.A256CBC_HS512_DEPRECATED);
        methods.add(EncryptionMethod.XC20P);
        SUPPORTED_ENCRYPTION_METHODS = Collections.unmodifiableSet(methods);
        HashMap encsMap = new HashMap();
        HashSet<EncryptionMethod> bit128Encs = new HashSet<EncryptionMethod>();
        HashSet<EncryptionMethod> bit192Encs = new HashSet<EncryptionMethod>();
        HashSet<EncryptionMethod> bit256Encs = new HashSet<EncryptionMethod>();
        HashSet<EncryptionMethod> bit384Encs = new HashSet<EncryptionMethod>();
        HashSet<EncryptionMethod> bit512Encs = new HashSet<EncryptionMethod>();
        bit128Encs.add(EncryptionMethod.A128GCM);
        bit192Encs.add(EncryptionMethod.A192GCM);
        bit256Encs.add(EncryptionMethod.A256GCM);
        bit256Encs.add(EncryptionMethod.A128CBC_HS256);
        bit256Encs.add(EncryptionMethod.A128CBC_HS256_DEPRECATED);
        bit256Encs.add(EncryptionMethod.XC20P);
        bit384Encs.add(EncryptionMethod.A192CBC_HS384);
        bit512Encs.add(EncryptionMethod.A256CBC_HS512);
        bit512Encs.add(EncryptionMethod.A256CBC_HS512_DEPRECATED);
        encsMap.put(128, Collections.unmodifiableSet(bit128Encs));
        encsMap.put(192, Collections.unmodifiableSet(bit192Encs));
        encsMap.put(256, Collections.unmodifiableSet(bit256Encs));
        encsMap.put(384, Collections.unmodifiableSet(bit384Encs));
        encsMap.put(512, Collections.unmodifiableSet(bit512Encs));
        COMPATIBLE_ENCRYPTION_METHODS = Collections.unmodifiableMap(encsMap);
    }
}

