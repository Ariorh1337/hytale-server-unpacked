/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.AlgorithmParametersHelper;
import com.nimbusds.jose.crypto.impl.CipherHelper;
import com.nimbusds.jose.crypto.opts.CipherMode;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class RSA_OAEP_SHA2 {
    private static final String RSA_OEAP_256_JCA_ALG = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String RSA_OEAP_384_JCA_ALG = "RSA/ECB/OAEPWithSHA-384AndMGF1Padding";
    private static final String RSA_OEAP_512_JCA_ALG = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
    private static final String SHA_256_JCA_ALG = "SHA-256";
    private static final String SHA_384_JCA_ALG = "SHA-384";
    private static final String SHA_512_JCA_ALG = "SHA-512";

    public static byte[] encryptCEK(RSAPublicKey pub, SecretKey cek, int shaBitSize, CipherMode mode, Provider provider) throws JOSEException {
        MGF1ParameterSpec mgf1ParameterSpec;
        String jcaShaAlgName;
        String jcaAlgName;
        assert (mode == CipherMode.WRAP_UNWRAP || mode == CipherMode.ENCRYPT_DECRYPT);
        if (256 == shaBitSize) {
            jcaAlgName = RSA_OEAP_256_JCA_ALG;
            jcaShaAlgName = SHA_256_JCA_ALG;
            mgf1ParameterSpec = MGF1ParameterSpec.SHA256;
        } else if (384 == shaBitSize) {
            jcaAlgName = RSA_OEAP_384_JCA_ALG;
            jcaShaAlgName = SHA_384_JCA_ALG;
            mgf1ParameterSpec = MGF1ParameterSpec.SHA384;
        } else if (512 == shaBitSize) {
            jcaAlgName = RSA_OEAP_512_JCA_ALG;
            jcaShaAlgName = SHA_512_JCA_ALG;
            mgf1ParameterSpec = MGF1ParameterSpec.SHA512;
        } else {
            throw new JOSEException("Unsupported SHA-2 bit size: " + shaBitSize);
        }
        try {
            AlgorithmParameters algp = AlgorithmParametersHelper.getInstance("OAEP", provider);
            OAEPParameterSpec paramSpec = new OAEPParameterSpec(jcaShaAlgName, "MGF1", mgf1ParameterSpec, PSource.PSpecified.DEFAULT);
            algp.init(paramSpec);
            Cipher cipher = CipherHelper.getInstance(jcaAlgName, provider);
            cipher.init(mode.getForJWEEncrypter(), (Key)pub, algp);
            if (mode == CipherMode.WRAP_UNWRAP) {
                return cipher.wrap(cek);
            }
            return cipher.doFinal(cek.getEncoded());
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("Encryption failed due to invalid RSA key for SHA-" + shaBitSize + ": The RSA key may be too short, use a longer key", e);
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public static SecretKey decryptCEK(PrivateKey priv, byte[] encryptedCEK, int shaBitSize, CipherMode mode, Provider provider) throws JOSEException {
        MGF1ParameterSpec mgf1ParameterSpec;
        String jcaShaAlgName;
        String jcaAlgName;
        assert (mode == CipherMode.WRAP_UNWRAP || mode == CipherMode.ENCRYPT_DECRYPT);
        if (256 == shaBitSize) {
            jcaAlgName = RSA_OEAP_256_JCA_ALG;
            jcaShaAlgName = SHA_256_JCA_ALG;
            mgf1ParameterSpec = MGF1ParameterSpec.SHA256;
        } else if (384 == shaBitSize) {
            jcaAlgName = RSA_OEAP_384_JCA_ALG;
            jcaShaAlgName = SHA_384_JCA_ALG;
            mgf1ParameterSpec = MGF1ParameterSpec.SHA384;
        } else if (512 == shaBitSize) {
            jcaAlgName = RSA_OEAP_512_JCA_ALG;
            jcaShaAlgName = SHA_512_JCA_ALG;
            mgf1ParameterSpec = MGF1ParameterSpec.SHA512;
        } else {
            throw new JOSEException("Unsupported SHA-2 bit size: " + shaBitSize);
        }
        try {
            AlgorithmParameters algp = AlgorithmParametersHelper.getInstance("OAEP", provider);
            OAEPParameterSpec paramSpec = new OAEPParameterSpec(jcaShaAlgName, "MGF1", mgf1ParameterSpec, PSource.PSpecified.DEFAULT);
            algp.init(paramSpec);
            Cipher cipher = CipherHelper.getInstance(jcaAlgName, provider);
            cipher.init(mode.getForJWEDecrypter(), (Key)priv, algp);
            if (mode == CipherMode.WRAP_UNWRAP) {
                return (SecretKey)cipher.unwrap(encryptedCEK, "AES", 3);
            }
            return new SecretKeySpec(cipher.doFinal(encryptedCEK), "AES");
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    private RSA_OAEP_SHA2() {
    }
}

