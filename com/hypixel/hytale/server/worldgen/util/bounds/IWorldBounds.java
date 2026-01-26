/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.bounds;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.worldgen.util.bounds.IChunkBounds;
import java.util.Random;
import javax.annotation.Nonnull;

public interface IWorldBounds
extends IChunkBounds {
    public int getLowBoundY();

    public int getHighBoundY();

    @Override
    default public boolean intersectsChunk(long chunkIndex) {
        return this.intersectsChunk(ChunkUtil.xOfChunkIndex(chunkIndex), ChunkUtil.zOfChunkIndex(chunkIndex));
    }

    default public int randomY(@Nonnull Random random) {
        return random.nextInt(this.getHighBoundY() - this.getLowBoundY()) + this.getLowBoundY();
    }

    default public double fractionY(double d) {
        return (double)(this.getHighBoundY() - this.getLowBoundY()) * d + (double)this.getLowBoundY();
    }
}

