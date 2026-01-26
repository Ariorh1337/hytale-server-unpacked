/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.keyderivation.internal.KeysetDeriverWrapper;
import com.google.crypto.tink.keyderivation.internal.PrfBasedDeriverKeyManager;
import com.google.crypto.tink.prf.HkdfPrfKeyManager;
import java.security.GeneralSecurityException;

public final class KeyDerivationConfig {
    public static void register() throws GeneralSecurityException {
        KeysetDeriverWrapper.register();
        if (TinkFips.useOnlyFips()) {
            return;
        }
        HkdfPrfKeyManager.register(true);
        PrfBasedDeriverKeyManager.register(true);
    }

    private KeyDerivationConfig() {
    }
}

