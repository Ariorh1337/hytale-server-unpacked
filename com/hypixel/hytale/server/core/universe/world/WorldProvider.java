/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world;

import com.hypixel.hytale.server.core.universe.world.World;
import javax.annotation.Nonnull;

public interface WorldProvider {
    @Nonnull
    public World getWorld();
}

