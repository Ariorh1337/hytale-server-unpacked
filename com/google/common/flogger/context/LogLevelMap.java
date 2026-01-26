/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.context;

import com.google.common.flogger.context.SegmentTrie;
import com.google.common.flogger.util.Checks;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;

public final class LogLevelMap {
    private final SegmentTrie<Level> trie;

    public static Builder builder() {
        return new Builder();
    }

    public static LogLevelMap create(Level level) {
        return LogLevelMap.create(Collections.emptyMap(), level);
    }

    public static LogLevelMap create(Map<String, ? extends Level> map) {
        return LogLevelMap.create(map, Level.OFF);
    }

    public static LogLevelMap create(Map<String, ? extends Level> map, Level defaultLevel) {
        Checks.checkNotNull(defaultLevel, "default log level must not be null");
        for (Map.Entry<String, ? extends Level> e : map.entrySet()) {
            String name = e.getKey();
            if (name.startsWith(".") || name.endsWith(".") || name.contains("..")) {
                throw new IllegalArgumentException("invalid logger name: " + name);
            }
            if (e.getValue() != null) continue;
            throw new IllegalArgumentException("log levels must not be null; logger=" + name);
        }
        return new LogLevelMap(map, defaultLevel);
    }

    private LogLevelMap(Map<String, ? extends Level> map, Level defaultLevel) {
        this.trie = SegmentTrie.create(map, '.', defaultLevel);
    }

    public Level getLevel(String loggerName) {
        return this.trie.find(loggerName);
    }

    public LogLevelMap merge(LogLevelMap other) {
        Map<String, Level> thisMap = this.trie.getEntryMap();
        Map<String, Level> otherMap = other.trie.getEntryMap();
        HashMap<String, Level> mergedMap = new HashMap<String, Level>();
        HashSet<String> allKeys = new HashSet<String>(thisMap.keySet());
        allKeys.addAll(otherMap.keySet());
        for (String key : allKeys) {
            if (!otherMap.containsKey(key)) {
                mergedMap.put(key, thisMap.get(key));
                continue;
            }
            if (!thisMap.containsKey(key)) {
                mergedMap.put(key, otherMap.get(key));
                continue;
            }
            mergedMap.put(key, LogLevelMap.min(thisMap.get(key), otherMap.get(key)));
        }
        Level defaultLevel = LogLevelMap.min(this.trie.getDefaultValue(), other.trie.getDefaultValue());
        return LogLevelMap.create(mergedMap, defaultLevel);
    }

    private static Level min(Level a, Level b) {
        return a.intValue() <= b.intValue() ? a : b;
    }

    public static final class Builder {
        private final Map<String, Level> map = new HashMap<String, Level>();
        private Level defaultLevel = Level.OFF;

        private Builder() {
        }

        private void put(String name, Level level) {
            if (this.map.put(name, level) != null) {
                throw new IllegalArgumentException("duplicate entry for class/package: " + name);
            }
        }

        public Builder add(Level level, Class<?> ... classes) {
            for (Class<?> cls : classes) {
                this.put(cls.getName(), level);
            }
            return this;
        }

        public Builder add(Level level, Package ... packages) {
            for (Package pkg : packages) {
                this.put(pkg.getName(), level);
            }
            return this;
        }

        public Builder setDefault(Level level) {
            Checks.checkNotNull(this.defaultLevel, "default log level must not be null");
            this.defaultLevel = level;
            return this;
        }

        public LogLevelMap build() {
            return LogLevelMap.create(this.map, this.defaultLevel);
        }
    }
}

