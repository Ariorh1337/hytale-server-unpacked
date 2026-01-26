/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.meta.state;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.List;

@Deprecated
public interface SendableBlockState {
    public void sendTo(List<Packet> var1);

    public void unloadFrom(List<Packet> var1);

    default public boolean canPlayerSee(PlayerRef player) {
        return true;
    }
}

