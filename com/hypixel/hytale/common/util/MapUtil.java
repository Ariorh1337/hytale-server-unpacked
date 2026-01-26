/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class MapUtil {
    @Nonnull
    public static <T, V> Map<T, V> combineUnmodifiable(@Nonnull Map<T, V> one, @Nonnull Map<T, V> two) {
        Object2ObjectOpenHashMap<T, V> map = new Object2ObjectOpenHashMap<T, V>();
        map.putAll(one);
        map.putAll(two);
        return Collections.unmodifiableMap(map);
    }

    @Nonnull
    public static <T, V, M extends Map<T, V>> Map<T, V> combineUnmodifiable(@Nonnull Map<T, V> one, @Nonnull Map<T, V> two, @Nonnull Supplier<M> supplier) {
        Map map = (Map)supplier.get();
        map.putAll(one);
        map.putAll(two);
        return Collections.unmodifiableMap(map);
    }

    @Nonnull
    public static <T, V, M extends Map<T, V>> M combine(@Nonnull Map<T, V> one, @Nonnull Map<T, V> two, @Nonnull Supplier<M> supplier) {
        Map map = (Map)supplier.get();
        map.putAll(one);
        map.putAll(two);
        return (M)map;
    }
}

