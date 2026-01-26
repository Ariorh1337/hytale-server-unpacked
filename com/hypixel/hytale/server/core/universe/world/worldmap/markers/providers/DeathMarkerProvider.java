/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerDeathPositionData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import com.hypixel.hytale.server.core.util.PositionUtil;
import java.util.List;
import javax.annotation.Nonnull;

public class DeathMarkerProvider
implements WorldMapManager.MarkerProvider {
    public static final DeathMarkerProvider INSTANCE = new DeathMarkerProvider();

    private DeathMarkerProvider() {
    }

    @Override
    public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
        WorldMapConfig worldMapConfig = world.getGameplayConfig().getWorldMapConfig();
        if (!worldMapConfig.isDisplayDeathMarker()) {
            return;
        }
        Player player = tracker.getPlayer();
        PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
        List<PlayerDeathPositionData> deathPositions = perWorldData.getDeathPositions();
        for (PlayerDeathPositionData deathPosition : deathPositions) {
            DeathMarkerProvider.addDeathMarker(tracker, playerChunkX, playerChunkZ, deathPosition);
        }
    }

    private static void addDeathMarker(@Nonnull MapMarkerTracker tracker, int playerChunkX, int playerChunkZ, @Nonnull PlayerDeathPositionData deathPosition) {
        String markerId = deathPosition.getMarkerId();
        Transform transform = deathPosition.getTransform();
        int deathDay = deathPosition.getDay();
        tracker.trySendMarker(-1, playerChunkX, playerChunkZ, transform.getPosition(), transform.getRotation().getYaw(), markerId, "Death (Day " + deathDay + ")", transform, (id, name, t) -> new MapMarker((String)id, (String)name, "Death.png", PositionUtil.toTransformPacket(t), null));
    }
}

