/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryUUID;
import io.sentry.util.LazyEvaluator;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class SpanId
implements JsonSerializable {
    public static final SpanId EMPTY_ID = new SpanId("00000000-0000-0000-0000-000000000000".replace("-", "").substring(0, 16));
    @NotNull
    private final LazyEvaluator<String> lazyValue;

    public SpanId(@NotNull String value) {
        Objects.requireNonNull(value, "value is required");
        this.lazyValue = new LazyEvaluator<String>(() -> value);
    }

    public SpanId() {
        this.lazyValue = new LazyEvaluator<String>(SentryUUID::generateSpanId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpanId spanId = (SpanId)o;
        return this.lazyValue.getValue().equals(spanId.lazyValue.getValue());
    }

    public int hashCode() {
        return this.lazyValue.getValue().hashCode();
    }

    public String toString() {
        return this.lazyValue.getValue();
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.value(this.lazyValue.getValue());
    }

    public static final class Deserializer
    implements JsonDeserializer<SpanId> {
        @Override
        @NotNull
        public SpanId deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return new SpanId(reader.nextString());
        }
    }
}

