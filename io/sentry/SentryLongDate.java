/*
 * Decompiled with CFR 0.152.
 */
package io.sentry;

import io.sentry.SentryDate;

public final class SentryLongDate
extends SentryDate {
    private final long nanos;

    public SentryLongDate(long nanos) {
        this.nanos = nanos;
    }

    @Override
    public long nanoTimestamp() {
        return this.nanos;
    }
}

