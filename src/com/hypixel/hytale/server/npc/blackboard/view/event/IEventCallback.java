package com.hypixel.hytale.server.npc.blackboard.view.event;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.blackboard.BlackboardSubscription;

@FunctionalInterface
public interface IEventCallback<EventType, NotificationType extends EventNotification> {
   void notify(BlackboardSubscription var1, Ref<EntityStore> var2, ComponentAccessor<EntityStore> var3, EventType var4, NotificationType var5);
}
