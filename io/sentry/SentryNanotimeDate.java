/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DateUtils;
import io.sentry.SentryDate;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryNanotimeDate
extends SentryDate {
    @NotNull
    private final Date date;
    private final long nanos;

    public SentryNanotimeDate() {
        this(DateUtils.getCurrentDateTime(), System.nanoTime());
    }

    public SentryNanotimeDate(@NotNull Date date, long nanos) {
        this.date = date;
        this.nanos = nanos;
    }

    @Override
    public long diff(@NotNull SentryDate otherDate) {
        if (otherDate instanceof SentryNanotimeDate) {
            @NotNull SentryNanotimeDate otherNanoDate = (SentryNanotimeDate)otherDate;
            return this.nanos - otherNanoDate.nanos;
        }
        return super.diff(otherDate);
    }

    @Override
    public long nanoTimestamp() {
        return DateUtils.dateToNanos(this.date);
    }

    @Override
    public long laterDateNanosTimestampByDiff(@Nullable SentryDate otherDate) {
        if (otherDate != null && otherDate instanceof SentryNanotimeDate) {
            @NotNull SentryNanotimeDate otherNanoDate = (SentryNanotimeDate)otherDate;
            if (this.compareTo(otherDate) < 0) {
                return this.nanotimeDiff(this, otherNanoDate);
            }
            return this.nanotimeDiff(otherNanoDate, this);
        }
        return super.laterDateNanosTimestampByDiff(otherDate);
    }

    @Override
    public int compareTo(@NotNull SentryDate otherDate) {
        if (otherDate instanceof SentryNanotimeDate) {
            long otherDateMillis;
            @NotNull SentryNanotimeDate otherNanoDate = (SentryNanotimeDate)otherDate;
            long thisDateMillis = this.date.getTime();
            if (thisDateMillis == (otherDateMillis = otherNanoDate.date.getTime())) {
                return Long.valueOf(this.nanos).compareTo(otherNanoDate.nanos);
            }
            return Long.valueOf(thisDateMillis).compareTo(otherDateMillis);
        }
        return super.compareTo(otherDate);
    }

    private long nanotimeDiff(@NotNull SentryNanotimeDate earlierDate, @NotNull SentryNanotimeDate laterDate) {
        long nanoDiff = laterDate.nanos - earlierDate.nanos;
        return earlierDate.nanoTimestamp() + nanoDiff;
    }
}

