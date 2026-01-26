/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.cell.jitter;

import com.hypixel.hytale.procedurallib.logic.DoubleArray;
import com.hypixel.hytale.procedurallib.logic.cell.jitter.CellJitter;
import javax.annotation.Nonnull;

public class DefaultCellJitter
implements CellJitter {
    public static final CellJitter DEFAULT_ONE = new DefaultCellJitter();

    @Override
    public double getMaxX() {
        return 1.0;
    }

    @Override
    public double getMaxY() {
        return 1.0;
    }

    @Override
    public double getMaxZ() {
        return 1.0;
    }

    @Override
    public double getPointX(int cx, @Nonnull DoubleArray.Double2 vec) {
        return (double)cx + vec.x;
    }

    @Override
    public double getPointY(int cy, @Nonnull DoubleArray.Double2 vec) {
        return (double)cy + vec.y;
    }

    @Override
    public double getPointX(int cx, @Nonnull DoubleArray.Double3 vec) {
        return (double)cx + vec.x;
    }

    @Override
    public double getPointY(int cy, @Nonnull DoubleArray.Double3 vec) {
        return (double)cy + vec.y;
    }

    @Override
    public double getPointZ(int cz, @Nonnull DoubleArray.Double3 vec) {
        return (double)cz + vec.z;
    }

    @Nonnull
    public String toString() {
        return "DefaultCellJitter{}";
    }
}

