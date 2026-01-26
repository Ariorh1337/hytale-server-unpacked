/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.mac;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.mac.AesCmacKeyManager;
import com.google.crypto.tink.mac.ChunkedMacWrapper;
import com.google.crypto.tink.mac.HmacKeyManager;
import com.google.crypto.tink.mac.MacWrapper;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;

public final class MacConfig {
    public static final String HMAC_TYPE_URL = MacConfig.initializeClassReturnInput("type.googleapis.com/google.crypto.tink.HmacKey");
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
        MacConfig.register();
    }

    public static void register() throws GeneralSecurityException {
        MacWrapper.register();
        ChunkedMacWrapper.register();
        HmacKeyManager.register(true);
        if (TinkFips.useOnlyFips()) {
            return;
        }
        AesCmacKeyManager.register(true);
    }

    @Deprecated
    public static void registerStandardKeyTypes() throws GeneralSecurityException {
        MacConfig.register();
    }

    private MacConfig() {
    }

    static {
        TINK_1_1_0 = TINK_1_0_0 = RegistryConfig.getDefaultInstance();
        LATEST = TINK_1_0_0;
        try {
            MacConfig.init();
        }
        catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

