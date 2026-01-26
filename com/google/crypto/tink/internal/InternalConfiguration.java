/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import java.security.GeneralSecurityException;

public abstract class InternalConfiguration
extends Configuration {
    public abstract <P> P wrap(KeysetHandleInterface var1, MonitoringAnnotations var2, Class<P> var3) throws GeneralSecurityException;

    public static InternalConfiguration createFromPrimitiveRegistry(PrimitiveRegistry registry) {
        return new InternalConfigurationImpl(registry);
    }

    private static class InternalConfigurationImpl
    extends InternalConfiguration {
        private final PrimitiveRegistry registry;

        private InternalConfigurationImpl(PrimitiveRegistry registry) {
            this.registry = registry;
        }

        @Override
        public <P> P wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, Class<P> clazz) throws GeneralSecurityException {
            return this.registry.wrap(keysetHandle, annotations, clazz);
        }
    }
}

