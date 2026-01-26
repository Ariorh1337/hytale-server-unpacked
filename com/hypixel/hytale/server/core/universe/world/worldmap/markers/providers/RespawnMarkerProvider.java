/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import com.hypixel.hytale.server.core.util.PositionUtil;
import javax.annotation.Nonnull;

public class RespawnMarkerProvider
implements WorldMapManager.MarkerProvider {
    public static final RespawnMarkerProvider INSTANCE = new RespawnMarkerProvider();

    private RespawnMarkerProvider() {
    }

    @Override
    public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
        WorldMapConfig worldMapConfig = world.getGameplayConfig().getWorldMapConfig();
        if (!worldMapConfig.isDisplayHome()) {
            return;
        }
        PlayerRespawnPointData[] respawnPoints = tracker.getPlayer().getPlayerConfigData().getPerWorldData(world.getName()).getRespawnPoints();
        if (respawnPoints == null) {
            return;
        }
        for (int i = 0; i < respawnPoints.length; ++i) {
            RespawnMarkerProvider.addRespawnMarker(tracker, playerChunkX, playerChunkZ, respawnPoints[i], i);
        }
    }

    private static void addRespawnMarker(@Nonnull MapMarkerTracker tracker, int playerChunkX, int playerChunkZ, @Nonnull PlayerRespawnPointData respawnPoint, int index) {
        String respawnPointName = respawnPoint.getName();
        Vector3i respawnPointPosition = respawnPoint.getBlockPosition();
        tracker.trySendMarker(-1, playerChunkX, playerChunkZ, respawnPointPosition.toVector3d(), 0.0f, respawnPointName + index, respawnPointName, respawnPointPosition, (id, name, rp) -> new MapMarker((String)id, (String)name, "Home.png", PositionUtil.toTransformPacket(new Transform((Vector3i)rp)), null));
    }
}

