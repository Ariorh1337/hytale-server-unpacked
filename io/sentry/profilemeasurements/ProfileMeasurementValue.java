/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.profilemeasurements;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ProfileMeasurementValue
implements JsonUnknown,
JsonSerializable {
    @Nullable
    private Map<String, Object> unknown;
    private double timestamp;
    @NotNull
    private String relativeStartNs;
    private double value;

    public ProfileMeasurementValue() {
        this(0L, 0, 0L);
    }

    public ProfileMeasurementValue(@NotNull Long relativeStartNs, @NotNull Number value, long nanoTimestamp) {
        this.relativeStartNs = relativeStartNs.toString();
        this.value = value.doubleValue();
        this.timestamp = DateUtils.nanosToSeconds(nanoTimestamp);
    }

    public double getTimestamp() {
        return this.timestamp;
    }

    public double getValue() {
        return this.value;
    }

    @NotNull
    public String getRelativeStartNs() {
        return this.relativeStartNs;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProfileMeasurementValue that = (ProfileMeasurementValue)o;
        return Objects.equals(this.unknown, that.unknown) && this.relativeStartNs.equals(that.relativeStartNs) && this.value == that.value && this.timestamp == that.timestamp;
    }

    public int hashCode() {
        return Objects.hash(this.unknown, this.relativeStartNs, this.value);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("value").value(logger, this.value);
        writer.name("elapsed_since_start_ns").value(logger, this.relativeStartNs);
        writer.name("timestamp").value(logger, this.doubleToBigDecimal(this.timestamp));
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
        public static final String VALUE = "value";
        public static final String START_NS = "elapsed_since_start_ns";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Deserializer
    implements JsonDeserializer<ProfileMeasurementValue> {
        @Override
        @NotNull
        public ProfileMeasurementValue deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            ProfileMeasurementValue data = new ProfileMeasurementValue();
            ConcurrentHashMap<String, Object> unknown = null;
            block12: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "value": {
                        Double value = reader.nextDoubleOrNull();
                        if (value == null) continue block12;
                        data.value = value;
                        continue block12;
                    }
                    case "elapsed_since_start_ns": {
                        String startNs = reader.nextStringOrNull();
                        if (startNs == null) continue block12;
                        data.relativeStartNs = startNs;
                        continue block12;
                    }
                    case "timestamp": {
                        Double timestamp;
                        try {
                            timestamp = reader.nextDoubleOrNull();
                        }
                        catch (NumberFormatException e) {
                            Date date = reader.nextDateOrNull(logger);
                            Double d = timestamp = date != null ? Double.valueOf(DateUtils.dateToSeconds(date)) : null;
                        }
                        if (timestamp == null) continue block12;
                        data.timestamp = timestamp;
                        continue block12;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            data.setUnknown(unknown);
            reader.endObject();
            return data;
        }
    }
}

