/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.CollectionUtils;
import io.sentry.util.HttpUtils;
import io.sentry.util.Objects;
import io.sentry.util.UrlUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Breadcrumb
implements JsonUnknown,
JsonSerializable,
Comparable<Breadcrumb> {
    @Nullable
    private final Long timestampMs;
    @Nullable
    private Date timestamp;
    @NotNull
    private final Long nanos;
    @Nullable
    private String message;
    @Nullable
    private String type;
    @NotNull
    private @NotNull Map<String, @NotNull Object> data = new ConcurrentHashMap<String, Object>();
    @Nullable
    private String category;
    @Nullable
    private String origin;
    @Nullable
    private SentryLevel level;
    @Nullable
    private Map<String, Object> unknown;

    public Breadcrumb(@NotNull Date timestamp) {
        this.nanos = System.nanoTime();
        this.timestamp = timestamp;
        this.timestampMs = null;
    }

    public Breadcrumb(long timestamp) {
        this.nanos = System.nanoTime();
        this.timestampMs = timestamp;
        this.timestamp = null;
    }

    Breadcrumb(@NotNull Breadcrumb breadcrumb) {
        this.nanos = System.nanoTime();
        this.timestamp = breadcrumb.timestamp;
        this.timestampMs = breadcrumb.timestampMs;
        this.message = breadcrumb.message;
        this.type = breadcrumb.type;
        this.category = breadcrumb.category;
        this.origin = breadcrumb.origin;
        Map<String, Object> dataClone = CollectionUtils.newConcurrentHashMap(breadcrumb.data);
        if (dataClone != null) {
            this.data = dataClone;
        }
        this.unknown = CollectionUtils.newConcurrentHashMap(breadcrumb.unknown);
        this.level = breadcrumb.level;
    }

    public static Breadcrumb fromMap(@NotNull Map<String, Object> map, @NotNull SentryOptions options) {
        @NotNull Date timestamp = DateUtils.getCurrentDateTime();
        String message = null;
        String type = null;
        @NotNull ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<String, Object>();
        String category = null;
        String origin = null;
        SentryLevel level = null;
        ConcurrentHashMap<String, Object> unknown = null;
        block20: for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            switch (entry.getKey()) {
                case "timestamp": {
                    Date deserializedDate;
                    if (!(value instanceof String) || (deserializedDate = ObjectReader.dateOrNull((String)value, options.getLogger())) == null) continue block20;
                    timestamp = deserializedDate;
                    break;
                }
                case "message": {
                    message = value instanceof String ? (String)value : null;
                    break;
                }
                case "type": {
                    type = value instanceof String ? (String)value : null;
                    break;
                }
                case "data": {
                    Map untypedData = value instanceof Map ? (Map)value : null;
                    if (untypedData == null) continue block20;
                    for (Map.Entry dataEntry : untypedData.entrySet()) {
                        if (dataEntry.getKey() instanceof String && dataEntry.getValue() != null) {
                            data.put((String)dataEntry.getKey(), dataEntry.getValue());
                            continue;
                        }
                        options.getLogger().log(SentryLevel.WARNING, "Invalid key or null value in data map.", new Object[0]);
                    }
                    continue block20;
                }
                case "category": {
                    category = value instanceof String ? (String)value : null;
                    break;
                }
                case "origin": {
                    origin = value instanceof String ? (String)value : null;
                    break;
                }
                case "level": {
                    String levelString = value instanceof String ? (String)value : null;
                    if (levelString == null) continue block20;
                    try {
                        level = SentryLevel.valueOf(levelString.toUpperCase(Locale.ROOT));
                    }
                    catch (Exception exception) {}
                    break;
                }
                default: {
                    if (unknown == null) {
                        unknown = new ConcurrentHashMap<String, Object>();
                    }
                    unknown.put(entry.getKey(), entry.getValue());
                }
            }
        }
        Breadcrumb breadcrumb = new Breadcrumb(timestamp);
        breadcrumb.message = message;
        breadcrumb.type = type;
        breadcrumb.data = data;
        breadcrumb.category = category;
        breadcrumb.origin = origin;
        breadcrumb.level = level;
        breadcrumb.setUnknown(unknown);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb http(@NotNull String url, @NotNull String method) {
        Breadcrumb breadcrumb = new Breadcrumb();
        @NotNull UrlUtils.UrlDetails urlDetails = UrlUtils.parse(url);
        breadcrumb.setType("http");
        breadcrumb.setCategory("http");
        if (urlDetails.getUrl() != null) {
            breadcrumb.setData("url", urlDetails.getUrl());
        }
        breadcrumb.setData("method", method.toUpperCase(Locale.ROOT));
        if (urlDetails.getQuery() != null) {
            breadcrumb.setData("http.query", urlDetails.getQuery());
        }
        if (urlDetails.getFragment() != null) {
            breadcrumb.setData("http.fragment", urlDetails.getFragment());
        }
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb http(@NotNull String url, @NotNull String method, @Nullable Integer code) {
        Breadcrumb breadcrumb = Breadcrumb.http(url, method);
        if (code != null) {
            breadcrumb.setData("status_code", code);
            breadcrumb.setLevel(Breadcrumb.levelFromHttpStatusCode(code));
        }
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb graphqlOperation(@Nullable String operationName, @Nullable String operationType, @Nullable String operationId) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("graphql");
        if (operationName != null) {
            breadcrumb.setData("operation_name", operationName);
        }
        if (operationType != null) {
            breadcrumb.setData("operation_type", operationType);
            breadcrumb.setCategory(operationType);
        } else {
            breadcrumb.setCategory("graphql.operation");
        }
        if (operationId != null) {
            breadcrumb.setData("operation_id", operationId);
        }
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb graphqlDataFetcher(@Nullable String path, @Nullable String field, @Nullable String type, @Nullable String objectType) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("graphql");
        breadcrumb.setCategory("graphql.fetcher");
        if (path != null) {
            breadcrumb.setData("path", path);
        }
        if (field != null) {
            breadcrumb.setData("field", field);
        }
        if (type != null) {
            breadcrumb.setData("type", type);
        }
        if (objectType != null) {
            breadcrumb.setData("object_type", objectType);
        }
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb graphqlDataLoader(@NotNull Iterable<?> keys, @Nullable Class<?> keyType, @Nullable Class<?> valueType, @Nullable String name) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("graphql");
        breadcrumb.setCategory("graphql.data_loader");
        ArrayList<String> serializedKeys = new ArrayList<String>();
        for (Object key : keys) {
            serializedKeys.add(key.toString());
        }
        breadcrumb.setData("keys", serializedKeys);
        if (keyType != null) {
            breadcrumb.setData("key_type", keyType.getName());
        }
        if (valueType != null) {
            breadcrumb.setData("value_type", valueType.getName());
        }
        if (name != null) {
            breadcrumb.setData("name", name);
        }
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb navigation(@NotNull String from, @NotNull String to) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setCategory("navigation");
        breadcrumb.setType("navigation");
        breadcrumb.setData("from", from);
        breadcrumb.setData("to", to);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb transaction(@NotNull String message) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("default");
        breadcrumb.setCategory("sentry.transaction");
        breadcrumb.setMessage(message);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb debug(@NotNull String message) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("debug");
        breadcrumb.setMessage(message);
        breadcrumb.setLevel(SentryLevel.DEBUG);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb error(@NotNull String message) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("error");
        breadcrumb.setMessage(message);
        breadcrumb.setLevel(SentryLevel.ERROR);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb info(@NotNull String message) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("info");
        breadcrumb.setMessage(message);
        breadcrumb.setLevel(SentryLevel.INFO);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb query(@NotNull String message) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("query");
        breadcrumb.setMessage(message);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb ui(@NotNull String category, @NotNull String message) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("default");
        breadcrumb.setCategory("ui." + category);
        breadcrumb.setMessage(message);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb user(@NotNull String category, @NotNull String message) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("user");
        breadcrumb.setCategory(category);
        breadcrumb.setMessage(message);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb userInteraction(@NotNull String subCategory, @Nullable String viewId, @Nullable String viewClass) {
        return Breadcrumb.userInteraction(subCategory, viewId, viewClass, Collections.emptyMap());
    }

    @NotNull
    public static Breadcrumb userInteraction(@NotNull String subCategory, @Nullable String viewId, @Nullable String viewClass, @Nullable String viewTag, @NotNull Map<String, Object> additionalData) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setType("user");
        breadcrumb.setCategory("ui." + subCategory);
        if (viewId != null) {
            breadcrumb.setData("view.id", viewId);
        }
        if (viewClass != null) {
            breadcrumb.setData("view.class", viewClass);
        }
        if (viewTag != null) {
            breadcrumb.setData("view.tag", viewTag);
        }
        for (Map.Entry<String, Object> entry : additionalData.entrySet()) {
            breadcrumb.getData().put(entry.getKey(), entry.getValue());
        }
        breadcrumb.setLevel(SentryLevel.INFO);
        return breadcrumb;
    }

    @NotNull
    public static Breadcrumb userInteraction(@NotNull String subCategory, @Nullable String viewId, @Nullable String viewClass, @NotNull Map<String, Object> additionalData) {
        return Breadcrumb.userInteraction(subCategory, viewId, viewClass, null, additionalData);
    }

    @Nullable
    private static SentryLevel levelFromHttpStatusCode(@NotNull Integer code) {
        if (HttpUtils.isHttpClientError(code)) {
            return SentryLevel.WARNING;
        }
        if (HttpUtils.isHttpServerError(code)) {
            return SentryLevel.ERROR;
        }
        return null;
    }

    public Breadcrumb() {
        this(System.currentTimeMillis());
    }

    public Breadcrumb(@Nullable String message) {
        this();
        this.message = message;
    }

    @NotNull
    public Date getTimestamp() {
        if (this.timestamp != null) {
            return (Date)this.timestamp.clone();
        }
        if (this.timestampMs != null) {
            this.timestamp = DateUtils.getDateTime(this.timestampMs);
            return this.timestamp;
        }
        throw new IllegalStateException("No timestamp set for breadcrumb");
    }

    @Nullable
    public String getMessage() {
        return this.message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Nullable
    public String getType() {
        return this.type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @ApiStatus.Internal
    @NotNull
    public Map<String, Object> getData() {
        return this.data;
    }

    @Nullable
    public Object getData(@Nullable String key) {
        if (key == null) {
            return null;
        }
        return this.data.get(key);
    }

    public void setData(@Nullable String key, @Nullable Object value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.removeData(key);
        } else {
            this.data.put(key, value);
        }
    }

    public void removeData(@Nullable String key) {
        if (key == null) {
            return;
        }
        this.data.remove(key);
    }

    @Nullable
    public String getCategory() {
        return this.category;
    }

    public void setCategory(@Nullable String category) {
        this.category = category;
    }

    @Nullable
    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(@Nullable String origin) {
        this.origin = origin;
    }

    @Nullable
    public SentryLevel getLevel() {
        return this.level;
    }

    public void setLevel(@Nullable SentryLevel level) {
        this.level = level;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Breadcrumb that = (Breadcrumb)o;
        if ("http".equals(this.type)) {
            return Breadcrumb.httpBreadcrumbEquals(this, that);
        }
        return Breadcrumb.breadcrumbEquals(this, that);
    }

    public int hashCode() {
        if ("http".equals(this.type)) {
            return Breadcrumb.httpBreadcrumbHashCode(this);
        }
        return Breadcrumb.breadcrumbHashCode(this);
    }

    private static boolean breadcrumbEquals(@NotNull Breadcrumb a, @NotNull Breadcrumb b) {
        return a.getTimestamp().getTime() == b.getTimestamp().getTime() && Objects.equals(a.message, b.message) && Objects.equals(a.type, b.type) && Objects.equals(a.category, b.category) && Objects.equals(a.origin, b.origin) && a.level == b.level;
    }

    private static boolean httpBreadcrumbEquals(@NotNull Breadcrumb a, @NotNull Breadcrumb b) {
        return Breadcrumb.breadcrumbEquals(a, b) && Objects.equals(a.getData("status_code"), b.getData("status_code")) && Objects.equals(a.getData("url"), b.getData("url")) && Objects.equals(a.getData("method"), b.getData("method")) && Objects.equals(a.getData("http.fragment"), b.getData("http.fragment")) && Objects.equals(a.getData("http.query"), b.getData("http.query"));
    }

    private static int breadcrumbHashCode(@NotNull Breadcrumb breadcrumb) {
        return Objects.hash(breadcrumb.getTimestamp().getTime(), breadcrumb.message, breadcrumb.type, breadcrumb.category, breadcrumb.origin, breadcrumb.level);
    }

    private static int httpBreadcrumbHashCode(@NotNull Breadcrumb breadcrumb) {
        return Objects.hash(breadcrumb.getTimestamp().getTime(), breadcrumb.message, breadcrumb.type, breadcrumb.category, breadcrumb.origin, breadcrumb.level, breadcrumb.getData("status_code"), breadcrumb.getData("url"), breadcrumb.getData("method"), breadcrumb.getData("http.fragment"), breadcrumb.getData("http.query"));
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
    public int compareTo(@NotNull Breadcrumb o) {
        return this.nanos.compareTo(o.nanos);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("timestamp").value(logger, this.getTimestamp());
        if (this.message != null) {
            writer.name("message").value(this.message);
        }
        if (this.type != null) {
            writer.name("type").value(this.type);
        }
        writer.name("data").value(logger, this.data);
        if (this.category != null) {
            writer.name("category").value(this.category);
        }
        if (this.origin != null) {
            writer.name("origin").value(this.origin);
        }
        if (this.level != null) {
            writer.name("level").value(logger, this.level);
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
        public static final String TIMESTAMP = "timestamp";
        public static final String MESSAGE = "message";
        public static final String TYPE = "type";
        public static final String DATA = "data";
        public static final String CATEGORY = "category";
        public static final String ORIGIN = "origin";
        public static final String LEVEL = "level";
    }

    public static final class Deserializer
    implements JsonDeserializer<Breadcrumb> {
        @Override
        @NotNull
        public Breadcrumb deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            @NotNull Date timestamp = DateUtils.getCurrentDateTime();
            String message = null;
            String type = null;
            @NotNull Map<K, V> data = new ConcurrentHashMap();
            String category = null;
            String origin = null;
            SentryLevel level = null;
            ConcurrentHashMap<String, Object> unknown = null;
            block20: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "timestamp": {
                        Date deserializedDate = reader.nextDateOrNull(logger);
                        if (deserializedDate == null) continue block20;
                        timestamp = deserializedDate;
                        continue block20;
                    }
                    case "message": {
                        message = reader.nextStringOrNull();
                        continue block20;
                    }
                    case "type": {
                        type = reader.nextStringOrNull();
                        continue block20;
                    }
                    case "data": {
                        Map deserializedData = CollectionUtils.newConcurrentHashMap((Map)reader.nextObjectOrNull());
                        if (deserializedData == null) continue block20;
                        data = deserializedData;
                        continue block20;
                    }
                    case "category": {
                        category = reader.nextStringOrNull();
                        continue block20;
                    }
                    case "origin": {
                        origin = reader.nextStringOrNull();
                        continue block20;
                    }
                    case "level": {
                        try {
                            level = new SentryLevel.Deserializer().deserialize(reader, logger);
                        }
                        catch (Exception exception) {
                            logger.log(SentryLevel.ERROR, exception, "Error when deserializing SentryLevel", new Object[0]);
                        }
                        continue block20;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            Breadcrumb breadcrumb = new Breadcrumb(timestamp);
            breadcrumb.message = message;
            breadcrumb.type = type;
            breadcrumb.data = data;
            breadcrumb.category = category;
            breadcrumb.origin = origin;
            breadcrumb.level = level;
            breadcrumb.setUnknown(unknown);
            reader.endObject();
            return breadcrumb;
        }
    }
}

