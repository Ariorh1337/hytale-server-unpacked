/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.logger;

import io.sentry.SentryAttributes;
import io.sentry.SentryDate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryLogParameters {
    @Nullable
    private SentryDate timestamp;
    @Nullable
    private SentryAttributes attributes;
    @NotNull
    private String origin = "manual";

    @Nullable
    public SentryDate getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(@Nullable SentryDate timestamp) {
        this.timestamp = timestamp;
    }

    @Nullable
    public SentryAttributes getAttributes() {
        return this.attributes;
    }

    public void setAttributes(@Nullable SentryAttributes attributes) {
        this.attributes = attributes;
    }

    @NotNull
    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(@NotNull String origin) {
        this.origin = origin;
    }

    @NotNull
    public static SentryLogParameters create(@Nullable SentryDate timestamp, @Nullable SentryAttributes attributes) {
        @NotNull SentryLogParameters params = new SentryLogParameters();
        params.setTimestamp(timestamp);
        params.setAttributes(attributes);
        return params;
    }

    @NotNull
    public static SentryLogParameters create(@Nullable SentryAttributes attributes) {
        return SentryLogParameters.create(null, attributes);
    }
}

