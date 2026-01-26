/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.event.events.entity;

import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.event.events.entity.EntityEvent;
import javax.annotation.Nonnull;

public class EntityRemoveEvent
extends EntityEvent<Entity, String> {
    public EntityRemoveEvent(Entity entity) {
        super(entity);
    }

    @Override
    @Nonnull
    public String toString() {
        return "EntityRemoveEvent{} " + super.toString();
    }
}

