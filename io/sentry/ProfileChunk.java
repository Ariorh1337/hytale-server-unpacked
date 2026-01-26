/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryDate;
import io.sentry.SentryOptions;
import io.sentry.profilemeasurements.ProfileMeasurement;
import io.sentry.protocol.DebugMeta;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.profiling.SentryProfile;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ProfileChunk
implements JsonUnknown,
JsonSerializable {
    public static final String PLATFORM_ANDROID = "android";
    public static final String PLATFORM_JAVA = "java";
    @Nullable
    private DebugMeta debugMeta;
    @NotNull
    private SentryId profilerId;
    @NotNull
    private SentryId chunkId;
    @Nullable
    private SdkVersion clientSdk;
    @NotNull
    private final Map<String, ProfileMeasurement> measurements;
    @NotNull
    private String platform;
    @NotNull
    private String release;
    @Nullable
    private String environment;
    @NotNull
    private String version;
    private double timestamp;
    @NotNull
    private final File traceFile;
    @Nullable
    private String sampledProfile = null;
    @Nullable
    private SentryProfile sentryProfile;
    @Nullable
    private Map<String, Object> unknown;

    public ProfileChunk() {
        this(SentryId.EMPTY_ID, SentryId.EMPTY_ID, new File("dummy"), new HashMap<String, ProfileMeasurement>(), 0.0, PLATFORM_ANDROID, SentryOptions.empty());
    }

    public ProfileChunk(@NotNull SentryId profilerId, @NotNull SentryId chunkId, @NotNull File traceFile, @NotNull Map<String, ProfileMeasurement> measurements, @NotNull Double timestamp, @NotNull String platform, @NotNull SentryOptions options) {
        this.profilerId = profilerId;
        this.chunkId = chunkId;
        this.traceFile = traceFile;
        this.measurements = measurements;
        this.debugMeta = null;
        this.clientSdk = options.getSdkVersion();
        this.release = options.getRelease() != null ? options.getRelease() : "";
        this.environment = options.getEnvironment();
        this.platform = platform;
        this.version = "2";
        this.timestamp = timestamp;
    }

    @NotNull
    public Map<String, ProfileMeasurement> getMeasurements() {
        return this.measurements;
    }

    @Nullable
    public DebugMeta getDebugMeta() {
        return this.debugMeta;
    }

    public void setDebugMeta(@Nullable DebugMeta debugMeta) {
        this.debugMeta = debugMeta;
    }

    @Nullable
    public SdkVersion getClientSdk() {
        return this.clientSdk;
    }

    @NotNull
    public SentryId getChunkId() {
        return this.chunkId;
    }

    @Nullable
    public String getEnvironment() {
        return this.environment;
    }

    @NotNull
    public String getPlatform() {
        return this.platform;
    }

    @NotNull
    public SentryId getProfilerId() {
        return this.profilerId;
    }

    @NotNull
    public String getRelease() {
        return this.release;
    }

    @Nullable
    public String getSampledProfile() {
        return this.sampledProfile;
    }

    public void setSampledProfile(@Nullable String sampledProfile) {
        this.sampledProfile = sampledProfile;
    }

    @NotNull
    public File getTraceFile() {
        return this.traceFile;
    }

    public double getTimestamp() {
        return this.timestamp;
    }

    @NotNull
    public String getVersion() {
        return this.version;
    }

    @Nullable
    public SentryProfile getSentryProfile() {
        return this.sentryProfile;
    }

    public void setSentryProfile(@Nullable SentryProfile sentryProfile) {
        this.sentryProfile = sentryProfile;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileChunk)) {
            return false;
        }
        ProfileChunk that = (ProfileChunk)o;
        return Objects.equals(this.debugMeta, that.debugMeta) && Objects.equals(this.profilerId, that.profilerId) && Objects.equals(this.chunkId, that.chunkId) && Objects.equals(this.clientSdk, that.clientSdk) && Objects.equals(this.measurements, that.measurements) && Objects.equals(this.platform, that.platform) && Objects.equals(this.release, that.release) && Objects.equals(this.environment, that.environment) && Objects.equals(this.version, that.version) && Objects.equals(this.sampledProfile, that.sampledProfile) && Objects.equals(this.unknown, that.unknown) && Objects.equals(this.sentryProfile, that.sentryProfile);
    }

    public int hashCode() {
        return Objects.hash(this.debugMeta, this.profilerId, this.chunkId, this.clientSdk, this.measurements, this.platform, this.release, this.environment, this.version, this.sampledProfile, this.sentryProfile, this.unknown);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        if (this.debugMeta != null) {
            writer.name("debug_meta").value(logger, this.debugMeta);
        }
        writer.name("profiler_id").value(logger, this.profilerId);
        writer.name("chunk_id").value(logger, this.chunkId);
        if (this.clientSdk != null) {
            writer.name("client_sdk").value(logger, this.clientSdk);
        }
        if (!this.measurements.isEmpty()) {
            String prevIndent = writer.getIndent();
            writer.setIndent("");
            writer.name("measurements").value(logger, this.measurements);
            writer.setIndent(prevIndent);
        }
        writer.name("platform").value(logger, this.platform);
        writer.name("release").value(logger, this.release);
        if (this.environment != null) {
            writer.name("environment").value(logger, this.environment);
        }
        writer.name("version").value(logger, this.version);
        if (this.sampledProfile != null) {
            writer.name("sampled_profile").value(logger, this.sampledProfile);
        }
        writer.name("timestamp").value(logger, this.doubleToBigDecimal(this.timestamp));
        if (this.sentryProfile != null) {
            writer.name("profile").value(logger, this.sentryProfile);
        }
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
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
        public static final String DEBUG_META = "debug_meta";
        public static final String PROFILER_ID = "profiler_id";
        public static final String CHUNK_ID = "chunk_id";
        public static final String CLIENT_SDK = "client_sdk";
        public static final String MEASUREMENTS = "measurements";
        public static final String PLATFORM = "platform";
        public static final String RELEASE = "release";
        public static final String ENVIRONMENT = "environment";
        public static final String VERSION = "version";
        public static final String SAMPLED_PROFILE = "sampled_profile";
        public static final String TIMESTAMP = "timestamp";
        public static final String SENTRY_PROFILE = "profile";
    }

    public static final class Deserializer
    implements JsonDeserializer<ProfileChunk> {
        @Override
        @NotNull
        public ProfileChunk deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            ProfileChunk data = new ProfileChunk();
            ConcurrentHashMap<String, Object> unknown = null;
            block28: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "debug_meta": {
                        DebugMeta debugMeta = reader.nextOrNull(logger, new DebugMeta.Deserializer());
                        if (debugMeta == null) continue block28;
                        data.debugMeta = debugMeta;
                        continue block28;
                    }
                    case "profiler_id": {
                        SentryId profilerId = reader.nextOrNull(logger, new SentryId.Deserializer());
                        if (profilerId == null) continue block28;
                        data.profilerId = profilerId;
                        continue block28;
                    }
                    case "chunk_id": {
                        SentryId chunkId = reader.nextOrNull(logger, new SentryId.Deserializer());
                        if (chunkId == null) continue block28;
                        data.chunkId = chunkId;
                        continue block28;
                    }
                    case "client_sdk": {
                        SdkVersion clientSdk = reader.nextOrNull(logger, new SdkVersion.Deserializer());
                        if (clientSdk == null) continue block28;
                        data.clientSdk = clientSdk;
                        continue block28;
                    }
                    case "measurements": {
                        Map<String, ProfileMeasurement> measurements = reader.nextMapOrNull(logger, new ProfileMeasurement.Deserializer());
                        if (measurements == null) continue block28;
                        data.measurements.putAll(measurements);
                        continue block28;
                    }
                    case "platform": {
                        String platform = reader.nextStringOrNull();
                        if (platform == null) continue block28;
                        data.platform = platform;
                        continue block28;
                    }
                    case "release": {
                        String release = reader.nextStringOrNull();
                        if (release == null) continue block28;
                        data.release = release;
                        continue block28;
                    }
                    case "environment": {
                        String environment = reader.nextStringOrNull();
                        if (environment == null) continue block28;
                        data.environment = environment;
                        continue block28;
                    }
                    case "version": {
                        String version = reader.nextStringOrNull();
                        if (version == null) continue block28;
                        data.version = version;
                        continue block28;
                    }
                    case "sampled_profile": {
                        String sampledProfile = reader.nextStringOrNull();
                        if (sampledProfile == null) continue block28;
                        data.sampledProfile = sampledProfile;
                        continue block28;
                    }
                    case "timestamp": {
                        Double timestamp = reader.nextDoubleOrNull();
                        if (timestamp == null) continue block28;
                        data.timestamp = timestamp;
                        continue block28;
                    }
                    case "profile": {
                        SentryProfile sentryProfile = reader.nextOrNull(logger, new SentryProfile.Deserializer());
                        if (sentryProfile == null) continue block28;
                        data.sentryProfile = sentryProfile;
                        continue block28;
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

    public static final class Builder {
        @NotNull
        private final SentryId profilerId;
        @NotNull
        private final SentryId chunkId;
        @NotNull
        private final Map<String, ProfileMeasurement> measurements;
        @NotNull
        private final File traceFile;
        private final double timestamp;
        @NotNull
        private final String platform;

        public Builder(@NotNull SentryId profilerId, @NotNull SentryId chunkId, @NotNull Map<String, ProfileMeasurement> measurements, @NotNull File traceFile, @NotNull SentryDate timestamp, @NotNull String platform) {
            this.profilerId = profilerId;
            this.chunkId = chunkId;
            this.measurements = new ConcurrentHashMap<String, ProfileMeasurement>(measurements);
            this.traceFile = traceFile;
            this.timestamp = DateUtils.nanosToSeconds(timestamp.nanoTimestamp());
            this.platform = platform;
        }

        public ProfileChunk build(SentryOptions options) {
            return new ProfileChunk(this.profilerId, this.chunkId, this.traceFile, this.measurements, this.timestamp, this.platform, options);
        }
    }
}

