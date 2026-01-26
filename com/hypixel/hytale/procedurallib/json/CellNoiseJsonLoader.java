/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.json.CellDistanceFunctionJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.PointEvaluatorJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.CellNoise;
import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import com.hypixel.hytale.procedurallib.logic.cell.CellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.CellType;
import com.hypixel.hytale.procedurallib.logic.cell.DistanceCalculationMode;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.PointEvaluator;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CellNoiseJsonLoader<K extends SeedResource>
extends JsonLoader<K, NoiseFunction> {
    public CellNoiseJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".CellNoise"), dataFolder, json);
    }

    @Override
    @Nonnull
    public NoiseFunction load() {
        CellDistanceFunction cellDistanceFunction = this.loadCellDistanceFunction();
        PointEvaluator pointEvaluator = this.loadPointEvaluator();
        CellNoise.CellFunction cellFunction = this.loadCellFunction();
        NoiseProperty noiseLookup = this.loadNoiseLookup();
        return new LoadedCellNoise(cellDistanceFunction, pointEvaluator, cellFunction, noiseLookup, (SeedResource)this.seed.get());
    }

    @Nullable
    protected CellDistanceFunction loadCellDistanceFunction() {
        return new CellDistanceFunctionJsonLoader(this.seed, this.dataFolder, this.json, null).load();
    }

    @Nullable
    protected PointEvaluator loadPointEvaluator() {
        return new PointEvaluatorJsonLoader(this.seed, this.dataFolder, this.json).load();
    }

    protected CellNoise.CellFunction loadCellFunction() {
        CellNoise.CellMode cellMode = Constants.DEFAULT_CELL_MODE;
        if (this.has("CellMode")) {
            cellMode = CellNoise.CellMode.valueOf(this.get("CellMode").getAsString());
        }
        return cellMode.getFunction();
    }

    @Nullable
    protected NoiseProperty loadNoiseLookup() {
        NoiseProperty noiseProperty = null;
        if (this.has("NoiseLookup")) {
            noiseProperty = new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("NoiseLookup")).load();
        }
        return noiseProperty;
    }

    public static interface Constants {
        public static final String KEY_JITTER = "Jitter";
        public static final String KEY_JITTER_X = "JitterX";
        public static final String KEY_JITTER_Y = "JitterY";
        public static final String KEY_JITTER_Z = "JitterZ";
        public static final String KEY_DENSITY = "Density";
        public static final String KEY_CELL_MODE = "CellMode";
        public static final String KEY_NOISE_LOOKUP = "NoiseLookup";
        public static final String KEY_DISTANCE_MODE = "DistanceMode";
        public static final String KEY_DISTANCE_RANGE = "DistanceRange";
        public static final String KEY_CELL_TYPE = "CellType";
        public static final String KEY_SKIP_CELLS = "Skip";
        public static final String KEY_SKIP_MODE = "SkipMode";
        public static final double DEFAULT_JITTER = 1.0;
        public static final double DEFAULT_DISTANCE_RANGE = 1.0;
        public static final double DEFAULT_DENSITY_LOWER = 0.0;
        public static final double DEFAULT_DENSITY_UPPER = 1.0;
        public static final DistanceCalculationMode DEFAULT_DISTANCE_MODE = DistanceCalculationMode.EUCLIDEAN;
        public static final CellNoise.CellMode DEFAULT_CELL_MODE = CellNoise.CellMode.CELL_VALUE;
        public static final CellType DEFAULT_CELL_TYPE = CellType.SQUARE;
    }

    private static class LoadedCellNoise
    extends CellNoise {
        private final SeedResource seedResource;

        public LoadedCellNoise(CellDistanceFunction cellDistanceFunction, PointEvaluator pointEvaluator, CellNoise.CellFunction cellFunction, @Nullable NoiseProperty noiseLookup, SeedResource seedResource) {
            super(cellDistanceFunction, pointEvaluator, cellFunction, noiseLookup);
            this.seedResource = seedResource;
        }

        @Override
        @Nonnull
        protected ResultBuffer.ResultBuffer2d localBuffer2d() {
            return this.seedResource.localBuffer2d();
        }

        @Override
        @Nonnull
        protected ResultBuffer.ResultBuffer3d localBuffer3d() {
            return this.seedResource.localBuffer3d();
        }
    }
}

