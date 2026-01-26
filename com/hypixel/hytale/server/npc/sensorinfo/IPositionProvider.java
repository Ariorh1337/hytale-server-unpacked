/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nullable;

public interface IPositionProvider {
    public boolean hasPosition();

    public boolean providePosition(Vector3d var1);

    public double getX();

    public double getY();

    public double getZ();

    @Nullable
    public Ref<EntityStore> getTarget();

    public void clear();
}

