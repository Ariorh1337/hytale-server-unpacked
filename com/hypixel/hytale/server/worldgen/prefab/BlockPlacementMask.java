/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.prefab;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockPlacementMask
implements BlockMaskCondition {
    public static final IMask DEFAULT_MASK = new DefaultMask();
    private IMask defaultMask;
    private Long2ObjectMap<Mask> specificMasks;

    public void set(IMask defaultMask, Long2ObjectMap<Mask> specificMasks) {
        this.defaultMask = defaultMask;
        this.specificMasks = specificMasks;
    }

    @Override
    public boolean eval(int currentBlock, int currentFluid, @Nonnull BlockFluidEntry entry) {
        IMask mask;
        IMask iMask = mask = this.specificMasks == null ? null : (IMask)this.specificMasks.get(MathUtil.packLong(entry.blockId(), entry.fluidId()));
        if (mask == null) {
            mask = this.defaultMask;
        }
        return mask.shouldReplace(currentBlock, currentFluid);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockPlacementMask that = (BlockPlacementMask)o;
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
        return "BlockPlacementMask{defaultMask=" + String.valueOf(this.defaultMask) + ", specificMasks=" + String.valueOf(this.specificMasks) + "}";
    }

    public static interface IMask {
        public boolean shouldReplace(int var1, int var2);
    }

    public static class DefaultMask
    implements IMask {
        @Override
        public boolean shouldReplace(int block, int fluid) {
            return block == 0 && fluid == 0;
        }

        public int hashCode() {
            return 137635105;
        }

        public boolean equals(Object o) {
            return o instanceof DefaultMask;
        }

        @Nonnull
        public String toString() {
            return "DefaultMask{}";
        }
    }

    public static class BlockArrayEntry
    implements IEntry {
        private ResolvedBlockArray blocks;
        private boolean replace;

        public void set(ResolvedBlockArray blocks, boolean replace) {
            this.blocks = blocks;
            this.replace = replace;
        }

        @Override
        public boolean shouldHandle(int current, int fluid) {
            return this.blocks.contains(current, fluid);
        }

        @Override
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
            BlockArrayEntry that = (BlockArrayEntry)o;
            if (this.replace != that.replace) {
                return false;
            }
            return this.blocks.equals(that.blocks);
        }

        public int hashCode() {
            int result = this.blocks.hashCode();
            result = 31 * result + (this.replace ? 1 : 0);
            return result;
        }

        @Nonnull
        public String toString() {
            return "BlockArrayEntry{blocks=" + String.valueOf(this.blocks) + ", replace=" + this.replace + "}";
        }
    }

    public static class WildcardEntry
    implements IEntry {
        private final boolean replace;

        public WildcardEntry(boolean replace) {
            this.replace = replace;
        }

        @Override
        public boolean shouldHandle(int block, int fluid) {
            return true;
        }

        @Override
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
            WildcardEntry that = (WildcardEntry)o;
            return this.replace == that.replace;
        }

        public int hashCode() {
            return this.replace ? 1 : 0;
        }

        @Nonnull
        public String toString() {
            return "WildcardEntry{replace=" + this.replace + "}";
        }
    }

    public static interface IEntry {
        public boolean shouldHandle(int var1, int var2);

        public boolean shouldReplace();
    }

    public static class Mask
    implements IMask {
        private final IEntry[] entries;

        public Mask(IEntry[] entries) {
            this.entries = entries;
        }

        @Override
        public boolean shouldReplace(int current, int fluid) {
            for (IEntry entry : this.entries) {
                if (!entry.shouldHandle(current, fluid)) continue;
                return entry.shouldReplace();
            }
            return false;
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
}

