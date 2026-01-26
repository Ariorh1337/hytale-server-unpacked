/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.config;

import io.sentry.config.AbstractPropertiesProvider;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;

final class SimplePropertiesProvider
extends AbstractPropertiesProvider {
    public SimplePropertiesProvider(@NotNull Properties properties) {
        super(properties);
    }
}

