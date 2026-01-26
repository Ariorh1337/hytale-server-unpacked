/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import io.sentry.protocol.SentryId;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProfileContext
implements JsonUnknown,
JsonSerializable {
    public static final String TYPE = "profile";
    @NotNull
    private SentryId profilerId;
    @Nullable
    private Map<String, Object> unknown;

    public ProfileContext() {
        this(SentryId.EMPTY_ID);
    }

    public ProfileContext(@NotNull SentryId profilerId) {
        this.profilerId = profilerId;
    }

    public ProfileContext(@NotNull ProfileContext profileContext) {
        this.profilerId = profileContext.profilerId;
        Map<String, Object> copiedUnknown = CollectionUtils.newConcurrentHashMap(profileContext.unknown);
        if (copiedUnknown != null) {
            this.unknown = copiedUnknown;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileContext)) {
            return false;
        }
        ProfileContext that = (ProfileContext)o;
        return this.profilerId.equals(that.profilerId);
    }

    public int hashCode() {
        return Objects.hash(this.profilerId);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("profiler_id").value(logger, this.profilerId);
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
            }
        }
        writer.endObject();
    }

    @NotNull
    public SentryId getProfilerId() {
        return this.profilerId;
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
        public static final String PROFILER_ID = "profiler_id";
    }

    public static final class Deserializer
    implements JsonDeserializer<ProfileContext> {
        @Override
        @NotNull
        public ProfileContext deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            ProfileContext data = new ProfileContext();
            ConcurrentHashMap<String, Object> unknown = null;
            block6: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "profiler_id": {
                        SentryId profilerId = reader.nextOrNull(logger, new SentryId.Deserializer());
                        if (profilerId == null) continue block6;
                        data.profilerId = profilerId;
                        continue block6;
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
}

