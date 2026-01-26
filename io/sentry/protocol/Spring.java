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
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Spring
implements JsonUnknown,
JsonSerializable {
    public static final String TYPE = "spring";
    @Nullable
    private String[] activeProfiles;
    private @Nullable Map<String, @NotNull Object> unknown;

    public Spring() {
    }

    public Spring(@NotNull Spring spring) {
        this.activeProfiles = spring.activeProfiles;
        this.unknown = CollectionUtils.newConcurrentHashMap(spring.unknown);
    }

    @Nullable
    public String[] getActiveProfiles() {
        return this.activeProfiles;
    }

    public void setActiveProfiles(@Nullable String[] activeProfiles) {
        this.activeProfiles = activeProfiles;
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Spring spring = (Spring)o;
        return Arrays.equals(this.activeProfiles, spring.activeProfiles);
    }

    public int hashCode() {
        return Arrays.hashCode(this.activeProfiles);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        if (this.activeProfiles != null) {
            writer.name("active_profiles").value(logger, this.activeProfiles);
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

    static /* synthetic */ String[] access$002(Spring x0, String[] x1) {
        x0.activeProfiles = x1;
        return x1;
    }

    public static final class JsonKeys {
        public static final String ACTIVE_PROFILES = "active_profiles";
    }

    public static final class Deserializer
    implements JsonDeserializer<Spring> {
        @Override
        @NotNull
        public Spring deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            Spring spring = new Spring();
            ConcurrentHashMap<String, Object> unknown = null;
            block6: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "active_profiles": {
                        List activeProfilesList = (List)reader.nextObjectOrNull();
                        if (activeProfilesList == null) continue block6;
                        String[] activeProfiles = new String[activeProfilesList.size()];
                        activeProfilesList.toArray(activeProfiles);
                        Spring.access$002(spring, activeProfiles);
                        continue block6;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            spring.setUnknown(unknown);
            reader.endObject();
            return spring;
        }
    }
}

