/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cache;

import com.hypixel.hytale.metrics.metric.AverageCollector;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nonnull;

public class InterpolatedBiomeCountList {
    @Nonnull
    private final Int2ObjectMap<BiomeCountResult> results = new Int2ObjectOpenHashMap<BiomeCountResult>();
    @Nonnull
    private final IntList biomeIds = new IntArrayList();
    private Biome center;

    public BiomeCountResult get(@Nonnull Biome biome) {
        return this.get(biome.getId());
    }

    public BiomeCountResult get(int index) {
        return (BiomeCountResult)this.results.get(index);
    }

    public void setCenter(@Nonnull ZoneBiomeResult result) {
        Biome biome;
        this.center = biome = result.getBiome();
        this.biomeIds.add(biome.getId());
        this.results.put(biome.getId(), new BiomeCountResult(biome, result.heightThresholdContext, result.heightmapNoise));
    }

    public void add(@Nonnull ZoneBiomeResult result, int distance2) {
        Biome biome = result.getBiome();
        int biomeId = biome.getId();
        if (this.center.getInterpolation().getBiomeRadius2(biomeId) < distance2) {
            return;
        }
        BiomeCountResult r = this.get(biomeId);
        if (r == null) {
            this.biomeIds.add(biomeId);
            this.results.put(biomeId, new BiomeCountResult(biome, result.heightThresholdContext, result.heightmapNoise));
        } else {
            r.heightNoise = AverageCollector.add(r.heightNoise, result.heightmapNoise, r.count);
            ++r.count;
        }
    }

    @Nonnull
    public IntList getBiomeIds() {
        return this.biomeIds;
    }

    @Nonnull
    public String toString() {
        return "InterpolatedBiomeCountList{results=" + String.valueOf(this.results) + ", biomeIds=" + String.valueOf(this.biomeIds) + "}";
    }

    public static class BiomeCountResult {
        @Nonnull
        public final Biome biome;
        public double heightThresholdContext;
        public double heightNoise;
        public int count;

        public BiomeCountResult(@Nonnull Biome biome, double heightThresholdContext, double heightNoise) {
            this.biome = biome;
            this.heightThresholdContext = heightThresholdContext;
            this.heightNoise = heightNoise;
            this.count = 1;
        }

        @Nonnull
        public String toString() {
            return "BiomeCountResult{biome=" + String.valueOf(this.biome) + ", heightThresholdContext=" + this.heightThresholdContext + ", heightNoise=" + this.heightNoise + ", count=" + this.count + "}";
        }
    }
}

