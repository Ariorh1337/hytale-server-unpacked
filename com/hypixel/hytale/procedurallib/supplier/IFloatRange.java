/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.supplier;

import com.hypixel.hytale.procedurallib.supplier.FloatSupplier;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier2d;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier3d;
import java.util.Random;

public interface IFloatRange {
    public float getValue(float var1);

    public float getValue(FloatSupplier var1);

    public float getValue(Random var1);

    public float getValue(int var1, double var2, double var4, IDoubleCoordinateSupplier2d var6);

    public float getValue(int var1, double var2, double var4, double var6, IDoubleCoordinateSupplier3d var8);
}

