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
import io.sentry.rrweb.RRWebBreadcrumbEvent;
import io.sentry.rrweb.RRWebEvent;
import io.sentry.rrweb.RRWebEventType;
import io.sentry.rrweb.RRWebIncrementalSnapshotEvent;
import io.sentry.rrweb.RRWebInteractionEvent;
import io.sentry.rrweb.RRWebInteractionMoveEvent;
import io.sentry.rrweb.RRWebMetaEvent;
import io.sentry.rrweb.RRWebSpanEvent;
import io.sentry.rrweb.RRWebVideoEvent;
import io.sentry.util.MapObjectReader;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReplayRecording
implements JsonUnknown,
JsonSerializable {
    @Nullable
    private Integer segmentId;
    @Nullable
    private List<? extends RRWebEvent> payload;
    @Nullable
    private Map<String, Object> unknown;

    @Nullable
    public Integer getSegmentId() {
        return this.segmentId;
    }

    public void setSegmentId(@Nullable Integer segmentId) {
        this.segmentId = segmentId;
    }

    @Nullable
    public List<? extends RRWebEvent> getPayload() {
        return this.payload;
    }

    public void setPayload(@Nullable List<? extends RRWebEvent> payload) {
        this.payload = payload;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ReplayRecording that = (ReplayRecording)o;
        return Objects.equals(this.segmentId, that.segmentId) && Objects.equals(this.payload, that.payload);
    }

    public int hashCode() {
        return Objects.hash(this.segmentId, this.payload);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        if (this.segmentId != null) {
            writer.name("segment_id").value(this.segmentId);
        }
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
            }
        }
        writer.endObject();
        writer.setLenient(true);
        if (this.segmentId != null) {
            writer.jsonValue("\n");
        }
        if (this.payload != null) {
            writer.value(logger, this.payload);
        }
        writer.setLenient(false);
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
        public static final String SEGMENT_ID = "segment_id";
    }

    public static final class Deserializer
    implements JsonDeserializer<ReplayRecording> {
        @Override
        @NotNull
        public ReplayRecording deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            ReplayRecording replay = new ReplayRecording();
            HashMap<String, Object> unknown = null;
            Integer segmentId = null;
            ArrayList<RRWebEvent> payload = null;
            reader.beginObject();
            block25: while (reader.peek() == JsonToken.NAME) {
                String nextName = reader.nextName();
                switch (nextName) {
                    case "segment_id": {
                        segmentId = reader.nextIntegerOrNull();
                        continue block25;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            reader.endObject();
            reader.setLenient(true);
            List events = (List)reader.nextObjectOrNull();
            reader.setLenient(false);
            if (events != null) {
                payload = new ArrayList<RRWebEvent>(events.size());
                for (Object event : events) {
                    if (!(event instanceof Map)) continue;
                    Map eventMap = (Map)event;
                    MapObjectReader mapReader = new MapObjectReader(eventMap);
                    block27: for (Map.Entry entry : eventMap.entrySet()) {
                        String key = (String)entry.getKey();
                        Object value = entry.getValue();
                        if (!key.equals("type")) continue;
                        RRWebEventType type = RRWebEventType.values()[(Integer)value];
                        block6 : switch (type) {
                            case IncrementalSnapshot: {
                                Integer sourceInt;
                                @Nullable Map<K, V> incrementalData = (Map)eventMap.get("data");
                                if (incrementalData == null) {
                                    incrementalData = Collections.emptyMap();
                                }
                                if ((sourceInt = (Integer)incrementalData.get("source")) == null) continue block27;
                                RRWebIncrementalSnapshotEvent.IncrementalSource source = RRWebIncrementalSnapshotEvent.IncrementalSource.values()[sourceInt];
                                switch (source) {
                                    case MouseInteraction: {
                                        RRWebInteractionEvent interactionEvent = new RRWebInteractionEvent.Deserializer().deserialize(mapReader, logger);
                                        payload.add(interactionEvent);
                                        break block6;
                                    }
                                    case TouchMove: {
                                        RRWebInteractionMoveEvent interactionMoveEvent = new RRWebInteractionMoveEvent.Deserializer().deserialize(mapReader, logger);
                                        payload.add(interactionMoveEvent);
                                        break block6;
                                    }
                                }
                                logger.log(SentryLevel.DEBUG, "Unsupported rrweb incremental snapshot type %s", source);
                                break;
                            }
                            case Meta: {
                                RRWebMetaEvent metaEvent = new RRWebMetaEvent.Deserializer().deserialize(mapReader, logger);
                                payload.add(metaEvent);
                                break;
                            }
                            case Custom: {
                                String tag;
                                @Nullable Map<K, V> customData = (Map)eventMap.get("data");
                                if (customData == null) {
                                    customData = Collections.emptyMap();
                                }
                                if ((tag = (String)customData.get("tag")) == null) continue block27;
                                switch (tag) {
                                    case "video": {
                                        RRWebVideoEvent videoEvent = new RRWebVideoEvent.Deserializer().deserialize(mapReader, logger);
                                        payload.add(videoEvent);
                                        break block6;
                                    }
                                    case "breadcrumb": {
                                        RRWebBreadcrumbEvent breadcrumbEvent = new RRWebBreadcrumbEvent.Deserializer().deserialize(mapReader, logger);
                                        payload.add(breadcrumbEvent);
                                        break block6;
                                    }
                                    case "performanceSpan": {
                                        RRWebSpanEvent spanEvent = new RRWebSpanEvent.Deserializer().deserialize(mapReader, logger);
                                        payload.add(spanEvent);
                                        break block6;
                                    }
                                }
                                logger.log(SentryLevel.DEBUG, "Unsupported rrweb event type %s", type);
                                break;
                            }
                            default: {
                                logger.log(SentryLevel.DEBUG, "Unsupported rrweb event type %s", type);
                            }
                        }
                    }
                }
            }
            replay.setSegmentId(segmentId);
            replay.setPayload(payload);
            replay.setUnknown(unknown);
            return replay;
        }
    }
}

