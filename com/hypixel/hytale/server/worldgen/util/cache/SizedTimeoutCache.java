/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.cache;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.worldgen.util.cache.Cache;
import com.hypixel.hytale.server.worldgen.util.cache.CleanupFutureAction;
import com.hypixel.hytale.server.worldgen.util.cache.CleanupRunnable;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SizedTimeoutCache<K, V>
implements Cache<K, V> {
    private final ArrayDeque<CacheEntry<K, V>> pool = new ArrayDeque();
    private final Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> map = new Object2ObjectLinkedOpenHashMap();
    private final long timeout;
    private final int maxSize;
    @Nullable
    private final Function<K, V> func;
    @Nullable
    private final BiConsumer<K, V> destroyer;
    @Nonnull
    private final ScheduledFuture<?> future;
    @Nonnull
    private final Cleaner.Cleanable cleanable;

    public SizedTimeoutCache(long expire, @Nonnull TimeUnit unit, int maxSize, @Nullable Function<K, V> func, @Nullable BiConsumer<K, V> destroyer) {
        this.timeout = unit.toNanos(expire);
        this.maxSize = maxSize;
        this.func = func;
        this.destroyer = destroyer;
        this.future = HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(new CleanupRunnable(new WeakReference(this)), expire, expire, unit);
        this.cleanable = CleanupFutureAction.CLEANER.register(this, new CleanupFutureAction(this.future));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cleanup() {
        this.reduceLength(this.maxSize);
        long expire = System.nanoTime() - this.timeout;
        while (true) {
            Object value;
            K key;
            Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> object2ObjectLinkedOpenHashMap = this.map;
            synchronized (object2ObjectLinkedOpenHashMap) {
                if (this.map.isEmpty()) {
                    break;
                }
                key = this.map.lastKey();
                CacheEntry<K, V> entry = this.map.get(key);
                if (entry.timestamp > expire) {
                    break;
                }
                this.map.remove(key);
                value = entry.value;
                if (this.pool.size() < this.maxSize) {
                    entry.key = null;
                    entry.value = null;
                    entry.timestamp = 0L;
                    this.pool.addLast(entry);
                }
            }
            if (this.destroyer == null) continue;
            this.destroyer.accept(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void reduceLength(int targetSize) {
        while (true) {
            Object value;
            Object key;
            Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> object2ObjectLinkedOpenHashMap = this.map;
            synchronized (object2ObjectLinkedOpenHashMap) {
                if (this.map.size() <= targetSize) {
                    break;
                }
                CacheEntry<K, V> entry = this.map.removeLast();
                key = entry.key;
                value = entry.value;
                if (this.pool.size() < this.maxSize) {
                    entry.key = null;
                    entry.value = null;
                    entry.timestamp = 0L;
                    this.pool.addLast(entry);
                }
            }
            if (this.destroyer == null) continue;
            this.destroyer.accept(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shutdown() {
        this.cleanable.clean();
        if (this.destroyer != null) {
            this.reduceLength(0);
        } else {
            Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> object2ObjectLinkedOpenHashMap = this.map;
            synchronized (object2ObjectLinkedOpenHashMap) {
                this.map.clear();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public V get(K key) {
        Object resultValue;
        CacheEntry<K, V> resultEntry;
        CacheEntry newEntry;
        if (this.future.isCancelled()) {
            throw new IllegalStateException("Cache has been shutdown!");
        }
        long timestamp = System.nanoTime();
        Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> object2ObjectLinkedOpenHashMap = this.map;
        synchronized (object2ObjectLinkedOpenHashMap) {
            CacheEntry<K, V> entry = this.map.getAndMoveToFirst(key);
            if (entry != null) {
                entry.timestamp = timestamp;
                return entry.value;
            }
        }
        if (this.func == null) {
            return null;
        }
        V value = this.func.apply(key);
        timestamp = System.nanoTime();
        Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> object2ObjectLinkedOpenHashMap2 = this.map;
        synchronized (object2ObjectLinkedOpenHashMap2) {
            newEntry = this.pool.isEmpty() ? new CacheEntry() : this.pool.removeLast();
            newEntry.key = key;
            newEntry.value = value;
            newEntry.timestamp = timestamp;
            resultEntry = this.map.getAndMoveToFirst(key);
            if (resultEntry != null) {
                resultEntry.timestamp = timestamp;
            } else {
                resultEntry = newEntry;
                this.map.put(key, resultEntry);
            }
            resultValue = resultEntry.value;
        }
        if (resultEntry != newEntry && this.destroyer != null) {
            this.destroyer.accept(key, value);
        }
        return resultValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void put(K key, V value) {
        CacheEntry oldEntry;
        if (this.future.isCancelled()) {
            throw new IllegalStateException("Cache has been shutdown!");
        }
        long timestamp = System.nanoTime();
        Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> object2ObjectLinkedOpenHashMap = this.map;
        synchronized (object2ObjectLinkedOpenHashMap) {
            CacheEntry entry = this.pool.isEmpty() ? new CacheEntry() : this.pool.removeLast();
            entry.key = key;
            entry.value = value;
            entry.timestamp = timestamp;
            oldEntry = this.map.putAndMoveToFirst(key, entry);
            if (oldEntry != null) {
                entry.key = oldEntry.key;
            }
        }
        if (oldEntry != null && this.destroyer != null) {
            this.destroyer.accept(key, oldEntry.value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public V getWithReusedKey(K reusedKey, @Nonnull Function<K, K> keyPool) {
        Object resultValue;
        CacheEntry<K, V> resultEntry;
        CacheEntry newEntry;
        if (this.future.isCancelled()) {
            throw new IllegalStateException("Cache has been shutdown!");
        }
        long timestamp = System.nanoTime();
        Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> object2ObjectLinkedOpenHashMap = this.map;
        synchronized (object2ObjectLinkedOpenHashMap) {
            CacheEntry<K, V> entry = this.map.getAndMoveToFirst(reusedKey);
            if (entry != null) {
                entry.timestamp = timestamp;
                return entry.value;
            }
        }
        if (this.func == null) {
            return null;
        }
        K newKey = keyPool.apply(reusedKey);
        V value = this.func.apply(newKey);
        timestamp = System.nanoTime();
        Object2ObjectLinkedOpenHashMap<K, CacheEntry<K, V>> object2ObjectLinkedOpenHashMap2 = this.map;
        synchronized (object2ObjectLinkedOpenHashMap2) {
            newEntry = this.pool.isEmpty() ? new CacheEntry() : this.pool.removeLast();
            newEntry.key = newKey;
            newEntry.value = value;
            newEntry.timestamp = timestamp;
            resultEntry = this.map.getAndMoveToFirst(newKey);
            if (resultEntry != null) {
                resultEntry.timestamp = timestamp;
            } else {
                resultEntry = newEntry;
                this.map.put(newKey, resultEntry);
            }
            resultValue = resultEntry.value;
        }
        if (resultEntry != newEntry && this.destroyer != null) {
            this.destroyer.accept(newKey, value);
        }
        return resultValue;
    }

    private static class CacheEntry<K, V> {
        @Nullable
        private V value;
        @Nullable
        private K key;
        private long timestamp;

        private CacheEntry() {
        }
    }
}

