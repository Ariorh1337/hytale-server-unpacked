/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.DateUtils;
import io.sentry.SentryDate;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

public final class SentryInstantDate
extends SentryDate {
    @NotNull
    private final Instant date;

    public SentryInstantDate() {
        this(Instant.now());
    }

    public SentryInstantDate(@NotNull Instant date) {
        this.date = date;
    }

    @Override
    public long nanoTimestamp() {
        return DateUtils.secondsToNanos(this.date.getEpochSecond()) + (long)this.date.getNano();
    }
}

