/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.system.tick;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.QuerySystem;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import javax.annotation.Nonnull;

public abstract class ArchetypeTickingSystem<ECS_TYPE>
extends TickingSystem<ECS_TYPE>
implements QuerySystem<ECS_TYPE> {
    @Override
    public boolean test(@Nonnull ComponentRegistry<ECS_TYPE> componentRegistry, @Nonnull Archetype<ECS_TYPE> archetype) {
        if (!this.isExplicitQuery() && componentRegistry.getNonTickingComponentType().test(archetype)) {
            return false;
        }
        return this.getQuery().test(archetype);
    }

    public boolean isExplicitQuery() {
        return false;
    }

    @Override
    public void tick(float dt, int systemIndex, @Nonnull Store<ECS_TYPE> store) {
        store.tick(this, dt, systemIndex);
    }

    public abstract void tick(float var1, @Nonnull ArchetypeChunk<ECS_TYPE> var2, @Nonnull Store<ECS_TYPE> var3, @Nonnull CommandBuffer<ECS_TYPE> var4);
}

