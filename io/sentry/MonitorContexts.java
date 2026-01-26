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
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SpanContext;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MonitorContexts
extends ConcurrentHashMap<String, Object>
implements JsonSerializable {
    private static final long serialVersionUID = 3987329379811822556L;

    public MonitorContexts() {
    }

    public MonitorContexts(@NotNull MonitorContexts contexts) {
        for (Map.Entry entry : contexts.entrySet()) {
            if (entry == null) continue;
            Object value = entry.getValue();
            if ("trace".equals(entry.getKey()) && value instanceof SpanContext) {
                this.setTrace(new SpanContext((SpanContext)value));
                continue;
            }
            this.put((String)entry.getKey(), value);
        }
    }

    @Nullable
    private <T> T toContextType(@NotNull String key, @NotNull Class<T> clazz) {
        Object item = this.get(key);
        return clazz.isInstance(item) ? (T)clazz.cast(item) : null;
    }

    @Nullable
    public SpanContext getTrace() {
        return this.toContextType("trace", SpanContext.class);
    }

    public void setTrace(@NotNull SpanContext traceContext) {
        Objects.requireNonNull(traceContext, "traceContext is required");
        this.put("trace", traceContext);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        ArrayList<String> sortedKeys = Collections.list(this.keys());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            Object value = this.get(key);
            if (value == null) continue;
            writer.name(key).value(logger, value);
        }
        writer.endObject();
    }

    public static final class Deserializer
    implements JsonDeserializer<MonitorContexts> {
        @Override
        @NotNull
        public MonitorContexts deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            MonitorContexts contexts = new MonitorContexts();
            reader.beginObject();
            block6: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "trace": {
                        contexts.setTrace(new SpanContext.Deserializer().deserialize(reader, logger));
                        continue block6;
                    }
                }
                Object object = reader.nextObjectOrNull();
                if (object == null) continue;
                contexts.put(nextName, object);
            }
            reader.endObject();
            return contexts;
        }
    }
}

