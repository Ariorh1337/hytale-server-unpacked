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
import io.sentry.rrweb.RRWebEventType;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebMetaEvent
extends RRWebEvent
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private String href = "";
    private int height;
    private int width;
    @Nullable
    private Map<String, Object> unknown;
    @Nullable
    private Map<String, Object> dataUnknown;

    public RRWebMetaEvent() {
        super(RRWebEventType.Meta);
    }

    @NotNull
    public String getHref() {
        return this.href;
    }

    public void setHref(@NotNull String href) {
        this.href = href;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Nullable
    public Map<String, Object> getDataUnknown() {
        return this.dataUnknown;
    }

    public void setDataUnknown(@Nullable Map<String, Object> dataUnknown) {
        this.dataUnknown = dataUnknown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RRWebMetaEvent metaEvent = (RRWebMetaEvent)o;
        return this.height == metaEvent.height && this.width == metaEvent.width && Objects.equals(this.href, metaEvent.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.href, this.height, this.width);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        new RRWebEvent.Serializer().serialize(this, writer, logger);
        writer.name("data");
        this.serializeData(writer, logger);
        writer.endObject();
    }

    private void serializeData(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("href").value(this.href);
        writer.name("height").value(this.height);
        writer.name("width").value(this.width);
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
        public static final String DATA = "data";
        public static final String HREF = "href";
        public static final String HEIGHT = "height";
        public static final String WIDTH = "width";
    }

    public static final class Deserializer
    implements JsonDeserializer<RRWebMetaEvent> {
        @Override
        @NotNull
        public RRWebMetaEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            @Nullable HashMap<String, Object> unknown = null;
            RRWebMetaEvent event = new RRWebMetaEvent();
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

        private void deserializeData(@NotNull RRWebMetaEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            ConcurrentHashMap<String, Object> unknown = null;
            reader.beginObject();
            block10: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "href": {
                        String href = reader.nextStringOrNull();
                        event.href = href == null ? "" : href;
                        continue block10;
                    }
                    case "height": {
                        Integer height = reader.nextIntegerOrNull();
                        event.height = height == null ? 0 : height;
                        continue block10;
                    }
                    case "width": {
                        Integer width = reader.nextIntegerOrNull();
                        event.width = width == null ? 0 : width;
                        continue block10;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            event.setDataUnknown(unknown);
            reader.endObject();
        }
    }
}

