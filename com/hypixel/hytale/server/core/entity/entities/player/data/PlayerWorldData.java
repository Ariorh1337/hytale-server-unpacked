/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.entities.player.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.SavedMovementStates;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerDeathPositionData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarker;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarkersStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public final class PlayerWorldData
implements UserMapMarkersStore {
    @Nonnull
    public static final BuilderCodec<PlayerWorldData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlayerWorldData.class, PlayerWorldData::new).append(new KeyedCodec<Transform>("LastPosition", Transform.CODEC), (playerWorldData, lastPosition) -> {
        playerWorldData.lastPosition = lastPosition;
    }, playerWorldData -> playerWorldData.lastPosition).documentation("The last known position of the player.").add()).append(new KeyedCodec<SavedMovementStates>("LastMovementStates", ProtocolCodecs.SAVED_MOVEMENT_STATES), (playerWorldData, lastMovementStates) -> {
        playerWorldData.lastMovementStates = lastMovementStates;
    }, playerWorldData -> playerWorldData.lastMovementStates).documentation("The last known movement states of the player.").add()).append(new KeyedCodec<Boolean>("FirstSpawn", Codec.BOOLEAN), (playerWorldData, value) -> {
        playerWorldData.firstSpawn = value;
    }, playerWorldData -> playerWorldData.firstSpawn).documentation("Whether this is the first spawn of the player.").add()).append(new KeyedCodec<T[]>("RespawnPoints", new ArrayCodec<PlayerRespawnPointData>(PlayerRespawnPointData.CODEC, PlayerRespawnPointData[]::new)), (playerWorldData, respawnPointData) -> {
        playerWorldData.respawnPoints = respawnPointData;
    }, playerWorldData -> playerWorldData.respawnPoints).documentation("The respawn points of the player.").add()).append(new KeyedCodec<T[]>("DeathPositions", new ArrayCodec<PlayerDeathPositionData>(PlayerDeathPositionData.CODEC, PlayerDeathPositionData[]::new)), (playerWorldData, deathPositions) -> {
        playerWorldData.deathPositions = ObjectArrayList.wrap(deathPositions);
    }, playerWorldData -> (PlayerDeathPositionData[])playerWorldData.deathPositions.toArray(PlayerDeathPositionData[]::new)).documentation("The death positions of the player in this world.").add()).append(new KeyedCodec<T[]>("UserMarkers", UserMapMarker.ARRAY_CODEC), (playerWorldData, value) -> {
        playerWorldData.mapMarkersById = Arrays.stream(value).collect(Collectors.toConcurrentMap(UserMapMarker::getId, m -> m));
    }, playerWorldData -> playerWorldData.getUserMapMarkers().toArray(new UserMapMarker[0])).documentation("The stored map markers submitted by this player.").add()).build();
    private static final int DEATH_POSITIONS_COUNT_MAX = 5;
    private transient PlayerConfigData playerConfigData;
    private Transform lastPosition;
    private SavedMovementStates lastMovementStates;
    private Map<String, UserMapMarker> mapMarkersById = new ConcurrentHashMap<String, UserMapMarker>();
    private boolean firstSpawn = true;
    @Nullable
    private PlayerRespawnPointData[] respawnPoints;
    @Nonnull
    private List<PlayerDeathPositionData> deathPositions = new ObjectArrayList<PlayerDeathPositionData>();

    private PlayerWorldData() {
    }

    PlayerWorldData(@Nonnull PlayerConfigData playerConfigData) {
        this.playerConfigData = playerConfigData;
    }

    public void setPlayerConfigData(@Nonnull PlayerConfigData playerConfigData) {
        this.playerConfigData = playerConfigData;
    }

    public Transform getLastPosition() {
        return this.lastPosition;
    }

    public void setLastPosition(@Nonnull Transform lastPosition) {
        this.lastPosition = lastPosition;
        this.playerConfigData.markChanged();
    }

    public SavedMovementStates getLastMovementStates() {
        return this.lastMovementStates;
    }

    public void setLastMovementStates(@Nonnull MovementStates lastMovementStates, boolean save) {
        this.setLastMovementStates_internal(lastMovementStates);
        if (save) {
            this.playerConfigData.markChanged();
        }
    }

    private void setLastMovementStates_internal(@Nonnull MovementStates lastMovementStates) {
        this.lastMovementStates = new SavedMovementStates(lastMovementStates.flying);
    }

    @Override
    @Nonnull
    public Collection<? extends UserMapMarker> getUserMapMarkers() {
        return this.mapMarkersById.values();
    }

    @Override
    @NonNullDecl
    public Collection<? extends UserMapMarker> getUserMapMarkers(UUID placedByUuid) {
        return this.getUserMapMarkers();
    }

    @Override
    public void setUserMapMarkers(@Nullable Collection<? extends UserMapMarker> markers) {
        this.mapMarkersById = markers == null ? new ConcurrentHashMap<String, UserMapMarker>() : (Map)markers.stream().collect(Collectors.toConcurrentMap(UserMapMarker::getId, x -> x));
        this.playerConfigData.markChanged();
    }

    @Override
    @Nullable
    public UserMapMarker getUserMapMarker(String markerId) {
        return this.mapMarkersById.get(markerId);
    }

    public boolean isFirstSpawn() {
        return this.firstSpawn;
    }

    public void setFirstSpawn(boolean firstSpawn) {
        this.firstSpawn = firstSpawn;
    }

    @Nullable
    public PlayerRespawnPointData[] getRespawnPoints() {
        return this.respawnPoints;
    }

    public void setRespawnPoints(@Nonnull PlayerRespawnPointData[] respawnPoints) {
        this.respawnPoints = respawnPoints;
        this.playerConfigData.markChanged();
    }

    @Nonnull
    public List<PlayerDeathPositionData> getDeathPositions() {
        return this.deathPositions;
    }

    public void addLastDeath(@Nonnull String markerId, @Nonnull Transform transform, int deathDay) {
        this.deathPositions.add(new PlayerDeathPositionData(markerId, transform, deathDay));
        while (this.deathPositions.size() > 5) {
            this.deathPositions.removeFirst();
        }
        this.playerConfigData.markChanged();
    }

    public boolean removeLastDeath(@Nonnull String markerId) {
        boolean removed = this.deathPositions.removeIf(deathPosition -> deathPosition.getMarkerId().equalsIgnoreCase(markerId));
        if (removed) {
            this.playerConfigData.markChanged();
        }
        return removed;
    }
}

