/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AbbreviationMap<Value> {
    private final Map<String, Value> abbreviationMap;

    public AbbreviationMap(@Nonnull Map<String, Value> abbreviationMap) {
        this.abbreviationMap = abbreviationMap;
    }

    public Value get(@Nonnull String abbreviation) {
        return this.abbreviationMap.get(abbreviation.toLowerCase());
    }

    @Nonnull
    public static <V> AbbreviationMapBuilder<V> create() {
        return new AbbreviationMapBuilder();
    }

    public static class AbbreviationMapBuilder<Value> {
        private final Map<String, Value> keys = new Object2ObjectOpenHashMap<String, Value>();

        @Nonnull
        public AbbreviationMapBuilder<Value> put(@Nonnull String key, @Nonnull Value value) {
            if (this.keys.putIfAbsent(key.toLowerCase(), value) != null) {
                throw new IllegalArgumentException("Cannot have values with the same key in AbbreviationMap: " + key);
            }
            return this;
        }

        @Nonnull
        public AbbreviationMap<Value> build() {
            Object2ObjectOpenHashMap abbreviationMap = new Object2ObjectOpenHashMap();
            for (Map.Entry<String, Value> entry : this.keys.entrySet()) {
                this.appendAbbreviation(entry.getKey(), entry.getValue(), abbreviationMap);
            }
            abbreviationMap.values().removeIf(Objects::isNull);
            abbreviationMap.trim();
            return new AbbreviationMap(Collections.unmodifiableMap(abbreviationMap));
        }

        private void appendAbbreviation(@Nonnull String key, @Nonnull Value value, @Nonnull Map<String, Value> map) {
            map.put(key, value);
            for (int i = 1; i < key.length(); ++i) {
                String substring = key.substring(0, key.length() - i);
                Value existingAbbreviationValue = map.get(substring);
                if (existingAbbreviationValue == null) {
                    map.put(substring, value);
                    continue;
                }
                if (this.keys.containsKey(substring) || existingAbbreviationValue.equals(value)) continue;
                map.put(substring, null);
            }
        }
    }
}

