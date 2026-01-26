/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.cell;

public interface PointDistanceFunction {
    public double distance2D(double var1, double var3);

    public double distance3D(double var1, double var3, double var5);

    default public double distance2D(int seed, int cellX, int cellY, double cellCentreX, double cellCentreY, double deltaX, double deltaY) {
        return this.distance2D(deltaX, deltaY);
    }

    default public double distance3D(int seed, int cellX, int cellY, int cellZ, double cellCentreX, double cellCentreY, double cellCentreZ, double deltaX, double deltaY, double deltaZ) {
        return this.distance3D(deltaX, deltaY, deltaZ);
    }
}

