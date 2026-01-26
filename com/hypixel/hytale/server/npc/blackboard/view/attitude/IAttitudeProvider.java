/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view.attitude;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.role.Role;
import javax.annotation.Nonnull;

public interface IAttitudeProvider {
    public static final int OVERRIDE_PRIORITY = 0;

    public Attitude getAttitude(@Nonnull Ref<EntityStore> var1, @Nonnull Role var2, @Nonnull Ref<EntityStore> var3, @Nonnull ComponentAccessor<EntityStore> var4);
}

