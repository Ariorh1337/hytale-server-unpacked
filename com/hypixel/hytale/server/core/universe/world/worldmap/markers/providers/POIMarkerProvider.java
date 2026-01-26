/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;

import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import java.util.Map;
import javax.annotation.Nonnull;

public class POIMarkerProvider
implements WorldMapManager.MarkerProvider {
    public static final POIMarkerProvider INSTANCE = new POIMarkerProvider();

    private POIMarkerProvider() {
    }

    @Override
    public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
        Map<String, MapMarker> globalMarkers = world.getWorldMapManager().getPointsOfInterest();
        if (globalMarkers.isEmpty()) {
            return;
        }
        for (MapMarker marker : globalMarkers.values()) {
            tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker);
        }
    }
}

