/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
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
import io.sentry.SentryOptions;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public final class TraceContext
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private final SentryId traceId;
    @NotNull
    private final String publicKey;
    @Nullable
    private final String release;
    @Nullable
    private final String environment;
    @Nullable
    private final String userId;
    @Nullable
    private final String transaction;
    @Nullable
    private final String sampleRate;
    @Nullable
    private final String sampleRand;
    @Nullable
    private final String sampled;
    @Nullable
    private final SentryId replayId;
    private @Nullable Map<String, @NotNull Object> unknown;

    TraceContext(@NotNull SentryId traceId, @NotNull String publicKey) {
        this(traceId, publicKey, null, null, null, null, null, null, null);
    }

    @Deprecated
    TraceContext(@NotNull SentryId traceId, @NotNull String publicKey, @Nullable String release, @Nullable String environment, @Nullable String userId, @Nullable String transaction, @Nullable String sampleRate, @Nullable String sampled, @Nullable SentryId replayId) {
        this(traceId, publicKey, release, environment, userId, transaction, sampleRate, sampled, replayId, null);
    }

    TraceContext(@NotNull SentryId traceId, @NotNull String publicKey, @Nullable String release, @Nullable String environment, @Nullable String userId, @Nullable String transaction, @Nullable String sampleRate, @Nullable String sampled, @Nullable SentryId replayId, @Nullable String sampleRand) {
        this.traceId = traceId;
        this.publicKey = publicKey;
        this.release = release;
        this.environment = environment;
        this.userId = userId;
        this.transaction = transaction;
        this.sampleRate = sampleRate;
        this.sampled = sampled;
        this.replayId = replayId;
        this.sampleRand = sampleRand;
    }

    @Nullable
    private static String getUserId(@NotNull SentryOptions options, @Nullable User user) {
        if (options.isSendDefaultPii() && user != null) {
            return user.getId();
        }
        return null;
    }

    @NotNull
    public SentryId getTraceId() {
        return this.traceId;
    }

    @NotNull
    public String getPublicKey() {
        return this.publicKey;
    }

    @Nullable
    public String getRelease() {
        return this.release;
    }

    @Nullable
    public String getEnvironment() {
        return this.environment;
    }

    @Nullable
    public String getUserId() {
        return this.userId;
    }

    @Nullable
    public String getTransaction() {
        return this.transaction;
    }

    @Nullable
    public String getSampleRate() {
        return this.sampleRate;
    }

    @Nullable
    public String getSampleRand() {
        return this.sampleRand;
    }

    @Nullable
    public String getSampled() {
        return this.sampled;
    }

    @Nullable
    public SentryId getReplayId() {
        return this.replayId;
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
        writer.name("trace_id").value(logger, this.traceId);
        writer.name("public_key").value(this.publicKey);
        if (this.release != null) {
            writer.name("release").value(this.release);
        }
        if (this.environment != null) {
            writer.name("environment").value(this.environment);
        }
        if (this.userId != null) {
            writer.name("user_id").value(this.userId);
        }
        if (this.transaction != null) {
            writer.name("transaction").value(this.transaction);
        }
        if (this.sampleRate != null) {
            writer.name("sample_rate").value(this.sampleRate);
        }
        if (this.sampleRand != null) {
            writer.name("sample_rand").value(this.sampleRand);
        }
        if (this.sampled != null) {
            writer.name("sampled").value(this.sampled);
        }
        if (this.replayId != null) {
            writer.name("replay_id").value(logger, this.replayId);
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

    public static final class JsonKeys {
        public static final String TRACE_ID = "trace_id";
        public static final String PUBLIC_KEY = "public_key";
        public static final String RELEASE = "release";
        public static final String ENVIRONMENT = "environment";
        public static final String USER_ID = "user_id";
        public static final String TRANSACTION = "transaction";
        public static final String SAMPLE_RATE = "sample_rate";
        public static final String SAMPLE_RAND = "sample_rand";
        public static final String SAMPLED = "sampled";
        public static final String REPLAY_ID = "replay_id";
    }

    public static final class Deserializer
    implements JsonDeserializer<TraceContext> {
        @Override
        @NotNull
        public TraceContext deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            SentryId traceId = null;
            String publicKey = null;
            String release = null;
            String environment = null;
            String userId = null;
            String transaction = null;
            String sampleRate = null;
            String sampleRand = null;
            String sampled = null;
            SentryId replayId = null;
            ConcurrentHashMap<String, Object> unknown = null;
            block24: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "trace_id": {
                        traceId = new SentryId.Deserializer().deserialize(reader, logger);
                        continue block24;
                    }
                    case "public_key": {
                        publicKey = reader.nextString();
                        continue block24;
                    }
                    case "release": {
                        release = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "environment": {
                        environment = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "user_id": {
                        userId = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "transaction": {
                        transaction = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "sample_rate": {
                        sampleRate = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "sample_rand": {
                        sampleRand = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "sampled": {
                        sampled = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "replay_id": {
                        replayId = new SentryId.Deserializer().deserialize(reader, logger);
                        continue block24;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            if (traceId == null) {
                throw this.missingRequiredFieldException("trace_id", logger);
            }
            if (publicKey == null) {
                throw this.missingRequiredFieldException("public_key", logger);
            }
            TraceContext traceContext = new TraceContext(traceId, publicKey, release, environment, userId, transaction, sampleRate, sampled, replayId, sampleRand);
            traceContext.setUnknown(unknown);
            reader.endObject();
            return traceContext;
        }

        private Exception missingRequiredFieldException(String field, ILogger logger) {
            String message = "Missing required field \"" + field + "\"";
            IllegalStateException exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            return exception;
        }
    }
}

