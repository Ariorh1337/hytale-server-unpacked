/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.supplier;

import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier2d;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier3d;
import com.hypixel.hytale.procedurallib.supplier.IFloatCoordinateSupplier;
import com.hypixel.hytale.procedurallib.supplier.IFloatRange;
import javax.annotation.Nonnull;

public class FloatRangeNoiseSupplier
implements IFloatCoordinateSupplier {
    protected final IFloatRange range;
    @Nonnull
    protected final NoiseProperty noiseProperty;
    @Nonnull
    protected final IDoubleCoordinateSupplier2d supplier2d;
    @Nonnull
    protected final IDoubleCoordinateSupplier3d supplier3d;

    public FloatRangeNoiseSupplier(IFloatRange range, @Nonnull NoiseProperty noiseProperty) {
        this.range = range;
        this.noiseProperty = noiseProperty;
        this.supplier2d = noiseProperty::get;
        this.supplier3d = noiseProperty::get;
    }

    @Override
    public float get(int seed, double x, double y) {
        return this.range.getValue(seed, x, y, this.supplier2d);
    }

    @Override
    public float get(int seed, double x, double y, double z) {
        return this.range.getValue(seed, x, y, z, this.supplier3d);
    }

    @Nonnull
    public String toString() {
        return "FloatRangeNoiseSupplier{range=" + String.valueOf(this.range) + ", noiseProperty=" + String.valueOf(this.noiseProperty) + "}";
    }
}

