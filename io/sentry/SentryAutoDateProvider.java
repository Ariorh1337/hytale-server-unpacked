/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.SentryDate;
import io.sentry.SentryDateProvider;
import io.sentry.SentryInstantDateProvider;
import io.sentry.SentryNanotimeDateProvider;
import io.sentry.util.Platform;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class SentryAutoDateProvider
implements SentryDateProvider {
    @NotNull
    private final SentryDateProvider dateProvider = SentryAutoDateProvider.checkInstantAvailabilityAndPrecision() ? new SentryInstantDateProvider() : new SentryNanotimeDateProvider();

    @Override
    @NotNull
    public SentryDate now() {
        return this.dateProvider.now();
    }

    private static boolean checkInstantAvailabilityAndPrecision() {
        return Platform.isJvm() && Platform.isJavaNinePlus();
    }
}

