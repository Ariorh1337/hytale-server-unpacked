/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.event.events.player;

import com.hypixel.hytale.server.core.event.events.player.PlayerRefEvent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

public class PlayerDisconnectEvent
extends PlayerRefEvent<Void> {
    public PlayerDisconnectEvent(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Nonnull
    public PacketHandler.DisconnectReason getDisconnectReason() {
        return this.playerRef.getPacketHandler().getDisconnectReason();
    }

    @Override
    @Nonnull
    public String toString() {
        return "PlayerDisconnectEvent{playerRef=" + String.valueOf(this.playerRef) + "} " + super.toString();
    }
}

