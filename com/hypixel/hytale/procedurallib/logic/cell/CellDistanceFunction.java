/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.cell;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.PointEvaluator;
import com.hypixel.hytale.procedurallib.logic.point.PointConsumer;

public interface CellDistanceFunction {
    default public double scale(double value) {
        return value;
    }

    default public double invScale(double value) {
        return value;
    }

    default public int getCellX(double x, double y) {
        return MathUtil.floor(x);
    }

    default public int getCellY(double x, double y) {
        return MathUtil.floor(y);
    }

    default public int getCellX(double x, double y, double z) {
        return MathUtil.floor(x);
    }

    default public int getCellY(double x, double y, double z) {
        return MathUtil.floor(y);
    }

    default public int getCellZ(double x, double y, double z) {
        return MathUtil.floor(z);
    }

    public void nearest2D(int var1, double var2, double var4, int var6, int var7, ResultBuffer.ResultBuffer2d var8, PointEvaluator var9);

    public void nearest3D(int var1, double var2, double var4, double var6, int var8, int var9, int var10, ResultBuffer.ResultBuffer3d var11, PointEvaluator var12);

    public void transition2D(int var1, double var2, double var4, int var6, int var7, ResultBuffer.ResultBuffer2d var8, PointEvaluator var9);

    public void transition3D(int var1, double var2, double var4, double var6, int var8, int var9, int var10, ResultBuffer.ResultBuffer3d var11, PointEvaluator var12);

    public void evalPoint(int var1, double var2, double var4, int var6, int var7, ResultBuffer.ResultBuffer2d var8, PointEvaluator var9);

    public void evalPoint(int var1, double var2, double var4, double var6, int var8, int var9, int var10, ResultBuffer.ResultBuffer3d var11, PointEvaluator var12);

    public void evalPoint2(int var1, double var2, double var4, int var6, int var7, ResultBuffer.ResultBuffer2d var8, PointEvaluator var9);

    public void evalPoint2(int var1, double var2, double var4, double var6, int var8, int var9, int var10, ResultBuffer.ResultBuffer3d var11, PointEvaluator var12);

    public <T> void collect(int var1, int var2, int var3, int var4, int var5, int var6, ResultBuffer.Bounds2d var7, T var8, PointConsumer<T> var9, PointEvaluator var10);
}

