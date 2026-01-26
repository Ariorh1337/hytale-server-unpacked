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
import io.sentry.JsonReflectionObjectSerializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.util.JsonSerializationUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class JsonObjectSerializer {
    public static final String OBJECT_PLACEHOLDER = "[OBJECT]";
    public final JsonReflectionObjectSerializer jsonReflectionObjectSerializer;

    public JsonObjectSerializer(int maxDepth) {
        this.jsonReflectionObjectSerializer = new JsonReflectionObjectSerializer(maxDepth);
    }

    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger, @Nullable Object object) throws IOException {
        if (object == null) {
            writer.nullValue();
        } else if (object instanceof Character) {
            writer.value(Character.toString(((Character)object).charValue()));
        } else if (object instanceof String) {
            writer.value((String)object);
        } else if (object instanceof Boolean) {
            writer.value((boolean)((Boolean)object));
        } else if (object instanceof Number) {
            writer.value((Number)object);
        } else if (object instanceof Date) {
            this.serializeDate(writer, logger, (Date)object);
        } else if (object instanceof TimeZone) {
            this.serializeTimeZone(writer, logger, (TimeZone)object);
        } else if (object instanceof JsonSerializable) {
            ((JsonSerializable)object).serialize(writer, logger);
        } else if (object instanceof Collection) {
            this.serializeCollection(writer, logger, (Collection)object);
        } else if (object instanceof boolean[]) {
            ArrayList<Boolean> bools = new ArrayList<Boolean>(((boolean[])object).length);
            for (boolean b : (boolean[])object) {
                bools.add(b);
            }
            this.serializeCollection(writer, logger, bools);
        } else if (object instanceof byte[]) {
            ArrayList<Byte> bytes = new ArrayList<Byte>(((byte[])object).length);
            for (byte b : (byte[])object) {
                bytes.add(b);
            }
            this.serializeCollection(writer, logger, bytes);
        } else if (object instanceof short[]) {
            ArrayList<Short> shorts = new ArrayList<Short>(((short[])object).length);
            for (short s : (short[])object) {
                shorts.add(s);
            }
            this.serializeCollection(writer, logger, shorts);
        } else if (object instanceof char[]) {
            ArrayList<Character> chars = new ArrayList<Character>(((char[])object).length);
            for (char s : (char[])object) {
                chars.add(Character.valueOf(s));
            }
            this.serializeCollection(writer, logger, chars);
        } else if (object instanceof int[]) {
            ArrayList<Integer> ints = new ArrayList<Integer>(((int[])object).length);
            for (int i : (int[])object) {
                ints.add(i);
            }
            this.serializeCollection(writer, logger, ints);
        } else if (object instanceof long[]) {
            ArrayList<Long> longs = new ArrayList<Long>(((long[])object).length);
            for (long l : (long[])object) {
                longs.add(l);
            }
            this.serializeCollection(writer, logger, longs);
        } else if (object instanceof float[]) {
            ArrayList<Float> floats = new ArrayList<Float>(((float[])object).length);
            for (float f : (float[])object) {
                floats.add(Float.valueOf(f));
            }
            this.serializeCollection(writer, logger, floats);
        } else if (object instanceof double[]) {
            ArrayList<Double> doubles = new ArrayList<Double>(((double[])object).length);
            for (double d : (double[])object) {
                doubles.add(d);
            }
            this.serializeCollection(writer, logger, doubles);
        } else if (object.getClass().isArray()) {
            this.serializeCollection(writer, logger, Arrays.asList((Object[])object));
        } else if (object instanceof Map) {
            this.serializeMap(writer, logger, (Map)object);
        } else if (object instanceof Locale) {
            writer.value(object.toString());
        } else if (object instanceof AtomicIntegerArray) {
            this.serializeCollection(writer, logger, JsonSerializationUtils.atomicIntegerArrayToList((AtomicIntegerArray)object));
        } else if (object instanceof AtomicBoolean) {
            writer.value(((AtomicBoolean)object).get());
        } else if (object instanceof URI) {
            writer.value(object.toString());
        } else if (object instanceof InetAddress) {
            writer.value(object.toString());
        } else if (object instanceof UUID) {
            writer.value(object.toString());
        } else if (object instanceof Currency) {
            writer.value(object.toString());
        } else if (object instanceof Calendar) {
            this.serializeMap(writer, logger, JsonSerializationUtils.calendarToMap((Calendar)object));
        } else if (object.getClass().isEnum()) {
            writer.value(object.toString());
        } else {
            try {
                Object serializableObject = this.jsonReflectionObjectSerializer.serialize(object, logger);
                this.serialize(writer, logger, serializableObject);
            }
            catch (Exception exception) {
                logger.log(SentryLevel.ERROR, "Failed serializing unknown object.", exception);
                writer.value(OBJECT_PLACEHOLDER);
            }
        }
    }

    private void serializeDate(@NotNull ObjectWriter writer, @NotNull ILogger logger, @NotNull Date date) throws IOException {
        try {
            writer.value(DateUtils.getTimestamp(date));
        }
        catch (Exception e) {
            logger.log(SentryLevel.ERROR, "Error when serializing Date", e);
            writer.nullValue();
        }
    }

    private void serializeTimeZone(@NotNull ObjectWriter writer, @NotNull ILogger logger, @NotNull TimeZone timeZone) throws IOException {
        try {
            writer.value(timeZone.getID());
        }
        catch (Exception e) {
            logger.log(SentryLevel.ERROR, "Error when serializing TimeZone", e);
            writer.nullValue();
        }
    }

    private void serializeCollection(@NotNull ObjectWriter writer, @NotNull ILogger logger, @NotNull Collection<?> collection) throws IOException {
        writer.beginArray();
        for (Object object : collection) {
            this.serialize(writer, logger, object);
        }
        writer.endArray();
    }

    private void serializeMap(@NotNull ObjectWriter writer, @NotNull ILogger logger, @NotNull Map<?, ?> map) throws IOException {
        writer.beginObject();
        for (Object key : map.keySet()) {
            if (!(key instanceof String)) continue;
            writer.name((String)key);
            this.serialize(writer, logger, map.get(key));
        }
        writer.endObject();
    }
}

