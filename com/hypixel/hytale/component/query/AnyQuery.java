/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.query;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.query.Query;

class AnyQuery<ECS_TYPE>
implements Query<ECS_TYPE> {
    static final AnyQuery<?> INSTANCE = new AnyQuery();

    AnyQuery() {
    }

    @Override
    public boolean test(Archetype<ECS_TYPE> archetype) {
        return true;
    }

    @Override
    public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> componentType) {
        return false;
    }

    @Override
    public void validateRegistry(ComponentRegistry<ECS_TYPE> registry) {
    }

    @Override
    public void validate() {
    }
}

