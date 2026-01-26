/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.player;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.OrderPriority;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.component.system.tick.RunWhenPausedSystem;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerPingSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import javax.annotation.Nonnull;

public class PlayerConnectionFlushSystem
extends EntityTickingSystem<EntityStore>
implements RunWhenPausedSystem<EntityStore> {
    public static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemGroupDependency<EntityStore>(Order.AFTER, EntityStore.SEND_PACKET_GROUP), new SystemDependency(Order.AFTER, PlayerPingSystem.class, OrderPriority.CLOSEST), RootDependency.last());
    private final ComponentType<EntityStore, PlayerRef> componentType;

    public PlayerConnectionFlushSystem(ComponentType<EntityStore, PlayerRef> componentType) {
        this.componentType = componentType;
    }

    @Override
    @Nonnull
    public Set<Dependency<EntityStore>> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return this.componentType;
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
    }

    @Override
    public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
        if (Constants.FORCE_NETWORK_FLUSH) {
            store.tick(this, dt, systemIndex);
        }
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        archetypeChunk.getComponent(index, this.componentType).getPacketHandler().tryFlush();
    }
}

