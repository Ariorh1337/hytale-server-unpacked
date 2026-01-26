/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.instructions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.instructions.RoleStateChange;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.util.IComponentExecutionControl;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Action
extends RoleStateChange,
IAnnotatedComponent,
IComponentExecutionControl {
    public static final Action[] EMPTY_ARRAY = new Action[0];

    public boolean canExecute(@Nonnull Ref<EntityStore> var1, @Nonnull Role var2, @Nullable InfoProvider var3, double var4, @Nonnull Store<EntityStore> var6);

    public boolean execute(@Nonnull Ref<EntityStore> var1, @Nonnull Role var2, @Nullable InfoProvider var3, double var4, @Nonnull Store<EntityStore> var6);

    public void activate(Role var1, InfoProvider var2);

    public void deactivate(Role var1, InfoProvider var2);

    public boolean isActivated();
}

