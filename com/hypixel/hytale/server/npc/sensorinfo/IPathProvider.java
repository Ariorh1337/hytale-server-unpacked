/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.server.core.universe.world.path.IPath;
import com.hypixel.hytale.server.core.universe.world.path.IPathWaypoint;
import com.hypixel.hytale.server.npc.sensorinfo.ExtraInfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IPathProvider
extends ExtraInfoProvider {
    public boolean hasPath();

    @Nullable
    public IPath<? extends IPathWaypoint> getPath();

    public void clear();

    @Nonnull
    default public Class<IPathProvider> getType() {
        return IPathProvider.class;
    }
}

