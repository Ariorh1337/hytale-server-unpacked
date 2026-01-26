/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.protocol.profiling;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SentryThreadMetadata
implements JsonUnknown,
JsonSerializable {
    @Nullable
    private String name;
    private int priority;
    @Nullable
    private Map<String, Object> unknown;

    @Nullable
    public String getName() {
        return this.name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        if (this.name != null) {
            writer.name("name").value(logger, this.name);
        }
        writer.name("priority").value(logger, this.priority);
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
        public static final String NAME = "name";
        public static final String PRIORITY = "priority";
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryThreadMetadata> {
        @Override
        @NotNull
        public SentryThreadMetadata deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            SentryThreadMetadata data = new SentryThreadMetadata();
            HashMap<String, Object> unknown = null;
            block8: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "name": {
                        data.name = reader.nextStringOrNull();
                        continue block8;
                    }
                    case "priority": {
                        data.priority = reader.nextInt();
                        continue block8;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            data.setUnknown(unknown);
            reader.endObject();
            return data;
        }
    }
}

