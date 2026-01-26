/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.ProfileLifecycle;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.TracesSamplingDecision;
import io.sentry.util.SentryRandom;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@ApiStatus.Internal
public final class SentryAppStartProfilingOptions
implements JsonUnknown,
JsonSerializable {
    boolean profileSampled;
    @Nullable
    Double profileSampleRate;
    boolean traceSampled;
    @Nullable
    Double traceSampleRate;
    @Nullable
    String profilingTracesDirPath;
    boolean isProfilingEnabled;
    boolean isContinuousProfilingEnabled;
    int profilingTracesHz;
    boolean continuousProfileSampled;
    boolean isEnableAppStartProfiling;
    boolean isStartProfilerOnAppStart;
    @NotNull
    ProfileLifecycle profileLifecycle;
    @Nullable
    private Map<String, Object> unknown;

    @VisibleForTesting
    public SentryAppStartProfilingOptions() {
        this.traceSampled = false;
        this.traceSampleRate = null;
        this.profileSampled = false;
        this.profileSampleRate = null;
        this.continuousProfileSampled = false;
        this.profilingTracesDirPath = null;
        this.isProfilingEnabled = false;
        this.isContinuousProfilingEnabled = false;
        this.profileLifecycle = ProfileLifecycle.MANUAL;
        this.profilingTracesHz = 0;
        this.isEnableAppStartProfiling = true;
        this.isStartProfilerOnAppStart = false;
    }

    SentryAppStartProfilingOptions(@NotNull SentryOptions options, @NotNull TracesSamplingDecision samplingDecision) {
        this.traceSampled = samplingDecision.getSampled();
        this.traceSampleRate = samplingDecision.getSampleRate();
        this.profileSampled = samplingDecision.getProfileSampled();
        this.profileSampleRate = samplingDecision.getProfileSampleRate();
        this.continuousProfileSampled = options.getInternalTracesSampler().sampleSessionProfile(SentryRandom.current().nextDouble());
        this.profilingTracesDirPath = options.getProfilingTracesDirPath();
        this.isProfilingEnabled = options.isProfilingEnabled();
        this.isContinuousProfilingEnabled = options.isContinuousProfilingEnabled();
        this.profileLifecycle = options.getProfileLifecycle();
        this.profilingTracesHz = options.getProfilingTracesHz();
        this.isEnableAppStartProfiling = options.isEnableAppStartProfiling();
        this.isStartProfilerOnAppStart = options.isStartProfilerOnAppStart();
    }

    public void setProfileSampled(boolean profileSampled) {
        this.profileSampled = profileSampled;
    }

    public boolean isProfileSampled() {
        return this.profileSampled;
    }

    public void setContinuousProfileSampled(boolean continuousProfileSampled) {
        this.continuousProfileSampled = continuousProfileSampled;
    }

    public boolean isContinuousProfileSampled() {
        return this.continuousProfileSampled;
    }

    public void setProfileLifecycle(@NotNull ProfileLifecycle profileLifecycle) {
        this.profileLifecycle = profileLifecycle;
    }

    @NotNull
    public ProfileLifecycle getProfileLifecycle() {
        return this.profileLifecycle;
    }

    public void setProfileSampleRate(@Nullable Double profileSampleRate) {
        this.profileSampleRate = profileSampleRate;
    }

    @Nullable
    public Double getProfileSampleRate() {
        return this.profileSampleRate;
    }

    public void setTraceSampled(boolean traceSampled) {
        this.traceSampled = traceSampled;
    }

    public boolean isTraceSampled() {
        return this.traceSampled;
    }

    public void setTraceSampleRate(@Nullable Double traceSampleRate) {
        this.traceSampleRate = traceSampleRate;
    }

    @Nullable
    public Double getTraceSampleRate() {
        return this.traceSampleRate;
    }

    public void setProfilingTracesDirPath(@Nullable String profilingTracesDirPath) {
        this.profilingTracesDirPath = profilingTracesDirPath;
    }

    @Nullable
    public String getProfilingTracesDirPath() {
        return this.profilingTracesDirPath;
    }

    public void setProfilingEnabled(boolean profilingEnabled) {
        this.isProfilingEnabled = profilingEnabled;
    }

    public boolean isProfilingEnabled() {
        return this.isProfilingEnabled;
    }

    public void setContinuousProfilingEnabled(boolean continuousProfilingEnabled) {
        this.isContinuousProfilingEnabled = continuousProfilingEnabled;
    }

    public boolean isContinuousProfilingEnabled() {
        return this.isContinuousProfilingEnabled;
    }

    public void setProfilingTracesHz(int profilingTracesHz) {
        this.profilingTracesHz = profilingTracesHz;
    }

    public int getProfilingTracesHz() {
        return this.profilingTracesHz;
    }

    public void setEnableAppStartProfiling(boolean enableAppStartProfiling) {
        this.isEnableAppStartProfiling = enableAppStartProfiling;
    }

    public boolean isEnableAppStartProfiling() {
        return this.isEnableAppStartProfiling;
    }

    public void setStartProfilerOnAppStart(boolean startProfilerOnAppStart) {
        this.isStartProfilerOnAppStart = startProfilerOnAppStart;
    }

    public boolean isStartProfilerOnAppStart() {
        return this.isStartProfilerOnAppStart;
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("profile_sampled").value(logger, this.profileSampled);
        writer.name("profile_sample_rate").value(logger, this.profileSampleRate);
        writer.name("continuous_profile_sampled").value(logger, this.continuousProfileSampled);
        writer.name("trace_sampled").value(logger, this.traceSampled);
        writer.name("trace_sample_rate").value(logger, this.traceSampleRate);
        writer.name("profiling_traces_dir_path").value(logger, this.profilingTracesDirPath);
        writer.name("is_profiling_enabled").value(logger, this.isProfilingEnabled);
        writer.name("is_continuous_profiling_enabled").value(logger, this.isContinuousProfilingEnabled);
        writer.name("profile_lifecycle").value(logger, this.profileLifecycle.name());
        writer.name("profiling_traces_hz").value(logger, this.profilingTracesHz);
        writer.name("is_enable_app_start_profiling").value(logger, this.isEnableAppStartProfiling);
        writer.name("is_start_profiler_on_app_start").value(logger, this.isStartProfilerOnAppStart);
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
        public static final String PROFILE_SAMPLED = "profile_sampled";
        public static final String PROFILE_SAMPLE_RATE = "profile_sample_rate";
        public static final String CONTINUOUS_PROFILE_SAMPLED = "continuous_profile_sampled";
        public static final String TRACE_SAMPLED = "trace_sampled";
        public static final String TRACE_SAMPLE_RATE = "trace_sample_rate";
        public static final String PROFILING_TRACES_DIR_PATH = "profiling_traces_dir_path";
        public static final String IS_PROFILING_ENABLED = "is_profiling_enabled";
        public static final String IS_CONTINUOUS_PROFILING_ENABLED = "is_continuous_profiling_enabled";
        public static final String PROFILE_LIFECYCLE = "profile_lifecycle";
        public static final String PROFILING_TRACES_HZ = "profiling_traces_hz";
        public static final String IS_ENABLE_APP_START_PROFILING = "is_enable_app_start_profiling";
        public static final String IS_START_PROFILER_ON_APP_START = "is_start_profiler_on_app_start";
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryAppStartProfilingOptions> {
        @Override
        @NotNull
        public SentryAppStartProfilingOptions deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            SentryAppStartProfilingOptions options = new SentryAppStartProfilingOptions();
            ConcurrentHashMap<String, Object> unknown = null;
            block30: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "profile_sampled": {
                        @Nullable Boolean profileSampled = reader.nextBooleanOrNull();
                        if (profileSampled == null) continue block30;
                        options.profileSampled = profileSampled;
                        continue block30;
                    }
                    case "profile_sample_rate": {
                        @Nullable Double profileSampleRate = reader.nextDoubleOrNull();
                        if (profileSampleRate == null) continue block30;
                        options.profileSampleRate = profileSampleRate;
                        continue block30;
                    }
                    case "continuous_profile_sampled": {
                        @Nullable Boolean continuousProfileSampled = reader.nextBooleanOrNull();
                        if (continuousProfileSampled == null) continue block30;
                        options.continuousProfileSampled = continuousProfileSampled;
                        continue block30;
                    }
                    case "trace_sampled": {
                        @Nullable Boolean traceSampled = reader.nextBooleanOrNull();
                        if (traceSampled == null) continue block30;
                        options.traceSampled = traceSampled;
                        continue block30;
                    }
                    case "trace_sample_rate": {
                        @Nullable Double traceSampleRate = reader.nextDoubleOrNull();
                        if (traceSampleRate == null) continue block30;
                        options.traceSampleRate = traceSampleRate;
                        continue block30;
                    }
                    case "profiling_traces_dir_path": {
                        @Nullable String profilingTracesDirPath = reader.nextStringOrNull();
                        if (profilingTracesDirPath == null) continue block30;
                        options.profilingTracesDirPath = profilingTracesDirPath;
                        continue block30;
                    }
                    case "is_profiling_enabled": {
                        @Nullable Boolean isProfilingEnabled = reader.nextBooleanOrNull();
                        if (isProfilingEnabled == null) continue block30;
                        options.isProfilingEnabled = isProfilingEnabled;
                        continue block30;
                    }
                    case "is_continuous_profiling_enabled": {
                        @Nullable Boolean isContinuousProfilingEnabled = reader.nextBooleanOrNull();
                        if (isContinuousProfilingEnabled == null) continue block30;
                        options.isContinuousProfilingEnabled = isContinuousProfilingEnabled;
                        continue block30;
                    }
                    case "profile_lifecycle": {
                        @Nullable String profileLifecycle = reader.nextStringOrNull();
                        if (profileLifecycle == null) continue block30;
                        try {
                            options.profileLifecycle = ProfileLifecycle.valueOf(profileLifecycle);
                        }
                        catch (IllegalArgumentException e) {
                            logger.log(SentryLevel.ERROR, "Error when deserializing ProfileLifecycle: " + profileLifecycle, new Object[0]);
                        }
                        continue block30;
                    }
                    case "profiling_traces_hz": {
                        @Nullable Integer profilingTracesHz = reader.nextIntegerOrNull();
                        if (profilingTracesHz == null) continue block30;
                        options.profilingTracesHz = profilingTracesHz;
                        continue block30;
                    }
                    case "is_enable_app_start_profiling": {
                        @Nullable Boolean isEnableAppStartProfiling = reader.nextBooleanOrNull();
                        if (isEnableAppStartProfiling == null) continue block30;
                        options.isEnableAppStartProfiling = isEnableAppStartProfiling;
                        continue block30;
                    }
                    case "is_start_profiler_on_app_start": {
                        @Nullable Boolean isStartProfilerOnAppStart = reader.nextBooleanOrNull();
                        if (isStartProfilerOnAppStart == null) continue block30;
                        options.isStartProfilerOnAppStart = isStartProfilerOnAppStart;
                        continue block30;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            options.setUnknown(unknown);
            reader.endObject();
            return options;
        }
    }
}

