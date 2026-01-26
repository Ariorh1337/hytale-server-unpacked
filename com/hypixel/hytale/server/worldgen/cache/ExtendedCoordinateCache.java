/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cache;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.server.worldgen.util.ObjectPool;
import com.hypixel.hytale.server.worldgen.util.cache.SizedTimeoutCache;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ExtendedCoordinateCache<K, T> {
    @Nonnull
    private final SizedTimeoutCache<ExtendedCoordinateKey<K>, T> cache;
    @Nonnull
    private final ExtendedCoordinateObjectFunction<K, T> loader;
    @Nonnull
    private final ObjectPool<ExtendedCoordinateKey<K>> vectorPool;

    public ExtendedCoordinateCache(@Nonnull ExtendedCoordinateObjectFunction<K, T> loader, @Nullable ExtendedCoordinateRemovalListener<T> removalListener, int maxSize, long expireAfterSeconds) {
        this.loader = loader;
        this.vectorPool = new ObjectPool<ExtendedCoordinateKey>(maxSize, ExtendedCoordinateKey::new);
        this.cache = new SizedTimeoutCache<ExtendedCoordinateKey, Object>(expireAfterSeconds, TimeUnit.SECONDS, maxSize, key -> {
            int x = ChunkUtil.xOfChunkIndex(key.coord);
            int z = ChunkUtil.zOfChunkIndex(key.coord);
            return loader.compute(key.k, key.seed, x, z);
        }, (key, value) -> {
            this.vectorPool.recycle(key);
            if (removalListener != null) {
                removalListener.onRemoval(value);
            }
        });
    }

    @Nullable
    public T get(@Nonnull K k, int seed, int x, int y) {
        return this.cache.getWithReusedKey(this.localKey().setLocation(k, seed, x, y), this.vectorPool);
    }

    protected abstract ExtendedCoordinateKey<K> localKey();

    @FunctionalInterface
    public static interface ExtendedCoordinateObjectFunction<K, T> {
        public T compute(K var1, int var2, int var3, int var4);
    }

    @FunctionalInterface
    public static interface ExtendedCoordinateRemovalListener<T> {
        public void onRemoval(T var1);
    }

    public static class ExtendedCoordinateKey<K>
    implements Function<ExtendedCoordinateKey<K>, ExtendedCoordinateKey<K>> {
        @Nullable
        private K k;
        private int seed;
        private long coord;
        private int hash;

        public ExtendedCoordinateKey() {
            this(null, 0, 0, 0);
        }

        public ExtendedCoordinateKey(@Nullable K k, int seed, int x, int y) {
            this.k = k;
            this.seed = seed;
            this.coord = ChunkUtil.indexChunk(x, y);
            this.hash = 31 * (k != null ? k.hashCode() : 0) + (int)HashUtil.hash(seed, this.coord);
        }

        @Nonnull
        public ExtendedCoordinateKey<K> setLocation(@Nonnull K k, int seed, int x, int y) {
            this.k = k;
            this.seed = seed;
            this.coord = ChunkUtil.indexChunk(x, y);
            this.hash = 31 * k.hashCode() + (int)HashUtil.hash(seed, this.coord);
            return this;
        }

        @Override
        @Nonnull
        public ExtendedCoordinateKey<K> apply(@Nonnull ExtendedCoordinateKey<K> cachedKey) {
            this.k = cachedKey.k;
            this.seed = cachedKey.seed;
            this.coord = cachedKey.coord;
            this.hash = cachedKey.hash;
            return this;
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object o) {
            ExtendedCoordinateKey that = (ExtendedCoordinateKey)o;
            return this.seed == that.seed && this.coord == that.coord && this.k.equals(that.k);
        }
    }
}

