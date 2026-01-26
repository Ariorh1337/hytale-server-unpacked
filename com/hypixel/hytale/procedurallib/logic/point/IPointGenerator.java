/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.point;

import com.hypixel.hytale.procedurallib.logic.ResultBuffer;

public interface IPointGenerator {
    public ResultBuffer.ResultBuffer2d nearest2D(int var1, double var2, double var4);

    public ResultBuffer.ResultBuffer3d nearest3D(int var1, double var2, double var4, double var6);

    public ResultBuffer.ResultBuffer2d transition2D(int var1, double var2, double var4);

    public ResultBuffer.ResultBuffer3d transition3D(int var1, double var2, double var4, double var6);

    public void collect(int var1, double var2, double var4, double var6, double var8, PointConsumer2d var10);

    public double getInterval();

    @FunctionalInterface
    public static interface PointConsumer2d {
        public void accept(double var1, double var3);
    }
}

