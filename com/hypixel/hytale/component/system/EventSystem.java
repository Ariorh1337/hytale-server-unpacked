/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.system;

import com.hypixel.hytale.component.system.EcsEvent;
import com.hypixel.hytale.component.system.ICancellableEcsEvent;
import javax.annotation.Nonnull;

public abstract class EventSystem<EventType extends EcsEvent> {
    @Nonnull
    private final Class<EventType> eventType;

    protected EventSystem(@Nonnull Class<EventType> eventType) {
        this.eventType = eventType;
    }

    protected boolean shouldProcessEvent(@Nonnull EventType event) {
        ICancellableEcsEvent cancellable;
        return !(event instanceof ICancellableEcsEvent) || !(cancellable = (ICancellableEcsEvent)event).isCancelled();
    }

    @Nonnull
    public Class<EventType> getEventType() {
        return this.eventType;
    }
}

