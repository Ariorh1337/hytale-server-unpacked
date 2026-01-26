/*
 * Decompiled with CFR 0.152.
 */
package io.sentry.config;

import io.sentry.config.AbstractPropertiesProvider;

final class SystemPropertyPropertiesProvider
extends AbstractPropertiesProvider {
    private static final String PREFIX = "sentry.";

    public SystemPropertyPropertiesProvider() {
        super(PREFIX, System.getProperties());
    }
}

