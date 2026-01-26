/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.events;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.WorldEvent;
import javax.annotation.Nonnull;

public class StartWorldEvent
extends WorldEvent {
    public StartWorldEvent(@Nonnull World world) {
        super(world);
    }

    @Override
    @Nonnull
    public String toString() {
        return "StartWorldEvent{} " + super.toString();
    }
}

