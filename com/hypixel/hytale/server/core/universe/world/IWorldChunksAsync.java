/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import java.util.concurrent.CompletableFuture;

@Deprecated
public interface IWorldChunksAsync {
    public CompletableFuture<WorldChunk> getChunkAsync(long var1);

    public CompletableFuture<WorldChunk> getNonTickingChunkAsync(long var1);

    default public CompletableFuture<WorldChunk> getChunkAsync(int x, int z) {
        return this.getChunkAsync(ChunkUtil.indexChunk(x, z));
    }

    default public CompletableFuture<WorldChunk> getNonTickingChunkAsync(int x, int z) {
        return this.getNonTickingChunkAsync(ChunkUtil.indexChunk(x, z));
    }
}

