/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.climate;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.cell.GridCellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.JitterPointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.NormalPointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.PointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.jitter.ConstantCellJitter;
import com.hypixel.hytale.server.worldgen.climate.ClimateNoise;
import com.hypixel.hytale.server.worldgen.climate.DirectGrid;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClimateGridJsonLoader<K extends SeedResource>
extends JsonLoader<K, ClimateNoise.Grid> {
    public ClimateGridJsonLoader(SeedString<K> seed, Path dataFolder, @Nullable JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public ClimateNoise.Grid load() {
        if (this.json == null) {
            return Constants.DEFAULT_GRID;
        }
        return new ClimateNoise.Grid(this.loadSeed(), this.loadScale(), GridCellDistanceFunction.DISTANCE_FUNCTION, this.loadEvaluator());
    }

    protected int loadSeed() {
        int seedVal = this.seed.hashCode();
        if (this.has("Seed")) {
            SeedString overwritten = this.seed.appendToOriginal(this.get("Seed").getAsString());
            seedVal = overwritten.hashCode();
        }
        return seedVal;
    }

    protected double loadScale() {
        return this.mustGetNumber("Scale", Constants.DEFAULT_SCALE).doubleValue();
    }

    protected PointEvaluator loadEvaluator() {
        PointEvaluator pointEvaluator = NormalPointEvaluator.EUCLIDEAN;
        double jitter = this.mustGetNumber("Jitter", Constants.DEFAULT_JITTER).doubleValue();
        ConstantCellJitter biomeJitter = new ConstantCellJitter(jitter, jitter, jitter);
        return new JitterPointEvaluator(pointEvaluator, biomeJitter);
    }

    public static interface Constants {
        public static final String KEY_SEED = "Seed";
        public static final String KEY_SCALE = "Scale";
        public static final String KEY_JITTER = "Jitter";
        public static final Double DEFAULT_SCALE = 1.0;
        public static final Double DEFAULT_JITTER = 0.8;
        public static final ClimateNoise.Grid DEFAULT_GRID = new ClimateNoise.Grid(0, 1.0, DirectGrid.INSTANCE, NormalPointEvaluator.EUCLIDEAN);
    }
}

