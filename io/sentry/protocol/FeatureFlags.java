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
import io.sentry.protocol.FeatureFlag;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FeatureFlags
implements JsonUnknown,
JsonSerializable {
    public static final String TYPE = "flags";
    @NotNull
    private List<FeatureFlag> values;
    private @Nullable Map<String, @NotNull Object> unknown;

    public FeatureFlags() {
        this.values = new ArrayList<FeatureFlag>();
    }

    FeatureFlags(@NotNull FeatureFlags featureFlags) {
        this.values = featureFlags.values;
        this.unknown = CollectionUtils.newConcurrentHashMap(featureFlags.unknown);
    }

    public FeatureFlags(@NotNull List<FeatureFlag> values) {
        this.values = values;
    }

    @NotNull
    public List<FeatureFlag> getValues() {
        return this.values;
    }

    public void setValues(@NotNull List<FeatureFlag> values) {
        this.values = values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FeatureFlags flags = (FeatureFlags)o;
        return Objects.equals(this.values, flags.values);
    }

    public int hashCode() {
        return Objects.hash(this.values);
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
        writer.name("values").value(logger, this.values);
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
            }
        }
        writer.endObject();
    }

    public static final class JsonKeys {
        public static final String VALUES = "values";
    }

    public static final class Deserializer
    implements JsonDeserializer<FeatureFlags> {
        @Override
        @NotNull
        public FeatureFlags deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            @Nullable List<FeatureFlag> values = null;
            ConcurrentHashMap<String, Object> unknown = null;
            block6: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "values": {
                        values = reader.nextListOrNull(logger, new FeatureFlag.Deserializer());
                        continue block6;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            if (values == null) {
                values = new ArrayList<FeatureFlag>();
            }
            FeatureFlags flags = new FeatureFlags(values);
            flags.setUnknown(unknown);
            reader.endObject();
            return flags;
        }
    }
}

