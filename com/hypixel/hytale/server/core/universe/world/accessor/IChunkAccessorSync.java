/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.accessor;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.accessor.BlockAccessor;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public interface IChunkAccessorSync<WorldChunk extends BlockAccessor> {
    @Nullable
    public WorldChunk getChunkIfInMemory(long var1);

    @Nullable
    public WorldChunk loadChunkIfInMemory(long var1);

    @Nullable
    public WorldChunk getChunkIfLoaded(long var1);

    @Nullable
    public WorldChunk getChunkIfNonTicking(long var1);

    @Nullable
    public WorldChunk getChunk(long var1);

    @Nullable
    public WorldChunk getNonTickingChunk(long var1);

    default public int getBlock(@Nonnull Vector3i pos) {
        return this.getChunk(ChunkUtil.indexChunkFromBlock(pos.getX(), pos.getZ())).getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    default public int getBlock(int x, int y, int z) {
        return this.getChunk(ChunkUtil.indexChunkFromBlock(x, z)).getBlock(x, y, z);
    }

    @Nullable
    default public BlockType getBlockType(@Nonnull Vector3i pos) {
        return this.getBlockType(pos.getX(), pos.getY(), pos.getZ());
    }

    @Nullable
    default public BlockType getBlockType(int x, int y, int z) {
        WorldChunk chunk = this.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
        int blockId = chunk.getBlock(x, y, z);
        return BlockType.getAssetMap().getAsset(blockId);
    }

    default public void setBlock(int x, int y, int z, String blockTypeKey) {
        this.setBlock(x, y, z, blockTypeKey, 0);
    }

    default public void setBlock(int x, int y, int z, String blockTypeKey, int settings) {
        this.getChunk(ChunkUtil.indexChunkFromBlock(x, z)).setBlock(x, y, z, blockTypeKey, settings);
    }

    default public boolean breakBlock(int x, int y, int z, int settings) {
        return this.getChunk(ChunkUtil.indexChunkFromBlock(x, z)).breakBlock(x, y, z, settings);
    }

    default public boolean testBlockTypes(int x, int y, int z, @Nonnull BlockType blockTypeToTest, int rotation, @Nonnull TestBlockFunction predicate) {
        return this.getChunk(ChunkUtil.indexChunkFromBlock(x, z)).testBlockTypes(x, y, z, blockTypeToTest, rotation, predicate);
    }

    default public boolean testPlaceBlock(int x, int y, int z, @Nonnull BlockType blockTypeToTest, int rotation) {
        return this.getChunk(ChunkUtil.indexChunkFromBlock(x, z)).testPlaceBlock(x, y, z, blockTypeToTest, rotation);
    }

    default public boolean testPlaceBlock(int x, int y, int z, @Nonnull BlockType blockTypeToTest, int rotation, @Nonnull TestBlockFunction predicate) {
        return this.getChunk(ChunkUtil.indexChunkFromBlock(x, z)).testPlaceBlock(x, y, z, blockTypeToTest, rotation, predicate);
    }

    @Nullable
    @Deprecated
    default public BlockState getState(int x, int y, int z, boolean followFiller) {
        int filler;
        WorldChunk chunk = this.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
        if (followFiller && (filler = chunk.getFiller(x, y, z)) != 0) {
            x -= FillerBlockUtil.unpackX(filler);
            y -= FillerBlockUtil.unpackY(filler);
            z -= FillerBlockUtil.unpackZ(filler);
        }
        if (y < 0 || y >= 320) {
            return null;
        }
        return chunk.getState(x, y, z);
    }

    @Nullable
    default public Holder<ChunkStore> getBlockComponentHolder(int x, int y, int z) {
        if (y < 0 || y >= 320) {
            return null;
        }
        return this.getChunk(ChunkUtil.indexChunkFromBlock(x, z)).getBlockComponentHolder(x, y, z);
    }

    default public void setBlockInteractionState(@Nonnull Vector3i blockPosition, @Nonnull BlockType blockType, @Nonnull String state) {
        this.getChunk(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z)).setBlockInteractionState(blockPosition, blockType, state);
    }

    @Nonnull
    @Deprecated(forRemoval=true)
    default public BlockPosition getBaseBlock(@Nonnull BlockPosition position) {
        WorldChunk chunk = this.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(position.x, position.z));
        int filler = chunk.getFiller(position.x, position.y, position.z);
        if (filler != 0) {
            return new BlockPosition(position.x - FillerBlockUtil.unpackX(filler), position.y - FillerBlockUtil.unpackY(filler), position.z - FillerBlockUtil.unpackZ(filler));
        }
        return position;
    }

    default public int getBlockRotationIndex(int x, int y, int z) {
        return this.getChunk(ChunkUtil.indexChunkFromBlock(x, z)).getRotationIndex(x, y, z);
    }

    @FunctionalInterface
    public static interface TestBlockFunction {
        public boolean test(int var1, int var2, int var3, BlockType var4, int var5, int var6);
    }
}

