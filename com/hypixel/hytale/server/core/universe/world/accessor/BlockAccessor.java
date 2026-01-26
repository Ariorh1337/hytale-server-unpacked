/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.accessor;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.function.predicate.TriIntPredicate;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.accessor.IChunkAccessorSync;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BlockAccessor {
    public int getX();

    public int getZ();

    public ChunkAccessor getChunkAccessor();

    public int getBlock(int var1, int var2, int var3);

    default public int getBlock(@Nonnull Vector3i pos) {
        return this.getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean setBlock(int var1, int var2, int var3, int var4, BlockType var5, int var6, int var7, int var8);

    default public boolean setBlock(int x, int y, int z, int id, BlockType blockType) {
        return this.setBlock(x, y, z, id, blockType, 0, 0, 0);
    }

    default public boolean setBlock(int x, int y, int z, String blockTypeKey) {
        return this.setBlock(x, y, z, blockTypeKey, 0);
    }

    default public boolean setBlock(int x, int y, int z, String blockTypeKey, int settings) {
        int index = BlockType.getAssetMap().getIndex(blockTypeKey);
        if (index == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! " + blockTypeKey);
        }
        return this.setBlock(x, y, z, index, settings);
    }

    default public boolean setBlock(int x, int y, int z, int id) {
        return this.setBlock(x, y, z, id, 0);
    }

    default public boolean setBlock(int x, int y, int z, int id, int settings) {
        return this.setBlock(x, y, z, id, BlockType.getAssetMap().getAsset(id), 0, 0, settings);
    }

    default public boolean setBlock(int x, int y, int z, @Nonnull BlockType blockType) {
        return this.setBlock(x, y, z, blockType, 0);
    }

    default public boolean setBlock(int x, int y, int z, @Nonnull BlockType blockType, int settings) {
        String key = blockType.getId();
        int index = BlockType.getAssetMap().getIndex(key);
        if (index == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! " + key);
        }
        return this.setBlock(x, y, z, index, blockType, 0, 0, settings);
    }

    default public boolean breakBlock(int x, int y, int z, int filler, int settings) {
        if ((settings & 0x10) == 0) {
            x -= FillerBlockUtil.unpackX(filler);
            y -= FillerBlockUtil.unpackY(filler);
            z -= FillerBlockUtil.unpackZ(filler);
        }
        return this.setBlock(x, y, z, 0, BlockType.EMPTY, 0, 0, settings);
    }

    default public boolean breakBlock(int x, int y, int z) {
        return this.breakBlock(x, y, z, 0);
    }

    default public boolean breakBlock(int x, int y, int z, int settings) {
        return this.breakBlock(x, y, z, 0, settings);
    }

    default public boolean testBlocks(int x, int y, int z, @Nonnull BlockType blockTypeToTest, int rotation, @Nonnull TriIntPredicate predicate) {
        int worldX = (this.getX() << 5) + (x & 0x1F);
        int worldZ = (this.getZ() << 5) + (z & 0x1F);
        return FillerBlockUtil.testFillerBlocks(BlockBoundingBoxes.getAssetMap().getAsset(blockTypeToTest.getHitboxTypeIndex()).get(rotation), (x1, y1, z1) -> {
            int blockX = worldX + x1;
            int blockY = y + y1;
            int blockZ = worldZ + z1;
            return predicate.test(blockX, blockY, blockZ);
        });
    }

    default public boolean testBlockTypes(int x, int y, int z, @Nonnull BlockType blockTypeToTest, int rotation, @Nonnull IChunkAccessorSync.TestBlockFunction predicate) {
        int worldX = (this.getX() << 5) + (x & 0x1F);
        int worldZ = (this.getZ() << 5) + (z & 0x1F);
        return this.testBlocks(x, y, z, blockTypeToTest, rotation, (blockX, blockY, blockZ) -> {
            int filler;
            int otherRotation;
            int block;
            boolean sameChunk = ChunkUtil.isSameChunk(worldX, worldZ, blockX, blockZ);
            if (sameChunk) {
                block = this.getBlock(blockX, blockY, blockZ);
                otherRotation = this.getRotationIndex(blockX, blockY, blockZ);
                filler = this.getFiller(blockX, blockY, blockZ);
            } else {
                Object chunk = this.getChunkAccessor().getNonTickingChunk(ChunkUtil.indexChunkFromBlock(blockX, blockZ));
                block = chunk.getBlock(blockX, blockY, blockZ);
                otherRotation = chunk.getRotationIndex(blockX, blockY, blockZ);
                filler = chunk.getFiller(blockX, blockY, blockZ);
            }
            return predicate.test(blockX, blockY, blockZ, BlockType.getAssetMap().getAsset(block), otherRotation, filler);
        });
    }

    default public boolean placeBlock(int x, int y, int z, String originalBlockTypeKey, @Nonnull Rotation yaw, @Nonnull Rotation pitch, @Nonnull Rotation roll, int settings) {
        return this.placeBlock(x, y, z, originalBlockTypeKey, RotationTuple.of(yaw, pitch, roll), settings, true);
    }

    default public boolean placeBlock(int x, int y, int z, String originalBlockTypeKey, @Nonnull RotationTuple rotationTuple, int settings, boolean validatePlacement) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        BlockType placedBlockType = (BlockType)assetMap.getAsset(originalBlockTypeKey);
        int rotationIndex = rotationTuple.index();
        if (validatePlacement && !this.testPlaceBlock(x, y, z, placedBlockType, rotationIndex)) {
            return false;
        }
        int setBlockSettings = 0;
        if ((settings & 2) != 0) {
            setBlockSettings |= 0x100;
        }
        this.setBlock(x, y, z, assetMap.getIndex(originalBlockTypeKey), placedBlockType, rotationIndex, 0, setBlockSettings);
        return true;
    }

    default public boolean placeBlock(int x, int y, int z, String blockTypeKey, @Nonnull Rotation yaw, @Nonnull Rotation pitch, @Nonnull Rotation roll) {
        return this.placeBlock(x, y, z, blockTypeKey, yaw, pitch, roll, 0);
    }

    default public boolean testPlaceBlock(int x, int y, int z, @Nonnull BlockType blockTypeToTest, int rotationIndex) {
        return this.testPlaceBlock(x, y, z, blockTypeToTest, rotationIndex, (x1, y1, z1, blockType, rotation, filler) -> false);
    }

    default public boolean testPlaceBlock(int x, int y, int z, @Nonnull BlockType blockTypeToTest, int rotationIndex, @Nonnull IChunkAccessorSync.TestBlockFunction filter) {
        return this.testBlockTypes(x, y, z, blockTypeToTest, rotationIndex, (blockX, blockY, blockZ, blockType, rotation, filler) -> {
            if (blockType == BlockType.EMPTY) {
                return true;
            }
            if (blockType.getMaterial() == BlockMaterial.Empty) {
                return true;
            }
            if (filler != 0 && blockType.isUnknown()) {
                return true;
            }
            return filter.test(blockX, blockY, blockZ, blockType, rotation, filler);
        });
    }

    @Nullable
    default public BlockType getBlockType(int x, int y, int z) {
        return BlockType.getAssetMap().getAsset(this.getBlock(x, y, z));
    }

    @Nullable
    default public BlockType getBlockType(@Nonnull Vector3i block) {
        return this.getBlockType(block.getX(), block.getY(), block.getZ());
    }

    public boolean setTicking(int var1, int var2, int var3, boolean var4);

    public boolean isTicking(int var1, int var2, int var3);

    @Nullable
    @Deprecated
    public BlockState getState(int var1, int var2, int var3);

    @Nullable
    @Deprecated
    public Holder<ChunkStore> getBlockComponentHolder(int var1, int var2, int var3);

    @Deprecated
    public void setState(int var1, int var2, int var3, BlockState var4, boolean var5);

    @Deprecated
    default public void setState(int x, int y, int z, BlockState state) {
        this.setState(x, y, z, state, true);
    }

    default public void setBlockInteractionState(@Nonnull Vector3i blockPosition, @Nonnull BlockType blockType, @Nonnull String state) {
        this.setBlockInteractionState(blockPosition.x, blockPosition.y, blockPosition.z, blockType, state, false);
    }

    default public void setBlockInteractionState(int x, int y, int z, @Nonnull BlockType blockType, @Nonnull String state, boolean force) {
        if (blockType.getData() == null) {
            return;
        }
        String currentState = BlockAccessor.getCurrentInteractionState(blockType);
        if (!force && currentState != null && currentState.equals(state)) {
            return;
        }
        BlockType newState = blockType.getBlockForState(state);
        if (newState == null) {
            return;
        }
        int settings = 198;
        int currentRotation = this.getRotationIndex(x, y, z);
        this.setBlock(x, y, z, BlockType.getAssetMap().getIndex(newState.getId()), newState, currentRotation, 0, 198);
    }

    @Nullable
    public static String getCurrentInteractionState(@Nonnull BlockType blockType) {
        return blockType.getState() != null ? blockType.getStateForBlock(blockType) : null;
    }

    @Deprecated(forRemoval=true)
    public int getFluidId(int var1, int var2, int var3);

    @Deprecated(forRemoval=true)
    public byte getFluidLevel(int var1, int var2, int var3);

    @Deprecated(forRemoval=true)
    public int getSupportValue(int var1, int var2, int var3);

    @Deprecated(forRemoval=true)
    public int getFiller(int var1, int var2, int var3);

    @Deprecated(forRemoval=true)
    public int getRotationIndex(int var1, int var2, int var3);

    @Deprecated(forRemoval=true)
    default public RotationTuple getRotation(int x, int y, int z) {
        return RotationTuple.get(this.getRotationIndex(x, y, z));
    }
}

