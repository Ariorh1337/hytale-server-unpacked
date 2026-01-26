/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IVersionDetector;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryOptions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class DefaultVersionDetector
implements IVersionDetector {
    @NotNull
    private final SentryOptions options;

    public DefaultVersionDetector(@NotNull SentryOptions options) {
        this.options = options;
    }

    @Override
    public boolean checkForMixedVersions() {
        return SentryIntegrationPackageStorage.getInstance().checkForMixedVersions(this.options.getFatalLogger());
    }
}

