/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.protocol;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.Span;
import io.sentry.SpanId;
import io.sentry.SpanStatus;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.protocol.FeatureFlag;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.MeasurementValue;
import io.sentry.protocol.SentryId;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SentrySpan
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private final Double startTimestamp;
    @Nullable
    private final Double timestamp;
    @NotNull
    private final SentryId traceId;
    @NotNull
    private final SpanId spanId;
    @Nullable
    private final SpanId parentSpanId;
    @NotNull
    private final String op;
    @Nullable
    private final String description;
    @Nullable
    private final SpanStatus status;
    @Nullable
    private final String origin;
    @NotNull
    private final Map<String, String> tags;
    @Nullable
    private Map<String, Object> data;
    @NotNull
    private final @NotNull Map<String, @NotNull MeasurementValue> measurements;
    @Nullable
    private Map<String, Object> unknown;

    public SentrySpan(@NotNull Span span) {
        this(span, span.getData());
    }

    @ApiStatus.Internal
    public SentrySpan(@NotNull Span span, @Nullable Map<String, Object> data) {
        Objects.requireNonNull(span, "span is required");
        this.description = span.getDescription();
        this.op = span.getOperation();
        this.spanId = span.getSpanId();
        this.parentSpanId = span.getParentSpanId();
        this.traceId = span.getTraceId();
        this.status = span.getStatus();
        this.origin = span.getSpanContext().getOrigin();
        ConcurrentHashMap tagsCopy = CollectionUtils.newConcurrentHashMap(span.getTags());
        this.tags = tagsCopy != null ? tagsCopy : new ConcurrentHashMap();
        ConcurrentHashMap measurementsCopy = CollectionUtils.newConcurrentHashMap(span.getMeasurements());
        this.measurements = measurementsCopy != null ? measurementsCopy : new ConcurrentHashMap();
        this.timestamp = span.getFinishDate() == null ? null : Double.valueOf(DateUtils.nanosToSeconds(span.getStartDate().laterDateNanosTimestampByDiff(span.getFinishDate())));
        this.startTimestamp = DateUtils.nanosToSeconds(span.getStartDate().nanoTimestamp());
        this.data = data;
        @NotNull IFeatureFlagBuffer featureFlagBuffer = span.getSpanContext().getFeatureFlagBuffer();
        @Nullable FeatureFlags featureFlags = featureFlagBuffer.getFeatureFlags();
        if (featureFlags != null) {
            if (this.data == null) {
                this.data = new HashMap<String, Object>();
            }
            for (FeatureFlag featureFlag : featureFlags.getValues()) {
                this.data.put("flag.evaluation." + featureFlag.getFlag(), featureFlag.getResult());
            }
        }
    }

    @ApiStatus.Internal
    public SentrySpan(@NotNull Double startTimestamp, @Nullable Double timestamp, @NotNull SentryId traceId, @NotNull SpanId spanId, @Nullable SpanId parentSpanId, @NotNull String op, @Nullable String description, @Nullable SpanStatus status, @Nullable String origin, @NotNull Map<String, String> tags, @NotNull Map<String, MeasurementValue> measurements, @Nullable Map<String, Object> data) {
        this.startTimestamp = startTimestamp;
        this.timestamp = timestamp;
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.op = op;
        this.description = description;
        this.status = status;
        this.origin = origin;
        this.tags = tags;
        this.measurements = measurements;
        this.data = data;
    }

    public boolean isFinished() {
        return this.timestamp != null;
    }

    @NotNull
    public Double getStartTimestamp() {
        return this.startTimestamp;
    }

    @Nullable
    public Double getTimestamp() {
        return this.timestamp;
    }

    @NotNull
    public SentryId getTraceId() {
        return this.traceId;
    }

    @NotNull
    public SpanId getSpanId() {
        return this.spanId;
    }

    @Nullable
    public SpanId getParentSpanId() {
        return this.parentSpanId;
    }

    @NotNull
    public String getOp() {
        return this.op;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    @Nullable
    public SpanStatus getStatus() {
        return this.status;
    }

    @NotNull
    public Map<String, String> getTags() {
        return this.tags;
    }

    @Nullable
    public Map<String, Object> getData() {
        return this.data;
    }

    public void setData(@Nullable Map<String, Object> data) {
        this.data = data;
    }

    @Nullable
    public String getOrigin() {
        return this.origin;
    }

    @NotNull
    public Map<String, MeasurementValue> getMeasurements() {
        return this.measurements;
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("start_timestamp").value(logger, this.doubleToBigDecimal(this.startTimestamp));
        if (this.timestamp != null) {
            writer.name("timestamp").value(logger, this.doubleToBigDecimal(this.timestamp));
        }
        writer.name("trace_id").value(logger, this.traceId);
        writer.name("span_id").value(logger, this.spanId);
        if (this.parentSpanId != null) {
            writer.name("parent_span_id").value(logger, this.parentSpanId);
        }
        writer.name("op").value(this.op);
        if (this.description != null) {
            writer.name("description").value(this.description);
        }
        if (this.status != null) {
            writer.name("status").value(logger, this.status);
        }
        if (this.origin != null) {
            writer.name("origin").value(logger, this.origin);
        }
        if (!this.tags.isEmpty()) {
            writer.name("tags").value(logger, this.tags);
        }
        if (this.data != null) {
            writer.name("data").value(logger, this.data);
        }
        if (!this.measurements.isEmpty()) {
            writer.name("measurements").value(logger, this.measurements);
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

    @NotNull
    private BigDecimal doubleToBigDecimal(@NotNull Double value) {
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.DOWN);
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
        public static final String START_TIMESTAMP = "start_timestamp";
        public static final String TIMESTAMP = "timestamp";
        public static final String TRACE_ID = "trace_id";
        public static final String SPAN_ID = "span_id";
        public static final String PARENT_SPAN_ID = "parent_span_id";
        public static final String OP = "op";
        public static final String DESCRIPTION = "description";
        public static final String STATUS = "status";
        public static final String ORIGIN = "origin";
        public static final String TAGS = "tags";
        public static final String MEASUREMENTS = "measurements";
        public static final String DATA = "data";
    }

    public static final class Deserializer
    implements JsonDeserializer<SentrySpan> {
        @Override
        @NotNull
        public SentrySpan deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            Double startTimestamp = null;
            Double timestamp = null;
            SentryId traceId = null;
            SpanId spanId = null;
            SpanId parentSpanId = null;
            String op = null;
            String description = null;
            SpanStatus status = null;
            String origin = null;
            HashMap<String, String> tags = null;
            HashMap<String, MeasurementValue> measurements = null;
            Map data = null;
            ConcurrentHashMap<String, Object> unknown = null;
            block32: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "start_timestamp": {
                        Date date;
                        try {
                            startTimestamp = reader.nextDoubleOrNull();
                        }
                        catch (NumberFormatException e) {
                            date = reader.nextDateOrNull(logger);
                            startTimestamp = date != null ? Double.valueOf(DateUtils.dateToSeconds(date)) : null;
                        }
                        continue block32;
                    }
                    case "timestamp": {
                        Date date;
                        try {
                            timestamp = reader.nextDoubleOrNull();
                        }
                        catch (NumberFormatException e) {
                            date = reader.nextDateOrNull(logger);
                            timestamp = date != null ? Double.valueOf(DateUtils.dateToSeconds(date)) : null;
                        }
                        continue block32;
                    }
                    case "trace_id": {
                        traceId = new SentryId.Deserializer().deserialize(reader, logger);
                        continue block32;
                    }
                    case "span_id": {
                        spanId = new SpanId.Deserializer().deserialize(reader, logger);
                        continue block32;
                    }
                    case "parent_span_id": {
                        parentSpanId = reader.nextOrNull(logger, new SpanId.Deserializer());
                        continue block32;
                    }
                    case "op": {
                        op = reader.nextStringOrNull();
                        continue block32;
                    }
                    case "description": {
                        description = reader.nextStringOrNull();
                        continue block32;
                    }
                    case "status": {
                        status = reader.nextOrNull(logger, new SpanStatus.Deserializer());
                        continue block32;
                    }
                    case "origin": {
                        origin = reader.nextStringOrNull();
                        continue block32;
                    }
                    case "tags": {
                        tags = (Map)reader.nextObjectOrNull();
                        continue block32;
                    }
                    case "data": {
                        data = (Map)reader.nextObjectOrNull();
                        continue block32;
                    }
                    case "measurements": {
                        measurements = reader.nextMapOrNull(logger, new MeasurementValue.Deserializer());
                        continue block32;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            if (startTimestamp == null) {
                throw this.missingRequiredFieldException("start_timestamp", logger);
            }
            if (traceId == null) {
                throw this.missingRequiredFieldException("trace_id", logger);
            }
            if (spanId == null) {
                throw this.missingRequiredFieldException("span_id", logger);
            }
            if (op == null) {
                throw this.missingRequiredFieldException("op", logger);
            }
            if (tags == null) {
                tags = new HashMap();
            }
            if (measurements == null) {
                measurements = new HashMap();
            }
            SentrySpan sentrySpan = new SentrySpan(startTimestamp, timestamp, traceId, spanId, parentSpanId, op, description, status, origin, tags, measurements, data);
            sentrySpan.setUnknown(unknown);
            reader.endObject();
            return sentrySpan;
        }

        private Exception missingRequiredFieldException(String field, ILogger logger) {
            String message = "Missing required field \"" + field + "\"";
            IllegalStateException exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            return exception;
        }
    }
}

