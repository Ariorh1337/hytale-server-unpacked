/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.daead;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.daead.AesSivKeyManager;
import com.google.crypto.tink.daead.DeterministicAeadWrapper;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import java.security.GeneralSecurityException;

public final class DeterministicAeadConfig {
    public static final String AES_SIV_TYPE_URL = DeterministicAeadConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesSivKey");
    @Deprecated
    public static final RegistryConfig TINK_1_1_0 = RegistryConfig.getDefaultInstance();
    @Deprecated
    public static final RegistryConfig LATEST = RegistryConfig.getDefaultInstance();

    @CanIgnoreReturnValue
    private static String initializeClassReturnInput(String s) {
        return s;
    }

    @Deprecated
    @InlineMe(replacement="DeterministicAeadConfig.register()", imports={"com.google.crypto.tink.daead.DeterministicAeadConfig"})
    public static void init() throws GeneralSecurityException {
        DeterministicAeadConfig.register();
    }

    public static void register() throws GeneralSecurityException {
        DeterministicAeadWrapper.register();
        if (TinkFips.useOnlyFips()) {
            return;
        }
        AesSivKeyManager.register(true);
    }

    private DeterministicAeadConfig() {
    }

    static {
        try {
            DeterministicAeadConfig.register();
        }
        catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

