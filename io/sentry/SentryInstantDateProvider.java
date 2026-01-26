/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package io.sentry;

import io.sentry.SentryDate;
import io.sentry.SentryDateProvider;
import io.sentry.SentryInstantDate;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SentryInstantDateProvider
implements SentryDateProvider {
    @Override
    public SentryDate now() {
        return new SentryInstantDate();
    }
}

