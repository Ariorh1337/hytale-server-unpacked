/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.navigation;

import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nullable;

public interface IWaypoint {
    public int getLength();

    public Vector3d getPosition();

    @Nullable
    public IWaypoint advance(int var1);

    @Nullable
    public IWaypoint next();
}

