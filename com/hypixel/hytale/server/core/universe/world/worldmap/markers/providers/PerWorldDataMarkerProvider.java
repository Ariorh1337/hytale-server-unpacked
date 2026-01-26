/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;

import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import javax.annotation.Nonnull;

public class PerWorldDataMarkerProvider
implements WorldMapManager.MarkerProvider {
    public static final PerWorldDataMarkerProvider INSTANCE = new PerWorldDataMarkerProvider();

    private PerWorldDataMarkerProvider() {
    }

    @Override
    public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
        Player player = tracker.getPlayer();
        PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
        MapMarker[] worldMapMarkers = perWorldData.getWorldMapMarkers();
        if (worldMapMarkers == null) {
            return;
        }
        for (MapMarker marker : worldMapMarkers) {
            tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker);
        }
    }
}

