/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.rrweb.RRWebEvent;
import io.sentry.rrweb.RRWebIncrementalSnapshotEvent;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebInteractionMoveEvent
extends RRWebIncrementalSnapshotEvent
implements JsonSerializable,
JsonUnknown {
    private int pointerId;
    @Nullable
    private List<Position> positions;
    @Nullable
    private Map<String, Object> unknown;
    @Nullable
    private Map<String, Object> dataUnknown;

    public RRWebInteractionMoveEvent() {
        super(RRWebIncrementalSnapshotEvent.IncrementalSource.TouchMove);
    }

    @Nullable
    public Map<String, Object> getDataUnknown() {
        return this.dataUnknown;
    }

    public void setDataUnknown(@Nullable Map<String, Object> dataUnknown) {
        this.dataUnknown = dataUnknown;
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

    @Nullable
    public List<Position> getPositions() {
        return this.positions;
    }

    public void setPositions(@Nullable List<Position> positions) {
        this.positions = positions;
    }

    public int getPointerId() {
        return this.pointerId;
    }

    public void setPointerId(int pointerId) {
        this.pointerId = pointerId;
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        new RRWebEvent.Serializer().serialize(this, writer, logger);
        writer.name("data");
        this.serializeData(writer, logger);
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key);
                writer.value(logger, value);
            }
        }
        writer.endObject();
    }

    private void serializeData(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        new RRWebIncrementalSnapshotEvent.Serializer().serialize(this, writer, logger);
        if (this.positions != null && !this.positions.isEmpty()) {
            writer.name("positions").value(logger, this.positions);
        }
        writer.name("pointerId").value(this.pointerId);
        if (this.dataUnknown != null) {
            for (String key : this.dataUnknown.keySet()) {
                Object value = this.dataUnknown.get(key);
                writer.name(key);
                writer.value(logger, value);
            }
        }
        writer.endObject();
    }

    public static final class JsonKeys {
        public static final String DATA = "data";
        public static final String POSITIONS = "positions";
        public static final String POINTER_ID = "pointerId";
    }

    public static final class Deserializer
    implements JsonDeserializer<RRWebInteractionMoveEvent> {
        @Override
        @NotNull
        public RRWebInteractionMoveEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            @Nullable HashMap<String, Object> unknown = null;
            RRWebInteractionMoveEvent event = new RRWebInteractionMoveEvent();
            RRWebEvent.Deserializer baseEventDeserializer = new RRWebEvent.Deserializer();
            block6: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "data": {
                        this.deserializeData(event, reader, logger);
                        continue block6;
                    }
                }
                if (baseEventDeserializer.deserializeValue(event, nextName, reader, logger)) continue;
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            event.setUnknown(unknown);
            reader.endObject();
            return event;
        }

        private void deserializeData(@NotNull RRWebInteractionMoveEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            HashMap<String, Object> dataUnknown = null;
            RRWebIncrementalSnapshotEvent.Deserializer baseEventDeserializer = new RRWebIncrementalSnapshotEvent.Deserializer();
            reader.beginObject();
            block8: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "positions": {
                        event.positions = reader.nextListOrNull(logger, new Position.Deserializer());
                        continue block8;
                    }
                    case "pointerId": {
                        event.pointerId = reader.nextInt();
                        continue block8;
                    }
                }
                if (baseEventDeserializer.deserializeValue(event, nextName, reader, logger)) continue;
                if (dataUnknown == null) {
                    dataUnknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, dataUnknown, nextName);
            }
            event.setDataUnknown(dataUnknown);
            reader.endObject();
        }
    }

    public static final class Position
    implements JsonSerializable,
    JsonUnknown {
        private int id;
        private float x;
        private float y;
        private long timeOffset;
        @Nullable
        private Map<String, Object> unknown;

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public float getX() {
            return this.x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return this.y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public long getTimeOffset() {
            return this.timeOffset;
        }

        public void setTimeOffset(long timeOffset) {
            this.timeOffset = timeOffset;
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

        @Override
        public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
            writer.beginObject();
            writer.name("id").value(this.id);
            writer.name("x").value(this.x);
            writer.name("y").value(this.y);
            writer.name("timeOffset").value(this.timeOffset);
            if (this.unknown != null) {
                for (String key : this.unknown.keySet()) {
                    Object value = this.unknown.get(key);
                    writer.name(key);
                    writer.value(logger, value);
                }
            }
            writer.endObject();
        }

        public static final class JsonKeys {
            public static final String ID = "id";
            public static final String X = "x";
            public static final String Y = "y";
            public static final String TIME_OFFSET = "timeOffset";
        }

        public static final class Deserializer
        implements JsonDeserializer<Position> {
            @Override
            @NotNull
            public Position deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
                reader.beginObject();
                @Nullable HashMap<String, Object> unknown = null;
                Position position = new Position();
                block12: while (reader.peek() == JsonToken.NAME) {
                    String nextName;
                    switch (nextName = reader.nextName()) {
                        case "id": {
                            position.id = reader.nextInt();
                            continue block12;
                        }
                        case "x": {
                            position.x = reader.nextFloat();
                            continue block12;
                        }
                        case "y": {
                            position.y = reader.nextFloat();
                            continue block12;
                        }
                        case "timeOffset": {
                            position.timeOffset = reader.nextLong();
                            continue block12;
                        }
                    }
                    if (unknown == null) {
                        unknown = new HashMap<String, Object>();
                    }
                    reader.nextUnknown(logger, unknown, nextName);
                }
                position.setUnknown(unknown);
                reader.endObject();
                return position;
            }
        }
    }
}

