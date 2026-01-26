/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SentryDate
implements Comparable<SentryDate> {
    public abstract long nanoTimestamp();

    public long laterDateNanosTimestampByDiff(@Nullable SentryDate otherDate) {
        if (otherDate != null && this.compareTo(otherDate) < 0) {
            return otherDate.nanoTimestamp();
        }
        return this.nanoTimestamp();
    }

    public long diff(@NotNull SentryDate otherDate) {
        return this.nanoTimestamp() - otherDate.nanoTimestamp();
    }

    public final boolean isBefore(@NotNull SentryDate otherDate) {
        return this.diff(otherDate) < 0L;
    }

    public final boolean isAfter(@NotNull SentryDate otherDate) {
        return this.diff(otherDate) > 0L;
    }

    @Override
    public int compareTo(@NotNull SentryDate otherDate) {
        return Long.valueOf(this.nanoTimestamp()).compareTo(otherDate.nanoTimestamp());
    }
}

