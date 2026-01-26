/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.SpanId;
import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class W3CTraceparentHeader {
    public static final String TRACEPARENT_HEADER = "traceparent";
    @NotNull
    private final SentryId traceId;
    @NotNull
    private final SpanId spanId;
    @Nullable
    private final Boolean sampled;

    public W3CTraceparentHeader(@NotNull SentryId traceId, @NotNull SpanId spanId, @Nullable Boolean sampled) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.sampled = sampled;
    }

    @NotNull
    public String getName() {
        return TRACEPARENT_HEADER;
    }

    @NotNull
    public String getValue() {
        String sampledFlag = this.sampled != null && this.sampled != false ? "01" : "00";
        return String.format("00-%s-%s-%s", this.traceId, this.spanId, sampledFlag);
    }
}

