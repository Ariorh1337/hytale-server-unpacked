/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.data.unknown.UnknownComponents;
import com.hypixel.hytale.component.query.ExactArchetypeQuery;
import com.hypixel.hytale.component.query.Query;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Archetype<ECS_TYPE>
implements Query<ECS_TYPE> {
    @Nonnull
    private static final Archetype EMPTY = new Archetype(0, 0, ComponentType.EMPTY_ARRAY);
    private final int minIndex;
    private final int count;
    @Nonnull
    private final ComponentType<ECS_TYPE, ?>[] componentTypes;
    @Nonnull
    private final ExactArchetypeQuery<ECS_TYPE> exactQuery = new ExactArchetypeQuery(this);

    public static <ECS_TYPE> Archetype<ECS_TYPE> empty() {
        return EMPTY;
    }

    private Archetype(int minIndex, int count, @Nonnull ComponentType<ECS_TYPE, ?>[] componentTypes) {
        this.minIndex = minIndex;
        this.count = count;
        this.componentTypes = componentTypes;
    }

    public int getMinIndex() {
        return this.minIndex;
    }

    public int count() {
        return this.count;
    }

    public int length() {
        return this.componentTypes.length;
    }

    @Nullable
    public ComponentType<ECS_TYPE, ?> get(int index) {
        return this.componentTypes[index];
    }

    public boolean isEmpty() {
        return this.componentTypes.length == 0;
    }

    public boolean contains(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
        int index = componentType.getIndex();
        return index < this.componentTypes.length && this.componentTypes[index] == componentType;
    }

    public boolean contains(@Nonnull Archetype<ECS_TYPE> archetype) {
        if (this == archetype || archetype.isEmpty()) {
            return true;
        }
        for (int i = archetype.minIndex; i < archetype.componentTypes.length; ++i) {
            ComponentType<ECS_TYPE, ?> componentType = archetype.componentTypes[i];
            if (componentType == null || this.contains(componentType)) continue;
            return false;
        }
        return true;
    }

    public void validateComponentType(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
        if (!this.contains(componentType)) {
            throw new IllegalArgumentException("ComponentType is not in archetype: " + String.valueOf(componentType) + ", " + String.valueOf(this));
        }
    }

    public void validateComponents(@Nonnull Component<ECS_TYPE>[] components, @Nullable ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> ignore) {
        int len = Math.max(this.componentTypes.length, components.length);
        for (int index = 0; index < len; ++index) {
            Component<ECS_TYPE> component;
            ComponentType<ECS_TYPE, ?> componentType = index >= this.componentTypes.length ? null : this.componentTypes[index];
            Component<ECS_TYPE> component2 = component = index >= components.length ? null : components[index];
            if (componentType == null) {
                if (component == null || ignore != null && index == ignore.getIndex()) continue;
                throw new IllegalStateException("Invalid component at index " + index + " expected null but found " + String.valueOf(component.getClass()));
            }
            Class<?> typeClass = componentType.getTypeClass();
            if (component == null) {
                throw new IllegalStateException("Invalid component at index " + index + " expected " + String.valueOf(typeClass) + " but found null");
            }
            Class<?> aClass = component.getClass();
            if (aClass.equals(typeClass)) continue;
            throw new IllegalStateException("Invalid component at index " + index + " expected " + String.valueOf(typeClass) + " but found " + String.valueOf(aClass));
        }
    }

    public boolean hasSerializableComponents(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
        if (this.isEmpty()) {
            return false;
        }
        if (this.contains(data.getRegistry().getNonSerializedComponentType())) {
            return false;
        }
        for (int index = this.minIndex; index < this.componentTypes.length; ++index) {
            ComponentType<ECS_TYPE, ?> componentType = this.componentTypes[index];
            if (componentType == null || data.getComponentCodec(componentType) == null) continue;
            return true;
        }
        return false;
    }

    @Nonnull
    public Archetype<ECS_TYPE> getSerializableArchetype(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
        int serializableMinIndex;
        if (this.isEmpty()) {
            return EMPTY;
        }
        if (this.contains(data.getRegistry().getNonSerializedComponentType())) {
            return EMPTY;
        }
        int lastSerializableIndex = this.componentTypes.length - 1;
        for (int index = this.componentTypes.length - 1; index >= this.minIndex; --index) {
            ComponentType<ECS_TYPE, ?> componentType = this.componentTypes[index];
            if (componentType == null || data.getComponentCodec(componentType) == null) continue;
            lastSerializableIndex = index;
            break;
        }
        if (lastSerializableIndex < this.minIndex) {
            return EMPTY;
        }
        ComponentType[] serializableComponentTypes = new ComponentType[lastSerializableIndex + 1];
        for (int index = serializableMinIndex = this.minIndex; index < serializableComponentTypes.length; ++index) {
            ComponentType<ECS_TYPE, ?> componentType = this.componentTypes[index];
            if (componentType == null || data.getComponentCodec(componentType) == null) continue;
            serializableMinIndex = Math.min(serializableMinIndex, index);
            serializableComponentTypes[index] = componentType;
        }
        return new Archetype<ECS_TYPE>(this.minIndex, serializableComponentTypes.length, serializableComponentTypes);
    }

    @Nonnull
    public ExactArchetypeQuery<ECS_TYPE> asExactQuery() {
        return this.exactQuery;
    }

    @Nonnull
    public static <ECS_TYPE> Archetype<ECS_TYPE> of(@Nonnull ComponentType<ECS_TYPE, ?> componentTypes) {
        int index = componentTypes.getIndex();
        ComponentType[] arr = new ComponentType[index + 1];
        arr[index] = componentTypes;
        return new Archetype<ECS_TYPE>(index, 1, arr);
    }

    @SafeVarargs
    public static <ECS_TYPE> Archetype<ECS_TYPE> of(ComponentType<ECS_TYPE, ?> ... componentTypes) {
        if (componentTypes.length == 0) {
            return EMPTY;
        }
        ComponentRegistry<ECS_TYPE> registry = componentTypes[0].getRegistry();
        int minIndex = Integer.MAX_VALUE;
        int maxIndex = Integer.MIN_VALUE;
        for (int i = 0; i < componentTypes.length; ++i) {
            componentTypes[i].validateRegistry(registry);
            int index = componentTypes[i].getIndex();
            if (index < minIndex) {
                minIndex = index;
            }
            if (index > maxIndex) {
                maxIndex = index;
            }
            for (int n = i + 1; n < componentTypes.length; ++n) {
                if (componentTypes[i] != componentTypes[n]) continue;
                throw new IllegalArgumentException("ComponentType provided multiple times! " + Arrays.toString(componentTypes));
            }
        }
        ComponentType[] arr = new ComponentType[maxIndex + 1];
        ComponentType<ECS_TYPE, ?>[] componentTypeArray = componentTypes;
        int n = componentTypeArray.length;
        for (int i = 0; i < n; ++i) {
            ComponentType<ECS_TYPE, ?> componentType;
            arr[componentType.getIndex()] = componentType = componentTypeArray[i];
        }
        return new Archetype<ECS_TYPE>(minIndex, componentTypes.length, arr);
    }

    @Nonnull
    public static <ECS_TYPE, T extends Component<ECS_TYPE>> Archetype<ECS_TYPE> add(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull ComponentType<ECS_TYPE, T> componentType) {
        if (archetype.isEmpty()) {
            return Archetype.of(componentType);
        }
        if (archetype.contains(componentType)) {
            throw new IllegalArgumentException("ComponentType is already in Archetype! " + String.valueOf(archetype) + ", " + String.valueOf(componentType));
        }
        archetype.validateRegistry(componentType.getRegistry());
        int index = componentType.getIndex();
        int minIndex = Math.min(index, archetype.minIndex);
        int newLength = Math.max(index + 1, archetype.componentTypes.length);
        ComponentType<ECS_TYPE, ?>[] arr = Arrays.copyOf(archetype.componentTypes, newLength);
        arr[index] = componentType;
        return new Archetype<ECS_TYPE>(minIndex, archetype.count + 1, arr);
    }

    public static <ECS_TYPE, T extends Component<ECS_TYPE>> Archetype<ECS_TYPE> remove(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull ComponentType<ECS_TYPE, T> componentType) {
        if (archetype.isEmpty()) {
            throw new IllegalArgumentException("Archetype is already empty!");
        }
        if (!archetype.contains(componentType)) {
            throw new IllegalArgumentException("Archetype doesn't contain ComponentType! " + String.valueOf(archetype) + ", " + String.valueOf(componentType));
        }
        int oldMinIndex = archetype.minIndex;
        int oldLength = archetype.componentTypes.length;
        int oldMaxIndex = oldLength - 1;
        if (oldMinIndex == oldMaxIndex) {
            return EMPTY;
        }
        int newCount = archetype.count - 1;
        int index = componentType.getIndex();
        if (index == oldMaxIndex) {
            int maxIndex;
            for (maxIndex = index - 1; maxIndex > oldMinIndex && archetype.componentTypes[maxIndex] == null; --maxIndex) {
            }
            return new Archetype<ECS_TYPE>(oldMinIndex, newCount, Arrays.copyOf(archetype.componentTypes, maxIndex + 1));
        }
        ComponentType<ECS_TYPE, ?>[] arr = Arrays.copyOf(archetype.componentTypes, oldLength);
        arr[index] = null;
        if (index == oldMinIndex) {
            int minIndex;
            for (minIndex = index + 1; minIndex < oldLength && arr[minIndex] == null; ++minIndex) {
            }
            return new Archetype<ECS_TYPE>(minIndex, newCount, arr);
        }
        return new Archetype<ECS_TYPE>(oldMinIndex, newCount, arr);
    }

    @Override
    public boolean test(@Nonnull Archetype<ECS_TYPE> archetype) {
        return archetype.contains(this);
    }

    @Override
    public boolean requiresComponentType(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
        return this.contains(componentType);
    }

    @Override
    public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
        if (this.isEmpty()) {
            return;
        }
        this.componentTypes[this.minIndex].validateRegistry(registry);
    }

    @Override
    public void validate() {
        for (int i = this.minIndex; i < this.componentTypes.length; ++i) {
            ComponentType<ECS_TYPE, ?> componentType = this.componentTypes[i];
            if (componentType == null) continue;
            componentType.validate();
        }
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Archetype archetype = (Archetype)o;
        return Arrays.equals(this.componentTypes, archetype.componentTypes);
    }

    public int hashCode() {
        return Arrays.hashCode(this.componentTypes);
    }

    @Nonnull
    public String toString() {
        return "Archetype{componentTypes=" + Arrays.toString(this.componentTypes) + "}";
    }
}

