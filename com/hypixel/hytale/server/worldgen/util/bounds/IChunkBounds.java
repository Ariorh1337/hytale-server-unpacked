/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.bounds;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import java.util.Random;
import javax.annotation.Nonnull;

public interface IChunkBounds {
    public int getLowBoundX();

    public int getLowBoundZ();

    public int getHighBoundX();

    public int getHighBoundZ();

    default public int getLowBoundX(@Nonnull PrefabRotation rotation) {
        return Math.min(rotation.getX(this.getLowBoundX(), this.getLowBoundZ()), rotation.getX(this.getHighBoundX(), this.getHighBoundZ()));
    }

    default public int getLowBoundZ(@Nonnull PrefabRotation rotation) {
        return Math.min(rotation.getZ(this.getLowBoundX(), this.getLowBoundZ()), rotation.getZ(this.getHighBoundX(), this.getHighBoundZ()));
    }

    default public int getHighBoundX(@Nonnull PrefabRotation rotation) {
        return Math.max(rotation.getX(this.getLowBoundX(), this.getLowBoundZ()), rotation.getX(this.getHighBoundX(), this.getHighBoundZ()));
    }

    default public int getHighBoundZ(@Nonnull PrefabRotation rotation) {
        return Math.max(rotation.getZ(this.getLowBoundX(), this.getLowBoundZ()), rotation.getZ(this.getHighBoundX(), this.getHighBoundZ()));
    }

    default public boolean intersectsChunk(long chunkIndex) {
        return this.intersectsChunk(ChunkUtil.xOfChunkIndex(chunkIndex), ChunkUtil.zOfChunkIndex(chunkIndex));
    }

    default public boolean intersectsChunk(int chunkX, int chunkZ) {
        return ChunkUtil.maxBlock(chunkX) >= this.getLowBoundX() && ChunkUtil.minBlock(chunkX) <= this.getHighBoundX() && ChunkUtil.maxBlock(chunkZ) >= this.getLowBoundZ() && ChunkUtil.minBlock(chunkZ) <= this.getHighBoundZ();
    }

    default public int randomX(@Nonnull Random random) {
        return random.nextInt(this.getHighBoundX() - this.getLowBoundX()) + this.getLowBoundX();
    }

    default public int randomZ(@Nonnull Random random) {
        return random.nextInt(this.getHighBoundZ() - this.getLowBoundZ()) + this.getLowBoundZ();
    }

    default public double fractionX(double d) {
        return (double)(this.getHighBoundX() - this.getLowBoundX()) * d + (double)this.getLowBoundX();
    }

    default public double fractionZ(double d) {
        return (double)(this.getHighBoundZ() - this.getLowBoundZ()) * d + (double)this.getLowBoundZ();
    }

    default public int getLowChunkX() {
        return ChunkUtil.chunkCoordinate(this.getLowBoundX());
    }

    default public int getLowChunkZ() {
        return ChunkUtil.chunkCoordinate(this.getLowBoundZ());
    }

    default public int getHighChunkX() {
        return ChunkUtil.chunkCoordinate(this.getHighBoundX());
    }

    default public int getHighChunkZ() {
        return ChunkUtil.chunkCoordinate(this.getHighBoundZ());
    }
}

