/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier2d;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoiseBlockArray {
    public static final NoiseBlockArray EMPTY = new NoiseBlockArray(new Entry[0]);
    protected final Entry[] entries;

    public NoiseBlockArray(Entry[] entries) {
        this.entries = entries;
    }

    public Entry[] getEntries() {
        return this.entries;
    }

    public BlockFluidEntry getTopBlockAt(int seed, double x, double z) {
        for (int i = 0; i < this.entries.length; ++i) {
            Entry entry = this.entries[i];
            int repetitions = entry.getRepetitions(seed, x, z);
            if (repetitions <= 0) continue;
            return entry.blockEntry;
        }
        return BlockFluidEntry.EMPTY;
    }

    public BlockFluidEntry getBottomBlockAt(int seed, double x, double z) {
        for (int i = this.entries.length - 1; i >= 0; --i) {
            Entry entry = this.entries[i];
            int repetitions = entry.getRepetitions(seed, x, z);
            if (repetitions <= 0) continue;
            return entry.blockEntry;
        }
        return BlockFluidEntry.EMPTY;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NoiseBlockArray that = (NoiseBlockArray)o;
        return Arrays.equals(this.entries, that.entries);
    }

    public int hashCode() {
        return Arrays.hashCode(this.entries);
    }

    @Nonnull
    public String toString() {
        return "NoiseBlockArray{entries=" + Arrays.toString(this.entries) + "}";
    }

    public static class Entry {
        protected final String blockName;
        protected final BlockFluidEntry blockEntry;
        protected final IDoubleRange repetitions;
        @Nonnull
        protected final NoiseProperty noise;
        @Nonnull
        protected final IDoubleCoordinateSupplier2d noiseSupplier;

        public Entry(String blockName, BlockFluidEntry blockEntry, IDoubleRange repetitions, @Nonnull NoiseProperty noise) {
            this.blockName = blockName;
            this.blockEntry = blockEntry;
            this.repetitions = repetitions;
            this.noise = noise;
            this.noiseSupplier = noise::get;
        }

        public String getBlockName() {
            return this.blockName;
        }

        public BlockFluidEntry getBlockEntry() {
            return this.blockEntry;
        }

        public int getRepetitions(int seed, double x, double z) {
            return MathUtil.floor(this.repetitions.getValue(seed, x, z, this.noiseSupplier));
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Entry entry = (Entry)o;
            if (this.blockEntry != entry.blockEntry) {
                return false;
            }
            if (!this.blockName.equals(entry.blockName)) {
                return false;
            }
            if (!this.repetitions.equals(entry.repetitions)) {
                return false;
            }
            if (!this.noise.equals(entry.noise)) {
                return false;
            }
            return this.noiseSupplier.equals(entry.noiseSupplier);
        }

        public int hashCode() {
            int result = this.blockName.hashCode();
            result = 31 * result + this.blockEntry.hashCode();
            result = 31 * result + this.repetitions.hashCode();
            result = 31 * result + this.noise.hashCode();
            result = 31 * result + this.noiseSupplier.hashCode();
            return result;
        }

        @Nonnull
        public String toString() {
            return "Entry{blockName='" + this.blockName + "', blockEntry=" + String.valueOf(this.blockEntry) + ", repetitions=" + String.valueOf(this.repetitions) + ", noise=" + String.valueOf(this.noise) + "}";
        }
    }
}

