/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.event.EntityEventType;
import com.hypixel.hytale.component.event.WorldEventType;
import com.hypixel.hytale.component.system.EcsEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ComponentAccessor<ECS_TYPE> {
    @Nullable
    public <T extends Component<ECS_TYPE>> T getComponent(@Nonnull Ref<ECS_TYPE> var1, @Nonnull ComponentType<ECS_TYPE, T> var2);

    @Nonnull
    public <T extends Component<ECS_TYPE>> T ensureAndGetComponent(@Nonnull Ref<ECS_TYPE> var1, @Nonnull ComponentType<ECS_TYPE, T> var2);

    @Nonnull
    public Archetype<ECS_TYPE> getArchetype(@Nonnull Ref<ECS_TYPE> var1);

    @Nonnull
    public <T extends Resource<ECS_TYPE>> T getResource(@Nonnull ResourceType<ECS_TYPE, T> var1);

    @Nonnull
    public ECS_TYPE getExternalData();

    public <T extends Component<ECS_TYPE>> void putComponent(@Nonnull Ref<ECS_TYPE> var1, @Nonnull ComponentType<ECS_TYPE, T> var2, @Nonnull T var3);

    public <T extends Component<ECS_TYPE>> void addComponent(@Nonnull Ref<ECS_TYPE> var1, @Nonnull ComponentType<ECS_TYPE, T> var2, @Nonnull T var3);

    public <T extends Component<ECS_TYPE>> T addComponent(@Nonnull Ref<ECS_TYPE> var1, @Nonnull ComponentType<ECS_TYPE, T> var2);

    public Ref<ECS_TYPE>[] addEntities(@Nonnull Holder<ECS_TYPE>[] var1, @Nonnull AddReason var2);

    @Nullable
    public Ref<ECS_TYPE> addEntity(@Nonnull Holder<ECS_TYPE> var1, @Nonnull AddReason var2);

    @Nonnull
    public Holder<ECS_TYPE> removeEntity(@Nonnull Ref<ECS_TYPE> var1, @Nonnull Holder<ECS_TYPE> var2, @Nonnull RemoveReason var3);

    public <T extends Component<ECS_TYPE>> void removeComponent(@Nonnull Ref<ECS_TYPE> var1, @Nonnull ComponentType<ECS_TYPE, T> var2);

    public <T extends Component<ECS_TYPE>> void tryRemoveComponent(@Nonnull Ref<ECS_TYPE> var1, @Nonnull ComponentType<ECS_TYPE, T> var2);

    public <Event extends EcsEvent> void invoke(@Nonnull Ref<ECS_TYPE> var1, @Nonnull Event var2);

    public <Event extends EcsEvent> void invoke(@Nonnull EntityEventType<ECS_TYPE, Event> var1, @Nonnull Ref<ECS_TYPE> var2, @Nonnull Event var3);

    public <Event extends EcsEvent> void invoke(@Nonnull Event var1);

    public <Event extends EcsEvent> void invoke(@Nonnull WorldEventType<ECS_TYPE, Event> var1, @Nonnull Event var2);
}

