/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.server.core.universe.world.IWorldChunksAsync;
import com.hypixel.hytale.server.core.universe.world.accessor.IChunkAccessorSync;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

@Deprecated
public interface IWorldChunks
extends IChunkAccessorSync<WorldChunk>,
IWorldChunksAsync {
    @Deprecated
    public void consumeTaskQueue();

    public boolean isInThread();

    @Override
    default public WorldChunk getChunk(long index) {
        WorldChunk worldChunk = (WorldChunk)this.loadChunkIfInMemory(index);
        if (worldChunk != null) {
            return worldChunk;
        }
        CompletableFuture<WorldChunk> future = this.getChunkAsync(index);
        return this.waitForFutureWithoutLock(future);
    }

    @Override
    default public WorldChunk getNonTickingChunk(long index) {
        WorldChunk worldChunk = (WorldChunk)this.getChunkIfInMemory(index);
        if (worldChunk != null) {
            return worldChunk;
        }
        CompletableFuture<WorldChunk> future = this.getNonTickingChunkAsync(index);
        return this.waitForFutureWithoutLock(future);
    }

    default public <T> T waitForFutureWithoutLock(@Nonnull CompletableFuture<T> future) {
        if (!this.isInThread()) {
            return future.join();
        }
        AssetRegistry.ASSET_LOCK.readLock().unlock();
        while (!future.isDone()) {
            AssetRegistry.ASSET_LOCK.readLock().lock();
            try {
                this.consumeTaskQueue();
            }
            finally {
                AssetRegistry.ASSET_LOCK.readLock().unlock();
            }
            Thread.yield();
        }
        AssetRegistry.ASSET_LOCK.readLock().lock();
        return future.join();
    }
}

