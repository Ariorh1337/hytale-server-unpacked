/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.cache;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.worldgen.util.cache.Cache;
import com.hypixel.hytale.server.worldgen.util.cache.CleanupFutureAction;
import com.hypixel.hytale.server.worldgen.util.cache.CleanupRunnable;
import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TimeoutCache<K, V>
implements Cache<K, V> {
    private final Map<K, CacheEntry<V>> map = new ConcurrentHashMap<K, CacheEntry<V>>();
    private final long timeout;
    @Nonnull
    private final Function<K, V> func;
    @Nullable
    private final BiConsumer<K, V> destroyer;
    @Nonnull
    private final ScheduledFuture<?> future;
    @Nonnull
    private final Cleaner.Cleanable cleanable;

    public TimeoutCache(long expire, @Nonnull TimeUnit unit, @Nonnull Function<K, V> func, @Nullable BiConsumer<K, V> destroyer) {
        this.timeout = unit.toNanos(expire);
        this.func = func;
        this.destroyer = destroyer;
        this.future = HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(new CleanupRunnable(new WeakReference(this)), expire, expire, unit);
        this.cleanable = CleanupFutureAction.CLEANER.register(this, new CleanupFutureAction(this.future));
    }

    @Override
    public void cleanup() {
        long expire = System.nanoTime() - this.timeout;
        for (Map.Entry<K, CacheEntry<V>> entry : this.map.entrySet()) {
            K key;
            CacheEntry<V> cacheEntry = entry.getValue();
            if (cacheEntry.timestamp >= expire || !this.map.remove(key = entry.getKey(), entry.getValue()) || this.destroyer == null) continue;
            this.destroyer.accept(key, cacheEntry.value);
        }
    }

    @Override
    public void shutdown() {
        this.cleanable.clean();
        Iterator<Map.Entry<K, CacheEntry<V>>> iterator = this.map.entrySet().iterator();
        while (iterator.hasNext()) {
            CacheEntry<V> cacheEntry;
            Map.Entry<K, CacheEntry<V>> entry = iterator.next();
            K key = entry.getKey();
            if (!this.map.remove(key, cacheEntry = entry.getValue())) continue;
            iterator.remove();
            if (this.destroyer == null) continue;
            this.destroyer.accept(key, cacheEntry.value);
        }
    }

    @Override
    public V get(K key) {
        if (this.future.isCancelled()) {
            throw new IllegalStateException("Cache has been shutdown!");
        }
        CacheEntry cacheEntry = this.map.compute(key, (k, v) -> {
            if (v != null) {
                v.timestamp = System.nanoTime();
                return v;
            }
            return new CacheEntry<V>(this.func.apply(k));
        });
        return cacheEntry.value;
    }

    private static class CacheEntry<V> {
        private final V value;
        private long timestamp;

        public CacheEntry(V value) {
            this.value = value;
            this.timestamp = System.nanoTime();
        }
    }
}

