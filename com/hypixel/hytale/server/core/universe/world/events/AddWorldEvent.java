/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.events;

import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.WorldEvent;
import javax.annotation.Nonnull;

public class AddWorldEvent
extends WorldEvent
implements ICancellable {
    private boolean cancelled = false;

    public AddWorldEvent(@Nonnull World world) {
        super(world);
    }

    @Override
    @Nonnull
    public String toString() {
        return "AddWorldEvent{cancelled=" + this.cancelled + "} " + super.toString();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

