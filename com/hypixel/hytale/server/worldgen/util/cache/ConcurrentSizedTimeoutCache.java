/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.cache;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.worldgen.util.LogUtil;
import com.hypixel.hytale.server.worldgen.util.cache.Cache;
import com.hypixel.hytale.server.worldgen.util.cache.CleanupFutureAction;
import com.hypixel.hytale.server.worldgen.util.cache.CleanupRunnable;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConcurrentSizedTimeoutCache<K, V>
implements Cache<K, V> {
    private static final int BUCKET_MIN_CAPACITY = 16;
    private static final float BUCKET_LOAD_FACTOR = 0.75f;
    private final int bucketMask;
    @Nonnull
    private final Bucket<K, V>[] buckets;
    @Nonnull
    private final Function<K, K> computeKey;
    @Nonnull
    private final Function<K, V> computeValue;
    @Nonnull
    private final BiConsumer<K, V> destroyer;
    @Nonnull
    private final ScheduledFuture<?> future;
    @Nonnull
    private final Cleaner.Cleanable cleanable;

    public ConcurrentSizedTimeoutCache(int capacity, int concurrencyLevel, long timeout, @Nonnull TimeUnit timeoutUnit, @Nonnull Function<K, K> computeKey, @Nonnull Function<K, V> computeValue, @Nullable BiConsumer<K, V> destroyer) {
        long timeout_ns = timeoutUnit.toNanos(timeout);
        int bucketCount = HashCommon.nextPowerOfTwo(concurrencyLevel);
        int bucketCapacity = Math.max(16, HashCommon.nextPowerOfTwo(capacity / bucketCount));
        this.bucketMask = bucketCount - 1;
        this.buckets = new Bucket[bucketCount];
        for (int i = 0; i < bucketCount; ++i) {
            this.buckets[i] = new Bucket(bucketCapacity, timeout_ns);
        }
        this.computeKey = computeKey;
        this.computeValue = computeValue;
        this.destroyer = destroyer != null ? destroyer : ConcurrentSizedTimeoutCache::noopDestroy;
        this.future = HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(new CleanupRunnable(new WeakReference(this)), timeout, timeout, timeoutUnit);
        this.cleanable = CleanupFutureAction.CLEANER.register(this, new CleanupFutureAction(this.future));
    }

    @Override
    public void shutdown() {
        this.cleanable.clean();
        for (Bucket<K, V> bucket : this.buckets) {
            bucket.clear(this.destroyer);
        }
    }

    @Override
    public void cleanup() {
        for (Bucket<K, V> bucket : this.buckets) {
            bucket.cleanup(this.destroyer);
        }
    }

    @Override
    @Nonnull
    public V get(K key) {
        if (this.future.isCancelled()) {
            throw new IllegalStateException("Cache has been shutdown!");
        }
        int hash = HashCommon.mix(key.hashCode());
        return this.buckets[hash & this.bucketMask].compute(key, this.computeKey, this.computeValue, this.destroyer);
    }

    private static <K, V> void noopDestroy(K key, V value) {
    }

    private static class Bucket<K, V> {
        private final int capacity;
        private final int trimThreshold;
        private final long timeout_ns;
        private final ArrayDeque<CacheEntry<K, V>> pool;
        private final Object2ObjectOpenHashMap<K, CacheEntry<K, V>> map;
        private final StampedLock lock = new StampedLock();

        public Bucket(int capacity, long timeout_ns) {
            this.capacity = capacity;
            this.trimThreshold = MathUtil.fastFloor((float)capacity * 0.75f);
            this.timeout_ns = timeout_ns;
            this.pool = new ArrayDeque(capacity);
            this.map = new Object2ObjectOpenHashMap(capacity, 0.75f);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nonnull
        public V compute(@Nonnull K key, @Nonnull Function<K, K> computeKey, @Nonnull Function<K, V> computeValue, @Nonnull BiConsumer<K, V> destroyer) {
            V newValue;
            long timestamp = System.nanoTime();
            long readStamp = this.lock.readLock();
            try {
                CacheEntry<K, V> entry = this.map.get(key);
                if (entry != null) {
                    V v = entry.markAndGet(timestamp);
                    return v;
                }
            }
            finally {
                this.lock.unlockRead(readStamp);
            }
            K newKey = computeKey.apply(key);
            V resultValue = newValue = computeValue.apply(key);
            long writeStamp = this.lock.writeLock();
            try {
                CacheEntry newEntry = this.pool.isEmpty() ? new CacheEntry() : this.pool.poll();
                Objects.requireNonNull(newEntry, "CacheEntry pool returned null entry!");
                newEntry.key = newKey;
                newEntry.value = newValue;
                newEntry.timestamp = timestamp;
                CacheEntry currentEntry = this.map.putIfAbsent(newKey, newEntry);
                if (currentEntry != null) {
                    Objects.requireNonNull(currentEntry.value);
                    resultValue = currentEntry.value;
                    currentEntry.timestamp = timestamp;
                    newEntry.key = null;
                    newEntry.value = null;
                    if (this.pool.size() < this.capacity) {
                        this.pool.offer(newEntry);
                    }
                }
            }
            finally {
                this.lock.unlockWrite(writeStamp);
            }
            if (newValue != resultValue) {
                destroyer.accept(newKey, newValue);
            }
            return resultValue;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void cleanup(@Nullable BiConsumer<K, V> destroyer) {
            long writeStamp = this.lock.writeLock();
            try {
                boolean needsTrim = this.map.size() >= this.trimThreshold;
                long expireTimestamp = System.nanoTime() - this.timeout_ns;
                ObjectIterator it = this.map.object2ObjectEntrySet().fastIterator();
                while (it.hasNext()) {
                    CacheEntry entry = (CacheEntry)((Object2ObjectMap.Entry)it.next()).getValue();
                    if (entry == null) {
                        LogUtil.getLogger().at(Level.SEVERE).log("Found null entry in cache bucket during cleanup!");
                        it.remove();
                        continue;
                    }
                    if (entry.timestamp < expireTimestamp) continue;
                    it.remove();
                    if (destroyer != null) {
                        destroyer.accept(entry.key, entry.value);
                    }
                    entry.key = null;
                    entry.value = null;
                    if (this.pool.size() >= this.capacity) continue;
                    this.pool.offer(entry);
                }
                if (needsTrim && this.map.size() < this.capacity) {
                    this.map.trim(this.capacity);
                }
            }
            finally {
                this.lock.unlockWrite(writeStamp);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void clear(@Nonnull BiConsumer<K, V> destroyer) {
            long writeStamp = this.lock.writeLock();
            try {
                ObjectIterator it = this.map.object2ObjectEntrySet().fastIterator();
                while (it.hasNext()) {
                    CacheEntry entry = (CacheEntry)((Object2ObjectMap.Entry)it.next()).getValue();
                    destroyer.accept(entry.key, entry.value);
                    it.remove();
                }
            }
            finally {
                this.lock.unlockWrite(writeStamp);
            }
        }
    }

    private static class CacheEntry<K, V> {
        private static final VarHandle TIMESTAMP;
        @Nullable
        public K key = null;
        @Nullable
        public V value = null;
        public long timestamp = 0L;

        private CacheEntry() {
        }

        @Nonnull
        protected V markAndGet(long timestamp) {
            Objects.requireNonNull(this.value);
            TIMESTAMP.setVolatile(this, timestamp);
            return this.value;
        }

        static {
            try {
                TIMESTAMP = MethodHandles.lookup().findVarHandle(CacheEntry.class, "timestamp", Long.TYPE);
            }
            catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}

