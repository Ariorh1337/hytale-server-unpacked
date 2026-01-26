/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.systems.ComputeVelocitySystem;
import com.hypixel.hytale.server.npc.systems.SteppableTickingSystem;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public class MovementStatesSystem
extends SteppableTickingSystem {
    @Nonnull
    private final ComponentType<EntityStore, NPCEntity> npcComponentType;
    @Nonnull
    private final ComponentType<EntityStore, Velocity> velocityComponentType;
    @Nonnull
    private final ComponentType<EntityStore, MovementStatesComponent> movementStatesComponentType;
    @Nonnull
    private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency(Order.AFTER, ComputeVelocitySystem.class));
    @Nonnull
    private final Query<EntityStore> query;

    public MovementStatesSystem(@Nonnull ComponentType<EntityStore, NPCEntity> npcComponentType, @Nonnull ComponentType<EntityStore, Velocity> velocityComponentType, @Nonnull ComponentType<EntityStore, MovementStatesComponent> movementStatesComponentType) {
        this.npcComponentType = npcComponentType;
        this.velocityComponentType = velocityComponentType;
        this.movementStatesComponentType = movementStatesComponentType;
        this.query = Query.and(npcComponentType, velocityComponentType, movementStatesComponentType);
    }

    @Override
    @Nonnull
    public Set<Dependency<EntityStore>> getDependencies() {
        return this.dependencies;
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return false;
    }

    @Override
    public void steppedTick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        NPCEntity npcComponent = archetypeChunk.getComponent(index, this.npcComponentType);
        assert (npcComponent != null);
        Velocity velocityComponent = archetypeChunk.getComponent(index, this.velocityComponentType);
        assert (velocityComponent != null);
        MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(index, this.movementStatesComponentType);
        assert (movementStatesComponent != null);
        try {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            if (Objects.equals(npcComponent.getRoleName(), "Empty_Role")) {
                return;
            }
            npcComponent.getRole().updateMovementState(ref, movementStatesComponent.getMovementStates(), velocityComponent.getVelocity(), commandBuffer);
        }
        catch (Exception e) {
            ((HytaleLogger.Api)((HytaleLogger.Api)NPCPlugin.get().getLogger().atSevere()).withCause(e)).log("Failed to update movement states for " + npcComponent.getRoleName() + ", Archetype: " + String.valueOf(archetypeChunk.getArchetype()) + ", Spawn config index: " + npcComponent.getSpawnConfiguration() + ": ");
        }
    }

    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}

