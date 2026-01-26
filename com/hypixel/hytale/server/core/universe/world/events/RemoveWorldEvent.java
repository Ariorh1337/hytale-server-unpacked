/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.events;

import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.WorldEvent;
import javax.annotation.Nonnull;

public class RemoveWorldEvent
extends WorldEvent
implements ICancellable {
    private boolean cancelled;
    @Nonnull
    private final RemovalReason removalReason;

    public RemoveWorldEvent(@Nonnull World world, @Nonnull RemovalReason removalReason) {
        super(world);
        this.removalReason = removalReason;
    }

    @Nonnull
    public RemovalReason getRemovalReason() {
        return this.removalReason;
    }

    @Override
    public boolean isCancelled() {
        if (this.removalReason == RemovalReason.EXCEPTIONAL) {
            return false;
        }
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    @Nonnull
    public String toString() {
        return "RemoveWorldEvent{cancelled=" + this.cancelled + "} " + super.toString();
    }

    public static enum RemovalReason {
        GENERAL,
        EXCEPTIONAL;

    }
}

