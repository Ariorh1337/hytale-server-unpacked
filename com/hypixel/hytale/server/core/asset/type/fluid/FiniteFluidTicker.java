/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.fluid;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FiniteFluidTicker
extends FluidTicker {
    @Nonnull
    public static BuilderCodec<FiniteFluidTicker> CODEC = BuilderCodec.builder(FiniteFluidTicker.class, FiniteFluidTicker::new, FluidTicker.BASE_CODEC).build();
    @Nonnull
    private static final Vector2i[] DIAG_OFFSETS = new Vector2i[]{new Vector2i(-1, -1), new Vector2i(1, 1), new Vector2i(1, -1), new Vector2i(-1, 1)};
    private static final int MAX_DROP_DISTANCE = 2;
    @Nonnull
    private static final List<List<Vector2i[]>> OFFSETS_LISTS = new ObjectArrayList<List<Vector2i[]>>();
    private static final int RANDOM_VARIANTS = 16;

    @Override
    @Nonnull
    protected FluidTicker.AliveStatus isAlive(@Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, @Nonnull BlockSection blockSection, Fluid fluid, int fluidId, byte fluidLevel, int worldX, int worldY, int worldZ) {
        return FluidTicker.AliveStatus.ALIVE;
    }

    @Override
    @Nonnull
    protected BlockTickStrategy spread(World world, long tick, @Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, BlockSection blockSection, @Nonnull Fluid fluid, int fluidId, byte fluidLevel, int worldX, int worldY, int worldZ) {
        byte bottomFluidLevel;
        BlockSection belowBlockSection;
        if (worldY == 0) {
            return BlockTickStrategy.SLEEP;
        }
        boolean isDifferentSectionBelow = fluidSection.getY() != ChunkUtil.chunkCoordinate(worldY - 1);
        FluidSection belowFluidSection = isDifferentSectionBelow ? accessor.getFluidSectionByBlock(worldX, worldY - 1, worldZ) : fluidSection;
        BlockSection blockSection2 = belowBlockSection = isDifferentSectionBelow ? accessor.getBlockSectionByBlock(worldX, worldY - 1, worldZ) : blockSection;
        if (belowFluidSection == null || belowBlockSection == null) {
            return BlockTickStrategy.SLEEP;
        }
        int bottomFluidId = belowFluidSection.getFluidId(worldX, worldY - 1, worldZ);
        if (this.spreadDownwards(accessor, fluidSection, blockSection, belowFluidSection, belowBlockSection, worldX, worldY, worldZ, fluid, fluidId, fluidLevel, bottomFluidId, bottomFluidLevel = belowFluidSection.getFluidLevel(worldX, worldY - 1, worldZ))) {
            return BlockTickStrategy.CONTINUE;
        }
        return this.spreadSideways(tick, accessor, fluidSection, blockSection, worldX, worldY, worldZ, fluid, fluidId, fluidLevel);
    }

    private boolean spreadDownwards(@Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, BlockSection blockSection, @Nonnull FluidSection belowFluidSection, @Nonnull BlockSection belowBlockSection, int worldX, int worldY, int worldZ, @Nonnull Fluid fluid, int fluidId, byte fluidLevel, int bottomFluidId, byte bottomFluidLevel) {
        if (fluidId != bottomFluidId && bottomFluidId != 0) {
            return false;
        }
        if (FiniteFluidTicker.isSolid(BlockType.getAssetMap().getAsset(belowBlockSection.get(worldX, worldY - 1, worldZ)))) {
            return false;
        }
        int topY = this.getTopY(accessor, fluidSection, worldX, worldY, worldZ, fluid, fluidId);
        boolean isTopDifferent = ChunkUtil.chunkCoordinate(topY) != fluidSection.getY();
        FluidSection topFluidSection = isTopDifferent ? accessor.getFluidSectionByBlock(worldX, topY, worldZ) : fluidSection;
        BlockSection topBlockSection = isTopDifferent ? accessor.getBlockSectionByBlock(worldX, topY, worldZ) : blockSection;
        byte topBlockLevel = topFluidSection.getFluidLevel(worldX, topY, worldZ);
        int transferLevel = Math.min(topBlockLevel, fluid.getMaxFluidLevel() - bottomFluidLevel);
        if (transferLevel == 0) {
            return false;
        }
        int newBottomLevel = bottomFluidId == 0 ? transferLevel : bottomFluidLevel + transferLevel;
        belowFluidSection.setFluid(worldX, worldY - 1, worldZ, fluidId, (byte)newBottomLevel);
        FiniteFluidTicker.setTickingSurrounding(accessor, belowBlockSection, worldX, worldY - 1, worldZ);
        boolean updated = transferLevel == topBlockLevel ? topFluidSection.setFluid(worldX, topY, worldZ, 0, (byte)0) : topFluidSection.setFluid(worldX, topY, worldZ, fluidId, (byte)(topBlockLevel - transferLevel));
        FiniteFluidTicker.setTickingSurrounding(accessor, topBlockSection, worldX, topY, worldZ);
        return updated;
    }

    @Nonnull
    private BlockTickStrategy spreadSideways(long tick, @Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, BlockSection blockSection, int worldX, int worldY, int worldZ, @Nonnull Fluid fluid, int fluidId, byte fluidLevel) {
        if (fluidLevel == 1) {
            return BlockTickStrategy.SLEEP;
        }
        int newLevel = fluidLevel;
        BlockTypeAssetMap<String, BlockType> blockTypeMap = BlockType.getAssetMap();
        long hash = HashUtil.rehash(worldX, worldY, worldZ, 4032035379L);
        int index = OFFSETS_LISTS.size() + (int)((hash + tick) % (long)OFFSETS_LISTS.size());
        List<Vector2i[]> offsetsList = OFFSETS_LISTS.get(index % OFFSETS_LISTS.size());
        for (int idx = 0; idx < offsetsList.size(); ++idx) {
            Vector2i[] offsetArray = offsetsList.get(idx);
            int offsets = this.getSpreadOffsets(blockTypeMap, accessor, fluidSection, blockSection, worldX, worldY, worldZ, offsetArray, fluidId, 2);
            boolean spreadDownhill = offsets != 0;
            block5: for (int i = 0; i < offsetArray.length && newLevel != 1; ++i) {
                Vector2i offset;
                SpreadOutcome spreadOutcome;
                if (spreadDownhill && (offsets & 1 << i) == 0 || (spreadOutcome = this.spreadToOffset(accessor, fluidSection, blockSection, offset = offsetArray[i], worldX, worldY, worldZ, fluid, fluidId, (byte)fluidLevel)) == null) continue;
                switch (spreadOutcome.ordinal()) {
                    case 0: {
                        --newLevel;
                        continue block5;
                    }
                    case 1: {
                        return BlockTickStrategy.WAIT_FOR_ADJACENT_CHUNK_LOAD;
                    }
                }
            }
            if (spreadDownhill) break;
        }
        if (newLevel == fluidLevel) {
            return BlockTickStrategy.SLEEP;
        }
        if (!this.drainFromTopBlock(accessor, fluidSection, blockSection, worldX, worldY, worldZ, fluid, fluidId, (byte)(fluidLevel - newLevel))) {
            return BlockTickStrategy.WAIT_FOR_ADJACENT_CHUNK_LOAD;
        }
        return BlockTickStrategy.CONTINUE;
    }

    @Nullable
    private SpreadOutcome spreadToOffset(@Nonnull FluidTicker.Accessor accessor, FluidSection fluidSection, BlockSection blockSection, @Nonnull Vector2i offset, int worldX, int worldY, int worldZ, Fluid fluid, int fluidId, byte fluidLevel) {
        BlockSection otherBlockSection;
        int z;
        int blockZ;
        if (!FiniteFluidTicker.isOffsetConnected(accessor, blockSection, offset, worldX, worldY, worldZ)) {
            return null;
        }
        int x = offset.getX();
        int blockX = worldX + x;
        boolean isDifferentSection = !ChunkUtil.isSameChunkSection(worldX, worldY, worldZ, blockX, worldY, blockZ = worldZ + (z = offset.getY()));
        FluidSection otherFluidSection = isDifferentSection ? accessor.getFluidSectionByBlock(blockX, worldY, blockZ) : fluidSection;
        BlockSection blockSection2 = otherBlockSection = isDifferentSection ? accessor.getBlockSectionByBlock(blockX, worldY, blockZ) : blockSection;
        if (otherFluidSection == null || otherBlockSection == null) {
            return SpreadOutcome.UNLOADED_CHUNK;
        }
        int blockId = otherBlockSection.get(blockX, worldY, blockZ);
        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
        if (FiniteFluidTicker.isSolid(blockType)) {
            return null;
        }
        int adjacentFluidId = otherFluidSection.getFluidId(blockX, worldY, blockZ);
        byte adjacentFluidLevel = otherFluidSection.getFluidLevel(blockX, worldY, blockZ);
        int newAdjacentFillLevel = 1;
        if (adjacentFluidId == 0 || adjacentFluidId == fluidId && adjacentFluidLevel < fluidLevel - 1) {
            if (adjacentFluidId == fluidId) {
                newAdjacentFillLevel = adjacentFluidLevel + 1;
            }
            if (otherFluidSection.setFluid(blockX, worldY, blockZ, fluidId, (byte)newAdjacentFillLevel)) {
                FiniteFluidTicker.setTickingSurrounding(accessor, otherBlockSection, blockX, worldY, blockZ);
                return SpreadOutcome.SUCCESS;
            }
        }
        return null;
    }

    private boolean drainFromTopBlock(@Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, BlockSection blockSection, int worldX, int worldY, int worldZ, @Nonnull Fluid fluid, int fluidId, byte drainLevels) {
        BlockSection topBlockSection;
        int topY = this.getTopY(accessor, fluidSection, worldX, worldY, worldZ, fluid, fluidId);
        boolean isDifferentSection = fluidSection.getY() != ChunkUtil.chunkCoordinate(topY);
        FluidSection topFluidSection = isDifferentSection ? accessor.getFluidSectionByBlock(worldX, topY, worldZ) : fluidSection;
        BlockSection blockSection2 = topBlockSection = isDifferentSection ? accessor.getBlockSectionByBlock(worldX, topY, worldZ) : blockSection;
        if (topFluidSection == null || topBlockSection == null) {
            return false;
        }
        byte topBlockFillLevels = topFluidSection.getFluidLevel(worldX, topY, worldZ);
        if (topBlockFillLevels > drainLevels) {
            FiniteFluidTicker.setTickingSurrounding(accessor, topBlockSection, worldX, topY, worldZ);
            return topFluidSection.setFluid(worldX, topY, worldZ, fluidId, (byte)(topBlockFillLevels - drainLevels));
        }
        if (topBlockFillLevels == drainLevels) {
            FiniteFluidTicker.setTickingSurrounding(accessor, topBlockSection, worldX, topY, worldZ);
            return topFluidSection.setFluid(worldX, topY, worldZ, 0, (byte)0);
        }
        int nextY = topY;
        boolean updated = true;
        FluidSection nextFluidSection = topFluidSection;
        BlockSection nextBlockSection = topBlockSection;
        while (drainLevels > 0) {
            byte nextFluidLevel;
            int transferLevels;
            boolean isDifferent;
            boolean bl = isDifferent = ChunkUtil.chunkCoordinate(nextY) != nextFluidSection.getY();
            if (isDifferent) {
                nextFluidSection = accessor.getFluidSectionByBlock(worldX, nextY, worldZ);
                nextBlockSection = accessor.getBlockSectionByBlock(worldX, nextY, worldZ);
                if (nextFluidSection == null || nextBlockSection == null) {
                    return false;
                }
            }
            if ((transferLevels = Math.min(nextFluidLevel = nextFluidSection.getFluidLevel(worldX, nextY, worldZ), drainLevels)) == nextFluidLevel) {
                updated &= nextFluidSection.setFluid(worldX, nextY, worldZ, 0, (byte)0);
            } else {
                updated &= nextFluidSection.setFluid(worldX, nextY, worldZ, fluidId, nextFluidLevel);
                FiniteFluidTicker.setTickingSurrounding(accessor, nextBlockSection, worldX, nextY, worldZ);
            }
            drainLevels = (byte)(drainLevels - (byte)transferLevels);
            --nextY;
        }
        return updated;
    }

    private int getTopY(@Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, int worldX, int worldY, int worldZ, @Nonnull Fluid fluid, int fluidId) {
        FluidSection aboveFluidSection;
        int topY = worldY;
        FluidSection fluidSection2 = aboveFluidSection = fluidSection.getY() != ChunkUtil.chunkCoordinate(topY + 1) ? accessor.getFluidSectionByBlock(worldX, topY + 1, worldZ) : fluidSection;
        while (true) {
            if (fluidSection.getY() != ChunkUtil.chunkCoordinate(topY)) {
                fluidSection = accessor.getFluidSectionByBlock(worldX, topY, worldZ);
            }
            if (aboveFluidSection.getY() != ChunkUtil.chunkCoordinate(topY + 1)) {
                aboveFluidSection = accessor.getFluidSectionByBlock(worldX, topY + 1, worldZ);
            }
            if (fluidSection.getFluidLevel(worldX, topY, worldZ) != fluid.getMaxFluidLevel() || aboveFluidSection.getFluidId(worldX, topY + 1, worldZ) != fluidId) break;
            ++topY;
        }
        return topY;
    }

    private static boolean isOffsetConnected(@Nonnull FluidTicker.Accessor accessor, BlockSection blockSection, @Nonnull Vector2i offset, int worldX, int worldY, int worldZ) {
        BlockSection section2;
        int x = offset.getX();
        int z = offset.getY();
        if (x == 0 || z == 0) {
            return true;
        }
        BlockSection section1 = ChunkUtil.isSameChunkSection(worldX, worldY, worldZ, worldX + x, worldY, worldZ) ? blockSection : accessor.getBlockSection(worldX + x, worldY, worldZ);
        BlockSection blockSection2 = section2 = ChunkUtil.isSameChunkSection(worldX, worldY, worldZ, worldX, worldY, worldZ + z) ? blockSection : accessor.getBlockSection(worldX, worldY, worldZ + z);
        if (section1 == null || section2 == null) {
            return false;
        }
        int block1 = section1.get(worldX + x, worldY, worldZ);
        int block2 = section2.get(worldX, worldY, worldZ + z);
        return block1 == 0 || block2 == 0 || !FiniteFluidTicker.isSolid(BlockType.getAssetMap().getAsset(block1)) || !FiniteFluidTicker.isSolid(BlockType.getAssetMap().getAsset(block2));
    }

    static {
        List<Vector2i[]> offsets = List.of(ORTO_OFFSETS, DIAG_OFFSETS);
        Random random = new Random(51966L);
        for (int i = 0; i < 16; ++i) {
            ObjectArrayList<Vector2i[]> offsetLists = new ObjectArrayList<Vector2i[]>();
            for (Vector2i[] offset : offsets) {
                List<Vector2i> offsetArray = Arrays.asList(offset);
                Collections.shuffle(offsetArray, random);
                offsetLists.add((Vector2i[])offsetArray.toArray(Vector2i[]::new));
            }
            OFFSETS_LISTS.add(offsetLists);
        }
    }

    private static enum SpreadOutcome {
        SUCCESS,
        UNLOADED_CHUNK;

    }
}

