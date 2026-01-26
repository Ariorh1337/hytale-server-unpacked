/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FeatureFlag
implements JsonUnknown,
JsonSerializable {
    @NotNull
    public static final String DATA_PREFIX = "flag.evaluation.";
    @NotNull
    private String flag;
    private boolean result;
    private @Nullable Map<String, @NotNull Object> unknown;

    public FeatureFlag(@NotNull String flag, boolean result) {
        this.flag = flag;
        this.result = result;
    }

    @NotNull
    public String getFlag() {
        return this.flag;
    }

    public void setFlag(@NotNull String flag) {
        this.flag = flag;
    }

    @NotNull
    public Boolean getResult() {
        return this.result;
    }

    public void setResult(@NotNull Boolean result) {
        this.result = result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        @NotNull FeatureFlag otherFlag = (FeatureFlag)o;
        return Objects.equals(this.flag, otherFlag.flag) && Objects.equals(this.result, otherFlag.result);
    }

    public int hashCode() {
        return Objects.hash(this.flag, this.result);
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
        writer.name("flag").value(this.flag);
        writer.name("result").value(this.result);
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
            }
        }
        writer.endObject();
    }

    public static final class JsonKeys {
        public static final String FLAG = "flag";
        public static final String RESULT = "result";
    }

    public static final class Deserializer
    implements JsonDeserializer<FeatureFlag> {
        @Override
        @NotNull
        public FeatureFlag deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            IllegalStateException exception;
            String message;
            reader.beginObject();
            @Nullable String flag = null;
            Boolean result = null;
            ConcurrentHashMap<String, Object> unknown = null;
            block8: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "flag": {
                        flag = reader.nextStringOrNull();
                        continue block8;
                    }
                    case "result": {
                        result = reader.nextBooleanOrNull();
                        continue block8;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            if (flag == null) {
                message = "Missing required field \"flag\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            if (result == null) {
                message = "Missing required field \"result\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            FeatureFlag app = new FeatureFlag(flag, result);
            app.setUnknown(unknown);
            reader.endObject();
            return app;
        }
    }
}

