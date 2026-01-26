/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryLogEvents
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private List<SentryLogEvent> items;
    @Nullable
    private Map<String, Object> unknown;

    public SentryLogEvents(@NotNull List<SentryLogEvent> items) {
        this.items = items;
    }

    @NotNull
    public List<SentryLogEvent> getItems() {
        return this.items;
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("items").value(logger, this.items);
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
            }
        }
        writer.endObject();
    }

    @Override
    @Nullable
    public Map<String, Object> getUnknown() {
        return this.unknown;
    }

    @Override
    public void setUnknown(@Nullable Map<String, Object> unknown) {
        this.unknown = unknown;
    }

    public static final class JsonKeys {
        public static final String ITEMS = "items";
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryLogEvents> {
        @Override
        @NotNull
        public SentryLogEvents deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            @Nullable HashMap<String, Object> unknown = null;
            List<SentryLogEvent> items = null;
            reader.beginObject();
            block6: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "items": {
                        items = reader.nextListOrNull(logger, new SentryLogEvent.Deserializer());
                        continue block6;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            reader.endObject();
            if (items == null) {
                String message = "Missing required field \"items\"";
                IllegalStateException exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            SentryLogEvents logEvent = new SentryLogEvents(items);
            logEvent.setUnknown(unknown);
            return logEvent;
        }
    }
}

