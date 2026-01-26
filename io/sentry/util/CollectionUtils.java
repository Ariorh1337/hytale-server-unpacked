/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static int size(@NotNull Iterable<?> data) {
        if (data instanceof Collection) {
            return ((Collection)data).size();
        }
        int counter = 0;
        for (Object ignored : data) {
            ++counter;
        }
        return counter;
    }

    public static <K, V> @Nullable Map<K, @NotNull V> newConcurrentHashMap(@Nullable Map<K, @NotNull V> map) {
        if (map != null) {
            ConcurrentHashMap<K, @NotNull V> concurrentMap = new ConcurrentHashMap<K, V>();
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) continue;
                concurrentMap.put(entry.getKey(), entry.getValue());
            }
            return concurrentMap;
        }
        return null;
    }

    public static <K, V> @Nullable Map<K, @NotNull V> newHashMap(@Nullable Map<K, @NotNull V> map) {
        if (map != null) {
            return new HashMap<K, V>(map);
        }
        return null;
    }

    @Nullable
    public static <T> List<T> newArrayList(@Nullable List<T> list) {
        if (list != null) {
            return new ArrayList<T>(list);
        }
        return null;
    }

    @NotNull
    public static <K, V> Map<K, V> filterMapEntries(@NotNull Map<K, V> map, @NotNull Predicate<Map.Entry<K, V>> predicate) {
        HashMap<K, V> filteredMap = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (!predicate.test(entry)) continue;
            filteredMap.put(entry.getKey(), entry.getValue());
        }
        return filteredMap;
    }

    @NotNull
    public static <T, R> List<R> map(@NotNull List<T> list, @NotNull Mapper<T, R> f) {
        ArrayList<R> mappedList = new ArrayList<R>(list.size());
        for (T t : list) {
            mappedList.add(f.map(t));
        }
        return mappedList;
    }

    @NotNull
    public static <T> List<T> filterListEntries(@NotNull List<T> list, @NotNull Predicate<T> predicate) {
        ArrayList<T> filteredList = new ArrayList<T>(list.size());
        for (T entry : list) {
            if (!predicate.test(entry)) continue;
            filteredList.add(entry);
        }
        return filteredList;
    }

    public static <T> boolean contains(@NotNull T[] array, @NotNull T element) {
        for (T t : array) {
            if (!element.equals(t)) continue;
            return true;
        }
        return false;
    }

    @NotNull
    public static <T> ListIterator<T> reverseListIterator(@NotNull CopyOnWriteArrayList<T> list) {
        @NotNull CopyOnWriteArrayList<T> copy = new CopyOnWriteArrayList<T>(list);
        return copy.listIterator(copy.size());
    }

    public static interface Predicate<T> {
        public boolean test(T var1);
    }

    public static interface Mapper<T, R> {
        public R map(T var1);
    }
}

