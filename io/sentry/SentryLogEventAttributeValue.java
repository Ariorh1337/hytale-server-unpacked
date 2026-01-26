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
import io.sentry.SentryAttributeType;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryLogEventAttributeValue
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private String type;
    @Nullable
    private Object value;
    @Nullable
    private Map<String, Object> unknown;

    public SentryLogEventAttributeValue(@NotNull String type, @Nullable Object value) {
        this.type = type;
        this.value = value != null && type.equals("string") ? value.toString() : value;
    }

    public SentryLogEventAttributeValue(@NotNull SentryAttributeType type, @Nullable Object value) {
        this(type.apiName(), value);
    }

    @NotNull
    public String getType() {
        return this.type;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("type").value(logger, this.type);
        writer.name("value").value(logger, this.value);
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
        public static final String TYPE = "type";
        public static final String VALUE = "value";
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryLogEventAttributeValue> {
        @Override
        @NotNull
        public SentryLogEventAttributeValue deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            @Nullable HashMap<String, Object> unknown = null;
            String type = null;
            Object value = null;
            reader.beginObject();
            block8: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "type": {
                        type = reader.nextStringOrNull();
                        continue block8;
                    }
                    case "value": {
                        value = reader.nextObjectOrNull();
                        continue block8;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            reader.endObject();
            if (type == null) {
                String message = "Missing required field \"type\"";
                IllegalStateException exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            SentryLogEventAttributeValue logEvent = new SentryLogEventAttributeValue(type, value);
            logEvent.setUnknown(unknown);
            return logEvent;
        }
    }
}

