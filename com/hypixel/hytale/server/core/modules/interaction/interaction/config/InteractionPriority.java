/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.protocol.PrioritySlot;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionPriorityCodec;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record InteractionPriority(@Nullable Map<PrioritySlot, Integer> values) implements NetworkSerializable<com.hypixel.hytale.protocol.InteractionPriority>
{
    public static final Codec<InteractionPriority> CODEC = new InteractionPriorityCodec();

    public InteractionPriority(int defaultValue) {
        this((Map<PrioritySlot, Integer>)(defaultValue != 0 ? new EnumMap<PrioritySlot, Integer>(Map.of(PrioritySlot.Default, defaultValue)) : null));
    }

    public int getPriority(PrioritySlot slot) {
        if (this.values == null) {
            return 0;
        }
        Integer value = this.values.get((Object)slot);
        if (value != null) {
            return value;
        }
        Integer defaultValue = this.values.get((Object)PrioritySlot.Default);
        return defaultValue != null ? defaultValue : 0;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.InteractionPriority toPacket() {
        com.hypixel.hytale.protocol.InteractionPriority packet = new com.hypixel.hytale.protocol.InteractionPriority();
        if (this.values != null && !this.values.isEmpty()) {
            packet.values = new EnumMap<PrioritySlot, Integer>(this.values);
        }
        return packet;
    }
}

