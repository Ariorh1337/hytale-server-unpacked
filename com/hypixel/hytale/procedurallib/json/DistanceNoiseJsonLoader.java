/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.json.CellDistanceFunctionJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.PointEvaluatorJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.DistanceNoise;
import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import com.hypixel.hytale.procedurallib.logic.cell.CellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.MeasurementMode;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.PointEvaluator;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DistanceNoiseJsonLoader<K extends SeedResource>
extends JsonLoader<K, NoiseFunction> {
    public DistanceNoiseJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".DistanceNoise"), dataFolder, json);
    }

    @Override
    @Nonnull
    public NoiseFunction load() {
        CellDistanceFunction cellDistanceFunction = this.loadCellDistanceFunction();
        PointEvaluator pointEvaluator = this.loadPointEvaluator();
        DistanceNoise.Distance2Function distance2Function = this.loadDistance2Function();
        return new LoadedDistanceNoise(cellDistanceFunction, pointEvaluator, distance2Function, (SeedResource)this.seed.get());
    }

    @Nullable
    protected CellDistanceFunction loadCellDistanceFunction() {
        MeasurementMode measurementMode = this.loadMeasurementMode();
        return new CellDistanceFunctionJsonLoader(this.seed, this.dataFolder, this.json, measurementMode, null).load();
    }

    @Nullable
    protected PointEvaluator loadPointEvaluator() {
        return new PointEvaluatorJsonLoader(this.seed, this.dataFolder, this.json).load();
    }

    @Nonnull
    protected MeasurementMode loadMeasurementMode() {
        if (this.has("Measurement")) {
            return MeasurementMode.valueOf(this.get("Measurement").getAsString());
        }
        return Constants.DEFAULT_MEASUREMENT;
    }

    protected DistanceNoise.Distance2Function loadDistance2Function() {
        DistanceNoise.Distance2Mode distance2Mode = Constants.DEFAULT_DISTANCE_2_MODE;
        if (this.has("Distance2Mode")) {
            distance2Mode = DistanceNoise.Distance2Mode.valueOf(this.get("Distance2Mode").getAsString());
        }
        return distance2Mode.getFunction();
    }

    public static interface Constants {
        public static final String KEY_MEASUREMENT = "Measurement";
        public static final String KEY_DISTANCE_2_MODE = "Distance2Mode";
        public static final MeasurementMode DEFAULT_MEASUREMENT = MeasurementMode.CENTRE_DISTANCE;
        public static final DistanceNoise.Distance2Mode DEFAULT_DISTANCE_2_MODE = DistanceNoise.Distance2Mode.SUB;
    }

    private static class LoadedDistanceNoise
    extends DistanceNoise {
        private final SeedResource seedResource;

        public LoadedDistanceNoise(CellDistanceFunction cellDistanceFunction, PointEvaluator pointEvaluator, DistanceNoise.Distance2Function distance2Function, SeedResource seedResource) {
            super(cellDistanceFunction, pointEvaluator, distance2Function);
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

