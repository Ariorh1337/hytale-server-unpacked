/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.prf;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.prf.AesCmacPrfKeyManager;
import com.google.crypto.tink.prf.HkdfPrfKeyManager;
import com.google.crypto.tink.prf.HmacPrfKeyManager;
import com.google.crypto.tink.prf.PrfSetWrapper;
import java.security.GeneralSecurityException;

public final class PrfConfig {
    public static final String PRF_TYPE_URL = HkdfPrfKeyManager.getKeyType();
    public static final String HMAC_PRF_TYPE_URL = HmacPrfKeyManager.getKeyType();

    public static void register() throws GeneralSecurityException {
        PrfSetWrapper.register();
        HmacPrfKeyManager.register(true);
        if (TinkFips.useOnlyFips()) {
            return;
        }
        AesCmacPrfKeyManager.register(true);
        HkdfPrfKeyManager.register(true);
    }

    private PrfConfig() {
    }
}

