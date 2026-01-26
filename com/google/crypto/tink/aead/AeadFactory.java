/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.RegistryConfiguration;
import com.google.crypto.tink.aead.AeadWrapper;
import java.security.GeneralSecurityException;

@Deprecated
public final class AeadFactory {
    @Deprecated
    public static Aead getPrimitive(KeysetHandle keysetHandle) throws GeneralSecurityException {
        AeadWrapper.register();
        return keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead.class);
    }

    private AeadFactory() {
    }
}

