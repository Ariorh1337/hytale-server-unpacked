/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.system;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.ISystem;
import javax.annotation.Nullable;

public interface QuerySystem<ECS_TYPE>
extends ISystem<ECS_TYPE> {
    default public boolean test(ComponentRegistry<ECS_TYPE> componentRegistry, Archetype<ECS_TYPE> archetype) {
        return this.getQuery().test(archetype);
    }

    @Nullable
    public Query<ECS_TYPE> getQuery();
}

