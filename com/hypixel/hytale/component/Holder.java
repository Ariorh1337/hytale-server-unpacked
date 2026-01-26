/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.data.change.ComponentChange;
import com.hypixel.hytale.component.data.change.DataChange;
import com.hypixel.hytale.component.data.unknown.TempUnknownComponent;
import com.hypixel.hytale.component.data.unknown.UnknownComponents;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class Holder<ECS_TYPE> {
    private static final Holder<?>[] EMPTY_ARRAY = new Holder[0];
    @Nullable
    private final ComponentRegistry<ECS_TYPE> registry;
    private final StampedLock lock = new StampedLock();
    @Nullable
    private Archetype<ECS_TYPE> archetype;
    @Nullable
    private Component<ECS_TYPE>[] components;
    private boolean ensureValidComponents = true;

    public static <T> Holder<T>[] emptyArray() {
        return EMPTY_ARRAY;
    }

    Holder() {
        this.registry = null;
    }

    Holder(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
        this.registry = registry;
        this.archetype = Archetype.empty();
        this.components = Component.EMPTY_ARRAY;
    }

    Holder(@Nonnull ComponentRegistry<ECS_TYPE> registry, @Nonnull Archetype<ECS_TYPE> archetype, @Nonnull Component<ECS_TYPE>[] components) {
        this.registry = registry;
        this.init(archetype, components);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Component<ECS_TYPE>[] ensureComponentsSize(int size) {
        long stamp = this.lock.writeLock();
        try {
            if (this.components == null) {
                Component<ECS_TYPE>[] componentArray = this.components = new Component[size];
                return componentArray;
            }
            if (this.components.length < size) {
                this.components = Arrays.copyOf(this.components, size);
            }
            Component<ECS_TYPE>[] componentArray = this.components;
            return componentArray;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull Component<ECS_TYPE>[] components) {
        archetype.validate();
        archetype.validateComponents(components, null);
        long stamp = this.lock.writeLock();
        try {
            this.archetype = archetype;
            this.components = components;
            this.ensureValidComponents = true;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void _internal_init(@Nonnull Archetype<ECS_TYPE> archetype, @Nonnull Component<ECS_TYPE>[] components, @Nonnull ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType) {
        archetype.validateComponents(components, unknownComponentType);
        long stamp = this.lock.writeLock();
        try {
            this.archetype = archetype;
            this.components = components;
            this.ensureValidComponents = false;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    @Nullable
    public Archetype<ECS_TYPE> getArchetype() {
        return this.archetype;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends Component<ECS_TYPE>> void ensureComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
        assert (this.archetype != null);
        assert (this.registry != null);
        if (this.ensureValidComponents) {
            componentType.validate();
        }
        long stamp = this.lock.writeLock();
        try {
            if (this.archetype.contains(componentType)) {
                return;
            }
            T component = this.registry.createComponent(componentType);
            this.addComponent0(componentType, component);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    @Nonnull
    public <T extends Component<ECS_TYPE>> T ensureAndGetComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
        this.ensureComponent(componentType);
        return this.getComponent(componentType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends Component<ECS_TYPE>> void addComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType, @Nonnull T component) {
        assert (this.archetype != null);
        long stamp = this.lock.writeLock();
        try {
            if (this.ensureValidComponents) {
                componentType.validate();
            }
            if (this.archetype.contains(componentType)) {
                throw new IllegalArgumentException("Entity contains component type: " + String.valueOf(componentType));
            }
            this.addComponent0(componentType, component);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    private <T extends Component<ECS_TYPE>> void addComponent0(@Nonnull ComponentType<ECS_TYPE, T> componentType, @Nonnull T component) {
        assert (this.archetype != null);
        assert (this.components != null);
        this.archetype = Archetype.add(this.archetype, componentType);
        int newLength = this.archetype.length();
        if (this.components.length < newLength) {
            this.components = Arrays.copyOf(this.components, newLength);
        }
        this.components[componentType.getIndex()] = component;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends Component<ECS_TYPE>> void replaceComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType, @Nonnull T component) {
        assert (this.archetype != null);
        assert (this.components != null);
        long stamp = this.lock.writeLock();
        try {
            if (this.ensureValidComponents) {
                componentType.validate();
            }
            this.archetype.validateComponentType(componentType);
            this.components[componentType.getIndex()] = component;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public <T extends Component<ECS_TYPE>> void putComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType, @Nonnull T component) {
        if (this.getComponent(componentType) != null) {
            this.replaceComponent(componentType, component);
        } else {
            this.addComponent(componentType, component);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public <T extends Component<ECS_TYPE>> T getComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
        assert (this.archetype != null);
        assert (this.components != null);
        long stamp = this.lock.readLock();
        try {
            if (this.ensureValidComponents) {
                componentType.validate();
            }
            if (!this.archetype.contains(componentType)) {
                T t = null;
                return t;
            }
            Component<ECS_TYPE> component = this.components[componentType.getIndex()];
            return (T)component;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends Component<ECS_TYPE>> void removeComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
        assert (this.archetype != null);
        assert (this.components != null);
        long stamp = this.lock.writeLock();
        try {
            if (this.ensureValidComponents) {
                componentType.validate();
            }
            this.archetype.validateComponentType(componentType);
            this.archetype = Archetype.remove(this.archetype, componentType);
            this.components[componentType.getIndex()] = null;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public <T extends Component<ECS_TYPE>> boolean tryRemoveComponent(@Nonnull ComponentType<ECS_TYPE, T> componentType) {
        if (this.getComponent(componentType) == null) {
            return false;
        }
        this.removeComponent(componentType);
        return true;
    }

    public boolean hasSerializableComponents(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
        assert (this.archetype != null);
        return this.archetype.hasSerializableComponents(data);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateData(@Nonnull ComponentRegistry.Data<ECS_TYPE> oldData, @Nonnull ComponentRegistry.Data<ECS_TYPE> newData) {
        assert (this.archetype != null);
        assert (this.components != null);
        assert (this.registry != null);
        long stamp = this.lock.writeLock();
        try {
            if (this.archetype.isEmpty()) {
                return;
            }
            ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = this.registry.getUnknownComponentType();
            block8: for (int i = 0; i < newData.getDataChangeCount(); ++i) {
                DataChange dataChange = newData.getDataChange(i);
                if (!(dataChange instanceof ComponentChange)) continue;
                ComponentChange componentChange = (ComponentChange)dataChange;
                ComponentType componentType = componentChange.getComponentType();
                switch (componentChange.getType()) {
                    case REGISTERED: {
                        assert (this.archetype != null);
                        if (this.archetype.contains(componentType) || !this.archetype.contains(unknownComponentType)) continue block8;
                        String componentId = newData.getComponentId(componentType);
                        Codec<Component<Object>> componentCodec = newData.getComponentCodec(componentType);
                        if (componentCodec == null) continue block8;
                        UnknownComponents unknownComponents = (UnknownComponents)this.components[unknownComponentType.getIndex()];
                        assert (unknownComponents != null);
                        Object component = unknownComponents.removeComponent(componentId, componentCodec);
                        if (component == null) continue block8;
                        this.addComponent0(componentType, component);
                        continue block8;
                    }
                    case UNREGISTERED: {
                        Object component;
                        UnknownComponents unknownComponents;
                        assert (this.archetype != null);
                        if (!this.archetype.contains(componentType)) continue block8;
                        String componentId = oldData.getComponentId(componentType);
                        Codec<Component<Object>> componentCodec = oldData.getComponentCodec(componentType);
                        if (componentCodec != null) {
                            if (this.archetype.contains(unknownComponentType)) {
                                unknownComponents = (UnknownComponents)this.components[unknownComponentType.getIndex()];
                                assert (unknownComponents != null);
                            } else {
                                unknownComponents = new UnknownComponents();
                                this.addComponent0(unknownComponentType, unknownComponents);
                            }
                            component = this.components[componentType.getIndex()];
                            unknownComponents.addComponent(componentId, component, componentCodec);
                        }
                        this.archetype = Archetype.remove(this.archetype, componentType);
                        this.components[componentType.getIndex()] = null;
                    }
                }
            }
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Holder<ECS_TYPE> clone() {
        assert (this.archetype != null);
        assert (this.components != null);
        assert (this.registry != null);
        long stamp = this.lock.readLock();
        try {
            Component[] componentsClone = new Component[this.components.length];
            for (int i = 0; i < this.components.length; ++i) {
                Component<ECS_TYPE> component = this.components[i];
                if (component == null) continue;
                componentsClone[i] = component.clone();
            }
            Holder<ECS_TYPE> holder = this.registry.newHolder(this.archetype, componentsClone);
            return holder;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Holder<ECS_TYPE> cloneSerializable(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
        assert (this.archetype != null);
        assert (this.components != null);
        assert (this.registry != null);
        long stamp = this.lock.readLock();
        try {
            Archetype<ECS_TYPE> serializableArchetype = this.archetype.getSerializableArchetype(data);
            Component[] componentsClone = new Component[serializableArchetype.length()];
            for (int i = serializableArchetype.getMinIndex(); i < serializableArchetype.length(); ++i) {
                ComponentType<ECS_TYPE, ?> componentType = serializableArchetype.get(i);
                if (componentType == null) continue;
                componentsClone[i] = this.components[i].cloneSerializable();
            }
            Holder<ECS_TYPE> holder = this.registry.newHolder(serializableArchetype, componentsClone);
            return holder;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void loadComponentsMap(@Nonnull ComponentRegistry.Data<ECS_TYPE> data, @Nonnull Map<String, Component<ECS_TYPE>> map) {
        assert (this.components != null);
        long stamp = this.lock.writeLock();
        try {
            ComponentType[] componentTypes = new ComponentType[map.size()];
            int i = 0;
            ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = data.getRegistry().getUnknownComponentType();
            UnknownComponents unknownComponents = (UnknownComponents)map.remove("Unknown");
            if (unknownComponents != null) {
                for (Map.Entry<String, BsonDocument> e : unknownComponents.getUnknownComponents().entrySet()) {
                    ComponentType<ECS_TYPE, ?> type = data.getComponentType(e.getKey());
                    if (type == null || map.containsKey(e.getKey())) continue;
                    Codec<?> codec = data.getComponentCodec(type);
                    ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                    Component decodedComponent = (Component)codec.decode(e.getValue(), extraInfo);
                    extraInfo.getValidationResults().logOrThrowValidatorExceptions(UnknownComponents.LOGGER);
                    if (componentTypes.length <= i) {
                        componentTypes = Arrays.copyOf(componentTypes, i + 1);
                    }
                    componentTypes[i++] = type;
                    int index = type.getIndex();
                    if (this.components.length <= index) {
                        this.components = Arrays.copyOf(this.components, index + 1);
                    }
                    this.components[index] = decodedComponent;
                }
                if (componentTypes.length <= i) {
                    componentTypes = Arrays.copyOf(componentTypes, i + 1);
                }
                componentTypes[i++] = unknownComponentType;
                int index = unknownComponentType.getIndex();
                if (this.components.length <= index) {
                    this.components = Arrays.copyOf(this.components, index + 1);
                }
                this.components[index] = unknownComponents;
            }
            for (Map.Entry<String, Component<ECS_TYPE>> entry : map.entrySet()) {
                int index;
                Component<ECS_TYPE> component = entry.getValue();
                if (component instanceof TempUnknownComponent) {
                    TempUnknownComponent tempUnknownComponent = (TempUnknownComponent)component;
                    if (unknownComponents == null) {
                        unknownComponents = new UnknownComponents();
                        if (componentTypes.length <= i) {
                            componentTypes = Arrays.copyOf(componentTypes, i + 1);
                        }
                        componentTypes[i++] = unknownComponentType;
                        index = unknownComponentType.getIndex();
                        if (this.components.length <= index) {
                            this.components = Arrays.copyOf(this.components, index + 1);
                        }
                        this.components[index] = unknownComponents;
                    }
                    unknownComponents.addComponent(entry.getKey(), tempUnknownComponent);
                    continue;
                }
                ComponentType<ECS_TYPE, ?> componentType = data.getComponentType(entry.getKey());
                if (componentTypes.length <= i) {
                    componentTypes = Arrays.copyOf(componentTypes, i + 1);
                }
                componentTypes[i++] = componentType;
                index = componentType.getIndex();
                if (this.components.length <= index) {
                    this.components = Arrays.copyOf(this.components, index + 1);
                }
                this.components[index] = component;
            }
            this.archetype = Archetype.of(componentTypes.length == i ? componentTypes : Arrays.copyOf(componentTypes, i));
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    Map<String, Component<ECS_TYPE>> createComponentsMap(@Nonnull ComponentRegistry.Data<ECS_TYPE> data) {
        assert (this.archetype != null);
        assert (this.components != null);
        long stamp = this.lock.readLock();
        try {
            if (this.archetype.isEmpty()) {
                Map<String, Component<ECS_TYPE>> map = Collections.emptyMap();
                return map;
            }
            ComponentRegistry<ECS_TYPE> registry = data.getRegistry();
            ComponentType<ECS_TYPE, UnknownComponents<ECS_TYPE>> unknownComponentType = registry.getUnknownComponentType();
            Object2ObjectOpenHashMap<String, Component<ECS_TYPE>> map = new Object2ObjectOpenHashMap<String, Component<ECS_TYPE>>(this.archetype.length());
            for (int i = this.archetype.getMinIndex(); i < this.archetype.length(); ++i) {
                ComponentType<ECS_TYPE, ?> componentType = this.archetype.get(i);
                if (componentType == null || data.getComponentCodec(componentType) == null) continue;
                if (componentType == unknownComponentType) {
                    UnknownComponents unknownComponents = (UnknownComponents)this.components[componentType.getIndex()];
                    for (Map.Entry<String, BsonDocument> entry : unknownComponents.getUnknownComponents().entrySet()) {
                        map.putIfAbsent(entry.getKey(), new TempUnknownComponent(entry.getValue()));
                    }
                    continue;
                }
                map.put(data.getComponentId(componentType), this.components[componentType.getIndex()]);
            }
            Object2ObjectOpenHashMap<String, Component<ECS_TYPE>> object2ObjectOpenHashMap = map;
            return object2ObjectOpenHashMap;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Holder that = (Holder)o;
        long stamp = this.lock.readLock();
        long thatStamp = that.lock.readLock();
        try {
            if (!Objects.equals(this.archetype, that.archetype)) {
                boolean bl = false;
                return bl;
            }
            boolean bl = Arrays.equals(this.components, that.components);
            return bl;
        }
        finally {
            that.lock.unlockRead(thatStamp);
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        long stamp = this.lock.readLock();
        try {
            int result = this.archetype != null ? this.archetype.hashCode() : 0;
            int n = result = 31 * result + Arrays.hashCode(this.components);
            return n;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public String toString() {
        long stamp = this.lock.readLock();
        try {
            String string = "EntityHolder{archetype=" + String.valueOf(this.archetype) + ", components=" + Arrays.toString(this.components) + "}";
            return string;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }
}

