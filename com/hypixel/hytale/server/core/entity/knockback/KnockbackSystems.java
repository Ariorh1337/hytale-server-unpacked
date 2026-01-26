/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.knockback;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.player.KnockbackSimulation;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.physics.systems.IVelocityModifyingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KnockbackSystems {

    public static class ApplyPlayerKnockback
    extends EntityTickingSystem<EntityStore>
    implements IVelocityModifyingSystem {
        public static boolean DO_SERVER_PREDICTION = false;
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(KnockbackComponent.getComponentType(), Player.getComponentType(), Velocity.getComponentType());

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            KnockbackComponent knockbackComponent = archetypeChunk.getComponent(index, KnockbackComponent.getComponentType());
            assert (knockbackComponent != null);
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            KnockbackSimulation knockbackSimulationComponent = archetypeChunk.getComponent(index, KnockbackSimulation.getComponentType());
            if (knockbackSimulationComponent == null && DO_SERVER_PREDICTION) {
                knockbackSimulationComponent = new KnockbackSimulation();
                commandBuffer.addComponent(ref, KnockbackSimulation.getComponentType(), knockbackSimulationComponent);
            }
            knockbackComponent.applyModifiers();
            switch (knockbackComponent.getVelocityType()) {
                case Add: {
                    if (DO_SERVER_PREDICTION) {
                        if (knockbackSimulationComponent == null) break;
                        knockbackSimulationComponent.addRequestedVelocity(knockbackComponent.getVelocity());
                        break;
                    }
                    Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
                    assert (velocityComponent != null);
                    velocityComponent.addInstruction(knockbackComponent.getVelocity(), knockbackComponent.getVelocityConfig(), ChangeVelocityType.Add);
                    if (knockbackComponent.getDuration() > 0.0f) {
                        knockbackComponent.incrementTimer(dt);
                    }
                    if (knockbackComponent.getDuration() != 0.0f && !(knockbackComponent.getTimer() > knockbackComponent.getDuration())) break;
                    commandBuffer.tryRemoveComponent(ref, KnockbackComponent.getComponentType());
                    break;
                }
                case Set: {
                    if (DO_SERVER_PREDICTION) {
                        if (knockbackSimulationComponent == null) break;
                        knockbackSimulationComponent.setRequestedVelocity(knockbackComponent.getVelocity());
                        break;
                    }
                    Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
                    assert (velocityComponent != null);
                    velocityComponent.addInstruction(knockbackComponent.getVelocity(), null, ChangeVelocityType.Set);
                    if (knockbackComponent.getDuration() > 0.0f) {
                        knockbackComponent.incrementTimer(dt);
                    }
                    if (knockbackComponent.getDuration() != 0.0f && !(knockbackComponent.getTimer() > knockbackComponent.getDuration())) break;
                    commandBuffer.tryRemoveComponent(ref, KnockbackComponent.getComponentType());
                }
            }
            if (DO_SERVER_PREDICTION && knockbackSimulationComponent != null) {
                knockbackSimulationComponent.reset();
            }
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }
    }

    public static class ApplyKnockback
    extends EntityTickingSystem<EntityStore>
    implements IVelocityModifyingSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, KnockbackComponent.getComponentType(), Velocity.getComponentType(), Query.not(Player.getComponentType()));

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            KnockbackComponent knockbackComponent = archetypeChunk.getComponent(index, KnockbackComponent.getComponentType());
            assert (knockbackComponent != null);
            knockbackComponent.applyModifiers();
            Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
            assert (velocityComponent != null);
            velocityComponent.addInstruction(knockbackComponent.getVelocity(), knockbackComponent.getVelocityConfig(), knockbackComponent.getVelocityType());
            if (knockbackComponent.getDuration() > 0.0f) {
                knockbackComponent.incrementTimer(dt);
            }
            if (knockbackComponent.getDuration() == 0.0f || knockbackComponent.getTimer() > knockbackComponent.getDuration()) {
                Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                commandBuffer.tryRemoveComponent(ref, KnockbackComponent.getComponentType());
            }
        }
    }
}

