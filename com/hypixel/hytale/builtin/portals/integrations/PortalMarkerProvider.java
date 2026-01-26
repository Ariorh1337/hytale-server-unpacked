/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.integrations;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.spawn.IndividualSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import com.hypixel.hytale.server.core.util.PositionUtil;

public class PortalMarkerProvider
implements WorldMapManager.MarkerProvider {
    public static final PortalMarkerProvider INSTANCE = new PortalMarkerProvider();

    @Override
    public void update(World world, MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        if (!(spawnProvider instanceof IndividualSpawnProvider)) {
            return;
        }
        IndividualSpawnProvider individualSpawnProvider = (IndividualSpawnProvider)spawnProvider;
        Transform spawnPoint = individualSpawnProvider.getFirstSpawnPoint();
        if (spawnPoint == null) {
            return;
        }
        tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, spawnPoint.getPosition(), spawnPoint.getRotation().getYaw(), "Portal", "Fragment Exit", spawnPoint, (id, name, sp) -> new MapMarker((String)id, (String)name, "Portal.png", PositionUtil.toTransformPacket(sp), null));
    }
}

