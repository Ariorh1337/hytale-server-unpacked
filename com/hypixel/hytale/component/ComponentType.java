/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.query.Query;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ComponentType<ECS_TYPE, T extends Component<ECS_TYPE>>
implements Comparable<ComponentType<ECS_TYPE, ?>>,
Query<ECS_TYPE> {
    @Nonnull
    public static final ComponentType[] EMPTY_ARRAY = new ComponentType[0];
    private ComponentRegistry<ECS_TYPE> registry;
    private Class<? super T> tClass;
    private int index;
    private boolean invalid = true;

    void init(@Nonnull ComponentRegistry<ECS_TYPE> registry, @Nonnull Class<? super T> tClass, int index) {
        this.registry = registry;
        this.tClass = tClass;
        this.index = index;
        this.invalid = false;
    }

    @Nonnull
    public ComponentRegistry<ECS_TYPE> getRegistry() {
        return this.registry;
    }

    @Nonnull
    public Class<? super T> getTypeClass() {
        return this.tClass;
    }

    public int getIndex() {
        return this.index;
    }

    void invalidate() {
        this.invalid = true;
    }

    boolean isValid() {
        return !this.invalid;
    }

    @Override
    public boolean test(@Nonnull Archetype<ECS_TYPE> archetype) {
        return archetype.contains(this);
    }

    @Override
    public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> componentType) {
        return this.equals(componentType);
    }

    @Override
    public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
        if (!this.registry.equals(registry)) {
            throw new IllegalArgumentException("ComponentType is for a different registry! " + String.valueOf(this));
        }
    }

    @Override
    public void validate() {
        if (this.invalid) {
            throw new IllegalStateException("ComponentType is invalid!");
        }
    }

    @Override
    public int compareTo(@Nonnull ComponentType<ECS_TYPE, ?> o) {
        return Integer.compare(this.index, o.index);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ComponentType that = (ComponentType)o;
        if (this.index != that.index) {
            return false;
        }
        return this.registry.equals(that.registry);
    }

    public int hashCode() {
        int result = this.registry.hashCode();
        result = 31 * result + this.index;
        return result;
    }

    @Nonnull
    public String toString() {
        return "ComponentType{registry=" + String.valueOf(this.registry.getClass()) + "@" + this.registry.hashCode() + ", typeClass=" + String.valueOf(this.tClass) + ", index=" + this.index + "}";
    }
}

