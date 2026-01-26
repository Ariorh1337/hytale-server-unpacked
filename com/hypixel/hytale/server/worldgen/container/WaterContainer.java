/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.container;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier;
import javax.annotation.Nonnull;

public class WaterContainer {
    public static final int NO_WATER_AT_COORDINATED = Integer.MIN_VALUE;
    private final Entry[] entries;

    public static boolean isValidWaterHeight(int height) {
        return height > 0;
    }

    public WaterContainer(Entry[] entries) {
        this.entries = entries;
    }

    public boolean hasEntries() {
        return this.entries.length != 0;
    }

    public Entry[] getEntries() {
        return this.entries;
    }

    public int getMaxHeight(int seed, int x, int z) {
        int totalMax = Integer.MIN_VALUE;
        for (Entry entry : this.entries) {
            int max;
            int min = entry.getMin(seed, x, z);
            if (min > (max = entry.getMax(seed, x, z)) || max <= totalMax) continue;
            totalMax = max;
        }
        if (totalMax < 0) {
            return Integer.MIN_VALUE;
        }
        return totalMax;
    }

    public static class Entry {
        public static final Entry[] EMPTY_ARRAY = new Entry[0];
        private final int block;
        private final int fluid;
        private final IDoubleCoordinateSupplier min;
        private final IDoubleCoordinateSupplier max;
        private final ICoordinateCondition mask;

        public Entry(int block, int fluid, IDoubleCoordinateSupplier min, IDoubleCoordinateSupplier max, ICoordinateCondition mask) {
            this.block = block;
            this.fluid = fluid;
            this.min = min;
            this.max = max;
            this.mask = mask;
        }

        public int getBlock() {
            return this.block;
        }

        public int getFluid() {
            return this.fluid;
        }

        public int getMax(int seed, int x, int z) {
            return MathUtil.floor(this.max.get(seed, x, z));
        }

        public int getMin(int seed, int x, int z) {
            return MathUtil.floor(this.min.get(seed, x, z));
        }

        public boolean shouldPopulate(int seed, int x, int z) {
            return this.mask.eval(seed, x, z);
        }

        public IDoubleCoordinateSupplier getMax() {
            return this.max;
        }

        public IDoubleCoordinateSupplier getMin() {
            return this.min;
        }

        @Nonnull
        public String toString() {
            return "Entry{, fluid=" + this.fluid + ", min=" + String.valueOf(this.min) + ", max=" + String.valueOf(this.max) + ", mask=" + String.valueOf(this.mask) + "}";
        }
    }
}

