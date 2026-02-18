/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers.user;

import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface UserMapMarkersStore {
    @Nonnull
    public Collection<? extends UserMapMarker> getUserMapMarkers();

    @Nonnull
    public Collection<? extends UserMapMarker> getUserMapMarkers(UUID var1);

    public void setUserMapMarkers(@Nullable Collection<? extends UserMapMarker> var1);

    default public void addUserMapMarker(UserMapMarker marker) {
        ArrayList<? extends UserMapMarker> markers = new ArrayList<UserMapMarker>(this.getUserMapMarkers());
        markers.add(marker);
        this.setUserMapMarkers(markers);
    }

    default public void removeUserMapMarker(String markerId) {
        ArrayList<? extends UserMapMarker> markers = new ArrayList<UserMapMarker>(this.getUserMapMarkers());
        markers.removeIf(marker -> markerId.equals(marker.getId()));
        this.setUserMapMarkers(markers);
    }

    @Nullable
    public UserMapMarker getUserMapMarker(String var1);
}

