/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.supplier;

import com.hypixel.hytale.procedurallib.supplier.IFloatCoordinateHashSupplier;
import javax.annotation.Nonnull;

public class ConstantFloatCoordinateHashSupplier
implements IFloatCoordinateHashSupplier {
    public static final ConstantFloatCoordinateHashSupplier ZERO = new ConstantFloatCoordinateHashSupplier(0.0f);
    public static final ConstantFloatCoordinateHashSupplier ONE = new ConstantFloatCoordinateHashSupplier(1.0f);
    protected final float result;

    public ConstantFloatCoordinateHashSupplier(float result) {
        this.result = result;
    }

    public float getResult() {
        return this.result;
    }

    @Override
    public float get(int seed, double x, double y, long hash) {
        return this.result;
    }

    @Nonnull
    public String toString() {
        return "ConstantFloatCoordinateHashSupplier{result=" + this.result + "}";
    }
}

