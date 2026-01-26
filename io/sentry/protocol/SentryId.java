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
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryUUID;
import io.sentry.util.LazyEvaluator;
import io.sentry.util.StringUtils;
import io.sentry.util.UUIDStringUtils;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryId
implements JsonSerializable {
    public static final SentryId EMPTY_ID = new SentryId("00000000-0000-0000-0000-000000000000".replace("-", ""));
    @NotNull
    private final LazyEvaluator<String> lazyStringValue;

    public SentryId() {
        this((UUID)null);
    }

    public SentryId(@Nullable UUID uuid) {
        this.lazyStringValue = uuid != null ? new LazyEvaluator<String>(() -> this.normalize(UUIDStringUtils.toSentryIdString(uuid))) : new LazyEvaluator<String>(SentryUUID::generateSentryId);
    }

    public SentryId(@NotNull String sentryIdString) {
        @NotNull String normalized = StringUtils.normalizeUUID(sentryIdString);
        if (normalized.length() != 32 && normalized.length() != 36) {
            throw new IllegalArgumentException("String representation of SentryId has either 32 (UUID no dashes) or 36 characters long (completed UUID). Received: " + sentryIdString);
        }
        this.lazyStringValue = normalized.length() == 36 ? new LazyEvaluator<String>(() -> this.normalize(normalized)) : new LazyEvaluator<String>(() -> normalized);
    }

    public String toString() {
        return this.lazyStringValue.getValue();
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SentryId sentryId = (SentryId)o;
        return this.lazyStringValue.getValue().equals(sentryId.lazyStringValue.getValue());
    }

    public int hashCode() {
        return this.lazyStringValue.getValue().hashCode();
    }

    @NotNull
    private String normalize(@NotNull String uuidString) {
        return StringUtils.normalizeUUID(uuidString).replace("-", "");
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.value(this.toString());
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryId> {
        @Override
        @NotNull
        public SentryId deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return new SentryId(reader.nextString());
        }
    }
}

