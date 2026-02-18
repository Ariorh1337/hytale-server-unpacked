/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.markers;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public interface MarkersCollector {
    public void add(MapMarker var1);

    public void addIgnoreViewDistance(MapMarker var1);

    @Deprecated
    @Nullable
    public Predicate<PlayerRef> getPlayerMapFilter();

    default public boolean isInViewDistance(Transform transform) {
        return this.isInViewDistance(transform.getPosition());
    }

    default public boolean isInViewDistance(Vector3d position) {
        return this.isInViewDistance(position.x, position.z);
    }

    public boolean isInViewDistance(double var1, double var3);
}

