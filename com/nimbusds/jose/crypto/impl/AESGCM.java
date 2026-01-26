/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.Container;
import com.nimbusds.jose.util.KeyUtils;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

@ThreadSafe
public class AESGCM {
    public static final int IV_BIT_LENGTH = 96;
    public static final int AUTH_TAG_BIT_LENGTH = 128;

    public static byte[] generateIV(SecureRandom randomGen) {
        byte[] bytes = new byte[12];
        randomGen.nextBytes(bytes);
        return bytes;
    }

    public static AuthenticatedCipherText encrypt(SecretKey secretKey, Container<byte[]> ivContainer, byte[] plainText, byte[] authData, Provider provider) throws JOSEException {
        byte[] cipherOutput;
        Cipher cipher;
        SecretKey aesKey = KeyUtils.toAESKey(secretKey);
        byte[] iv = ivContainer.get();
        try {
            cipher = provider != null ? Cipher.getInstance("AES/GCM/NoPadding", provider) : Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(1, (Key)aesKey, gcmSpec);
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new JOSEException("Couldn't create AES/GCM/NoPadding cipher: " + e.getMessage(), e);
        }
        cipher.updateAAD(authData);
        try {
            cipherOutput = cipher.doFinal(plainText);
        }
        catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new JOSEException("Couldn't encrypt with AES/GCM/NoPadding: " + e.getMessage(), e);
        }
        int tagPos = cipherOutput.length - ByteUtils.byteLength(128);
        byte[] cipherText = ByteUtils.subArray(cipherOutput, 0, tagPos);
        byte[] authTag = ByteUtils.subArray(cipherOutput, tagPos, ByteUtils.byteLength(128));
        ivContainer.set(AESGCM.actualIVOf(cipher));
        return new AuthenticatedCipherText(cipherText, authTag);
    }

    private static byte[] actualIVOf(Cipher cipher) throws JOSEException {
        GCMParameterSpec actualParams = AESGCM.actualParamsOf(cipher);
        byte[] iv = actualParams.getIV();
        int tLen = actualParams.getTLen();
        AESGCM.validate(iv, tLen);
        return iv;
    }

    private static void validate(byte[] iv, int authTagLength) throws JOSEException {
        if (ByteUtils.safeBitLength(iv) != 96) {
            throw new JOSEException(String.format("IV length of %d bits is required, got %d", 96, ByteUtils.safeBitLength(iv)));
        }
        if (authTagLength != 128) {
            throw new JOSEException(String.format("Authentication tag length of %d bits is required, got %d", 128, authTagLength));
        }
    }

    private static GCMParameterSpec actualParamsOf(Cipher cipher) throws JOSEException {
        AlgorithmParameters algorithmParameters = cipher.getParameters();
        if (algorithmParameters == null) {
            throw new JOSEException("AES GCM ciphers are expected to make use of algorithm parameters");
        }
        try {
            return algorithmParameters.getParameterSpec(GCMParameterSpec.class);
        }
        catch (InvalidParameterSpecException shouldNotHappen) {
            throw new JOSEException(shouldNotHappen.getMessage(), shouldNotHappen);
        }
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] iv, byte[] cipherText, byte[] authData, byte[] authTag, Provider provider) throws JOSEException {
        Cipher cipher;
        SecretKey aesKey = KeyUtils.toAESKey(secretKey);
        try {
            cipher = provider != null ? Cipher.getInstance("AES/GCM/NoPadding", provider) : Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(2, (Key)aesKey, gcmSpec);
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new JOSEException("Couldn't create AES/GCM/NoPadding cipher: " + e.getMessage(), e);
        }
        cipher.updateAAD(authData);
        try {
            return cipher.doFinal(ByteUtils.concat(cipherText, authTag));
        }
        catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new JOSEException("AES/GCM/NoPadding decryption failed: " + e.getMessage(), e);
        }
    }

    private AESGCM() {
    }
}

