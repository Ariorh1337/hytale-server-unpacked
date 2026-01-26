/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cache;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.worldgen.cache.CoordinateCache;
import com.hypixel.hytale.server.worldgen.cache.CoreDataCacheEntry;
import com.hypixel.hytale.server.worldgen.cache.InterpolatedBiomeCountList;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
import com.hypixel.hytale.server.worldgen.util.ObjectPool;
import com.hypixel.hytale.server.worldgen.util.cache.ConcurrentSizedTimeoutCache;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChunkGeneratorCache {
    private static final int CONCURRENCY_LEVEL = Math.max(1, ChunkGenerator.POOL_SIZE / 2);
    private final ZoneBiomeResultFunction zoneBiomeResultFunction;
    private final BiomeCountFunction biomeCountFunction;
    private final HeightFunction heightFunction;
    private final HeightNoiseFunction heightNoiseFunction;
    private final ObjectPool<CoordinateCache.CoordinateKey> keyPool;
    private final ConcurrentSizedTimeoutCache<CoordinateCache.CoordinateKey, CoreDataCacheEntry> cache;

    public ChunkGeneratorCache(ZoneBiomeResultFunction zoneBiomeResultFunction, BiomeCountFunction biomeCountFunction, HeightFunction heightFunction, HeightNoiseFunction heightNoiseFunction, int maxSize, long expireAfterSeconds) {
        this.zoneBiomeResultFunction = zoneBiomeResultFunction;
        this.biomeCountFunction = biomeCountFunction;
        this.heightFunction = heightFunction;
        this.heightNoiseFunction = heightNoiseFunction;
        this.keyPool = new ObjectPool<CoordinateCache.CoordinateKey>(maxSize, CoordinateCache.CoordinateKey::new);
        this.cache = new ConcurrentSizedTimeoutCache<CoordinateCache.CoordinateKey, CoreDataCacheEntry>(maxSize, CONCURRENCY_LEVEL, expireAfterSeconds, TimeUnit.SECONDS, this::computeKey, this::computeValue, this::destroyEntry);
    }

    @Nonnull
    public CoreDataCacheEntry get(int seed, int x, int z) {
        return this.cache.get(ChunkGeneratorCache.localKey().setLocation(seed, x, z));
    }

    public ZoneBiomeResult getZoneBiomeResult(int seed, int x, int z) {
        return this.get((int)seed, (int)x, (int)z).zoneBiomeResult;
    }

    @Nullable
    public InterpolatedBiomeCountList getBiomeCountResult(int seed, int x, int z) {
        CoreDataCacheEntry entry = this.get(seed, x, z);
        this.ensureBiomeCountList(seed, x, z, entry);
        return entry.biomeCountList;
    }

    public void putHeight(int seed, int x, int z, int height) {
        this.get((int)seed, (int)x, (int)z).height = height;
    }

    public int getHeight(int seed, int x, int z) {
        CoreDataCacheEntry entry = this.get(seed, x, z);
        this.ensureHeight(seed, x, z, entry);
        return entry.height;
    }

    public void ensureBiomeCountList(int seed, int x, int z, @Nonnull CoreDataCacheEntry entry) {
        if (entry.biomeCountList == null) {
            InterpolatedBiomeCountList list = new InterpolatedBiomeCountList();
            this.biomeCountFunction.compute(seed, x, z, list);
            entry.biomeCountList = list;
        }
    }

    public void ensureHeight(int seed, int x, int z, @Nonnull CoreDataCacheEntry entry) {
        if (entry.height == -1) {
            entry.height = this.heightFunction.compute(seed, x, z);
        }
    }

    public void ensureHeightNoise(int seed, int x, int z, @Nonnull CoreDataCacheEntry entry) {
        if (entry.heightNoise == Double.NEGATIVE_INFINITY) {
            this.ensureBiomeCountList(seed, x, z, entry);
            entry.heightNoise = this.heightNoiseFunction.compute(entry.biomeCountList);
        }
    }

    @Nonnull
    protected final CoordinateCache.CoordinateKey computeKey(CoordinateCache.CoordinateKey key) {
        return this.keyPool.apply(key);
    }

    @Nonnull
    protected final CoreDataCacheEntry computeValue(@Nonnull CoordinateCache.CoordinateKey key) {
        int seed = key.seed();
        long coord = key.coord();
        int x = ChunkUtil.xOfChunkIndex(coord);
        int z = ChunkUtil.zOfChunkIndex(coord);
        return new CoreDataCacheEntry(this.zoneBiomeResultFunction.compute(seed, x, z));
    }

    protected final void destroyEntry(CoordinateCache.CoordinateKey key, CoreDataCacheEntry value) {
        this.keyPool.recycle(key);
    }

    @Nonnull
    protected static CoordinateCache.CoordinateKey localKey() {
        return ChunkGenerator.getResource().cacheCoordinateKey;
    }

    @FunctionalInterface
    public static interface ZoneBiomeResultFunction {
        public ZoneBiomeResult compute(int var1, int var2, int var3);
    }

    @FunctionalInterface
    public static interface BiomeCountFunction {
        public void compute(int var1, int var2, int var3, InterpolatedBiomeCountList var4);
    }

    @FunctionalInterface
    public static interface HeightFunction {
        public int compute(int var1, int var2, int var3);
    }

    @FunctionalInterface
    public static interface HeightNoiseFunction {
        public double compute(InterpolatedBiomeCountList var1);
    }
}

