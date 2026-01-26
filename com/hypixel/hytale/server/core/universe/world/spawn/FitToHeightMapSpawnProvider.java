/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.spawn;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import java.util.UUID;
import javax.annotation.Nonnull;

public class FitToHeightMapSpawnProvider
implements ISpawnProvider {
    @Nonnull
    public static BuilderCodec<FitToHeightMapSpawnProvider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FitToHeightMapSpawnProvider.class, FitToHeightMapSpawnProvider::new).documentation("A spawn provider that takes a spawn point from another provider and attempts to fit it to the heightmap of the world whenever the spawn point would place the player out of bounds.")).append(new KeyedCodec<ISpawnProvider>("SpawnProvider", ISpawnProvider.CODEC), (o, i) -> {
        o.spawnProvider = i;
    }, o -> o.spawnProvider).documentation("The target spawn provider to take the initial spawn point from.").add()).build();
    private ISpawnProvider spawnProvider;

    protected FitToHeightMapSpawnProvider() {
    }

    public FitToHeightMapSpawnProvider(@Nonnull ISpawnProvider spawnProvider) {
        this.spawnProvider = spawnProvider;
    }

    @Override
    @Nonnull
    public Transform getSpawnPoint(@Nonnull World world, @Nonnull UUID uuid) {
        Object worldChunk;
        Transform spawnPoint = this.spawnProvider.getSpawnPoint(world, uuid);
        Vector3d position = spawnPoint.getPosition();
        if (position.getY() < 0.0 && (worldChunk = world.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(position.getX(), position.getZ()))) != null) {
            int x = MathUtil.floor(position.getX());
            int z = MathUtil.floor(position.getZ());
            position.setY(((WorldChunk)worldChunk).getHeight(x, z) + 1);
        }
        return spawnPoint;
    }

    @Override
    public Transform[] getSpawnPoints() {
        return this.spawnProvider.getSpawnPoints();
    }

    @Override
    public boolean isWithinSpawnDistance(@Nonnull Vector3d position, double distance) {
        return this.spawnProvider.isWithinSpawnDistance(position, distance);
    }
}

