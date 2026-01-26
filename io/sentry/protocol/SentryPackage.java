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
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryPackage
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private String name;
    @NotNull
    private String version;
    @Nullable
    private Map<String, Object> unknown;

    public SentryPackage(@NotNull String name, @NotNull String version) {
        this.name = io.sentry.util.Objects.requireNonNull(name, "name is required.");
        this.version = io.sentry.util.Objects.requireNonNull(version, "version is required.");
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = io.sentry.util.Objects.requireNonNull(name, "name is required.");
    }

    @NotNull
    public String getVersion() {
        return this.version;
    }

    public void setVersion(@NotNull String version) {
        this.version = io.sentry.util.Objects.requireNonNull(version, "version is required.");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SentryPackage that = (SentryPackage)o;
        return Objects.equals(this.name, that.name) && Objects.equals(this.version, that.version);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.version);
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
        writer.name("name").value(this.name);
        writer.name("version").value(this.version);
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
            }
        }
        writer.endObject();
    }

    public static final class JsonKeys {
        public static final String NAME = "name";
        public static final String VERSION = "version";
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryPackage> {
        @Override
        @NotNull
        public SentryPackage deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            IllegalStateException exception;
            String message;
            String name = null;
            String version = null;
            HashMap<String, Object> unknown = null;
            reader.beginObject();
            block8: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "name": {
                        name = reader.nextString();
                        continue block8;
                    }
                    case "version": {
                        version = reader.nextString();
                        continue block8;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            reader.endObject();
            if (name == null) {
                message = "Missing required field \"name\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            if (version == null) {
                message = "Missing required field \"version\"";
                exception = new IllegalStateException(message);
                logger.log(SentryLevel.ERROR, message, exception);
                throw exception;
            }
            SentryPackage sentryPackage = new SentryPackage(name, version);
            sentryPackage.setUnknown(unknown);
            return sentryPackage;
        }
    }
}

