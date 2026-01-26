/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.path;

import com.hypixel.hytale.server.core.universe.world.path.IPathWaypoint;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public interface IPath<Waypoint extends IPathWaypoint> {
    @Nullable
    public UUID getId();

    @Nullable
    public String getName();

    public List<Waypoint> getPathWaypoints();

    public int length();

    public Waypoint get(int var1);
}

