/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.cache;

import io.sentry.IOptionsObserver;
import io.sentry.JsonDeserializer;
import io.sentry.SentryOptions;
import io.sentry.cache.CacheUtils;
import io.sentry.protocol.SdkVersion;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PersistingOptionsObserver
implements IOptionsObserver {
    public static final String OPTIONS_CACHE = ".options-cache";
    public static final String RELEASE_FILENAME = "release.json";
    public static final String PROGUARD_UUID_FILENAME = "proguard-uuid.json";
    public static final String SDK_VERSION_FILENAME = "sdk-version.json";
    public static final String ENVIRONMENT_FILENAME = "environment.json";
    public static final String DIST_FILENAME = "dist.json";
    public static final String TAGS_FILENAME = "tags.json";
    public static final String REPLAY_ERROR_SAMPLE_RATE_FILENAME = "replay-error-sample-rate.json";
    @NotNull
    private final SentryOptions options;

    public PersistingOptionsObserver(@NotNull SentryOptions options) {
        this.options = options;
    }

    @Override
    public void setRelease(@Nullable String release) {
        if (release == null) {
            this.delete(RELEASE_FILENAME);
        } else {
            this.store(release, RELEASE_FILENAME);
        }
    }

    @Override
    public void setProguardUuid(@Nullable String proguardUuid) {
        if (proguardUuid == null) {
            this.delete(PROGUARD_UUID_FILENAME);
        } else {
            this.store(proguardUuid, PROGUARD_UUID_FILENAME);
        }
    }

    @Override
    public void setSdkVersion(@Nullable SdkVersion sdkVersion) {
        if (sdkVersion == null) {
            this.delete(SDK_VERSION_FILENAME);
        } else {
            this.store(sdkVersion, SDK_VERSION_FILENAME);
        }
    }

    @Override
    public void setDist(@Nullable String dist) {
        if (dist == null) {
            this.delete(DIST_FILENAME);
        } else {
            this.store(dist, DIST_FILENAME);
        }
    }

    @Override
    public void setEnvironment(@Nullable String environment) {
        if (environment == null) {
            this.delete(ENVIRONMENT_FILENAME);
        } else {
            this.store(environment, ENVIRONMENT_FILENAME);
        }
    }

    @Override
    public void setTags(@NotNull @NotNull Map<String, @NotNull String> tags) {
        this.store(tags, TAGS_FILENAME);
    }

    @Override
    public void setReplayErrorSampleRate(@Nullable Double replayErrorSampleRate) {
        if (replayErrorSampleRate == null) {
            this.delete(REPLAY_ERROR_SAMPLE_RATE_FILENAME);
        } else {
            this.store(replayErrorSampleRate.toString(), REPLAY_ERROR_SAMPLE_RATE_FILENAME);
        }
    }

    private <T> void store(@NotNull T entity, @NotNull String fileName) {
        CacheUtils.store(this.options, entity, OPTIONS_CACHE, fileName);
    }

    private void delete(@NotNull String fileName) {
        CacheUtils.delete(this.options, OPTIONS_CACHE, fileName);
    }

    @Nullable
    public static <T> T read(@NotNull SentryOptions options, @NotNull String fileName, @NotNull Class<T> clazz) {
        return PersistingOptionsObserver.read(options, fileName, clazz, null);
    }

    @Nullable
    public static <T, R> T read(@NotNull SentryOptions options, @NotNull String fileName, @NotNull Class<T> clazz, @Nullable JsonDeserializer<R> elementDeserializer) {
        return CacheUtils.read(options, OPTIONS_CACHE, fileName, clazz, elementDeserializer);
    }
}

