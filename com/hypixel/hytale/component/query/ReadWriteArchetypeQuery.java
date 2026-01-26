/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.query;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.query.Query;
import javax.annotation.Nonnull;

public interface ReadWriteArchetypeQuery<ECS_TYPE>
extends Query<ECS_TYPE> {
    public Archetype<ECS_TYPE> getReadArchetype();

    public Archetype<ECS_TYPE> getWriteArchetype();

    @Override
    default public boolean test(@Nonnull Archetype<ECS_TYPE> archetype) {
        return archetype.contains(this.getReadArchetype()) && archetype.contains(this.getWriteArchetype());
    }

    @Override
    default public boolean requiresComponentType(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
        return this.getReadArchetype().contains(componentType) || this.getWriteArchetype().contains(componentType);
    }

    @Override
    default public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
        this.getReadArchetype().validateRegistry(registry);
        this.getWriteArchetype().validateRegistry(registry);
    }

    @Override
    default public void validate() {
        this.getReadArchetype().validate();
        this.getWriteArchetype().validate();
    }
}

