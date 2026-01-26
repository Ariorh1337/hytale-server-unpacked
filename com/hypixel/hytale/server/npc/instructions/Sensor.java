/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.instructions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.instructions.NullSensor;
import com.hypixel.hytale.server.npc.instructions.RoleStateChange;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.util.IComponentExecutionControl;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Sensor
extends RoleStateChange,
IAnnotatedComponent,
IComponentExecutionControl {
    public static final Sensor NULL = new NullSensor();

    public boolean matches(@Nonnull Ref<EntityStore> var1, @Nonnull Role var2, double var3, @Nonnull Store<EntityStore> var5);

    default public void done() {
    }

    @Nullable
    public InfoProvider getSensorInfo();
}

