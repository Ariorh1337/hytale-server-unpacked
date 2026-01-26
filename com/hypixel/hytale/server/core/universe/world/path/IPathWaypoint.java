/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.path;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public interface IPathWaypoint {
    public int getOrder();

    public Vector3d getWaypointPosition(@Nonnull ComponentAccessor<EntityStore> var1);

    public Vector3f getWaypointRotation(@Nonnull ComponentAccessor<EntityStore> var1);

    public double getPauseTime();

    public float getObservationAngle();
}

