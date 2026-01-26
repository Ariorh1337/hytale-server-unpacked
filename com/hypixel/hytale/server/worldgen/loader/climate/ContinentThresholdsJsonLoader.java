/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.climate;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.climate.ClimateNoise;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class ContinentThresholdsJsonLoader<K extends SeedResource>
extends JsonLoader<K, ClimateNoise.Thresholds> {
    public ContinentThresholdsJsonLoader(SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public ClimateNoise.Thresholds load() {
        return new ClimateNoise.Thresholds(this.loadLandThreshold(), this.loadIslandThreshold(), this.loadBeachSize(), this.loadShallowOceanSize());
    }

    protected double loadLandThreshold() {
        return this.mustGetNumber("Land", Constants.DEFAULT_LAND).doubleValue();
    }

    protected double loadIslandThreshold() {
        return this.mustGetNumber("Island", Constants.DEFAULT_ISLAND).doubleValue();
    }

    protected double loadBeachSize() {
        return this.mustGetNumber("BeachSize", Constants.DEFAULT_BEACH_SIZE).doubleValue();
    }

    protected double loadShallowOceanSize() {
        return this.mustGetNumber("ShallowOceanSize", Constants.DEFAULT_SHALLOW_OCEAN_SIZE).doubleValue();
    }

    public static interface Constants {
        public static final String KEY_LAND = "Land";
        public static final String KEY_ISLAND = "Island";
        public static final String KEY_BEACH_SIZE = "BeachSize";
        public static final String KEY_SHALLOW_OCEAN_SIZE = "ShallowOceanSize";
        public static final Double DEFAULT_LAND = 0.5;
        public static final Double DEFAULT_ISLAND = 0.8;
        public static final Double DEFAULT_BEACH_SIZE = 0.05;
        public static final Double DEFAULT_SHALLOW_OCEAN_SIZE = 0.15;
    }
}

