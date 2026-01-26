/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.adapter;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.function.BiPredicate;

public interface PlayerPacketFilter
extends BiPredicate<PlayerRef, Packet> {
    @Override
    public boolean test(PlayerRef var1, Packet var2);
}

