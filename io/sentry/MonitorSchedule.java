/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.MonitorScheduleType;
import io.sentry.MonitorScheduleUnit;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MonitorSchedule
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private String type;
    @NotNull
    private String value;
    @Nullable
    private String unit;
    @Nullable
    private Map<String, Object> unknown;

    @NotNull
    public static MonitorSchedule crontab(@NotNull String value) {
        return new MonitorSchedule(MonitorScheduleType.CRONTAB.apiName(), value, null);
    }

    @NotNull
    public static MonitorSchedule interval(@NotNull Integer value, @NotNull MonitorScheduleUnit unit) {
        return new MonitorSchedule(MonitorScheduleType.INTERVAL.apiName(), value.toString(), unit.apiName());
    }

    @ApiStatus.Internal
    public MonitorSchedule(@NotNull String type, @NotNull String value, @Nullable String unit) {
        this.type = type;
        this.value = value;
        this.unit = unit;
    }

    @NotNull
    public String getType() {
        return this.type;
    }

    public void setType(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public String getValue() {
        return this.value;
    }

    public void setValue(@NotNull String value) {
        this.value = value;
    }

    public void setValue(@NotNull Integer value) {
        this.value = value.toString();
    }

    @Nullable
    public String getUnit() {
        return this.unit;
    }

    public void setUnit(@Nullable String unit) {
        this.unit = unit;
    }

    public void setUnit(@Nullable MonitorScheduleUnit unit) {
        this.unit = unit == null ? null : unit.apiName();
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
        writer.name("type").value(this.type);
        if (MonitorScheduleType.INTERVAL.apiName().equalsIgnoreCase(this.type)) {
            try {
                writer.name("value").value(Integer.valueOf(this.value));
            }
            catch (Throwable t) {
                logger.log(SentryLevel.ERROR, "Unable to serialize monitor schedule value: %s", this.value);
            }
        } else {
            writer.name("value").value(this.value);
        }
        if (this.unit != null) {
            writer.name("unit").value(this.unit);
        }
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
            }
        }
        writer.endObject();
    }

    public static final class JsonKeys {
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String UNIT = "unit";
    }

    public static final class Deserializer
    implements JsonDeserializer<MonitorSchedule> {
        @Override
        @NotNull
        public MonitorSchedule deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            IllegalStateException exception;
            String message;
            String type = null;
            String value = null;
            String unit = null;
            HashMap<String, Object> unknown = null;
            reader.beginObject();
            block10: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "type": {
                        type = reader.nextStringOrNull();
                        continue block10;
                    }
                    case "value": {
                        value = reader.nextStringOrNull();
                        continue block10;
                    }
                    case "unit": {
                        unit = reader.nextStringOrNull();
                        continue block10;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            reader.endObject();
            if (type == null) {
                message = "Missing required field \"type\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            if (value == null) {
                message = "Missing required field \"value\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            MonitorSchedule monitorSchedule = new MonitorSchedule(type, value, unit);
            monitorSchedule.setUnknown(unknown);
            return monitorSchedule;
        }
    }
}

