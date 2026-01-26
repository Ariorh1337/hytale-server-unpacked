/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.CheckInStatus;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.MonitorConfig;
import io.sentry.MonitorContexts;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.protocol.SentryId;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CheckIn
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private final SentryId checkInId;
    @NotNull
    private String monitorSlug;
    @NotNull
    private String status;
    @Nullable
    private Double duration;
    @Nullable
    private String release;
    @Nullable
    private String environment;
    @NotNull
    private final MonitorContexts contexts = new MonitorContexts();
    @Nullable
    private MonitorConfig monitorConfig;
    @Nullable
    private Map<String, Object> unknown;

    public CheckIn(@NotNull String monitorSlug, @NotNull CheckInStatus status) {
        this(null, monitorSlug, status.apiName());
    }

    public CheckIn(@Nullable SentryId id, @NotNull String monitorSlug, @NotNull CheckInStatus status) {
        this(id, monitorSlug, status.apiName());
    }

    @ApiStatus.Internal
    public CheckIn(@Nullable SentryId checkInId, @NotNull String monitorSlug, @NotNull String status) {
        this.checkInId = checkInId == null ? new SentryId() : checkInId;
        this.monitorSlug = monitorSlug;
        this.status = status;
    }

    @NotNull
    public SentryId getCheckInId() {
        return this.checkInId;
    }

    @NotNull
    public String getMonitorSlug() {
        return this.monitorSlug;
    }

    public void setMonitorSlug(@NotNull String monitorSlug) {
        this.monitorSlug = monitorSlug;
    }

    @NotNull
    public String getStatus() {
        return this.status;
    }

    public void setStatus(@NotNull String status) {
        this.status = status;
    }

    public void setStatus(@NotNull CheckInStatus status) {
        this.status = status.apiName();
    }

    @Nullable
    public Double getDuration() {
        return this.duration;
    }

    public void setDuration(@Nullable Double duration) {
        this.duration = duration;
    }

    @Nullable
    public String getRelease() {
        return this.release;
    }

    public void setRelease(@Nullable String release) {
        this.release = release;
    }

    @Nullable
    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(@Nullable String environment) {
        this.environment = environment;
    }

    @Nullable
    public MonitorConfig getMonitorConfig() {
        return this.monitorConfig;
    }

    public void setMonitorConfig(@Nullable MonitorConfig monitorConfig) {
        this.monitorConfig = monitorConfig;
    }

    @NotNull
    public MonitorContexts getContexts() {
        return this.contexts;
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
        writer.name("check_in_id");
        this.checkInId.serialize(writer, logger);
        writer.name("monitor_slug").value(this.monitorSlug);
        writer.name("status").value(this.status);
        if (this.duration != null) {
            writer.name("duration").value(this.duration);
        }
        if (this.release != null) {
            writer.name("release").value(this.release);
        }
        if (this.environment != null) {
            writer.name("environment").value(this.environment);
        }
        if (this.monitorConfig != null) {
            writer.name("monitor_config");
            this.monitorConfig.serialize(writer, logger);
        }
        if (this.contexts != null) {
            writer.name("contexts");
            this.contexts.serialize(writer, logger);
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
        public static final String CHECK_IN_ID = "check_in_id";
        public static final String MONITOR_SLUG = "monitor_slug";
        public static final String STATUS = "status";
        public static final String DURATION = "duration";
        public static final String RELEASE = "release";
        public static final String ENVIRONMENT = "environment";
        public static final String CONTEXTS = "contexts";
        public static final String MONITOR_CONFIG = "monitor_config";
    }

    public static final class Deserializer
    implements JsonDeserializer<CheckIn> {
        @Override
        @NotNull
        public CheckIn deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            IllegalStateException exception;
            String message;
            SentryId sentryId = null;
            MonitorConfig monitorConfig = null;
            String monitorSlug = null;
            String status = null;
            Double duration = null;
            String release = null;
            String environment = null;
            MonitorContexts contexts = null;
            HashMap<String, Object> unknown = null;
            reader.beginObject();
            block20: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "check_in_id": {
                        sentryId = new SentryId.Deserializer().deserialize(reader, logger);
                        continue block20;
                    }
                    case "monitor_slug": {
                        monitorSlug = reader.nextStringOrNull();
                        continue block20;
                    }
                    case "status": {
                        status = reader.nextStringOrNull();
                        continue block20;
                    }
                    case "duration": {
                        duration = reader.nextDoubleOrNull();
                        continue block20;
                    }
                    case "release": {
                        release = reader.nextStringOrNull();
                        continue block20;
                    }
                    case "environment": {
                        environment = reader.nextStringOrNull();
                        continue block20;
                    }
                    case "monitor_config": {
                        monitorConfig = new MonitorConfig.Deserializer().deserialize(reader, logger);
                        continue block20;
                    }
                    case "contexts": {
                        contexts = new MonitorContexts.Deserializer().deserialize(reader, logger);
                        continue block20;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            reader.endObject();
            if (sentryId == null) {
                message = "Missing required field \"check_in_id\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            if (monitorSlug == null) {
                message = "Missing required field \"monitor_slug\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            if (status == null) {
                message = "Missing required field \"status\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            CheckIn checkIn = new CheckIn(sentryId, monitorSlug, status);
            checkIn.setDuration(duration);
            checkIn.setRelease(release);
            checkIn.setEnvironment(environment);
            checkIn.setMonitorConfig(monitorConfig);
            checkIn.getContexts().putAll(contexts);
            checkIn.setUnknown(unknown);
            return checkIn;
        }
    }
}

