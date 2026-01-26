/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityUtils {
    @Nonnull
    public static Holder<EntityStore> toHolder(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk) {
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        Archetype<EntityStore> archetype = archetypeChunk.getArchetype();
        for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
            Object component;
            ComponentType<EntityStore, ?> componentType = archetype.get(i);
            if (componentType == null || (component = archetypeChunk.getComponent(index, componentType)) == null) continue;
            holder.addComponent(componentType, component);
        }
        return holder;
    }

    @Nullable
    private static <T extends Entity> ComponentType<EntityStore, T> findComponentType(@Nonnull Archetype<EntityStore> archetype) {
        return EntityUtils.findComponentType(archetype, Entity.class);
    }

    @Nullable
    private static <C extends Component<EntityStore>, T extends C> ComponentType<EntityStore, T> findComponentType(@Nonnull Archetype<EntityStore> archetype, @Nonnull Class<C> entityClass) {
        for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
            ComponentType<EntityStore, ?> componentType = archetype.get(i);
            if (componentType == null || !entityClass.isAssignableFrom(componentType.getTypeClass())) continue;
            return componentType;
        }
        return null;
    }

    @Deprecated
    @Nullable
    public static Entity getEntity(@Nullable Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (ref == null || !ref.isValid()) {
            return null;
        }
        ComponentType componentType = EntityUtils.findComponentType(componentAccessor.getArchetype(ref));
        if (componentType == null) {
            return null;
        }
        return (Entity)componentAccessor.getComponent(ref, componentType);
    }

    @Nullable
    @Deprecated
    public static Entity getEntity(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk) {
        ComponentType componentType = EntityUtils.findComponentType(archetypeChunk.getArchetype());
        if (componentType == null) {
            return null;
        }
        return (Entity)archetypeChunk.getComponent(index, componentType);
    }

    @Nullable
    @Deprecated
    public static Entity getEntity(@Nonnull Holder<EntityStore> holder) {
        Archetype<EntityStore> archetype = holder.getArchetype();
        if (archetype == null) {
            return null;
        }
        ComponentType componentType = EntityUtils.findComponentType(archetype);
        if (componentType == null) {
            return null;
        }
        return (Entity)holder.getComponent(componentType);
    }

    @Deprecated
    public static boolean hasEntity(@Nonnull Archetype<EntityStore> archetype) {
        return EntityUtils.findComponentType(archetype) != null;
    }

    @Deprecated
    public static boolean hasLivingEntity(@Nonnull Archetype<EntityStore> archetype) {
        return EntityUtils.findComponentType(archetype, LivingEntity.class) != null;
    }

    @Nonnull
    @Deprecated
    public static PhysicsValues getPhysicsValues(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Model model;
        PhysicsValues physicsValuesComponent = componentAccessor.getComponent(ref, PhysicsValues.getComponentType());
        if (physicsValuesComponent != null) {
            return physicsValuesComponent;
        }
        ModelComponent modelComponent = componentAccessor.getComponent(ref, ModelComponent.getComponentType());
        Model model2 = model = modelComponent != null ? modelComponent.getModel() : null;
        if (model != null && model.getPhysicsValues() != null) {
            return model.getPhysicsValues();
        }
        return PhysicsValues.getDefault();
    }
}

