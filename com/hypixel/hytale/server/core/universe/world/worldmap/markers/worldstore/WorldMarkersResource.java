/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers.worldstore;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarker;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarkersStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class WorldMarkersResource
implements Resource<ChunkStore>,
UserMapMarkersStore {
    public static final BuilderCodec<WorldMarkersResource> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(WorldMarkersResource.class, WorldMarkersResource::new).append(new KeyedCodec<T[]>("UserMarkers", UserMapMarker.ARRAY_CODEC), (res, value) -> {
        res.mapMarkersById = Arrays.stream(value).collect(Collectors.toConcurrentMap(UserMapMarker::getId, m -> m));
    }, res -> res.getUserMapMarkers().toArray(new UserMapMarker[0])).documentation("The stored map markers submitted by this player.").add()).build();
    private Map<String, UserMapMarker> mapMarkersById = new ConcurrentHashMap<String, UserMapMarker>();

    public static ResourceType<ChunkStore, WorldMarkersResource> getResourceType() {
        return Universe.get().getWorldMarkersResourceType();
    }

    @Override
    @NonNullDecl
    public Collection<? extends UserMapMarker> getUserMapMarkers() {
        return this.mapMarkersById.values();
    }

    @Override
    @NonNullDecl
    public Collection<? extends UserMapMarker> getUserMapMarkers(UUID createdByUuid) {
        ArrayList<UserMapMarker> filtered = new ArrayList<UserMapMarker>();
        for (UserMapMarker marker : this.mapMarkersById.values()) {
            if (!createdByUuid.equals(marker.getCreatedByUuid())) continue;
            filtered.add(marker);
        }
        return filtered;
    }

    @Override
    public void setUserMapMarkers(@NullableDecl Collection<? extends UserMapMarker> markers) {
        this.mapMarkersById = markers == null ? new ConcurrentHashMap<String, UserMapMarker>() : (Map)markers.stream().collect(Collectors.toConcurrentMap(UserMapMarker::getId, x -> x));
    }

    @Override
    @NullableDecl
    public UserMapMarker getUserMapMarker(String markerId) {
        return this.mapMarkersById.get(markerId);
    }

    @Override
    @NullableDecl
    public Resource<ChunkStore> clone() {
        WorldMarkersResource clone = new WorldMarkersResource();
        clone.mapMarkersById = new ConcurrentHashMap<String, UserMapMarker>(this.mapMarkersById);
        return clone;
    }
}

