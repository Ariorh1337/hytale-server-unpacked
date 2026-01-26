/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.Validators;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesGcmJceUtil {
    public static final int IV_SIZE_IN_BYTES = 12;
    public static final int TAG_SIZE_IN_BYTES = 16;
    private static final ThreadLocal<Cipher> localCipher = new ThreadLocal<Cipher>(){

        @Override
        protected Cipher initialValue() {
            try {
                return EngineFactory.CIPHER.getInstance("AES/GCM/NoPadding");
            }
            catch (GeneralSecurityException ex) {
                throw new IllegalStateException(ex);
            }
        }
    };

    public static Cipher getThreadLocalCipher() {
        return localCipher.get();
    }

    public static SecretKey getSecretKey(byte[] key) throws GeneralSecurityException {
        Validators.validateAesKeySize(key.length);
        return new SecretKeySpec(key, "AES");
    }

    public static AlgorithmParameterSpec getParams(byte[] iv) {
        return AesGcmJceUtil.getParams(iv, 0, iv.length);
    }

    public static AlgorithmParameterSpec getParams(byte[] buf, int offset, int len) {
        Integer apiLevel = Util.getAndroidApiLevel();
        if (apiLevel != null && apiLevel <= 19) {
            return new IvParameterSpec(buf, offset, len);
        }
        return new GCMParameterSpec(128, buf, offset, len);
    }

    private AesGcmJceUtil() {
    }
}

