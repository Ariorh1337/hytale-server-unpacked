/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.fluid;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import javax.annotation.Nonnull;

public record FluidState(int fluidLevel, byte verticalFill) {
    public static int SOURCE_LEVEL = 0;
    public static final int FULL_LEVEL = 8;
    public static final FluidState[] FLUID_STATES = FluidState.generateFluidStates(8);

    public FluidState(int fluidLevel, int verticalFill) {
        this(fluidLevel, (byte)verticalFill);
    }

    @Nonnull
    public static FluidState[] generateFluidStates(int maxLevel) {
        ObjectArrayList fluidStateList = new ObjectArrayList();
        fluidStateList.add(new FluidState(SOURCE_LEVEL, maxLevel));
        for (int i = 1; i <= maxLevel; ++i) {
            fluidStateList.add(new FluidState(i, i));
        }
        return (FluidState[])fluidStateList.toArray(FluidState[]::new);
    }

    @Override
    @Nonnull
    public String toString() {
        return "FluidState{fluidLevel=" + this.fluidLevel + ", verticalFill=" + this.verticalFill + "}";
    }
}

