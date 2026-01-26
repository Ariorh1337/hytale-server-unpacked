/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.systems;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.NonTicking;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.systems.StepCleanupSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Set;
import javax.annotation.Nonnull;

public class NewSpawnStartTickingSystem
extends TickingSystem<EntityStore> {
    @Nonnull
    private final ResourceType<EntityStore, QueueResource> queueResourceType;
    @Nonnull
    private final ComponentType<EntityStore, NonTicking<EntityStore>> nonTickingComponentType;
    @Nonnull
    private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency(Order.AFTER, StepCleanupSystem.class));

    public NewSpawnStartTickingSystem(@Nonnull ResourceType<EntityStore, QueueResource> queueResourceType) {
        this.queueResourceType = queueResourceType;
        this.nonTickingComponentType = EntityStore.REGISTRY.getNonTickingComponentType();
    }

    @Override
    @Nonnull
    public Set<Dependency<EntityStore>> getDependencies() {
        return this.dependencies;
    }

    @Override
    public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
        ObjectList<Ref<EntityStore>> queue = store.getResource(this.queueResourceType).queue;
        if (queue.isEmpty()) {
            return;
        }
        for (Ref ref : queue) {
            if (!ref.isValid()) continue;
            store.removeComponent(ref, this.nonTickingComponentType);
        }
        queue.clear();
    }

    public static void queueNewSpawn(@Nonnull Ref<EntityStore> reference, @Nonnull Store<EntityStore> store) {
        store.ensureComponent(reference, EntityStore.REGISTRY.getNonTickingComponentType());
        store.getResource(QueueResource.getResourceType()).queue.add(reference);
    }

    public static class QueueResource
    implements Resource<EntityStore> {
        @Nonnull
        private final ObjectList<Ref<EntityStore>> queue = new ObjectArrayList<Ref<EntityStore>>();

        @Nonnull
        public static ResourceType<EntityStore, QueueResource> getResourceType() {
            return NPCPlugin.get().getNewSpawnStartTickingQueueResourceType();
        }

        @Override
        @Nonnull
        public Resource<EntityStore> clone() {
            QueueResource queue = new QueueResource();
            queue.queue.addAll(this.queue);
            return queue;
        }
    }
}

