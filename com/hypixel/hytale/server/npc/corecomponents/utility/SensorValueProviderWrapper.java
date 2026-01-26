/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderSensorValueProviderWrapper;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderValueToParameterMapping;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.support.DebugSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.ValueWrappedInfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.MultipleParameterProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.SingleDoubleParameterProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.SingleIntParameterProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.SingleParameterProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.SingleStringParameterProvider;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponentCollection;
import com.hypixel.hytale.server.npc.valuestore.ValueStore;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SensorValueProviderWrapper
extends SensorBase
implements IAnnotatedComponentCollection {
    protected static final IntObjectPair<?>[] EMPTY_ARRAY = new IntObjectPair[0];
    @Nonnull
    protected final Sensor sensor;
    protected final boolean passValues;
    @Nonnull
    protected final IntObjectPair<SingleStringParameterProvider>[] stringParameterProviders;
    @Nonnull
    protected final IntObjectPair<SingleIntParameterProvider>[] intParameterProviders;
    @Nonnull
    protected final IntObjectPair<SingleDoubleParameterProvider>[] doubleParameterProviders;
    @Nonnull
    protected final ValueWrappedInfoProvider infoProvider;
    protected final MultipleParameterProvider multipleParameterProvider = new MultipleParameterProvider();
    protected final ComponentType<EntityStore, ValueStore> valueStoreComponentType;

    public SensorValueProviderWrapper(@Nonnull BuilderSensorValueProviderWrapper builder, @Nonnull BuilderSupport support, @Nonnull Sensor sensor) {
        super(builder);
        this.sensor = sensor;
        this.passValues = builder.isPassValues(support);
        this.infoProvider = new ValueWrappedInfoProvider(sensor.getSensorInfo(), this.multipleParameterProvider);
        ObjectArrayList<IntObjectPair<SingleStringParameterProvider>> stringMappings = new ObjectArrayList<IntObjectPair<SingleStringParameterProvider>>();
        ObjectArrayList<IntObjectPair<SingleStringParameterProvider>> intMappings = new ObjectArrayList<IntObjectPair<SingleStringParameterProvider>>();
        ObjectArrayList<IntObjectPair<SingleStringParameterProvider>> doubleMappings = new ObjectArrayList<IntObjectPair<SingleStringParameterProvider>>();
        List<BuilderValueToParameterMapping.ValueToParameterMapping> parameterMappings = builder.getParameterMappings(support);
        if (parameterMappings != null) {
            block5: for (int i = 0; i < parameterMappings.size(); ++i) {
                BuilderValueToParameterMapping.ValueToParameterMapping mapping = parameterMappings.get(i);
                int slot = mapping.getToParameterSlot();
                switch (mapping.getType()) {
                    case String: {
                        SingleParameterProvider provider = new SingleStringParameterProvider(slot);
                        this.multipleParameterProvider.addParameterProvider(slot, provider);
                        stringMappings.add(IntObjectPair.of(mapping.getFromValueSlot(), provider));
                        continue block5;
                    }
                    case Int: {
                        SingleParameterProvider provider = new SingleIntParameterProvider(slot);
                        this.multipleParameterProvider.addParameterProvider(slot, provider);
                        intMappings.add(IntObjectPair.of(mapping.getFromValueSlot(), provider));
                        continue block5;
                    }
                    case Double: {
                        SingleParameterProvider provider = new SingleDoubleParameterProvider(slot);
                        this.multipleParameterProvider.addParameterProvider(slot, provider);
                        doubleMappings.add(IntObjectPair.of(mapping.getFromValueSlot(), provider));
                    }
                }
            }
        }
        this.stringParameterProviders = stringMappings.isEmpty() ? EMPTY_ARRAY : (IntObjectPair[])stringMappings.toArray(IntObjectPair[]::new);
        this.intParameterProviders = intMappings.isEmpty() ? EMPTY_ARRAY : (IntObjectPair[])intMappings.toArray(IntObjectPair[]::new);
        this.doubleParameterProviders = doubleMappings.isEmpty() ? EMPTY_ARRAY : (IntObjectPair[])doubleMappings.toArray(IntObjectPair[]::new);
        this.valueStoreComponentType = ValueStore.getComponentType();
    }

    @Override
    public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, double dt, @Nonnull Store<EntityStore> store) {
        if (!super.matches(ref, role, dt, store) || !this.sensor.matches(ref, role, dt, store)) {
            DebugSupport debugSupport = role.getDebugSupport();
            if (debugSupport.isTraceSensorFails()) {
                debugSupport.setLastFailingSensor(this.sensor);
            }
            this.multipleParameterProvider.clear();
            return false;
        }
        if (!this.passValues) {
            return true;
        }
        ValueStore valueStore = store.getComponent(ref, this.valueStoreComponentType);
        if (valueStore == null) {
            return false;
        }
        for (IntObjectPair<SingleStringParameterProvider> intObjectPair : this.stringParameterProviders) {
            String value = valueStore.readString(intObjectPair.firstInt());
            ((SingleStringParameterProvider)intObjectPair.value()).overrideString(value);
        }
        for (IntObjectPair<SingleParameterProvider> intObjectPair : this.intParameterProviders) {
            int value = valueStore.readInt(intObjectPair.firstInt());
            ((SingleIntParameterProvider)intObjectPair.value()).overrideInt(value);
        }
        for (IntObjectPair<SingleParameterProvider> intObjectPair : this.doubleParameterProviders) {
            double value = valueStore.readDouble(intObjectPair.firstInt());
            ((SingleDoubleParameterProvider)intObjectPair.value()).overrideDouble(value);
        }
        return true;
    }

    @Override
    public InfoProvider getSensorInfo() {
        return this.infoProvider;
    }

    @Override
    public void registerWithSupport(Role role) {
        this.sensor.registerWithSupport(role);
    }

    @Override
    public void motionControllerChanged(@Nullable Ref<EntityStore> ref, @Nonnull NPCEntity npcComponent, MotionController motionController, @Nullable ComponentAccessor<EntityStore> componentAccessor) {
        this.sensor.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
    }

    @Override
    public void loaded(Role role) {
        this.sensor.loaded(role);
    }

    @Override
    public void spawned(Role role) {
        this.sensor.spawned(role);
    }

    @Override
    public void unloaded(Role role) {
        this.sensor.unloaded(role);
    }

    @Override
    public void removed(Role role) {
        this.sensor.removed(role);
    }

    @Override
    public void teleported(Role role, World from, World to) {
        this.sensor.teleported(role, from, to);
    }

    @Override
    public void done() {
        this.sensor.done();
    }

    @Override
    public int componentCount() {
        return 1;
    }

    @Override
    @Nonnull
    public IAnnotatedComponent getComponent(int index) {
        if (index >= this.componentCount()) {
            throw new IndexOutOfBoundsException();
        }
        return this.sensor;
    }

    @Override
    public void setContext(IAnnotatedComponent parent, int index) {
        super.setContext(parent, index);
        this.sensor.setContext(this, index);
    }
}

