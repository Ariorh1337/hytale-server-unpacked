/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.condition;

import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
import javax.annotation.Nonnull;

public class DefaultBlockMaskCondition
implements BlockMaskCondition {
    public static final DefaultBlockMaskCondition DEFAULT_TRUE = new DefaultBlockMaskCondition(true);
    public static final DefaultBlockMaskCondition DEFAULT_FALSE = new DefaultBlockMaskCondition(false);
    private final boolean result;

    public DefaultBlockMaskCondition(boolean result) {
        this.result = result;
    }

    @Override
    public boolean eval(int currentBlockId, int currentFluidId, BlockFluidEntry next) {
        return this.result;
    }

    @Nonnull
    public String toString() {
        return "DefaultBiIntCondition{result=" + this.result + "}";
    }
}

