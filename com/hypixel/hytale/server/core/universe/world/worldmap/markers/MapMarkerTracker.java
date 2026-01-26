/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers;

import com.hypixel.hytale.function.function.TriFunction;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMap;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class MapMarkerTracker {
    private final WorldMapTracker worldMapTracker;
    private final Player player;
    private final Map<String, MapMarker> sentToClientById = new ConcurrentHashMap<String, MapMarker>();
    public static final float SMALL_MOVEMENTS_UPDATE_INTERVAL = 10.0f;
    private float smallMovementsTimer;
    private Predicate<PlayerRef> playerMapFilter;
    @Nonnull
    private final Set<String> tempToRemove = new HashSet<String>();
    @Nonnull
    private final Set<MapMarker> tempToAdd = new HashSet<MapMarker>();
    @Nonnull
    private final Set<String> tempTestedMarkers = new HashSet<String>();

    public MapMarkerTracker(WorldMapTracker worldMapTracker) {
        this.worldMapTracker = worldMapTracker;
        this.player = worldMapTracker.getPlayer();
    }

    public Player getPlayer() {
        return this.player;
    }

    public Map<String, MapMarker> getSentMarkers() {
        return this.sentToClientById;
    }

    public Predicate<PlayerRef> getPlayerMapFilter() {
        return this.playerMapFilter;
    }

    public void setPlayerMapFilter(Predicate<PlayerRef> playerMapFilter) {
        this.playerMapFilter = playerMapFilter;
    }

    private boolean isSendingSmallMovements() {
        return this.smallMovementsTimer <= 0.0f;
    }

    private void resetSmallMovementTimer() {
        this.smallMovementsTimer = 10.0f;
    }

    public void updatePointsOfInterest(float dt, @Nonnull World world, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
        if (this.worldMapTracker.getTransformComponent() == null) {
            return;
        }
        this.smallMovementsTimer -= dt;
        WorldMapManager worldMapManager = world.getWorldMapManager();
        Map<String, WorldMapManager.MarkerProvider> markerProviders = worldMapManager.getMarkerProviders();
        this.tempToAdd.clear();
        this.tempTestedMarkers.clear();
        for (WorldMapManager.MarkerProvider provider : markerProviders.values()) {
            provider.update(world, this, chunkViewRadius, playerChunkX, playerChunkZ);
        }
        if (this.isSendingSmallMovements()) {
            this.resetSmallMovementTimer();
        }
        this.tempToRemove.clear();
        this.tempToRemove.addAll(this.sentToClientById.keySet());
        if (!this.tempTestedMarkers.isEmpty()) {
            this.tempToRemove.removeAll(this.tempTestedMarkers);
        }
        for (String removedMarkerId : this.tempToRemove) {
            this.sentToClientById.remove(removedMarkerId);
        }
        if (!this.tempToAdd.isEmpty() || !this.tempToRemove.isEmpty()) {
            MapMarker[] addedMarkers = !this.tempToAdd.isEmpty() ? (MapMarker[])this.tempToAdd.toArray(MapMarker[]::new) : null;
            String[] removedMarkers = !this.tempToRemove.isEmpty() ? (String[])this.tempToRemove.toArray(String[]::new) : null;
            this.player.getPlayerConnection().writeNoCache(new UpdateWorldMap(null, addedMarkers, removedMarkers));
        }
    }

    public void trySendMarker(int chunkViewRadius, int playerChunkX, int playerChunkZ, @Nonnull MapMarker marker) {
        this.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker.transform.position.x, marker.transform.position.z, marker.transform.orientation.yaw, marker.id, marker.name, marker, (id, name, m) -> m);
    }

    public <T> void trySendMarker(int chunkViewRadius, int playerChunkX, int playerChunkZ, @Nonnull Vector3d markerPos, float markerYaw, @Nonnull String markerId, @Nonnull String markerDisplayName, @Nonnull T param, @Nonnull TriFunction<String, String, T, MapMarker> markerSupplier) {
        this.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, markerPos.x, markerPos.z, markerYaw, markerId, markerDisplayName, param, markerSupplier);
    }

    private <T> void trySendMarker(int chunkViewRadius, int playerChunkX, int playerChunkZ, double markerX, double markerZ, float markerYaw, @Nonnull String markerId, @Nonnull String markerName, @Nonnull T param, @Nonnull TriFunction<String, String, T, MapMarker> markerSupplier) {
        boolean shouldBeVisible;
        boolean bl = shouldBeVisible = chunkViewRadius == -1 || WorldMapTracker.shouldBeVisible(chunkViewRadius, MathUtil.floor(markerX) >> 5, MathUtil.floor(markerZ) >> 5, playerChunkX, playerChunkZ);
        if (!shouldBeVisible) {
            return;
        }
        this.tempTestedMarkers.add(markerId);
        boolean needsUpdate = false;
        MapMarker oldMarker = this.sentToClientById.get(markerId);
        if (oldMarker != null) {
            if (!markerName.equals(oldMarker.name)) {
                needsUpdate = true;
            }
            if (!needsUpdate) {
                double distance = Math.abs(oldMarker.transform.orientation.yaw - markerYaw);
                boolean bl2 = needsUpdate = distance > 0.05 || this.isSendingSmallMovements() && distance > 0.001;
            }
            if (!needsUpdate) {
                Position oldPosition = oldMarker.transform.position;
                double distance = Vector2d.distance(oldPosition.x, oldPosition.z, markerX, markerZ);
                needsUpdate = distance > 5.0 || this.isSendingSmallMovements() && distance > 0.1;
            }
        } else {
            needsUpdate = true;
        }
        if (!needsUpdate) {
            return;
        }
        MapMarker marker = markerSupplier.apply(markerId, markerName, (String)param);
        this.sentToClientById.put(markerId, marker);
        this.tempToAdd.add(marker);
    }

    public void copyFrom(@Nonnull MapMarkerTracker other) {
        for (Map.Entry<String, MapMarker> entry : other.sentToClientById.entrySet()) {
            this.sentToClientById.put(entry.getKey(), new MapMarker(entry.getValue()));
        }
    }
}

