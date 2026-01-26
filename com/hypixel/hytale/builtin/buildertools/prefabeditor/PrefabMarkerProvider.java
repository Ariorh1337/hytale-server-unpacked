/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSession;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSessionManager;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditingMetadata;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import javax.annotation.Nonnull;

public class PrefabMarkerProvider
implements WorldMapManager.MarkerProvider {
    public static final PrefabMarkerProvider INSTANCE = new PrefabMarkerProvider();

    @Override
    public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
        Player player;
        PrefabEditSessionManager sessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
        PrefabEditSession session = sessionManager.getPrefabEditSession((player = tracker.getPlayer()).getUuid());
        if (session == null || !session.getWorldName().equals(world.getName())) {
            return;
        }
        for (PrefabEditingMetadata metadata : session.getLoadedPrefabMetadata().values()) {
            MapMarker marker = PrefabEditSession.createPrefabMarker(metadata);
            tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker);
        }
    }
}

