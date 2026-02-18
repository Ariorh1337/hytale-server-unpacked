/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers.user;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.worldmap.CreateUserMarker;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.worldmap.UserMapMarkerConfig;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarker;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarkersStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.worldstore.WorldMarkersResource;
import java.util.Collection;
import java.util.UUID;

public final class UserMarkerValidator {
    private static final int NAME_LENGTH_LIMIT = 24;

    public static PlaceResult validatePlacing(Ref<EntityStore> ref, CreateUserMarker packet) {
        int limit;
        boolean shared = packet.shared;
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        Player player = store.getComponent(ref, Player.getComponentType());
        if (UserMarkerValidator.isPlayerTooFarFromMarker(ref, packet.x, packet.z)) {
            return new Fail("server.worldmap.markers.edit.tooFar");
        }
        if (packet.name != null && packet.name.length() > 24) {
            return new Fail("server.worldmap.markers.create.nameTooLong");
        }
        UserMapMarkersStore markersStore = shared ? (UserMapMarkersStore)world.getChunkStore().getStore().getResource(WorldMarkersResource.getResourceType()) : player.getPlayerConfigData().getPerWorldData(world.getName());
        UUID playerUuid = store.getComponent(ref, UUIDComponent.getComponentType()).getUuid();
        Collection<? extends UserMapMarker> markersByPlayer = markersStore.getUserMapMarkers(playerUuid);
        UserMapMarkerConfig markersConfig = world.getGameplayConfig().getWorldMapConfig().getUserMapMarkerConfig();
        if (!markersConfig.isAllowCreatingMarkers()) {
            return new Fail("server.worldmap.markers.create.creationDisabled");
        }
        int n = limit = shared ? markersConfig.getMaxSharedMarkersPerPlayer() : markersConfig.getMaxPersonalMarkersPerPlayer();
        if (markersByPlayer.size() + 1 >= limit) {
            String msg = shared ? "server.worldmap.markers.create.tooManyShared" : "server.worldmap.markers.create.tooManyPersonal";
            return new Fail(Message.translation(msg).param("limit", limit));
        }
        return new CanSpawn(player, markersStore);
    }

    public static RemoveResult validateRemove(Ref<EntityStore> ref, UserMapMarker marker) {
        boolean hasPermission;
        UUID createdBy;
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        if (UserMarkerValidator.isPlayerTooFarFromMarker(ref, marker.getX(), marker.getZ())) {
            return new Fail("server.worldmap.markers.edit.tooFar");
        }
        UserMapMarkerConfig markersConfig = world.getGameplayConfig().getWorldMapConfig().getUserMapMarkerConfig();
        UUID playerUuid = store.getComponent(ref, UUIDComponent.getComponentType()).getUuid();
        boolean isOwner = playerUuid.equals(createdBy = marker.getCreatedByUuid()) || createdBy == null;
        boolean bl = hasPermission = isOwner || markersConfig.isAllowDeleteOtherPlayersSharedMarkers();
        if (!hasPermission) {
            return new Fail("server.worldmap.markers.edit.notOwner");
        }
        return new CanRemove();
    }

    private static boolean isPlayerTooFarFromMarker(Ref<EntityStore> ref, double markerX, double markerZ) {
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        Transform transform = store.getComponent(ref, TransformComponent.getComponentType()).getTransform();
        Vector3d playerPosition = transform.getPosition();
        double distanceToMarker = playerPosition.distanceSquaredTo(markerX, playerPosition.y, markerZ);
        return distanceToMarker > UserMarkerValidator.getMaxRemovalDistanceSquared(player);
    }

    private static double getMaxRemovalDistanceSquared(Player player) {
        double maxDistance = (double)player.getViewRadius() * 1.5 * 32.0;
        return maxDistance * maxDistance;
    }

    public record Fail(Message errorMsg) implements PlaceResult,
    RemoveResult
    {
        public Fail(String messageKey) {
            this(Message.translation(messageKey));
        }
    }

    public record CanSpawn(Player player, UserMapMarkersStore markersStore) implements PlaceResult
    {
    }

    public record CanRemove() implements RemoveResult
    {
    }

    public static sealed interface RemoveResult
    permits Fail, CanRemove {
    }

    public static sealed interface PlaceResult
    permits Fail, CanSpawn {
    }
}

