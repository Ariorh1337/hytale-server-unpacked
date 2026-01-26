/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import javax.annotation.Nonnull;

public final class WorldUtil {
    public static boolean isFluidOnlyBlock(@Nonnull BlockType blockType, int fluidId) {
        return blockType.getMaterial() == BlockMaterial.Empty && fluidId != 0;
    }

    public static boolean isSolidOnlyBlock(@Nonnull BlockType blockType, int fluidId) {
        return blockType.getMaterial() == BlockMaterial.Solid && fluidId == 0;
    }

    public static boolean isEmptyOnlyBlock(@Nonnull BlockType blockType, int fluidId) {
        return blockType.getMaterial() == BlockMaterial.Empty && fluidId == 0;
    }

    public static int getFluidIdAtPosition(@Nonnull ComponentAccessor<ChunkStore> chunkStore, @Nonnull ChunkColumn chunkColumnComponent, int x, int y, int z) {
        if (y < 0 || y >= 320) {
            return 0;
        }
        Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(y));
        if (sectionRef == null || !sectionRef.isValid()) {
            return 0;
        }
        FluidSection fluidSectionComponent = chunkStore.getComponent(sectionRef, FluidSection.getComponentType());
        if (fluidSectionComponent == null) {
            return 0;
        }
        return fluidSectionComponent.getFluidId(x, y, z);
    }

    public static long getPackedMaterialAndFluidAtPosition(@Nonnull Ref<ChunkStore> chunkRef, @Nonnull ComponentAccessor<ChunkStore> chunkStore, double x, double y, double z) {
        int blockId;
        double yTest;
        Fluid fluid;
        FluidSection fluidSectionComponent;
        if (y < 0.0 || y >= 320.0) {
            return MathUtil.packLong(BlockMaterial.Empty.ordinal(), 0);
        }
        int blockX = MathUtil.floor(x);
        int blockY = MathUtil.floor(y);
        int blockZ = MathUtil.floor(z);
        ChunkColumn chunkColumnComponent = chunkStore.getComponent(chunkRef, ChunkColumn.getComponentType());
        if (chunkColumnComponent == null) {
            return MathUtil.packLong(BlockMaterial.Empty.ordinal(), 0);
        }
        BlockChunk blockChunkComponent = chunkStore.getComponent(chunkRef, BlockChunk.getComponentType());
        if (blockChunkComponent == null) {
            return MathUtil.packLong(BlockMaterial.Empty.ordinal(), 0);
        }
        BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(blockY);
        int fluidId = 0;
        Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(y));
        if (sectionRef != null && sectionRef.isValid() && (fluidSectionComponent = chunkStore.getComponent(sectionRef, FluidSection.getComponentType())) != null && (fluidId = fluidSectionComponent.getFluidId(blockX, blockY, blockZ)) != 0 && (fluid = Fluid.getAssetMap().getAsset(fluidId)) != null && (yTest = y - (double)blockY) > (double)fluidSectionComponent.getFluidLevel(blockX, blockY, blockZ) / (double)fluid.getMaxFluidLevel()) {
            fluidId = 0;
        }
        if ((blockId = blockSection.get(blockX, blockY, blockZ)) == 0) {
            return MathUtil.packLong(BlockMaterial.Empty.ordinal(), fluidId);
        }
        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
        if (blockType == null || blockType.isUnknown()) {
            return MathUtil.packLong(BlockMaterial.Empty.ordinal(), fluidId);
        }
        double relativeY = y - (double)blockY;
        String blockTypeKey = blockType.getId();
        BlockType blockTypeAsset = (BlockType)BlockType.getAssetMap().getAsset(blockTypeKey);
        if (blockTypeAsset == null) {
            return MathUtil.packLong(BlockMaterial.Empty.ordinal(), fluidId);
        }
        BlockMaterial blockTypeMaterial = blockType.getMaterial();
        int filler = blockSection.getFiller(blockX, blockY, blockZ);
        int rotation = blockSection.getRotationIndex(blockX, blockY, blockZ);
        if (filler != 0 && blockTypeAsset.getMaterial() == BlockMaterial.Solid) {
            int fillerZ;
            int fillerY;
            int fillerX;
            BlockBoundingBoxes boundingBoxes = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
            if (boundingBoxes == null) {
                return MathUtil.packLong(BlockMaterial.Empty.ordinal(), fluidId);
            }
            BlockBoundingBoxes.RotatedVariantBoxes rotatedBoxes = boundingBoxes.get(rotation);
            if (rotatedBoxes.containsPosition(x - (double)blockX + (double)(fillerX = FillerBlockUtil.unpackX(filler)), relativeY + (double)(fillerY = FillerBlockUtil.unpackY(filler)), z - (double)blockZ + (double)(fillerZ = FillerBlockUtil.unpackZ(filler)))) {
                return MathUtil.packLong(BlockMaterial.Solid.ordinal(), fluidId);
            }
        } else if (blockTypeMaterial == BlockMaterial.Solid) {
            BlockBoundingBoxes boundingBoxes = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
            if (boundingBoxes == null) {
                return MathUtil.packLong(BlockMaterial.Empty.ordinal(), fluidId);
            }
            BlockBoundingBoxes.RotatedVariantBoxes rotatedBoxes = boundingBoxes.get(rotation);
            if (rotatedBoxes.containsPosition(x - (double)blockX, relativeY, z - (double)blockZ)) {
                return MathUtil.packLong(BlockMaterial.Solid.ordinal(), fluidId);
            }
        }
        return MathUtil.packLong(BlockMaterial.Empty.ordinal(), fluidId);
    }

    public static int findFluidBlock(@Nonnull ComponentAccessor<ChunkStore> chunkStore, @Nonnull ChunkColumn chunkColumnComponent, @Nonnull BlockChunk blockChunkComponent, int x, int y, int z, boolean allowBubble) {
        BlockMaterial materialLowerBlock;
        if (y < 0 || y >= 320) {
            return -1;
        }
        if (WorldUtil.getFluidIdAtPosition(chunkStore, chunkColumnComponent, x, y++, z) != 0) {
            return y;
        }
        if (y == 320 || !allowBubble) {
            return -1;
        }
        BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(y);
        int blockId = blockSection.get(x, y++, z);
        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
        BlockMaterial blockMaterial = materialLowerBlock = blockType != null ? blockType.getMaterial() : BlockMaterial.Empty;
        if (WorldUtil.getFluidIdAtPosition(chunkStore, chunkColumnComponent, x, y++, z) != 0) {
            return y;
        }
        if (materialLowerBlock != BlockMaterial.Solid || y == 320) {
            return -1;
        }
        return WorldUtil.getFluidIdAtPosition(chunkStore, chunkColumnComponent, x, y++, z) != 0 ? y : -1;
    }

    public static int getWaterLevel(@Nonnull ComponentAccessor<ChunkStore> chunkStore, @Nonnull ChunkColumn chunkColumnComponent, @Nonnull BlockChunk blockChunkComponent, int x, int z, int startY) {
        if ((startY = WorldUtil.findFluidBlock(chunkStore, chunkColumnComponent, blockChunkComponent, x, startY, z, true)) == -1) {
            return -1;
        }
        while (startY + 1 < 320) {
            int fluidId;
            BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(startY + 1);
            int blockId = blockSection.get(x, startY + 1, z);
            BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
            if (blockType == null || !WorldUtil.isFluidOnlyBlock(blockType, fluidId = WorldUtil.getFluidIdAtPosition(chunkStore, chunkColumnComponent, x, startY + 1, z))) break;
            ++startY;
        }
        return startY;
    }

    public static int findFarthestEmptySpaceBelow(@Nonnull ComponentAccessor<ChunkStore> chunkStore, @Nonnull ChunkColumn chunkColumnComponent, @Nonnull BlockChunk blockChunkComponent, int x, int y, int z, int yFail) {
        if (y < 0) {
            return yFail;
        }
        if (y >= 320) {
            y = 319;
        }
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        int indexSection = ChunkUtil.indexSection(y);
        while (indexSection >= 0) {
            Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(indexSection);
            FluidSection fluidSectionComponent = chunkStore.getComponent(sectionRef, FluidSection.getComponentType());
            BlockSection chunkSection = blockChunkComponent.getSectionAtIndex(indexSection);
            if (chunkSection.isSolidAir() && fluidSectionComponent != null && fluidSectionComponent.isEmpty()) {
                y = 32 * indexSection - 1;
                if (y <= 0) {
                    return 0;
                }
                --indexSection;
                continue;
            }
            int yBottom = 32 * indexSection--;
            while (y >= yBottom) {
                int fluidId;
                int blockId = chunkSection.get(x, y--, z);
                int n = fluidId = fluidSectionComponent != null ? fluidSectionComponent.getFluidId(x, y, z) : 0;
                if (blockId == 0 && fluidId == 0) continue;
                BlockType blockType = assetMap.getAsset(blockId);
                if (blockType == null || blockType.isUnknown()) {
                    return y + 2;
                }
                int filler = chunkSection.getFiller(x, y, z);
                if (filler == 0 && WorldUtil.isEmptyOnlyBlock(blockType, fluidId)) continue;
                return y + 2;
            }
        }
        return 0;
    }

    public static int findFarthestEmptySpaceAbove(@Nonnull ComponentAccessor<ChunkStore> chunkStore, @Nonnull ChunkColumn chunkColumnComponent, @Nonnull BlockChunk blockChunkComponent, int x, int y, int z, int yFail) {
        if (y >= 320) {
            return Integer.MAX_VALUE;
        }
        if (y < 0) {
            return yFail;
        }
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        int sectionCount = blockChunkComponent.getSectionCount();
        int indexSection = ChunkUtil.indexSection(y);
        while (indexSection < sectionCount) {
            Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(indexSection);
            FluidSection fluidSectionComponent = chunkStore.getComponent(sectionRef, FluidSection.getComponentType());
            BlockSection chunkSection = blockChunkComponent.getSectionAtIndex(indexSection);
            if (chunkSection.isSolidAir() && fluidSectionComponent != null && fluidSectionComponent.isEmpty()) {
                if ((y = 32 * ++indexSection) < 320) continue;
                return 319;
            }
            int yTop = 32 * ++indexSection;
            while (y < yTop) {
                int fluidId;
                int blockId = chunkSection.get(x, y++, z);
                int n = fluidId = fluidSectionComponent != null ? fluidSectionComponent.getFluidId(x, y, z) : 0;
                if (blockId == 0 && fluidId == 0) continue;
                BlockType blockType = assetMap.getAsset(blockId);
                if (blockType == null || blockType.isUnknown()) {
                    return y - 1;
                }
                int filler = chunkSection.getFiller(x, y, z);
                if (filler == 0 && WorldUtil.isEmptyOnlyBlock(blockType, fluidId)) continue;
                return y - 1;
            }
        }
        return Integer.MAX_VALUE;
    }
}

