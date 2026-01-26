/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.supplier;

import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateHashSupplier;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import javax.annotation.Nonnull;

public class DoubleRangeCoordinateHashSupplier
implements IDoubleCoordinateHashSupplier {
    protected final IDoubleRange range;

    public DoubleRangeCoordinateHashSupplier(IDoubleRange range) {
        this.range = range;
    }

    @Override
    public double get(int seed, int x, int y, long hash) {
        return this.range.getValue(HashUtil.random(seed, x, y, hash));
    }

    @Nonnull
    public String toString() {
        return "DoubleRangeCoordinateHashSupplier{range=" + String.valueOf(this.range) + "}";
    }
}

