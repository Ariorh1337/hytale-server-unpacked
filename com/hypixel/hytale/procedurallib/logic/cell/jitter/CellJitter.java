/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.cell.jitter;

import com.hypixel.hytale.procedurallib.logic.DoubleArray;
import com.hypixel.hytale.procedurallib.logic.cell.jitter.ConstantCellJitter;
import com.hypixel.hytale.procedurallib.logic.cell.jitter.DefaultCellJitter;
import javax.annotation.Nonnull;

public interface CellJitter {
    public double getMaxX();

    public double getMaxY();

    public double getMaxZ();

    public double getPointX(int var1, DoubleArray.Double2 var2);

    public double getPointY(int var1, DoubleArray.Double2 var2);

    public double getPointX(int var1, DoubleArray.Double3 var2);

    public double getPointY(int var1, DoubleArray.Double3 var2);

    public double getPointZ(int var1, DoubleArray.Double3 var2);

    @Nonnull
    public static CellJitter of(double x, double y, double z) {
        if (x == 1.0 && y == 1.0 && z == 1.0) {
            return DefaultCellJitter.DEFAULT_ONE;
        }
        return new ConstantCellJitter(x, y, z);
    }
}

