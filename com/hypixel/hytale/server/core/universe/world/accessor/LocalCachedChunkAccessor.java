/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.accessor;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.accessor.OverridableChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LocalCachedChunkAccessor
implements OverridableChunkAccessor<WorldChunk> {
    private final ChunkAccessor<WorldChunk> delegate;
    private final int minX;
    private final int minZ;
    private final int length;
    @Nonnull
    private final WorldChunk[] chunks;

    @Nonnull
    public static LocalCachedChunkAccessor atWorldCoords(ChunkAccessor<WorldChunk> delegate, int centerX, int centerZ, int blockRadius) {
        int chunkRadius = ChunkUtil.chunkCoordinate(blockRadius) + 1;
        return LocalCachedChunkAccessor.atChunkCoords(delegate, ChunkUtil.chunkCoordinate(centerX), ChunkUtil.chunkCoordinate(centerZ), chunkRadius);
    }

    @Nonnull
    public static LocalCachedChunkAccessor atChunkCoords(ChunkAccessor<WorldChunk> delegate, int centerX, int centerZ, int chunkRadius) {
        return new LocalCachedChunkAccessor(delegate, centerX, centerZ, chunkRadius);
    }

    @Nonnull
    public static LocalCachedChunkAccessor atChunk(ChunkAccessor<WorldChunk> delegate, @Nonnull WorldChunk chunk, int chunkRadius) {
        LocalCachedChunkAccessor accessor = new LocalCachedChunkAccessor(delegate, chunk.getX(), chunk.getZ(), chunkRadius);
        accessor.overwrite(chunk);
        return accessor;
    }

    protected LocalCachedChunkAccessor(ChunkAccessor<WorldChunk> delegate, int centerX, int centerZ, int radius) {
        this.delegate = delegate;
        this.minX = centerX - radius;
        this.minZ = centerZ - radius;
        this.length = radius * 2 + 1;
        this.chunks = new WorldChunk[this.length * this.length];
    }

    public ChunkAccessor getDelegate() {
        return this.delegate;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getLength() {
        return this.length;
    }

    public int getCenterX() {
        return this.minX + this.length / 2;
    }

    public int getCenterZ() {
        return this.minZ + this.length / 2;
    }

    public void cacheChunksInRadius() {
        for (int xOffset = 0; xOffset < this.length; ++xOffset) {
            for (int zOffset = 0; zOffset < this.length; ++zOffset) {
                int arrayIndex = xOffset * this.length + zOffset;
                WorldChunk chunk = this.chunks[arrayIndex];
                if (chunk != null) continue;
                this.chunks[arrayIndex] = (WorldChunk)this.delegate.getChunkIfInMemory(ChunkUtil.indexChunk(this.minX + xOffset, this.minZ + zOffset));
            }
        }
    }

    @Override
    public void overwrite(@Nonnull WorldChunk wc) {
        int x = wc.getX();
        int z = wc.getZ();
        if ((x -= this.minX) >= 0 && x < this.length && (z -= this.minZ) >= 0 && z < this.length) {
            int arrayIndex = x * this.length + z;
            this.chunks[arrayIndex] = wc;
        }
    }

    @Override
    public WorldChunk getChunkIfInMemory(long index) {
        int x = ChunkUtil.xOfChunkIndex(index);
        int z = ChunkUtil.zOfChunkIndex(index);
        if ((x -= this.minX) >= 0 && x < this.length && (z -= this.minZ) >= 0 && z < this.length) {
            int arrayIndex = x * this.length + z;
            WorldChunk chunk = this.chunks[arrayIndex];
            if (chunk != null) {
                return chunk;
            }
            this.chunks[arrayIndex] = (WorldChunk)this.delegate.getChunkIfInMemory(index);
            return this.chunks[arrayIndex];
        }
        return (WorldChunk)this.delegate.getChunkIfInMemory(index);
    }

    @Nullable
    public WorldChunk getChunkIfInMemory(int x, int z) {
        int xOffset = x - this.minX;
        int zOffset = z - this.minZ;
        if (xOffset >= 0 && xOffset < this.length && zOffset >= 0 && zOffset < this.length) {
            int arrayIndex = xOffset * this.length + zOffset;
            WorldChunk chunk = this.chunks[arrayIndex];
            if (chunk != null) {
                return chunk;
            }
            this.chunks[arrayIndex] = (WorldChunk)this.delegate.getChunkIfInMemory(ChunkUtil.indexChunk(x, z));
            return this.chunks[arrayIndex];
        }
        return (WorldChunk)this.delegate.getChunkIfInMemory(ChunkUtil.indexChunk(x, z));
    }

    @Override
    public WorldChunk loadChunkIfInMemory(long index) {
        int x = ChunkUtil.xOfChunkIndex(index);
        int z = ChunkUtil.zOfChunkIndex(index);
        if ((x -= this.minX) >= 0 && x < this.length && (z -= this.minZ) >= 0 && z < this.length) {
            int arrayIndex = x * this.length + z;
            WorldChunk chunk = this.chunks[arrayIndex];
            if (chunk != null) {
                chunk.setFlag(ChunkFlag.TICKING, true);
                return chunk;
            }
            this.chunks[arrayIndex] = (WorldChunk)this.delegate.loadChunkIfInMemory(index);
            return this.chunks[arrayIndex];
        }
        return (WorldChunk)this.delegate.loadChunkIfInMemory(index);
    }

    @Override
    @Nullable
    public WorldChunk getChunkIfLoaded(long index) {
        int x = ChunkUtil.xOfChunkIndex(index);
        int z = ChunkUtil.zOfChunkIndex(index);
        if ((x -= this.minX) >= 0 && x < this.length && (z -= this.minZ) >= 0 && z < this.length) {
            int arrayIndex = x * this.length + z;
            WorldChunk chunk = this.chunks[arrayIndex];
            if (chunk == null) {
                chunk = this.chunks[arrayIndex] = (WorldChunk)this.delegate.getChunkIfInMemory(index);
            }
            return chunk != null && chunk.is(ChunkFlag.TICKING) ? chunk : null;
        }
        return (WorldChunk)this.delegate.getChunkIfLoaded(index);
    }

    @Nullable
    public WorldChunk getChunkIfLoaded(int x, int z) {
        int xOffset = x - this.minX;
        int zOffset = z - this.minZ;
        if (xOffset >= 0 && xOffset < this.length && zOffset >= 0 && zOffset < this.length) {
            int arrayIndex = xOffset * this.length + zOffset;
            WorldChunk chunk = this.chunks[arrayIndex];
            if (chunk == null) {
                chunk = this.chunks[arrayIndex] = (WorldChunk)this.delegate.getChunkIfInMemory(ChunkUtil.indexChunk(x, z));
            }
            return chunk != null && chunk.is(ChunkFlag.TICKING) ? chunk : null;
        }
        return (WorldChunk)this.delegate.getChunkIfLoaded(ChunkUtil.indexChunk(x, z));
    }

    @Override
    @Nullable
    public WorldChunk getChunkIfNonTicking(long index) {
        int x = ChunkUtil.xOfChunkIndex(index);
        int z = ChunkUtil.zOfChunkIndex(index);
        if ((x -= this.minX) >= 0 && x < this.length && (z -= this.minZ) >= 0 && z < this.length) {
            int arrayIndex = x * this.length + z;
            WorldChunk chunk = this.chunks[arrayIndex];
            if (chunk == null) {
                chunk = this.chunks[arrayIndex] = (WorldChunk)this.delegate.getChunkIfInMemory(index);
            }
            return chunk != null && chunk.is(ChunkFlag.TICKING) ? null : chunk;
        }
        return (WorldChunk)this.delegate.getChunkIfNonTicking(index);
    }

    @Override
    public WorldChunk getChunk(long index) {
        int x = ChunkUtil.xOfChunkIndex(index);
        int z = ChunkUtil.zOfChunkIndex(index);
        if ((x -= this.minX) >= 0 && x < this.length && (z -= this.minZ) >= 0 && z < this.length) {
            int arrayIndex = x * this.length + z;
            WorldChunk chunk = this.chunks[arrayIndex];
            if (chunk != null) {
                return chunk;
            }
            this.chunks[arrayIndex] = (WorldChunk)this.delegate.getChunk(index);
            return this.chunks[arrayIndex];
        }
        return (WorldChunk)this.delegate.getChunk(index);
    }

    @Override
    public WorldChunk getNonTickingChunk(long index) {
        int x = ChunkUtil.xOfChunkIndex(index);
        int z = ChunkUtil.zOfChunkIndex(index);
        if ((x -= this.minX) >= 0 && x < this.length && (z -= this.minZ) >= 0 && z < this.length) {
            int arrayIndex = x * this.length + z;
            WorldChunk chunk = this.chunks[arrayIndex];
            if (chunk != null) {
                return chunk;
            }
            this.chunks[arrayIndex] = (WorldChunk)this.delegate.getNonTickingChunk(index);
            return this.chunks[arrayIndex];
        }
        return (WorldChunk)this.delegate.getNonTickingChunk(index);
    }
}

