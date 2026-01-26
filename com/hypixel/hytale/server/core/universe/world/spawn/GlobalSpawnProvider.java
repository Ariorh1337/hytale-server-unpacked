/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.spawn;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import java.util.UUID;
import javax.annotation.Nonnull;

public class GlobalSpawnProvider
implements ISpawnProvider {
    @Nonnull
    public static BuilderCodec<GlobalSpawnProvider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(GlobalSpawnProvider.class, GlobalSpawnProvider::new).documentation("A spawn provider that provides a single static spawn point for all players.")).append(new KeyedCodec<Transform>("SpawnPoint", Transform.CODEC_DEGREES), (o, i) -> {
        o.spawnPoint = i;
    }, o -> o.spawnPoint).documentation("The spawn point for all players to spawn at").add()).build();
    private Transform spawnPoint;

    public GlobalSpawnProvider() {
    }

    public GlobalSpawnProvider(@Nonnull Transform spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Override
    public Transform getSpawnPoint(@Nonnull World world, @Nonnull UUID uuid) {
        return this.spawnPoint.clone();
    }

    @Override
    @Nonnull
    public Transform[] getSpawnPoints() {
        return new Transform[]{this.spawnPoint};
    }

    @Override
    public boolean isWithinSpawnDistance(@Nonnull Vector3d position, double distance) {
        return position.distanceSquaredTo(this.spawnPoint.getPosition()) < distance * distance;
    }
}

