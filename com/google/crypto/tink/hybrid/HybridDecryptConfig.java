/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.Config;
import com.google.crypto.tink.hybrid.HybridConfig;
import java.security.GeneralSecurityException;

@Deprecated
public final class HybridDecryptConfig {
    @Deprecated
    public static void registerStandardKeyTypes() throws GeneralSecurityException {
        Config.register(HybridConfig.TINK_1_0_0);
    }

    private HybridDecryptConfig() {
    }
}

