/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.cell;

import com.hypixel.hytale.procedurallib.logic.DoubleArray;

public interface CellPointFunction {
    default public double scale(double value) {
        return value;
    }

    default public double normalize(double value) {
        return value;
    }

    public int getHash(int var1, int var2, int var3);

    public double getX(double var1, double var3);

    public double getY(double var1, double var3);

    public DoubleArray.Double2 getOffsets(int var1);
}

