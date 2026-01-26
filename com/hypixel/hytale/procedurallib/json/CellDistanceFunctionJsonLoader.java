/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.CellBorderDistanceFunctionJsonLoader;
import com.hypixel.hytale.procedurallib.json.CellNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.cell.CellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.CellType;
import com.hypixel.hytale.procedurallib.logic.cell.GridCellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.HexCellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.MeasurementMode;
import com.hypixel.hytale.procedurallib.logic.cell.PointDistanceFunction;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CellDistanceFunctionJsonLoader<K extends SeedResource>
extends JsonLoader<K, CellDistanceFunction> {
    protected final MeasurementMode measurementMode;
    protected final PointDistanceFunction pointDistanceFunction;

    public CellDistanceFunctionJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, @Nullable PointDistanceFunction pointDistanceFunction) {
        this(seed, dataFolder, json, MeasurementMode.CENTRE_DISTANCE, pointDistanceFunction);
    }

    public CellDistanceFunctionJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, MeasurementMode measurementMode, @Nullable PointDistanceFunction pointDistanceFunction) {
        super(seed.append(".CellDistanceFunction"), dataFolder, json);
        this.measurementMode = measurementMode;
        this.pointDistanceFunction = pointDistanceFunction;
    }

    @Override
    public CellDistanceFunction load() {
        CellDistanceFunction distanceFunction = this.loadDistanceFunction();
        return switch (this.measurementMode) {
            default -> throw new MatchException(null, null);
            case MeasurementMode.CENTRE_DISTANCE -> distanceFunction;
            case MeasurementMode.BORDER_DISTANCE -> new CellBorderDistanceFunctionJsonLoader(this.seed, this.dataFolder, this.json, distanceFunction).load();
        };
    }

    @Nonnull
    protected CellType loadCellType() {
        CellType cellType = CellNoiseJsonLoader.Constants.DEFAULT_CELL_TYPE;
        if (this.has("CellType")) {
            cellType = CellType.valueOf(this.get("CellType").getAsString());
        }
        return cellType;
    }

    @Nonnull
    protected CellDistanceFunction loadDistanceFunction() {
        return switch (this.loadCellType()) {
            default -> throw new MatchException(null, null);
            case CellType.SQUARE -> GridCellDistanceFunction.DISTANCE_FUNCTION;
            case CellType.HEX -> HexCellDistanceFunction.DISTANCE_FUNCTION;
        };
    }
}

