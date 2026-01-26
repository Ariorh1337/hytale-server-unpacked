/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.physics.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemTypeDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import javax.annotation.Nonnull;

public class GenericVelocityInstructionSystem
extends EntityTickingSystem<EntityStore> {
    @Nonnull
    private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemTypeDependency<EntityStore, ISystem<EntityStore>>(Order.AFTER, EntityModule.get().getVelocityModifyingSystemType()));
    @Nonnull
    private final Query<EntityStore> query = Query.and(Velocity.getComponentType());

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
        assert (velocityComponent != null);
        for (Velocity.Instruction instruction : velocityComponent.getInstructions()) {
            switch (instruction.getType()) {
                case Set: {
                    velocityComponent.set(instruction.getVelocity());
                    break;
                }
                case Add: {
                    velocityComponent.addForce(instruction.getVelocity());
                }
            }
        }
        velocityComponent.getInstructions().clear();
    }

    @Override
    @Nonnull
    public Set<Dependency<EntityStore>> getDependencies() {
        return this.dependencies;
    }

    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}

