/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.JsonSerializationUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class JsonReflectionObjectSerializer {
    private final Set<Object> visiting = new HashSet<Object>();
    private final int maxDepth;

    JsonReflectionObjectSerializer(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public Object serialize(@Nullable Object object, @NotNull ILogger logger) throws Exception {
        if (object == null) {
            return null;
        }
        if (object instanceof Character) {
            return object.toString();
        }
        if (object instanceof Number) {
            return object;
        }
        if (object instanceof Boolean) {
            return object;
        }
        if (object instanceof String) {
            return object;
        }
        if (object instanceof Locale) {
            return object.toString();
        }
        if (object instanceof AtomicIntegerArray) {
            return JsonSerializationUtils.atomicIntegerArrayToList((AtomicIntegerArray)object);
        }
        if (object instanceof AtomicBoolean) {
            return ((AtomicBoolean)object).get();
        }
        if (object instanceof URI) {
            return object.toString();
        }
        if (object instanceof InetAddress) {
            return object.toString();
        }
        if (object instanceof UUID) {
            return object.toString();
        }
        if (object instanceof Currency) {
            return object.toString();
        }
        if (object instanceof Calendar) {
            return JsonSerializationUtils.calendarToMap((Calendar)object);
        }
        if (object.getClass().isEnum()) {
            return object.toString();
        }
        if (this.visiting.contains(object)) {
            logger.log(SentryLevel.INFO, "Cyclic reference detected. Calling toString() on object.", new Object[0]);
            return object.toString();
        }
        this.visiting.add(object);
        if (this.visiting.size() > this.maxDepth) {
            this.visiting.remove(object);
            logger.log(SentryLevel.INFO, "Max depth exceeded. Calling toString() on object.", new Object[0]);
            return object.toString();
        }
        Object serializedObject = null;
        try {
            Map<String, Object> objectAsMap;
            serializedObject = object.getClass().isArray() ? this.list((Object[])object, logger) : (object instanceof Collection ? this.list((Collection)object, logger) : (object instanceof Map ? this.map((Map)object, logger) : ((objectAsMap = this.serializeObject(object, logger)).isEmpty() ? object.toString() : objectAsMap)));
        }
        catch (Exception exception) {
            logger.log(SentryLevel.INFO, "Not serializing object due to throwing sub-path.", exception);
        }
        finally {
            this.visiting.remove(object);
        }
        return serializedObject;
    }

    @NotNull
    public Map<String, Object> serializeObject(@NotNull Object object, @NotNull ILogger logger) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) continue;
            String fieldName = field.getName();
            try {
                field.setAccessible(true);
                Object fieldObject = field.get(object);
                map.put(fieldName, this.serialize(fieldObject, logger));
                field.setAccessible(false);
            }
            catch (Exception exception) {
                logger.log(SentryLevel.INFO, "Cannot access field " + fieldName + ".", new Object[0]);
            }
        }
        return map;
    }

    @NotNull
    private List<Object> list(@NotNull Object[] objectArray, @NotNull ILogger logger) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        for (Object object : objectArray) {
            list.add(this.serialize(object, logger));
        }
        return list;
    }

    @NotNull
    private List<Object> list(@NotNull Collection<?> collection, @NotNull ILogger logger) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        for (Object object : collection) {
            list.add(this.serialize(object, logger));
        }
        return list;
    }

    @NotNull
    private Map<String, Object> map(@NotNull Map<?, ?> map, @NotNull ILogger logger) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        for (Object key : map.keySet()) {
            Object object = map.get(key);
            if (object != null) {
                hashMap.put(key.toString(), this.serialize(object, logger));
                continue;
            }
            hashMap.put(key.toString(), null);
        }
        return hashMap;
    }
}

