/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature;

import com.google.crypto.tink.signature.SignatureConfig;
import java.security.GeneralSecurityException;

@Deprecated
public final class PublicKeySignConfig {
    @Deprecated
    public static void registerStandardKeyTypes() throws GeneralSecurityException {
        SignatureConfig.register();
    }

    private PublicKeySignConfig() {
    }
}

