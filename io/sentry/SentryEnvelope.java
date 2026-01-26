/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ISerializer;
import io.sentry.ProfilingTraceData;
import io.sentry.SentryBaseEvent;
import io.sentry.SentryEnvelopeHeader;
import io.sentry.SentryEnvelopeItem;
import io.sentry.Session;
import io.sentry.exception.SentryEnvelopeException;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.util.Objects;
import java.io.IOException;
import java.util.ArrayList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryEnvelope {
    @NotNull
    private final SentryEnvelopeHeader header;
    @NotNull
    private final Iterable<SentryEnvelopeItem> items;

    @ApiStatus.Internal
    @NotNull
    public Iterable<SentryEnvelopeItem> getItems() {
        return this.items;
    }

    @ApiStatus.Internal
    @NotNull
    public SentryEnvelopeHeader getHeader() {
        return this.header;
    }

    @ApiStatus.Internal
    public SentryEnvelope(@NotNull SentryEnvelopeHeader header, @NotNull Iterable<SentryEnvelopeItem> items) {
        this.header = Objects.requireNonNull(header, "SentryEnvelopeHeader is required.");
        this.items = Objects.requireNonNull(items, "SentryEnvelope items are required.");
    }

    @ApiStatus.Internal
    public SentryEnvelope(@Nullable SentryId eventId, @Nullable SdkVersion sdkVersion, @NotNull Iterable<SentryEnvelopeItem> items) {
        this.header = new SentryEnvelopeHeader(eventId, sdkVersion);
        this.items = Objects.requireNonNull(items, "SentryEnvelope items are required.");
    }

    @ApiStatus.Internal
    public SentryEnvelope(@Nullable SentryId eventId, @Nullable SdkVersion sdkVersion, @NotNull SentryEnvelopeItem item) {
        Objects.requireNonNull(item, "SentryEnvelopeItem is required.");
        this.header = new SentryEnvelopeHeader(eventId, sdkVersion);
        ArrayList<SentryEnvelopeItem> items = new ArrayList<SentryEnvelopeItem>(1);
        items.add(item);
        this.items = items;
    }

    @ApiStatus.Internal
    @NotNull
    public static SentryEnvelope from(@NotNull ISerializer serializer, @NotNull Session session, @Nullable SdkVersion sdkVersion) throws IOException {
        Objects.requireNonNull(serializer, "Serializer is required.");
        Objects.requireNonNull(session, "session is required.");
        return new SentryEnvelope(null, sdkVersion, SentryEnvelopeItem.fromSession(serializer, session));
    }

    @ApiStatus.Internal
    @NotNull
    public static SentryEnvelope from(@NotNull ISerializer serializer, @NotNull SentryBaseEvent event, @Nullable SdkVersion sdkVersion) throws IOException {
        Objects.requireNonNull(serializer, "Serializer is required.");
        Objects.requireNonNull(event, "item is required.");
        return new SentryEnvelope(event.getEventId(), sdkVersion, SentryEnvelopeItem.fromEvent(serializer, event));
    }

    @ApiStatus.Internal
    @NotNull
    public static SentryEnvelope from(@NotNull ISerializer serializer, @NotNull ProfilingTraceData profilingTraceData, long maxTraceFileSize, @Nullable SdkVersion sdkVersion) throws SentryEnvelopeException {
        Objects.requireNonNull(serializer, "Serializer is required.");
        Objects.requireNonNull(profilingTraceData, "Profiling trace data is required.");
        return new SentryEnvelope(new SentryId(profilingTraceData.getProfileId()), sdkVersion, SentryEnvelopeItem.fromProfilingTrace(profilingTraceData, maxTraceFileSize, serializer));
    }
}

