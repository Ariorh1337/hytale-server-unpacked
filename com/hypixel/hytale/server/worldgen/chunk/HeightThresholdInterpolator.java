/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.chunk;

import com.hypixel.hytale.server.worldgen.cache.CoreDataCacheEntry;
import com.hypixel.hytale.server.worldgen.cache.InterpolatedBiomeCountList;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGeneratorExecution;
import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
import javax.annotation.Nonnull;

public class HeightThresholdInterpolator {
    public static final int MAX_RADIUS = 5;
    public static final int MAX_RADIUS2 = 25;
    private final ChunkGeneratorExecution execution;
    @Nonnull
    private final CoreDataCacheEntry[] entries;
    private final int radius;
    private final int size;
    private final int totalSize;

    public HeightThresholdInterpolator(ChunkGeneratorExecution execution) {
        this.execution = execution;
        this.radius = 5;
        this.size = 32;
        this.totalSize = this.size + 2 * this.radius;
        this.entries = new CoreDataCacheEntry[this.totalSize * this.totalSize];
    }

    @Nonnull
    public CoreDataCacheEntry[] getEntries() {
        return this.entries;
    }

    @Nonnull
    public HeightThresholdInterpolator populate(int seed) {
        int cx;
        ChunkGenerator generator = this.execution.getChunkGenerator();
        int mx = this.size + this.radius;
        for (cx = -this.radius; cx < mx; ++cx) {
            int mz = this.size + this.radius;
            for (int cz = -this.radius; cz < mz; ++cz) {
                this.setTableEntry(cx, cz, generator.getCoreData(seed, this.execution.globalX(cx), this.execution.globalZ(cz)));
            }
        }
        for (cx = 0; cx < this.size; ++cx) {
            for (int cz = 0; cz < this.size; ++cz) {
                CoreDataCacheEntry entry = this.tableEntry(cx, cz);
                if (entry.biomeCountList == null) {
                    InterpolatedBiomeCountList list = new InterpolatedBiomeCountList();
                    this.generateInterpolatedBiomeCountAt(cx, cz, list);
                    entry.biomeCountList = list;
                }
                if (entry.heightNoise != Double.NEGATIVE_INFINITY) continue;
                entry.heightNoise = generator.generateInterpolatedHeightNoise(entry.biomeCountList);
            }
        }
        return this;
    }

    public void generateInterpolatedBiomeCountAt(int cx, int cz, @Nonnull InterpolatedBiomeCountList biomeCountList) {
        ZoneBiomeResult center = this.tableEntry((int)cx, (int)cz).zoneBiomeResult;
        biomeCountList.setCenter(center);
        int radius = center.getBiome().getInterpolation().getRadius();
        int radius2 = radius * radius;
        for (int ix = -radius; ix <= radius; ++ix) {
            for (int iz = -radius; iz <= radius; ++iz) {
                int distance2;
                if (ix == 0 && iz == 0 || (distance2 = ix * ix + iz * iz) > radius2) continue;
                ZoneBiomeResult biomeResult = this.tableEntry((int)(cx + ix), (int)(cz + iz)).zoneBiomeResult;
                biomeCountList.add(biomeResult, distance2);
            }
        }
        if (biomeCountList.getBiomeIds().size() == 1) {
            InterpolatedBiomeCountList.BiomeCountResult result = biomeCountList.get(center.getBiome());
            result.heightNoise = center.heightmapNoise;
            result.count = 1;
        }
    }

    public double getHeightNoise(int cx, int cz) {
        return this.tableEntry((int)cx, (int)cz).heightNoise;
    }

    public float getHeightThreshold(int seed, int x, int z, int y) {
        return this.interpolateHeightThreshold(seed, x, z, y);
    }

    private float interpolateHeightThreshold(int seed, int x, int z, int y) {
        CoreDataCacheEntry entry = this.tableEntry(this.execution.localX(x), this.execution.localZ(z));
        return ChunkGenerator.generateInterpolatedThreshold(seed, x, z, y, entry.biomeCountList);
    }

    protected CoreDataCacheEntry tableEntry(int cx, int cz) {
        return this.entries[this.indexLocal(cx, cz)];
    }

    protected void setTableEntry(int cx, int cz, CoreDataCacheEntry entry) {
        this.entries[this.indexLocal((int)cx, (int)cz)] = entry;
    }

    protected ZoneBiomeResult zoneBiomeResult(int cx, int cz) {
        return this.tableEntry((int)cx, (int)cz).zoneBiomeResult;
    }

    public int getLowestNonOne(int cx, int cz) {
        return this.execution.getChunkGenerator().generateLowestThresholdDependent(this.tableEntry((int)cx, (int)cz).biomeCountList);
    }

    public int getHighestNonZero(int cx, int cz) {
        return this.execution.getChunkGenerator().generateHighestThresholdDependent(this.tableEntry((int)cx, (int)cz).biomeCountList);
    }

    protected int indexLocal(int x, int z) {
        return (x + this.radius) * this.totalSize + z + this.radius;
    }
}

