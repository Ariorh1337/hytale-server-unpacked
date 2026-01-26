/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Message
implements JsonUnknown,
JsonSerializable {
    @Nullable
    private String formatted;
    @Nullable
    private String message;
    @Nullable
    private List<String> params;
    @Nullable
    private Map<String, Object> unknown;

    @Nullable
    public String getFormatted() {
        return this.formatted;
    }

    public void setFormatted(@Nullable String formatted) {
        this.formatted = formatted;
    }

    @Nullable
    public String getMessage() {
        return this.message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Nullable
    public List<String> getParams() {
        return this.params;
    }

    public void setParams(@Nullable List<String> params) {
        this.params = CollectionUtils.newArrayList(params);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        if (this.formatted != null) {
            writer.name("formatted").value(this.formatted);
        }
        if (this.message != null) {
            writer.name("message").value(this.message);
        }
        if (this.params != null && !this.params.isEmpty()) {
            writer.name("params").value(logger, this.params);
        }
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key);
                writer.value(logger, value);
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
        public static final String FORMATTED = "formatted";
        public static final String MESSAGE = "message";
        public static final String PARAMS = "params";
    }

    public static final class Deserializer
    implements JsonDeserializer<Message> {
        @Override
        @NotNull
        public Message deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            Message message = new Message();
            ConcurrentHashMap<String, Object> unknown = null;
            block10: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "formatted": {
                        message.formatted = reader.nextStringOrNull();
                        continue block10;
                    }
                    case "message": {
                        message.message = reader.nextStringOrNull();
                        continue block10;
                    }
                    case "params": {
                        List deserializedParams = (List)reader.nextObjectOrNull();
                        if (deserializedParams == null) continue block10;
                        message.params = deserializedParams;
                        continue block10;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            message.setUnknown(unknown);
            reader.endObject();
            return message;
        }
    }
}

