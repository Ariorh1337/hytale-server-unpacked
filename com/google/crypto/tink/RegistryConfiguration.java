/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Configuration;
import java.security.GeneralSecurityException;

public class RegistryConfiguration {
    private RegistryConfiguration() {
    }

    public static Configuration get() throws GeneralSecurityException {
        return com.google.crypto.tink.internal.RegistryConfiguration.get();
    }
}

