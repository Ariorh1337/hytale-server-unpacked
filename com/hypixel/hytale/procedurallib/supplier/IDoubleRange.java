/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.supplier;

import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier2d;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier3d;
import java.util.Random;
import java.util.function.DoubleSupplier;

public interface IDoubleRange {
    public double getValue(double var1);

    public double getValue(DoubleSupplier var1);

    public double getValue(Random var1);

    public double getValue(int var1, double var2, double var4, IDoubleCoordinateSupplier2d var6);

    public double getValue(int var1, double var2, double var4, double var6, IDoubleCoordinateSupplier3d var8);
}

