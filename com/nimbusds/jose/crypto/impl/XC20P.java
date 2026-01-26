/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.google.crypto.tink.subtle.XChaCha20Poly1305;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.Container;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;

@ThreadSafe
public class XC20P {
    public static final int AUTH_TAG_BIT_LENGTH = 128;
    public static final int IV_BIT_LENGTH = 192;

    public static AuthenticatedCipherText encryptAuthenticated(SecretKey secretKey, Container<byte[]> ivContainer, byte[] plainText, byte[] authData) throws JOSEException {
        byte[] cipherOutput;
        XChaCha20Poly1305 aead;
        try {
            aead = new XChaCha20Poly1305(secretKey.getEncoded());
        }
        catch (GeneralSecurityException e) {
            throw new JOSEException("Invalid XChaCha20Poly1305 key: " + e.getMessage(), e);
        }
        try {
            cipherOutput = aead.encrypt(plainText, authData);
        }
        catch (GeneralSecurityException e) {
            throw new JOSEException("Couldn't encrypt with XChaCha20Poly1305: " + e.getMessage(), e);
        }
        int tagPos = cipherOutput.length - ByteUtils.byteLength(128);
        int cipherTextPos = ByteUtils.byteLength(192);
        byte[] iv = ByteUtils.subArray(cipherOutput, 0, cipherTextPos);
        byte[] cipherText = ByteUtils.subArray(cipherOutput, cipherTextPos, tagPos - cipherTextPos);
        byte[] authTag = ByteUtils.subArray(cipherOutput, tagPos, ByteUtils.byteLength(128));
        ivContainer.set(iv);
        return new AuthenticatedCipherText(cipherText, authTag);
    }

    public static byte[] decryptAuthenticated(SecretKey secretKey, byte[] iv, byte[] cipherText, byte[] authData, byte[] authTag) throws JOSEException {
        XChaCha20Poly1305 aead;
        try {
            aead = new XChaCha20Poly1305(secretKey.getEncoded());
        }
        catch (GeneralSecurityException e) {
            throw new JOSEException("Invalid XChaCha20Poly1305 key: " + e.getMessage(), e);
        }
        byte[] cipherInput = ByteUtils.concat(iv, cipherText, authTag);
        try {
            return aead.decrypt(cipherInput, authData);
        }
        catch (GeneralSecurityException e) {
            throw new JOSEException("XChaCha20Poly1305 decryption failed: " + e.getMessage(), e);
        }
    }
}

