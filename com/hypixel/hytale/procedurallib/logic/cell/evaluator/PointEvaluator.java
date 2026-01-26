/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.cell.evaluator;

import com.hypixel.hytale.procedurallib.condition.DefaultDoubleCondition;
import com.hypixel.hytale.procedurallib.condition.IDoubleCondition;
import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import com.hypixel.hytale.procedurallib.logic.cell.PointDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.DensityPointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.DistancePointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.JitterPointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.NormalPointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.SkipCellPointEvaluator;
import com.hypixel.hytale.procedurallib.logic.cell.jitter.CellJitter;
import com.hypixel.hytale.procedurallib.logic.cell.jitter.DefaultCellJitter;
import com.hypixel.hytale.procedurallib.logic.point.PointConsumer;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PointEvaluator {
    default public CellJitter getJitter() {
        return DefaultCellJitter.DEFAULT_ONE;
    }

    public void evalPoint(int var1, double var2, double var4, int var6, int var7, int var8, double var9, double var11, ResultBuffer.ResultBuffer2d var13);

    public void evalPoint2(int var1, double var2, double var4, int var6, int var7, int var8, double var9, double var11, ResultBuffer.ResultBuffer2d var13);

    public void evalPoint(int var1, double var2, double var4, double var6, int var8, int var9, int var10, int var11, double var12, double var14, double var16, ResultBuffer.ResultBuffer3d var18);

    public void evalPoint2(int var1, double var2, double var4, double var6, int var8, int var9, int var10, int var11, double var12, double var14, double var16, ResultBuffer.ResultBuffer3d var18);

    default public <T> void collectPoint(int cellHash, int cellX, int cellY, double cellCentreX, double cellCentreY, T ctx, @Nonnull PointConsumer<T> consumer) {
        consumer.accept(cellCentreX, cellCentreY, ctx);
    }

    public static PointEvaluator of(PointDistanceFunction distanceFunction, @Nullable IDoubleCondition density, @Nullable IDoubleRange distanceMod, CellJitter jitter) {
        return PointEvaluator.of(distanceFunction, density, distanceMod, 0, SkipCellPointEvaluator.DEFAULT_MODE, jitter);
    }

    public static PointEvaluator of(PointDistanceFunction distanceFunction, @Nullable IDoubleCondition density, @Nullable IDoubleRange distanceMod, int skipCount, @Nonnull SkipCellPointEvaluator.Mode skipMode, CellJitter jitter) {
        PointEvaluator pointEvaluator = NormalPointEvaluator.of(distanceFunction);
        if (distanceMod != null) {
            pointEvaluator = new DistancePointEvaluator(distanceFunction, distanceMod);
        }
        if (density != null && density != DefaultDoubleCondition.DEFAULT_TRUE) {
            pointEvaluator = new DensityPointEvaluator(pointEvaluator, density);
        }
        if (skipCount > 0) {
            pointEvaluator = new SkipCellPointEvaluator(pointEvaluator, skipMode, skipCount);
        }
        if (jitter != DefaultCellJitter.DEFAULT_ONE) {
            pointEvaluator = new JitterPointEvaluator(pointEvaluator, jitter);
        }
        return pointEvaluator;
    }
}

