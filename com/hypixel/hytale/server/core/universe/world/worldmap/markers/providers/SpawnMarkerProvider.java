/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import com.hypixel.hytale.server.core.util.PositionUtil;
import javax.annotation.Nonnull;

public class SpawnMarkerProvider
implements WorldMapManager.MarkerProvider {
    public static final SpawnMarkerProvider INSTANCE = new SpawnMarkerProvider();

    private SpawnMarkerProvider() {
    }

    @Override
    public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
        WorldMapConfig worldMapConfig = world.getGameplayConfig().getWorldMapConfig();
        if (!worldMapConfig.isDisplaySpawn()) {
            return;
        }
        Player player = tracker.getPlayer();
        Transform spawnPoint = world.getWorldConfig().getSpawnProvider().getSpawnPoint(player);
        if (spawnPoint == null) {
            return;
        }
        Vector3d spawnPosition = spawnPoint.getPosition();
        tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, spawnPosition, spawnPoint.getRotation().getYaw(), "Spawn", "Spawn", spawnPosition, (id, name, pos) -> new MapMarker((String)id, (String)name, "Spawn.png", PositionUtil.toTransformPacket(new Transform((Vector3d)pos)), null));
    }
}

