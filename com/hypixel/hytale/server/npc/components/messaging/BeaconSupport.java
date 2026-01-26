/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.components.messaging;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.components.messaging.MessageSupport;
import com.hypixel.hytale.server.npc.components.messaging.NPCMessage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeaconSupport
extends MessageSupport
implements Component<EntityStore> {
    private NPCMessage[] messageSlots;
    private Object2IntMap<String> messageIndices;
    private Int2ObjectMap<String> indicesToMessages;

    public static ComponentType<EntityStore, BeaconSupport> getComponentType() {
        return NPCPlugin.get().getBeaconSupportComponentType();
    }

    public void postMessage(String message, Ref<EntityStore> target, double age) {
        if (this.messageSlots == null) {
            return;
        }
        if (age < 0.0 && age != -1.0) {
            return;
        }
        int slot = this.messageIndices.getInt(message);
        if (slot != Integer.MIN_VALUE && this.messageSlots[slot].isEnabled()) {
            this.messageSlots[slot].activate(target, age);
        }
    }

    @Nullable
    public Ref<EntityStore> pollMessage(int messageIndex) {
        NPCMessage beacon = this.messageSlots[messageIndex];
        beacon.deactivate();
        return beacon.getTarget();
    }

    @Nullable
    public Ref<EntityStore> peekMessage(int messageIndex) {
        return this.messageSlots[messageIndex].getTarget();
    }

    public void initialise(@Nonnull Object2IntMap<String> messageIndices) {
        this.messageIndices = messageIndices;
        this.indicesToMessages = new Int2ObjectOpenHashMap<String>();
        messageIndices.forEach((key, value) -> this.indicesToMessages.put((Integer)value, (String)key));
        NPCMessage[] messages = new NPCMessage[messageIndices.size()];
        for (int i = 0; i < messages.length; ++i) {
            messages[i] = new NPCMessage();
        }
        this.messageSlots = messages;
    }

    public String getMessageTextForIndex(int messageIndex) {
        return (String)this.indicesToMessages.get(messageIndex);
    }

    @Override
    public NPCMessage[] getMessageSlots() {
        return this.messageSlots;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        BeaconSupport beaconSupport = new BeaconSupport();
        beaconSupport.messageSlots = new NPCMessage[this.messageSlots.length];
        for (int i = 0; i < beaconSupport.messageSlots.length; ++i) {
            beaconSupport.messageSlots[i] = this.messageSlots[i].clone();
        }
        beaconSupport.messageIndices = this.messageIndices;
        return beaconSupport;
    }
}

