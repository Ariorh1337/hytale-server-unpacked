/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.aead.AeadWrapper;
import com.google.crypto.tink.aead.AesCtrHmacAeadKeyManager;
import com.google.crypto.tink.aead.AesEaxKeyManager;
import com.google.crypto.tink.aead.AesGcmKeyManager;
import com.google.crypto.tink.aead.AesGcmSivKeyManager;
import com.google.crypto.tink.aead.ChaCha20Poly1305KeyManager;
import com.google.crypto.tink.aead.KmsAeadKeyManager;
import com.google.crypto.tink.aead.KmsEnvelopeAeadKeyManager;
import com.google.crypto.tink.aead.XAesGcmKeyManager;
import com.google.crypto.tink.aead.XChaCha20Poly1305KeyManager;
import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.mac.MacConfig;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;

public final class AeadConfig {
    public static final String AES_CTR_HMAC_AEAD_TYPE_URL = AeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey");
    public static final String AES_GCM_TYPE_URL = AeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesGcmKey");
    public static final String AES_GCM_SIV_TYPE_URL = AeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesGcmSivKey");
    public static final String AES_EAX_TYPE_URL = AeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesEaxKey");
    public static final String KMS_AEAD_TYPE_URL = AeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.KmsAeadKey");
    public static final String KMS_ENVELOPE_AEAD_TYPE_URL = AeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey");
    public static final String CHACHA20_POLY1305_TYPE_URL = AeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.ChaCha20Poly1305Key");
    public static final String XCHACHA20_POLY1305_TYPE_URL = AeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.XChaCha20Poly1305Key");
    @Deprecated
    public static final RegistryConfig TINK_1_0_0;
    @Deprecated
    public static final RegistryConfig TINK_1_1_0;
    @Deprecated
    public static final RegistryConfig LATEST;

    @CanIgnoreReturnValue
    private static String initializeClassReturnInput(String s) {
        return s;
    }

    @Deprecated
    public static void init() throws GeneralSecurityException {
        AeadConfig.register();
    }

    public static void register() throws GeneralSecurityException {
        AeadWrapper.register();
        MacConfig.register();
        AesCtrHmacAeadKeyManager.register(true);
        AesGcmKeyManager.register(true);
        if (TinkFips.useOnlyFips()) {
            return;
        }
        AesEaxKeyManager.register(true);
        AesGcmSivKeyManager.register(true);
        ChaCha20Poly1305KeyManager.register(true);
        KmsAeadKeyManager.register(true);
        KmsEnvelopeAeadKeyManager.register(true);
        XChaCha20Poly1305KeyManager.register(true);
        XAesGcmKeyManager.register(true);
    }

    @Deprecated
    public static void registerStandardKeyTypes() throws GeneralSecurityException {
        AeadConfig.register();
    }

    private AeadConfig() {
    }

    static {
        TINK_1_1_0 = TINK_1_0_0 = RegistryConfig.getDefaultInstance();
        LATEST = TINK_1_0_0;
        try {
            AeadConfig.init();
        }
        catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

