/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.ObjectReader;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MapObjectReader
implements ObjectReader {
    private final Deque<Map.Entry<String, Object>> stack = new ArrayDeque<Map.Entry<String, Object>>();

    public MapObjectReader(Map<String, Object> root) {
        this.stack.addLast(new AbstractMap.SimpleEntry<Object, Map<String, Object>>(null, root));
    }

    @Override
    public void nextUnknown(@NotNull ILogger logger, Map<String, Object> unknown, String name) {
        try {
            unknown.put(name, this.nextObjectOrNull());
        }
        catch (Exception exception) {
            logger.log(SentryLevel.ERROR, exception, "Error deserializing unknown key: %s", name);
        }
    }

    @Override
    @Nullable
    public <T> List<T> nextListOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws IOException {
        if (this.peek() == JsonToken.NULL) {
            this.nextNull();
            return null;
        }
        try {
            this.beginArray();
            ArrayList<T> list = new ArrayList<T>();
            if (this.hasNext()) {
                do {
                    try {
                        list.add(deserializer.deserialize(this, logger));
                    }
                    catch (Exception e) {
                        logger.log(SentryLevel.WARNING, "Failed to deserialize object in list.", e);
                    }
                } while (this.peek() == JsonToken.BEGIN_OBJECT);
            }
            this.endArray();
            return list;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    @Nullable
    public <T> Map<String, T> nextMapOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws IOException {
        if (this.peek() == JsonToken.NULL) {
            this.nextNull();
            return null;
        }
        try {
            this.beginObject();
            HashMap<String, T> map = new HashMap<String, T>();
            if (this.hasNext()) {
                do {
                    try {
                        String key = this.nextName();
                        map.put(key, deserializer.deserialize(this, logger));
                    }
                    catch (Exception e) {
                        logger.log(SentryLevel.WARNING, "Failed to deserialize object in map.", e);
                    }
                } while (this.peek() == JsonToken.BEGIN_OBJECT || this.peek() == JsonToken.NAME);
            }
            this.endObject();
            return map;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    @Nullable
    public <T> Map<String, List<T>> nextMapOfListOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws IOException {
        if (this.peek() == JsonToken.NULL) {
            this.nextNull();
            return null;
        }
        @NotNull HashMap<String, List<T>> result = new HashMap<String, List<T>>();
        try {
            this.beginObject();
            if (this.hasNext()) {
                do {
                    @NotNull String key = this.nextName();
                    @Nullable List<T> list = this.nextListOrNull(logger, deserializer);
                    if (list == null) continue;
                    result.put(key, list);
                } while (this.peek() == JsonToken.BEGIN_OBJECT || this.peek() == JsonToken.NAME);
            }
            this.endObject();
            return result;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    @Nullable
    public <T> T nextOrNull(@NotNull ILogger logger, @NotNull JsonDeserializer<T> deserializer) throws Exception {
        return this.nextValueOrNull(logger, deserializer);
    }

    @Override
    @Nullable
    public Date nextDateOrNull(@NotNull ILogger logger) throws IOException {
        String dateString = this.nextStringOrNull();
        return ObjectReader.dateOrNull(dateString, logger);
    }

    @Override
    @Nullable
    public TimeZone nextTimeZoneOrNull(@NotNull ILogger logger) throws IOException {
        String timeZoneId = this.nextStringOrNull();
        return timeZoneId != null ? TimeZone.getTimeZone(timeZoneId) : null;
    }

    @Override
    @Nullable
    public Object nextObjectOrNull() throws IOException {
        return this.nextValueOrNull();
    }

    @Override
    @NotNull
    public JsonToken peek() throws IOException {
        if (this.stack.isEmpty()) {
            return JsonToken.END_DOCUMENT;
        }
        Map.Entry<String, Object> currentEntry = this.stack.peekLast();
        if (currentEntry == null) {
            return JsonToken.END_DOCUMENT;
        }
        if (currentEntry.getKey() != null) {
            return JsonToken.NAME;
        }
        Object value = currentEntry.getValue();
        if (value instanceof Map) {
            return JsonToken.BEGIN_OBJECT;
        }
        if (value instanceof List) {
            return JsonToken.BEGIN_ARRAY;
        }
        if (value instanceof String) {
            return JsonToken.STRING;
        }
        if (value instanceof Number) {
            return JsonToken.NUMBER;
        }
        if (value instanceof Boolean) {
            return JsonToken.BOOLEAN;
        }
        if (value instanceof JsonToken) {
            return (JsonToken)((Object)value);
        }
        return JsonToken.END_DOCUMENT;
    }

    @Override
    @NotNull
    public String nextName() throws IOException {
        Map.Entry<String, Object> currentEntry = this.stack.peekLast();
        if (currentEntry != null && currentEntry.getKey() != null) {
            return currentEntry.getKey();
        }
        throw new IOException("Expected a name but was " + (Object)((Object)this.peek()));
    }

    @Override
    public void beginObject() throws IOException {
        Map.Entry<String, Object> currentEntry = this.stack.removeLast();
        if (currentEntry == null) {
            throw new IOException("No more entries");
        }
        Object value = currentEntry.getValue();
        if (value instanceof Map) {
            this.stack.addLast(new AbstractMap.SimpleEntry<Object, JsonToken>(null, JsonToken.END_OBJECT));
            for (Map.Entry entry : ((Map)value).entrySet()) {
                this.stack.addLast(entry);
            }
        } else {
            throw new IOException("Current token is not an object");
        }
    }

    @Override
    public void endObject() throws IOException {
        if (this.stack.size() > 1) {
            this.stack.removeLast();
        }
    }

    @Override
    public void beginArray() throws IOException {
        Map.Entry<String, Object> currentEntry = this.stack.removeLast();
        if (currentEntry == null) {
            throw new IOException("No more entries");
        }
        Object value = currentEntry.getValue();
        if (value instanceof List) {
            this.stack.addLast(new AbstractMap.SimpleEntry<Object, JsonToken>(null, JsonToken.END_ARRAY));
            for (int i = ((List)value).size() - 1; i >= 0; --i) {
                Object entry = ((List)value).get(i);
                this.stack.addLast(new AbstractMap.SimpleEntry(null, entry));
            }
        } else {
            throw new IOException("Current token is not an object");
        }
    }

    @Override
    public void endArray() throws IOException {
        if (this.stack.size() > 1) {
            this.stack.removeLast();
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        return !this.stack.isEmpty();
    }

    @Override
    public int nextInt() throws IOException {
        Object value = this.nextValueOrNull();
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        throw new IOException("Expected int");
    }

    @Override
    @Nullable
    public Integer nextIntegerOrNull() throws IOException {
        Object value = this.nextValueOrNull();
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        return null;
    }

    @Override
    public long nextLong() throws IOException {
        Object value = this.nextValueOrNull();
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        throw new IOException("Expected long");
    }

    @Override
    @Nullable
    public Long nextLongOrNull() throws IOException {
        Object value = this.nextValueOrNull();
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        return null;
    }

    @Override
    public String nextString() throws IOException {
        String value = (String)this.nextValueOrNull();
        if (value != null) {
            return value;
        }
        throw new IOException("Expected string");
    }

    @Override
    @Nullable
    public String nextStringOrNull() throws IOException {
        return (String)this.nextValueOrNull();
    }

    @Override
    public boolean nextBoolean() throws IOException {
        Boolean value = (Boolean)this.nextValueOrNull();
        if (value != null) {
            return value;
        }
        throw new IOException("Expected boolean");
    }

    @Override
    @Nullable
    public Boolean nextBooleanOrNull() throws IOException {
        return (Boolean)this.nextValueOrNull();
    }

    @Override
    public double nextDouble() throws IOException {
        Object value = this.nextValueOrNull();
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        throw new IOException("Expected double");
    }

    @Override
    @Nullable
    public Double nextDoubleOrNull() throws IOException {
        Object value = this.nextValueOrNull();
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        return null;
    }

    @Override
    @Nullable
    public Float nextFloatOrNull() throws IOException {
        Object value = this.nextValueOrNull();
        if (value instanceof Number) {
            return Float.valueOf(((Number)value).floatValue());
        }
        return null;
    }

    @Override
    public float nextFloat() throws IOException {
        Object value = this.nextValueOrNull();
        if (value instanceof Number) {
            return ((Number)value).floatValue();
        }
        throw new IOException("Expected float");
    }

    @Override
    public void nextNull() throws IOException {
        Object value = this.nextValueOrNull();
        if (value != null) {
            throw new IOException("Expected null but was " + (Object)((Object)this.peek()));
        }
    }

    @Override
    public void setLenient(boolean lenient) {
    }

    @Override
    public void skipValue() throws IOException {
    }

    @Nullable
    private <T> T nextValueOrNull() throws IOException {
        try {
            return this.nextValueOrNull(null, null);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Nullable
    private <T> T nextValueOrNull(@Nullable ILogger logger, @Nullable JsonDeserializer<T> deserializer) throws Exception {
        Map.Entry<String, Object> currentEntry = this.stack.peekLast();
        if (currentEntry == null) {
            return null;
        }
        Object value = currentEntry.getValue();
        if (deserializer != null && logger != null) {
            return deserializer.deserialize(this, logger);
        }
        this.stack.removeLast();
        return (T)value;
    }

    @Override
    public void close() throws IOException {
        this.stack.clear();
    }
}

