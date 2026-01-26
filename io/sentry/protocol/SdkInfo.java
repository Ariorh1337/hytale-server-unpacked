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
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SdkInfo
implements JsonUnknown,
JsonSerializable {
    @Nullable
    private String sdkName;
    @Nullable
    private Integer versionMajor;
    @Nullable
    private Integer versionMinor;
    @Nullable
    private Integer versionPatchlevel;
    @Nullable
    private Map<String, Object> unknown;

    @Nullable
    public String getSdkName() {
        return this.sdkName;
    }

    public void setSdkName(@Nullable String sdkName) {
        this.sdkName = sdkName;
    }

    @Nullable
    public Integer getVersionMajor() {
        return this.versionMajor;
    }

    public void setVersionMajor(@Nullable Integer versionMajor) {
        this.versionMajor = versionMajor;
    }

    @Nullable
    public Integer getVersionMinor() {
        return this.versionMinor;
    }

    public void setVersionMinor(@Nullable Integer versionMinor) {
        this.versionMinor = versionMinor;
    }

    @Nullable
    public Integer getVersionPatchlevel() {
        return this.versionPatchlevel;
    }

    public void setVersionPatchlevel(@Nullable Integer versionPatchlevel) {
        this.versionPatchlevel = versionPatchlevel;
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
        if (this.sdkName != null) {
            writer.name("sdk_name").value(this.sdkName);
        }
        if (this.versionMajor != null) {
            writer.name("version_major").value(this.versionMajor);
        }
        if (this.versionMinor != null) {
            writer.name("version_minor").value(this.versionMinor);
        }
        if (this.versionPatchlevel != null) {
            writer.name("version_patchlevel").value(this.versionPatchlevel);
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
        public static final String SDK_NAME = "sdk_name";
        public static final String VERSION_MAJOR = "version_major";
        public static final String VERSION_MINOR = "version_minor";
        public static final String VERSION_PATCHLEVEL = "version_patchlevel";
    }

    public static final class Deserializer
    implements JsonDeserializer<SdkInfo> {
        @Override
        @NotNull
        public SdkInfo deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            SdkInfo sdkInfo = new SdkInfo();
            HashMap<String, Object> unknown = null;
            reader.beginObject();
            block12: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "sdk_name": {
                        sdkInfo.sdkName = reader.nextStringOrNull();
                        continue block12;
                    }
                    case "version_major": {
                        sdkInfo.versionMajor = reader.nextIntegerOrNull();
                        continue block12;
                    }
                    case "version_minor": {
                        sdkInfo.versionMinor = reader.nextIntegerOrNull();
                        continue block12;
                    }
                    case "version_patchlevel": {
                        sdkInfo.versionPatchlevel = reader.nextIntegerOrNull();
                        continue block12;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            reader.endObject();
            sdkInfo.setUnknown(unknown);
            return sdkInfo;
        }
    }
}

