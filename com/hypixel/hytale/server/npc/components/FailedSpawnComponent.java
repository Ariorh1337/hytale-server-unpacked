/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import javax.annotation.Nonnull;

public class FailedSpawnComponent
implements Component<EntityStore> {
    public static ComponentType<EntityStore, FailedSpawnComponent> getComponentType() {
        return NPCPlugin.get().getFailedSpawnComponentType();
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        return new FailedSpawnComponent();
    }
}

