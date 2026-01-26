/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.components.messaging;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockMembership;
import com.hypixel.hytale.server.npc.blackboard.view.event.EntityEventNotification;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventType;
import com.hypixel.hytale.server.npc.components.messaging.EventMessage;
import com.hypixel.hytale.server.npc.components.messaging.EventSupport;
import javax.annotation.Nonnull;

public abstract class EntityEventSupport
extends EventSupport<EntityEventType, EntityEventNotification> {
    @Override
    public void postMessage(EntityEventType type, @Nonnull EntityEventNotification notification, @Nonnull Ref<EntityStore> parent, @Nonnull Store<EntityStore> store) {
        boolean isSameFlock;
        double z;
        double y;
        Vector3d pos;
        double x;
        EventMessage slot = this.getMessageSlot(type, notification);
        if (slot == null || !slot.isEnabled()) {
            return;
        }
        Vector3d parentEntityPosition = store.getComponent(parent, TransformComponent.getComponentType()).getPosition();
        double distanceSquared = parentEntityPosition.distanceSquaredTo(x = (pos = notification.getPosition()).getX(), y = pos.getY(), z = pos.getZ());
        if (distanceSquared > slot.getMaxRangeSquared()) {
            return;
        }
        FlockMembership flockMembership = store.getComponent(parent, FlockMembership.getComponentType());
        Ref<EntityStore> flockReference = flockMembership != null ? flockMembership.getFlockRef() : null;
        boolean bl = isSameFlock = flockReference != null && flockReference.equals(notification.getFlockReference());
        if (!slot.isActivated() || distanceSquared < slot.getPosition().distanceSquaredTo(parentEntityPosition) || !slot.isSameFlock() && isSameFlock) {
            slot.activate(x, y, z, notification.getInitiator(), 2.0);
            slot.setSameFlock(isSameFlock);
        }
    }

    public boolean hasFlockMatchingMessage(int messageIndex, @Nonnull Vector3d parentPosition, double range, boolean flockOnly) {
        if (!this.isMessageQueued(messageIndex)) {
            return false;
        }
        EventMessage event = this.messageSlots[messageIndex];
        if (flockOnly && !event.isSameFlock()) {
            return false;
        }
        return event.getPosition().distanceSquaredTo(parentPosition) < range * range;
    }
}

