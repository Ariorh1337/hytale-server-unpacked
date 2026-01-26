/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.condition;

import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
import javax.annotation.Nonnull;

public class ConstantBlockFluidCondition
implements IBlockFluidCondition {
    public static final ConstantBlockFluidCondition DEFAULT_TRUE = new ConstantBlockFluidCondition(true);
    public static final ConstantBlockFluidCondition DEFAULT_FALSE = new ConstantBlockFluidCondition(false);
    protected final boolean result;

    public ConstantBlockFluidCondition(boolean result) {
        this.result = result;
    }

    @Override
    public boolean eval(int block, int fluid) {
        return this.result;
    }

    @Nonnull
    public String toString() {
        return "ConstantBlockFluidCondition{result=" + this.result + "}";
    }
}

