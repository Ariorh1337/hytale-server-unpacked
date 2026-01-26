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
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryLockReason
implements JsonUnknown,
JsonSerializable {
    public static final int LOCKED = 1;
    public static final int WAITING = 2;
    public static final int SLEEPING = 4;
    public static final int BLOCKED = 8;
    public static final int ANY = 15;
    private int type;
    @Nullable
    private String address;
    @Nullable
    private String packageName;
    @Nullable
    private String className;
    @Nullable
    private Long threadId;
    @Nullable
    private Map<String, Object> unknown;

    public SentryLockReason() {
    }

    public SentryLockReason(@NotNull SentryLockReason other) {
        this.type = other.type;
        this.address = other.address;
        this.packageName = other.packageName;
        this.className = other.className;
        this.threadId = other.threadId;
        this.unknown = CollectionUtils.newConcurrentHashMap(other.unknown);
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Nullable
    public String getAddress() {
        return this.address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(@Nullable String packageName) {
        this.packageName = packageName;
    }

    @Nullable
    public String getClassName() {
        return this.className;
    }

    public void setClassName(@Nullable String className) {
        this.className = className;
    }

    @Nullable
    public Long getThreadId() {
        return this.threadId;
    }

    public void setThreadId(@Nullable Long threadId) {
        this.threadId = threadId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SentryLockReason that = (SentryLockReason)o;
        return Objects.equals(this.address, that.address);
    }

    public int hashCode() {
        return Objects.hash(this.address);
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
        writer.name("type").value(this.type);
        if (this.address != null) {
            writer.name("address").value(this.address);
        }
        if (this.packageName != null) {
            writer.name("package_name").value(this.packageName);
        }
        if (this.className != null) {
            writer.name("class_name").value(this.className);
        }
        if (this.threadId != null) {
            writer.name("thread_id").value(this.threadId);
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
        public static final String TYPE = "type";
        public static final String ADDRESS = "address";
        public static final String PACKAGE_NAME = "package_name";
        public static final String CLASS_NAME = "class_name";
        public static final String THREAD_ID = "thread_id";
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryLockReason> {
        @Override
        @NotNull
        public SentryLockReason deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            SentryLockReason sentryLockReason = new SentryLockReason();
            ConcurrentHashMap<String, Object> unknown = null;
            reader.beginObject();
            block14: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "type": {
                        sentryLockReason.type = reader.nextInt();
                        continue block14;
                    }
                    case "address": {
                        sentryLockReason.address = reader.nextStringOrNull();
                        continue block14;
                    }
                    case "package_name": {
                        sentryLockReason.packageName = reader.nextStringOrNull();
                        continue block14;
                    }
                    case "class_name": {
                        sentryLockReason.className = reader.nextStringOrNull();
                        continue block14;
                    }
                    case "thread_id": {
                        sentryLockReason.threadId = reader.nextLongOrNull();
                        continue block14;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            sentryLockReason.setUnknown(unknown);
            reader.endObject();
            return sentryLockReason;
        }
    }
}

