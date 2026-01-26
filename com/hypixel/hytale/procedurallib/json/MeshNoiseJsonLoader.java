/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.condition.DoubleThresholdCondition;
import com.hypixel.hytale.procedurallib.condition.IIntCondition;
import com.hypixel.hytale.procedurallib.json.AbstractCellJitterJsonLoader;
import com.hypixel.hytale.procedurallib.json.CellNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.DoubleThresholdJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.HexMeshNoise;
import com.hypixel.hytale.procedurallib.logic.MeshNoise;
import com.hypixel.hytale.procedurallib.logic.cell.CellType;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.DensityPointEvaluator;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class MeshNoiseJsonLoader<K extends SeedResource>
extends AbstractCellJitterJsonLoader<K, NoiseFunction> {
    public MeshNoiseJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".MeshNoise"), dataFolder, json);
    }

    @Override
    public NoiseFunction load() {
        return switch (this.loadCellType()) {
            default -> throw new MatchException(null, null);
            case CellType.SQUARE -> this.loadGridMeshNoise();
            case CellType.HEX -> this.loadHexMeshNoise();
        };
    }

    @Nonnull
    protected MeshNoise loadGridMeshNoise() {
        double defaultJitter = this.loadDefaultJitter();
        return new MeshNoise(this.loadDensity(), this.loadThickness(), this.loadJitterX(defaultJitter), this.loadJitterY(defaultJitter));
    }

    @Nonnull
    protected HexMeshNoise loadHexMeshNoise() {
        return new HexMeshNoise(this.loadDensity(), this.loadThickness(), this.loadJitter(), this.loadLinesX(), this.loadLinesY(), this.loadLinesZ());
    }

    @Nonnull
    protected CellType loadCellType() {
        CellType cellType = CellNoiseJsonLoader.Constants.DEFAULT_CELL_TYPE;
        if (this.has("CellType")) {
            cellType = CellType.valueOf(this.get("CellType").getAsString());
        }
        return cellType;
    }

    protected double loadThickness() {
        if (!this.has("Thickness")) {
            throw new IllegalStateException("Could not find thickness. Keyword: Thickness");
        }
        return this.get("Thickness").getAsDouble();
    }

    @Nonnull
    protected IIntCondition loadDensity() {
        return DensityPointEvaluator.getDensityCondition(this.has("Density") ? new DoubleThresholdCondition(new DoubleThresholdJsonLoader(this.seed, this.dataFolder, this.get("Density")).load()) : null);
    }

    protected boolean loadLinesX() {
        return this.loadLinesFlag("LinesX", true);
    }

    protected boolean loadLinesY() {
        return this.loadLinesFlag("LinesY", true);
    }

    protected boolean loadLinesZ() {
        return this.loadLinesFlag("LinesZ", true);
    }

    protected boolean loadLinesFlag(String key, boolean defaulValue) {
        if (!this.has(key)) {
            return defaulValue;
        }
        return this.get(key).getAsBoolean();
    }

    public static interface Constants {
        public static final String KEY_THICKNESS = "Thickness";
        public static final String KEY_LINES_X = "LinesX";
        public static final String KEY_LINES_Y = "LinesY";
        public static final String KEY_LINES_Z = "LinesZ";
        public static final String ERROR_NO_THICKNESS = "Could not find thickness. Keyword: Thickness";
        public static final boolean DEFAULT_LINES_X = true;
        public static final boolean DEFAULT_LINES_Y = true;
        public static final boolean DEFAULT_LINES_Z = true;
    }
}

