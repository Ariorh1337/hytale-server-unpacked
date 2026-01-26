/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.condition;

import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
import javax.annotation.Nonnull;

public class FilteredBlockFluidCondition
implements IBlockFluidCondition {
    private final IBlockFluidCondition filter;
    private final IBlockFluidCondition condition;

    public FilteredBlockFluidCondition(int blockId, IBlockFluidCondition condition) {
        this((block, fluid) -> block == blockId && fluid == 0, condition);
    }

    public FilteredBlockFluidCondition(IBlockFluidCondition filter, IBlockFluidCondition condition) {
        this.filter = filter;
        this.condition = condition;
    }

    @Override
    public boolean eval(int block, int fluid) {
        if (this.filter.eval(block, fluid)) {
            return false;
        }
        return this.condition.eval(block, fluid);
    }

    @Nonnull
    public String toString() {
        return "FilteredBlockFluidCondition{filter=" + String.valueOf(this.filter) + ", condition=" + String.valueOf(this.condition) + "}";
    }
}

