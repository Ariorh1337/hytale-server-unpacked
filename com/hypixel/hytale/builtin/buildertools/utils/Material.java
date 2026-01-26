/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.utils;

import com.hypixel.hytale.builtin.buildertools.utils.FluidPatternHelper;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Material {
    public static final Material EMPTY = new Material(0, 0, 0, 0);
    private final int blockId;
    private final int fluidId;
    private final byte fluidLevel;
    private final int rotation;

    private Material(int blockId, int fluidId, byte fluidLevel, int rotation) {
        this.blockId = blockId;
        this.fluidId = fluidId;
        this.fluidLevel = fluidLevel;
        this.rotation = rotation;
    }

    @Nonnull
    public static Material block(int blockId) {
        return Material.block(blockId, 0);
    }

    @Nonnull
    public static Material block(int blockId, int rotation) {
        if (blockId == 0) {
            return EMPTY;
        }
        return new Material(blockId, 0, 0, rotation);
    }

    @Nonnull
    public static Material fluid(int fluidId, byte fluidLevel) {
        if (fluidId == 0) {
            return EMPTY;
        }
        return new Material(0, fluidId, fluidLevel, 0);
    }

    @Nullable
    public static Material fromKey(@Nonnull String key) {
        int blockId;
        FluidPatternHelper.FluidInfo fluidInfo;
        if (key.equalsIgnoreCase("empty")) {
            return EMPTY;
        }
        BlockPattern.BlockEntry blockEntry = BlockPattern.tryParseBlockTypeKey(key);
        if (blockEntry != null) {
            fluidInfo = FluidPatternHelper.getFluidInfo(blockEntry.blockTypeKey());
            if (fluidInfo != null) {
                return Material.fluid(fluidInfo.fluidId(), fluidInfo.fluidLevel());
            }
            blockId = BlockType.getAssetMap().getIndex(blockEntry.blockTypeKey());
            if (blockId != Integer.MIN_VALUE) {
                return Material.block(blockId, blockEntry.rotation());
            }
        }
        if ((fluidInfo = FluidPatternHelper.getFluidInfo(key)) != null) {
            return Material.fluid(fluidInfo.fluidId(), fluidInfo.fluidLevel());
        }
        blockId = BlockType.getAssetMap().getIndex(key);
        if (blockId != Integer.MIN_VALUE) {
            return Material.block(blockId);
        }
        return null;
    }

    public boolean isFluid() {
        return this.fluidId != 0;
    }

    public boolean isBlock() {
        return this.blockId != 0 && this.fluidId == 0;
    }

    public boolean isEmpty() {
        return this.blockId == 0 && this.fluidId == 0;
    }

    public int getBlockId() {
        return this.blockId;
    }

    public int getFluidId() {
        return this.fluidId;
    }

    public byte getFluidLevel() {
        return this.fluidLevel;
    }

    public int getRotation() {
        return this.rotation;
    }

    public boolean hasRotation() {
        return this.rotation != 0;
    }

    public String toString() {
        if (this.isEmpty()) {
            return "Material[empty]";
        }
        if (this.isFluid()) {
            Fluid fluid = Fluid.getAssetMap().getAsset(this.fluidId);
            return "Material[fluid=" + String.valueOf(fluid != null ? fluid.getId() : Integer.valueOf(this.fluidId)) + ", level=" + this.fluidLevel + "]";
        }
        BlockType block = BlockType.getAssetMap().getAsset(this.blockId);
        String rotStr = this.hasRotation() ? ", rotation=" + String.valueOf(RotationTuple.get(this.rotation)) : "";
        return "Material[block=" + String.valueOf(block != null ? block.getId() : Integer.valueOf(this.blockId)) + rotStr + "]";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Material)) {
            return false;
        }
        Material other = (Material)obj;
        return this.blockId == other.blockId && this.fluidId == other.fluidId && this.fluidLevel == other.fluidLevel && this.rotation == other.rotation;
    }

    public int hashCode() {
        return 31 * (31 * (31 * this.blockId + this.fluidId) + this.fluidLevel) + this.rotation;
    }

    @Nonnull
    public static Material fromPattern(@Nonnull BlockPattern pattern, @Nonnull Random random) {
        BlockPattern.BlockEntry blockEntry = pattern.nextBlockTypeKey(random);
        if (blockEntry != null) {
            FluidPatternHelper.FluidInfo fluidInfo = FluidPatternHelper.getFluidInfo(blockEntry.blockTypeKey());
            if (fluidInfo != null) {
                return Material.fluid(fluidInfo.fluidId(), fluidInfo.fluidLevel());
            }
            int blockId = BlockType.getAssetMap().getIndex(blockEntry.blockTypeKey());
            if (blockId != Integer.MIN_VALUE) {
                return Material.block(blockId, blockEntry.rotation());
            }
        }
        return Material.block(pattern.nextBlock(random));
    }
}

