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
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebInteractionEvent
extends RRWebIncrementalSnapshotEvent
implements JsonSerializable,
JsonUnknown {
    private static final int POINTER_TYPE_TOUCH = 2;
    @Nullable
    private InteractionType interactionType;
    private int id;
    private float x;
    private float y;
    private int pointerType = 2;
    private int pointerId;
    @Nullable
    private Map<String, Object> unknown;
    @Nullable
    private Map<String, Object> dataUnknown;

    public RRWebInteractionEvent() {
        super(RRWebIncrementalSnapshotEvent.IncrementalSource.MouseInteraction);
    }

    @Nullable
    public InteractionType getInteractionType() {
        return this.interactionType;
    }

    public void setInteractionType(@Nullable InteractionType type) {
        this.interactionType = type;
    }

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

    public int getPointerType() {
        return this.pointerType;
    }

    public void setPointerType(int pointerType) {
        this.pointerType = pointerType;
    }

    public int getPointerId() {
        return this.pointerId;
    }

    public void setPointerId(int pointerId) {
        this.pointerId = pointerId;
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
        writer.name("type").value(logger, this.interactionType);
        writer.name("id").value(this.id);
        writer.name("x").value(this.x);
        writer.name("y").value(this.y);
        writer.name("pointerType").value(this.pointerType);
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

    public static enum InteractionType implements JsonSerializable
    {
        MouseUp,
        MouseDown,
        Click,
        ContextMenu,
        DblClick,
        Focus,
        Blur,
        TouchStart,
        TouchMove_Departed,
        TouchEnd,
        TouchCancel;


        @Override
        public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
            writer.value(this.ordinal());
        }

        public static final class Deserializer
        implements JsonDeserializer<InteractionType> {
            @Override
            @NotNull
            public InteractionType deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
                return InteractionType.values()[reader.nextInt()];
            }
        }
    }

    public static final class JsonKeys {
        public static final String DATA = "data";
        public static final String TYPE = "type";
        public static final String ID = "id";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String POINTER_TYPE = "pointerType";
        public static final String POINTER_ID = "pointerId";
    }

    public static final class Deserializer
    implements JsonDeserializer<RRWebInteractionEvent> {
        @Override
        @NotNull
        public RRWebInteractionEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            @Nullable HashMap<String, Object> unknown = null;
            RRWebInteractionEvent event = new RRWebInteractionEvent();
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

        private void deserializeData(@NotNull RRWebInteractionEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            HashMap<String, Object> dataUnknown = null;
            RRWebIncrementalSnapshotEvent.Deserializer baseEventDeserializer = new RRWebIncrementalSnapshotEvent.Deserializer();
            reader.beginObject();
            block16: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "type": {
                        event.interactionType = reader.nextOrNull(logger, new InteractionType.Deserializer());
                        continue block16;
                    }
                    case "id": {
                        event.id = reader.nextInt();
                        continue block16;
                    }
                    case "x": {
                        event.x = reader.nextFloat();
                        continue block16;
                    }
                    case "y": {
                        event.y = reader.nextFloat();
                        continue block16;
                    }
                    case "pointerType": {
                        event.pointerType = reader.nextInt();
                        continue block16;
                    }
                    case "pointerId": {
                        event.pointerId = reader.nextInt();
                        continue block16;
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
}

