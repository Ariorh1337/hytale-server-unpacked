/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.npccombatactionevaluator.memory;

import com.hypixel.hytale.builtin.npccombatactionevaluator.memory.DamageMemory;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DamageMemorySystems {

    public static class CollectDamage
    extends DamageEventSystem {
        private final ComponentType<EntityStore, DamageMemory> damageMemoryComponentType;
        @Nonnull
        private final Query<EntityStore> query;

        public CollectDamage(ComponentType<EntityStore, DamageMemory> damageMemoryComponentType) {
            this.damageMemoryComponentType = damageMemoryComponentType;
            this.query = damageMemoryComponentType;
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            DamageMemory memory = archetypeChunk.getComponent(index, this.damageMemoryComponentType);
            memory.addDamage(damage.getAmount());
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }
    }
}

