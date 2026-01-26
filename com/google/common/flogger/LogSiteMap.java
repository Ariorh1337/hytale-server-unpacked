/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger;

import com.google.common.flogger.LogContext;
import com.google.common.flogger.LogSiteKey;
import com.google.common.flogger.LoggingScope;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.util.Checks;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LogSiteMap<V> {
    private final ConcurrentHashMap<LogSiteKey, V> concurrentMap = new ConcurrentHashMap();

    protected LogSiteMap() {
    }

    protected abstract V initialValue();

    boolean contains(LogSiteKey key) {
        return this.concurrentMap.containsKey(key);
    }

    public final V get(LogSiteKey key, Metadata metadata) {
        V value = this.concurrentMap.get(key);
        if (value != null) {
            return value;
        }
        value = Checks.checkNotNull(this.initialValue(), "initial map value");
        V race = this.concurrentMap.putIfAbsent(key, value);
        if (race != null) {
            return race;
        }
        this.addRemovalHook(key, metadata);
        return value;
    }

    private void addRemovalHook(final LogSiteKey key, Metadata metadata) {
        Runnable removalHook = null;
        int count = metadata.size();
        for (int i = 0; i < count; ++i) {
            Object groupByKey;
            if (!LogContext.Key.LOG_SITE_GROUPING_KEY.equals(metadata.getKey(i)) || !((groupByKey = metadata.getValue(i)) instanceof LoggingScope)) continue;
            if (removalHook == null) {
                removalHook = new Runnable(){

                    @Override
                    public void run() {
                        LogSiteMap.this.concurrentMap.remove(key);
                    }
                };
            }
            ((LoggingScope)groupByKey).onClose(removalHook);
        }
    }
}

