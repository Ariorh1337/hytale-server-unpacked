/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.components.messaging;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.components.messaging.EntityEventSupport;
import javax.annotation.Nonnull;

public class PlayerEntityEventSupport
extends EntityEventSupport
implements Component<EntityStore> {
    public static ComponentType<EntityStore, PlayerEntityEventSupport> getComponentType() {
        return NPCPlugin.get().getPlayerEntityEventSupportComponentType();
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        PlayerEntityEventSupport support = new PlayerEntityEventSupport();
        this.cloneTo(support);
        return support;
    }
}

