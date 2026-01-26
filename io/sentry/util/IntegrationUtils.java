/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.util;

import io.sentry.SentryIntegrationPackageStorage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class IntegrationUtils {
    public static void addIntegrationToSdkVersion(@NotNull String name) {
        SentryIntegrationPackageStorage.getInstance().addIntegration(name);
    }
}

