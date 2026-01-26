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
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Mechanism
implements JsonUnknown,
JsonSerializable {
    @Nullable
    private final transient Thread thread;
    @Nullable
    private String type;
    @Nullable
    private String description;
    @Nullable
    private String helpLink;
    @Nullable
    private Boolean handled;
    @Nullable
    private Map<String, Object> meta;
    @Nullable
    private Map<String, Object> data;
    @Nullable
    private Boolean synthetic;
    @Nullable
    private Integer exceptionId;
    @Nullable
    private Integer parentId;
    @Nullable
    private Boolean exceptionGroup;
    @Nullable
    private Map<String, Object> unknown;

    public Mechanism() {
        this(null);
    }

    public Mechanism(@Nullable Thread thread) {
        this.thread = thread;
    }

    @Nullable
    public String getType() {
        return this.type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getHelpLink() {
        return this.helpLink;
    }

    public void setHelpLink(@Nullable String helpLink) {
        this.helpLink = helpLink;
    }

    @Nullable
    public Boolean isHandled() {
        return this.handled;
    }

    public void setHandled(@Nullable Boolean handled) {
        this.handled = handled;
    }

    @Nullable
    public Map<String, Object> getMeta() {
        return this.meta;
    }

    public void setMeta(@Nullable Map<String, Object> meta) {
        this.meta = CollectionUtils.newHashMap(meta);
    }

    @Nullable
    public Map<String, Object> getData() {
        return this.data;
    }

    public void setData(@Nullable Map<String, Object> data) {
        this.data = CollectionUtils.newHashMap(data);
    }

    @Nullable
    Thread getThread() {
        return this.thread;
    }

    @Nullable
    public Boolean getSynthetic() {
        return this.synthetic;
    }

    public void setSynthetic(@Nullable Boolean synthetic) {
        this.synthetic = synthetic;
    }

    @Nullable
    public Integer getExceptionId() {
        return this.exceptionId;
    }

    public void setExceptionId(@Nullable Integer exceptionId) {
        this.exceptionId = exceptionId;
    }

    @Nullable
    public Integer getParentId() {
        return this.parentId;
    }

    public void setParentId(@Nullable Integer parentId) {
        this.parentId = parentId;
    }

    @Nullable
    public Boolean isExceptionGroup() {
        return this.exceptionGroup;
    }

    public void setExceptionGroup(@Nullable Boolean exceptionGroup) {
        this.exceptionGroup = exceptionGroup;
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
        if (this.type != null) {
            writer.name("type").value(this.type);
        }
        if (this.description != null) {
            writer.name("description").value(this.description);
        }
        if (this.helpLink != null) {
            writer.name("help_link").value(this.helpLink);
        }
        if (this.handled != null) {
            writer.name("handled").value(this.handled);
        }
        if (this.meta != null) {
            writer.name("meta").value(logger, this.meta);
        }
        if (this.data != null) {
            writer.name("data").value(logger, this.data);
        }
        if (this.synthetic != null) {
            writer.name("synthetic").value(this.synthetic);
        }
        if (this.exceptionId != null) {
            writer.name("exception_id").value(logger, this.exceptionId);
        }
        if (this.parentId != null) {
            writer.name("parent_id").value(logger, this.parentId);
        }
        if (this.exceptionGroup != null) {
            writer.name("is_exception_group").value(this.exceptionGroup);
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
        public static final String TYPE = "type";
        public static final String DESCRIPTION = "description";
        public static final String HELP_LINK = "help_link";
        public static final String HANDLED = "handled";
        public static final String META = "meta";
        public static final String DATA = "data";
        public static final String SYNTHETIC = "synthetic";
        public static final String EXCEPTION_ID = "exception_id";
        public static final String PARENT_ID = "parent_id";
        public static final String IS_EXCEPTION_GROUP = "is_exception_group";
    }

    public static final class Deserializer
    implements JsonDeserializer<Mechanism> {
        @Override
        @NotNull
        public Mechanism deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            Mechanism mechanism = new Mechanism();
            HashMap<String, Object> unknown = null;
            reader.beginObject();
            block24: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "type": {
                        mechanism.type = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "description": {
                        mechanism.description = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "help_link": {
                        mechanism.helpLink = reader.nextStringOrNull();
                        continue block24;
                    }
                    case "handled": {
                        mechanism.handled = reader.nextBooleanOrNull();
                        continue block24;
                    }
                    case "meta": {
                        mechanism.meta = CollectionUtils.newConcurrentHashMap((Map)reader.nextObjectOrNull());
                        continue block24;
                    }
                    case "data": {
                        mechanism.data = CollectionUtils.newConcurrentHashMap((Map)reader.nextObjectOrNull());
                        continue block24;
                    }
                    case "synthetic": {
                        mechanism.synthetic = reader.nextBooleanOrNull();
                        continue block24;
                    }
                    case "exception_id": {
                        mechanism.exceptionId = reader.nextIntegerOrNull();
                        continue block24;
                    }
                    case "parent_id": {
                        mechanism.parentId = reader.nextIntegerOrNull();
                        continue block24;
                    }
                    case "is_exception_group": {
                        mechanism.exceptionGroup = reader.nextBooleanOrNull();
                        continue block24;
                    }
                }
                if (unknown == null) {
                    unknown = new HashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            reader.endObject();
            mechanism.setUnknown(unknown);
            return mechanism;
        }
    }
}

