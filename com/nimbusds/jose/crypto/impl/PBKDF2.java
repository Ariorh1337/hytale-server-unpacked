/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.crypto.impl.PRFParams;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PBKDF2 {
    public static final int MIN_SALT_LENGTH = 8;
    static final byte[] ZERO_BYTE = new byte[]{0};
    static final long MAX_DERIVED_KEY_LENGTH = 0xFFFFFFFFL;

    public static byte[] formatSalt(JWEAlgorithm alg, byte[] salt) throws JOSEException {
        byte[] algBytes = alg.toString().getBytes(StandardCharset.UTF_8);
        if (salt == null) {
            throw new JOSEException("The salt must not be null");
        }
        if (salt.length < 8) {
            throw new JOSEException("The salt must be at least 8 bytes long");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(algBytes);
            out.write(ZERO_BYTE);
            out.write(salt);
        }
        catch (IOException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        return out.toByteArray();
    }

    public static SecretKey deriveKey(byte[] password, byte[] formattedSalt, int iterationCount, PRFParams prfParams, Provider jcaProvider) throws JOSEException {
        if (formattedSalt == null) {
            throw new JOSEException("The formatted salt must not be null");
        }
        if (iterationCount < 1) {
            throw new JOSEException("The iteration count must be greater than 0");
        }
        int keyLengthInBits = ByteUtils.bitLength(prfParams.getDerivedKeyByteLength());
        PBEKeySpec spec = new PBEKeySpec(new String(password, StandardCharsets.UTF_8).toCharArray(), formattedSalt, iterationCount, keyLengthInBits);
        try {
            SecretKeyFactory skf = jcaProvider != null ? SecretKeyFactory.getInstance("PBKDF2With" + prfParams.getMACAlgorithm(), jcaProvider) : SecretKeyFactory.getInstance("PBKDF2With" + prfParams.getMACAlgorithm());
            return new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "AES");
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new JOSEException(ex.getLocalizedMessage(), ex);
        }
    }

    static byte[] extractBlock(byte[] formattedSalt, int iterationCount, int blockIndex, Mac prf) throws JOSEException {
        if (formattedSalt == null) {
            throw new JOSEException("The formatted salt must not be null");
        }
        if (iterationCount < 1) {
            throw new JOSEException("The iteration count must be greater than 0");
        }
        byte[] lastU = null;
        byte[] xorU = null;
        for (int i = 1; i <= iterationCount; ++i) {
            byte[] currentU;
            if (i == 1) {
                byte[] inputBytes = ByteUtils.concat(formattedSalt, IntegerUtils.toBytes(blockIndex));
                xorU = currentU = prf.doFinal(inputBytes);
            } else {
                currentU = prf.doFinal(lastU);
                for (int j = 0; j < currentU.length; ++j) {
                    xorU[j] = (byte)(currentU[j] ^ xorU[j]);
                }
            }
            lastU = currentU;
        }
        return xorU;
    }

    private PBKDF2() {
    }
}

