/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.condition;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMaskCondition {
    public static final Mask DEFAULT_MASK = new Mask(true, new MaskEntry[0]);
    public static final BlockMaskCondition DEFAULT_TRUE = new BlockMaskCondition();
    public static final BlockMaskCondition DEFAULT_FALSE = new BlockMaskCondition();
    @Nonnull
    private Mask defaultMask = DEFAULT_MASK;
    @Nonnull
    private Long2ObjectMap<Mask> specificMasks = Long2ObjectMaps.emptyMap();

    public void set(@Nonnull Mask defaultMask, @Nonnull Long2ObjectMap<Mask> specificMasks) {
        this.defaultMask = defaultMask;
        this.specificMasks = specificMasks;
    }

    public boolean eval(int currentBlock, int currentFluid, int nextBlockId, int nextFluidId) {
        Mask mask = this.specificMasks.getOrDefault(MathUtil.packLong(nextBlockId, nextFluidId), this.defaultMask);
        return mask.shouldReplace(currentBlock, currentFluid);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockMaskCondition that = (BlockMaskCondition)o;
        if (!this.defaultMask.equals(that.defaultMask)) {
            return false;
        }
        return Objects.equals(this.specificMasks, that.specificMasks);
    }

    public int hashCode() {
        int result = this.defaultMask.hashCode();
        result = 31 * result + (this.specificMasks != null ? this.specificMasks.hashCode() : 0);
        return result;
    }

    @Nonnull
    public String toString() {
        return "BlockMaskCondition{defaultMask=" + String.valueOf(this.defaultMask) + ", specificMasks=" + String.valueOf(this.specificMasks) + "}";
    }

    public static class Mask {
        private final boolean matchEmpty;
        private final MaskEntry[] entries;

        public Mask(@Nonnull MaskEntry[] entries) {
            this(false, entries);
        }

        private Mask(boolean matchEmpty, @Nonnull MaskEntry[] entries) {
            this.entries = entries;
            this.matchEmpty = matchEmpty;
        }

        public boolean shouldReplace(int current, int fluid) {
            for (MaskEntry entry : this.entries) {
                if (!entry.shouldHandle(current, fluid)) continue;
                return entry.shouldReplace();
            }
            return this.matchEmpty && (current == 0 || fluid == 0);
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Mask mask = (Mask)o;
            return Arrays.equals(this.entries, mask.entries);
        }

        public int hashCode() {
            return Arrays.hashCode(this.entries);
        }

        @Nonnull
        public String toString() {
            return "Mask{entries=" + Arrays.toString(this.entries) + "}";
        }
    }

    public static class MaskEntry {
        public static final MaskEntry WILDCARD_TRUE = new MaskEntry(true, true);
        public static final MaskEntry WILDCARD_FALSE = new MaskEntry(true, false);
        private ResolvedBlockArray blocks = ResolvedBlockArray.EMPTY;
        private final boolean any;
        private boolean replace;

        public MaskEntry() {
            this(false, false);
        }

        private MaskEntry(boolean any, boolean replace) {
            this.any = any;
            this.replace = replace;
        }

        public void set(ResolvedBlockArray blocks, boolean replace) {
            this.blocks = blocks;
            this.replace = replace;
        }

        public boolean shouldHandle(int current, int fluid) {
            return this.any || this.blocks.contains(current, fluid);
        }

        public boolean shouldReplace() {
            return this.replace;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MaskEntry that = (MaskEntry)o;
            if (this.any != that.any) {
                return false;
            }
            if (this.replace != that.replace) {
                return false;
            }
            return this.blocks.equals(that.blocks);
        }

        public int hashCode() {
            int result = this.blocks.hashCode();
            result = 31 * result + (this.any ? 1 : 0);
            result = 31 * result + (this.replace ? 1 : 0);
            return result;
        }

        @Nonnull
        public String toString() {
            return "MaskEntry{blocks=" + String.valueOf(this.blocks) + ", replace=" + this.replace + "}";
        }
    }
}

