/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryEvent;
import io.sentry.Session;
import io.sentry.clientreport.ClientReport;
import io.sentry.protocol.SentryTransaction;
import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public enum SentryItemType implements JsonSerializable
{
    Session("session"),
    Event("event"),
    UserFeedback("user_report"),
    Attachment("attachment"),
    Transaction("transaction"),
    Profile("profile"),
    ProfileChunk("profile_chunk"),
    ClientReport("client_report"),
    ReplayEvent("replay_event"),
    ReplayRecording("replay_recording"),
    ReplayVideo("replay_video"),
    CheckIn("check_in"),
    Feedback("feedback"),
    Log("log"),
    TraceMetric("trace_metric"),
    Span("span"),
    Unknown("__unknown__");

    private final String itemType;

    public static SentryItemType resolve(Object item) {
        if (item instanceof SentryEvent) {
            return ((SentryEvent)item).getContexts().getFeedback() == null ? Event : Feedback;
        }
        if (item instanceof SentryTransaction) {
            return Transaction;
        }
        if (item instanceof Session) {
            return Session;
        }
        if (item instanceof ClientReport) {
            return ClientReport;
        }
        return Attachment;
    }

    private SentryItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemType() {
        return this.itemType;
    }

    @NotNull
    public static SentryItemType valueOfLabel(String itemType) {
        for (SentryItemType sentryItemType : SentryItemType.values()) {
            if (!sentryItemType.itemType.equals(itemType)) continue;
            return sentryItemType;
        }
        return Unknown;
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.value(this.itemType);
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryItemType> {
        @Override
        @NotNull
        public SentryItemType deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return SentryItemType.valueOfLabel(reader.nextString().toLowerCase(Locale.ROOT));
        }
    }
}

