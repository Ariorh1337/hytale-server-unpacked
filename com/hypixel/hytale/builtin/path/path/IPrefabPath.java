/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.path.path;

import com.hypixel.hytale.builtin.path.waypoint.IPrefabPathWaypoint;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.path.IPath;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public interface IPrefabPath
extends IPath<IPrefabPathWaypoint> {
    public short registerNewWaypoint(@Nonnull IPrefabPathWaypoint var1, int var2);

    public void registerNewWaypointAt(int var1, @Nonnull IPrefabPathWaypoint var2, int var3);

    public void addLoadedWaypoint(@Nonnull IPrefabPathWaypoint var1, int var2, int var3, int var4);

    public void removeWaypoint(int var1, int var2);

    public void unloadWaypoint(int var1);

    public boolean hasLoadedWaypoints();

    public boolean isFullyLoaded();

    public int loadedWaypointCount();

    public int getWorldGenId();

    public Vector3d getNearestWaypointPosition(@Nonnull Vector3d var1, @Nonnull ComponentAccessor<EntityStore> var2);

    public void mergeInto(@Nonnull IPrefabPath var1, int var2, @Nonnull ComponentAccessor<EntityStore> var3);

    public void compact(int var1);
}

