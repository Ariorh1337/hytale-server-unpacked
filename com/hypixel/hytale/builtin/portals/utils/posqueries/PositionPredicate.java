/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.utils.posqueries;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;
import javax.annotation.Nonnull;

public interface PositionPredicate {
    public boolean test(@Nonnull World var1, @Nonnull Vector3d var2);
}

