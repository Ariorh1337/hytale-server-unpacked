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

public class NPCEntityEventSupport
extends EntityEventSupport
implements Component<EntityStore> {
    public static ComponentType<EntityStore, NPCEntityEventSupport> getComponentType() {
        return NPCPlugin.get().getNpcEntityEventSupportComponentType();
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        NPCEntityEventSupport support = new NPCEntityEventSupport();
        this.cloneTo(support);
        return support;
    }
}

