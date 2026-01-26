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
import io.sentry.SentryLockReason;
import io.sentry.protocol.SentryStackTrace;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryThread
implements JsonUnknown,
JsonSerializable {
    @Nullable
    private Long id;
    @Nullable
    private Integer priority;
    @Nullable
    private String name;
    @Nullable
    private String state;
    @Nullable
    private Boolean crashed;
    @Nullable
    private Boolean current;
    @Nullable
    private Boolean daemon;
    @Nullable
    private Boolean main;
    @Nullable
    private SentryStackTrace stacktrace;
    @Nullable
    private Map<String, SentryLockReason> heldLocks;
    @Nullable
    private Map<String, Object> unknown;

    @Nullable
    public Long getId() {
        return this.id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public Boolean isCrashed() {
        return this.crashed;
    }

    public void setCrashed(@Nullable Boolean crashed) {
        this.crashed = crashed;
    }

    @Nullable
    public Boolean isCurrent() {
        return this.current;
    }

    public void setCurrent(@Nullable Boolean current) {
        this.current = current;
    }

    @Nullable
    public SentryStackTrace getStacktrace() {
        return this.stacktrace;
    }

    public void setStacktrace(@Nullable SentryStackTrace stacktrace) {
        this.stacktrace = stacktrace;
    }

    @Nullable
    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(@Nullable Integer priority) {
        this.priority = priority;
    }

    @Nullable
    public Boolean isDaemon() {
        return this.daemon;
    }

    public void setDaemon(@Nullable Boolean daemon) {
        this.daemon = daemon;
    }

    @Nullable
    public Boolean isMain() {
        return this.main;
    }

    public void setMain(@Nullable Boolean main) {
        this.main = main;
    }

    @Nullable
    public String getState() {
        return this.state;
    }

    public void setState(@Nullable String state) {
        this.state = state;
    }

    @Nullable
    public Map<String, SentryLockReason> getHeldLocks() {
        return this.heldLocks;
    }

    public void setHeldLocks(@Nullable Map<String, SentryLockReason> heldLocks) {
        this.heldLocks = heldLocks;
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
        if (this.id != null) {
            writer.name("id").value(this.id);
        }
        if (this.priority != null) {
            writer.name("priority").value(this.priority);
        }
        if (this.name != null) {
            writer.name("name").value(this.name);
        }
        if (this.state != null) {
            writer.name("state").value(this.state);
        }
        if (this.crashed != null) {
            writer.name("crashed").value(this.crashed);
        }
        if (this.current != null) {
            writer.name("current").value(this.current);
        }
        if (this.daemon != null) {
            writer.name("daemon").value(this.daemon);
        }
        if (this.main != null) {
            writer.name("main").value(this.main);
        }
        if (this.stacktrace != null) {
            writer.name("stacktrace").value(logger, this.stacktrace);
        }
        if (this.heldLocks != null) {
            writer.name("held_locks").value(logger, this.heldLocks);
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
        public static final String ID = "id";
        public static final String PRIORITY = "priority";
        public static final String NAME = "name";
        public static final String STATE = "state";
        public static final String CRASHED = "crashed";
        public static final String CURRENT = "current";
        public static final String DAEMON = "daemon";
        public static final String MAIN = "main";
        public static final String STACKTRACE = "stacktrace";
        public static final String HELD_LOCKS = "held_locks";
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryThread> {
        @Override
        @NotNull
        public SentryThread deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            SentryThread sentryThread = new SentryThread();
            ConcurrentHashMap<String, Object> unknown = null;
            reader.beginObject();
            block24: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "id": {
                        sentryThread.id = reader.nextLongOrNull();
                        continue block24;
                    }
                    case "priority": {
                        sentryThread.priority = reader.nextIntegerOrNull();
                        continue block24;
                    }
                    case "name": {
                        sentryThread.name = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "state": {
                        sentryThread.state = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "crashed": {
                        sentryThread.crashed = reader.nextBooleanOrNull();
                        continue block24;
                    }
                    case "current": {
                        sentryThread.current = reader.nextBooleanOrNull();
                        continue block24;
                    }
                    case "daemon": {
                        sentryThread.daemon = reader.nextBooleanOrNull();
                        continue block24;
                    }
                    case "main": {
                        sentryThread.main = reader.nextBooleanOrNull();
                        continue block24;
                    }
                    case "stacktrace": {
                        sentryThread.stacktrace = reader.nextOrNull(logger, new SentryStackTrace.Deserializer());
                        continue block24;
                    }
                    case "held_locks": {
                        Map<String, SentryLockReason> heldLocks = reader.nextMapOrNull(logger, new SentryLockReason.Deserializer());
                        if (heldLocks == null) continue block24;
                        sentryThread.heldLocks = new HashMap<String, SentryLockReason>(heldLocks);
                        continue block24;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            sentryThread.setUnknown(unknown);
            reader.endObject();
            return sentryThread;
        }
    }
}

