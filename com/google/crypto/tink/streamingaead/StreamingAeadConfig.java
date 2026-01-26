/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.crypto.tink.streamingaead.AesCtrHmacStreamingKeyManager;
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingKeyManager;
import com.google.crypto.tink.streamingaead.StreamingAeadWrapper;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;

public final class StreamingAeadConfig {
    public static final String AES_CTR_HMAC_STREAMINGAEAD_TYPE_URL = StreamingAeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey");
    public static final String AES_GCM_HKDF_STREAMINGAEAD_TYPE_URL = StreamingAeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey");
    @Deprecated
    public static final RegistryConfig TINK_1_1_0 = RegistryConfig.getDefaultInstance();
    public static final RegistryConfig LATEST = RegistryConfig.getDefaultInstance();

    @CanIgnoreReturnValue
    private static String initializeClassReturnInput(String s) {
        return s;
    }

    @Deprecated
    public static void init() throws GeneralSecurityException {
        StreamingAeadConfig.register();
    }

    public static void register() throws GeneralSecurityException {
        StreamingAeadWrapper.register();
        if (TinkFips.useOnlyFips()) {
            return;
        }
        AesCtrHmacStreamingKeyManager.register(true);
        AesGcmHkdfStreamingKeyManager.register(true);
    }

    private StreamingAeadConfig() {
    }

    static {
        try {
            StreamingAeadConfig.init();
        }
        catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

