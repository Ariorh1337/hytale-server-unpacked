/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view.event;

import com.hypixel.hytale.server.npc.blackboard.view.event.EventNotification;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

@FunctionalInterface
public interface IEventCallback<EventType, NotificationType extends EventNotification> {
    public void notify(NPCEntity var1, EventType var2, NotificationType var3);
}

