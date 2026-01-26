/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.rrweb.RRWebEventType;
import io.sentry.util.Objects;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public abstract class RRWebEvent {
    @NotNull
    private RRWebEventType type;
    private long timestamp;

    protected RRWebEvent(@NotNull RRWebEventType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    protected RRWebEvent() {
        this(RRWebEventType.Custom);
    }

    @NotNull
    public RRWebEventType getType() {
        return this.type;
    }

    public void setType(@NotNull RRWebEventType type) {
        this.type = type;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RRWebEvent)) {
            return false;
        }
        RRWebEvent that = (RRWebEvent)o;
        return this.timestamp == that.timestamp && this.type == that.type;
    }

    public int hashCode() {
        return Objects.hash(this.type, this.timestamp);
    }

    public static final class Deserializer {
        public boolean deserializeValue(@NotNull RRWebEvent baseEvent, @NotNull String nextName, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            switch (nextName) {
                case "type": {
                    baseEvent.type = Objects.requireNonNull(reader.nextOrNull(logger, new RRWebEventType.Deserializer()), "");
                    return true;
                }
                case "timestamp": {
                    baseEvent.timestamp = reader.nextLong();
                    return true;
                }
            }
            return false;
        }
    }

    public static final class Serializer {
        public void serialize(@NotNull RRWebEvent baseEvent, @NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
            writer.name("type").value(logger, baseEvent.type);
            writer.name("timestamp").value(baseEvent.timestamp);
        }
    }

    public static final class JsonKeys {
        public static final String TYPE = "type";
        public static final String TIMESTAMP = "timestamp";
        public static final String TAG = "tag";
    }
}

