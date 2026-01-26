/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.components.messaging;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.components.messaging.NPCMessage;

public abstract class MessageSupport
implements Component<EntityStore> {
    public abstract NPCMessage[] getMessageSlots();

    public boolean isMessageQueued(int messageIndex) {
        if (this.getMessageSlots() == null) {
            return false;
        }
        return this.getMessageSlots()[messageIndex].isActivated();
    }

    public boolean isMessageEnabled(int messageIndex) {
        if (this.getMessageSlots() == null) {
            return false;
        }
        return this.getMessageSlots()[messageIndex].isEnabled();
    }

    @Override
    public abstract Component<EntityStore> clone();
}

