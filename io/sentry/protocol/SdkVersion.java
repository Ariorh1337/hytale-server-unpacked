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
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryLevel;
import io.sentry.protocol.SentryPackage;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SdkVersion
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private String name;
    @NotNull
    private String version;
    @Nullable
    private Set<SentryPackage> deserializedPackages;
    @Nullable
    private Set<String> deserializedIntegrations;
    @Nullable
    private Map<String, Object> unknown;

    public SdkVersion(@NotNull String name, @NotNull String version) {
        this.name = Objects.requireNonNull(name, "name is required.");
        this.version = Objects.requireNonNull(version, "version is required.");
    }

    @NotNull
    public String getVersion() {
        return this.version;
    }

    public void setVersion(@NotNull String version) {
        this.version = Objects.requireNonNull(version, "version is required.");
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Objects.requireNonNull(name, "name is required.");
    }

    public void addPackage(@NotNull String name, @NotNull String version) {
        SentryIntegrationPackageStorage.getInstance().addPackage(name, version);
    }

    public void addIntegration(@NotNull String integration) {
        SentryIntegrationPackageStorage.getInstance().addIntegration(integration);
    }

    @NotNull
    public Set<SentryPackage> getPackageSet() {
        return this.deserializedPackages != null ? this.deserializedPackages : SentryIntegrationPackageStorage.getInstance().getPackages();
    }

    @NotNull
    public Set<String> getIntegrationSet() {
        return this.deserializedIntegrations != null ? this.deserializedIntegrations : SentryIntegrationPackageStorage.getInstance().getIntegrations();
    }

    @NotNull
    public static SdkVersion updateSdkVersion(@Nullable SdkVersion sdk, @NotNull String name, @NotNull String version) {
        Objects.requireNonNull(name, "name is required.");
        Objects.requireNonNull(version, "version is required.");
        if (sdk == null) {
            sdk = new SdkVersion(name, version);
        } else {
            sdk.setName(name);
            sdk.setVersion(version);
        }
        return sdk;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SdkVersion that = (SdkVersion)o;
        return this.name.equals(that.name) && this.version.equals(that.version);
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
        Set<SentryPackage> packages = this.getPackageSet();
        Set<String> integrations = this.getIntegrationSet();
        if (!packages.isEmpty()) {
            writer.name("packages").value(logger, packages);
        }
        if (!integrations.isEmpty()) {
            writer.name("integrations").value(logger, integrations);
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
        public static final String NAME = "name";
        public static final String VERSION = "version";
        public static final String PACKAGES = "packages";
        public static final String INTEGRATIONS = "integrations";
    }

    public static final class Deserializer
    implements JsonDeserializer<SdkVersion> {
        @Override
        @NotNull
        public SdkVersion deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            IllegalStateException exception;
            String message;
            String name = null;
            String version = null;
            ArrayList<SentryPackage> packages = new ArrayList<SentryPackage>();
            ArrayList integrations = new ArrayList();
            HashMap<String, Object> unknown = null;
            reader.beginObject();
            block12: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "name": {
                        name = reader.nextString();
                        continue block12;
                    }
                    case "version": {
                        version = reader.nextString();
                        continue block12;
                    }
                    case "packages": {
                        List<SentryPackage> deserializedPackages = reader.nextListOrNull(logger, new SentryPackage.Deserializer());
                        if (deserializedPackages == null) continue block12;
                        packages.addAll(deserializedPackages);
                        continue block12;
                    }
                    case "integrations": {
                        List deserializedIntegrations = (List)reader.nextObjectOrNull();
                        if (deserializedIntegrations == null) continue block12;
                        integrations.addAll(deserializedIntegrations);
                        continue block12;
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
            SdkVersion sdkVersion = new SdkVersion(name, version);
            sdkVersion.deserializedPackages = new CopyOnWriteArraySet(packages);
            sdkVersion.deserializedIntegrations = new CopyOnWriteArraySet(integrations);
            sdkVersion.setUnknown(unknown);
            return sdkVersion;
        }
    }
}

