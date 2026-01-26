/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.condition.ConstantIntCondition;
import com.hypixel.hytale.procedurallib.condition.IDoubleCondition;
import com.hypixel.hytale.procedurallib.condition.IIntCondition;
import com.hypixel.hytale.procedurallib.json.AbstractCellJitterJsonLoader;
import com.hypixel.hytale.procedurallib.json.DoubleConditionJsonLoader;
import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.BranchNoise;
import com.hypixel.hytale.procedurallib.logic.DistanceNoise;
import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import com.hypixel.hytale.procedurallib.logic.cell.CellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.CellPointFunction;
import com.hypixel.hytale.procedurallib.logic.cell.CellType;
import com.hypixel.hytale.procedurallib.logic.cell.DistanceCalculationMode;
import com.hypixel.hytale.procedurallib.logic.cell.GridCellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.HexCellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.BranchEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.DensityPointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.PointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.jitter.CellJitter;
import com.hypixel.hytale.procedurallib.property.NoiseFormulaProperty;
import com.hypixel.hytale.procedurallib.supplier.DoubleRange;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import java.nio.file.Path;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BranchNoiseJsonLoader<T extends SeedResource>
extends AbstractCellJitterJsonLoader<T, BranchNoise> {
    public BranchNoiseJsonLoader(SeedString<T> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public BranchNoise load() {
        Object resource = this.seed.get();
        CellType parentCellType = this.loadParentCellType();
        CellDistanceFunction parentFunction = BranchNoiseJsonLoader.getCellDistanceFunction(parentCellType);
        PointEvaluator parentEvaluator = this.loadParentEvaluator();
        double parentValue = this.loadDouble("ParentValue", 0.0);
        IDoubleRange parentFade = this.loadRange("ParentFade", 0.0);
        IIntCondition parentDensity = this.loadParentDensity();
        DistanceNoise.Distance2Function parentDistanceType = this.loadParentDistance2Function();
        NoiseFormulaProperty.NoiseFormula.Formula parentFormula = this.loadParentFormula();
        CellType lineCellType = this.loadLineCellType();
        CellPointFunction linePointFunction = BranchNoiseJsonLoader.getCellPointFunction(lineCellType);
        double lineScale = this.loadDouble("LineScale", 0.1);
        IDoubleRange lineThickness = this.loadRange("LineThickness", 0.1);
        CellDistanceFunction lineFunction = BranchNoiseJsonLoader.getCellDistanceFunction(lineCellType);
        PointEvaluator lineEvaluator = this.loadLineEvaluator(parentFunction, linePointFunction, lineScale);
        return new LoadedBranchNoise(parentFunction, parentEvaluator, parentValue, parentFade, parentDensity, parentDistanceType, parentFormula, lineFunction, lineEvaluator, lineScale, lineThickness, (SeedResource)resource);
    }

    protected CellType loadParentCellType() {
        return this.loadEnum("ParentType", CellType::valueOf, Constant.DEFAULT_PARENT_CELL_TYPE);
    }

    protected CellType loadLineCellType() {
        return this.loadEnum("LineType", CellType::valueOf, Constant.DEFAULT_LINE_CELL_TYPE);
    }

    protected PointEvaluator loadParentEvaluator() {
        double defaultJitter = this.loadDouble("ParentJitter", 1.0);
        double jitterX = this.loadDouble("ParentJitterX", defaultJitter);
        double jitterY = this.loadDouble("ParentJitterY", defaultJitter);
        CellJitter jitter = CellJitter.of(jitterX, jitterY, 1.0);
        DistanceCalculationMode distanceMode = this.loadEnum("ParentDistanceMode", DistanceCalculationMode::valueOf, Constant.DEFAULT_PARENT_DISTANCE_CAL_MODE);
        return PointEvaluator.of(distanceMode.getFunction(), null, null, jitter);
    }

    @Nonnull
    protected IIntCondition loadParentDensity() {
        IIntCondition density = ConstantIntCondition.DEFAULT_TRUE;
        if (this.has("ParentDensity")) {
            IDoubleCondition densityRange = new DoubleConditionJsonLoader(this.seed, this.dataFolder, this.get("ParentDensity")).load();
            density = DensityPointEvaluator.getDensityCondition(densityRange);
        }
        return density;
    }

    protected DistanceNoise.Distance2Function loadParentDistance2Function() {
        return this.loadEnum("ParentDistanceType", DistanceNoise.Distance2Mode::valueOf, Constant.DEFAULT_PARENT_DISTANCE_TYPE).getFunction();
    }

    protected NoiseFormulaProperty.NoiseFormula.Formula loadParentFormula() {
        return this.loadEnum("ParentFormula", NoiseFormulaProperty.NoiseFormula::valueOf, Constant.DEFAULT_PARENT_FORMULA).getFormula();
    }

    @Nonnull
    protected PointEvaluator loadLineEvaluator(@Nonnull CellDistanceFunction parentFunction, @Nonnull CellPointFunction linePointFunction, double lineScale) {
        double defaultJitter = this.loadDouble("LineJitter", 1.0);
        double jitterX = this.loadDouble("LineJitterX", defaultJitter);
        double jitterY = this.loadDouble("LineJitterY", defaultJitter);
        CellJitter jitter = CellJitter.of(jitterX, jitterY, 1.0);
        BranchEvaluator.Direction direction = this.loadEnum("LineDirection", BranchEvaluator.Direction::valueOf, Constant.DEFAULT_LINE_DIRECTION);
        return new BranchEvaluator(parentFunction, linePointFunction, direction, jitter, lineScale);
    }

    protected double loadDouble(String key, double def) {
        if (!this.has(key)) {
            return def;
        }
        return this.get(key).getAsDouble();
    }

    @Nullable
    protected IDoubleRange loadRange(String key, double def) {
        if (!this.has(key)) {
            return new DoubleRange.Constant(def);
        }
        return new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get(key)).load();
    }

    protected <E extends Enum<E>> E loadEnum(String key, @Nonnull Function<String, E> valueOf, E def) {
        if (!this.has(key)) {
            return def;
        }
        return (E)((Enum)valueOf.apply(this.get(key).getAsString()));
    }

    @Nonnull
    protected static CellDistanceFunction getCellDistanceFunction(@Nonnull CellType cellType) {
        return switch (cellType) {
            default -> throw new MatchException(null, null);
            case CellType.SQUARE -> GridCellDistanceFunction.DISTANCE_FUNCTION;
            case CellType.HEX -> HexCellDistanceFunction.DISTANCE_FUNCTION;
        };
    }

    @Nonnull
    protected static CellPointFunction getCellPointFunction(@Nonnull CellType cellType) {
        return switch (cellType) {
            default -> throw new MatchException(null, null);
            case CellType.SQUARE -> GridCellDistanceFunction.POINT_FUNCTION;
            case CellType.HEX -> HexCellDistanceFunction.POINT_FUNCTION;
        };
    }

    public static interface Constant {
        public static final String KEY_PARENT_TYPE = "ParentType";
        public static final String KEY_PARENT_DISTANCE_CALCULATION_MODE = "ParentDistanceMode";
        public static final String KEY_PARENT_DISTANCE_TYPE = "ParentDistanceType";
        public static final String KEY_PARENT_FORMULA = "ParentFormula";
        public static final String KEY_PARENT_JITTER = "ParentJitter";
        public static final String KEY_PARENT_JITTER_X = "ParentJitterX";
        public static final String KEY_PARENT_JITTER_Y = "ParentJitterY";
        public static final String KEY_PARENT_VALUE = "ParentValue";
        public static final String KEY_PARENT_FADE = "ParentFade";
        public static final String KEY_PARENT_DENSITY = "ParentDensity";
        public static final String KEY_LINE_TYPE = "LineType";
        public static final String KEY_LINE_DIRECTION_MODE = "LineDirection";
        public static final String KEY_LINE_SCALE = "LineScale";
        public static final String KEY_LINE_JITTER = "LineJitter";
        public static final String KEY_LINE_JITTER_X = "LineJitterX";
        public static final String KEY_LINE_JITTER_Y = "LineJitterY";
        public static final String KEY_LINE_THICKNESS = "LineThickness";
        public static final double DEFAULT_JITTER = 1.0;
        public static final double DEFAULT_PARENT_VALUE = 0.0;
        public static final double DEFAULT_PARENT_FADE = 0.0;
        public static final double DEFAULT_LINE_SCALE = 0.1;
        public static final double DEFAULT_LINE_THICKNESS = 0.1;
        public static final CellType DEFAULT_PARENT_CELL_TYPE = CellType.SQUARE;
        public static final CellType DEFAULT_LINE_CELL_TYPE = CellType.SQUARE;
        public static final BranchEvaluator.Direction DEFAULT_LINE_DIRECTION = BranchEvaluator.Direction.OUTWARD;
        public static final DistanceCalculationMode DEFAULT_PARENT_DISTANCE_CAL_MODE = DistanceCalculationMode.EUCLIDEAN;
        public static final DistanceNoise.Distance2Mode DEFAULT_PARENT_DISTANCE_TYPE = DistanceNoise.Distance2Mode.DIV;
        public static final NoiseFormulaProperty.NoiseFormula DEFAULT_PARENT_FORMULA = NoiseFormulaProperty.NoiseFormula.SQRT;
    }

    protected static class LoadedBranchNoise
    extends BranchNoise {
        protected final SeedResource seedResource;

        public LoadedBranchNoise(CellDistanceFunction parentFunction, PointEvaluator parentEvaluator, double parentValue, IDoubleRange parentFade, IIntCondition parentDensity, DistanceNoise.Distance2Function distance2Function, NoiseFormulaProperty.NoiseFormula.Formula noiseFormula, CellDistanceFunction lineFunction, PointEvaluator lineEvaluator, double lineScale, IDoubleRange lineThickness, SeedResource seedResource) {
            super(parentFunction, parentEvaluator, parentValue, parentFade, parentDensity, distance2Function, noiseFormula, lineFunction, lineEvaluator, lineScale, lineThickness);
            this.seedResource = seedResource;
        }

        @Override
        @Nonnull
        protected ResultBuffer.ResultBuffer2d localBuffer2d() {
            return this.seedResource.localBuffer2d();
        }

        @Override
        @Nonnull
        public String toString() {
            return "LoadedBranchNoise{seedResource=" + String.valueOf(this.seedResource) + ", parentFunction=" + String.valueOf(this.parentFunction) + ", parentEvaluator=" + String.valueOf(this.parentEvaluator) + ", parentValue=" + this.parentValue + ", parentFade=" + String.valueOf(this.parentFade) + ", distance2Function=" + String.valueOf(this.distance2Function) + ", noiseFormula=" + String.valueOf(this.noiseFormula) + ", lineFunction=" + String.valueOf(this.lineFunction) + ", lineEvaluator=" + String.valueOf(this.lineEvaluator) + ", lineScale=" + this.lineScale + ", lineThickness=" + String.valueOf(this.lineThickness) + "}";
        }
    }
}

