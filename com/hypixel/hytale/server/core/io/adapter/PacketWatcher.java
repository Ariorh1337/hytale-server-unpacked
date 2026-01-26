/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.adapter;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.io.PacketHandler;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface PacketWatcher
extends BiConsumer<PacketHandler, Packet> {
    @Override
    public void accept(PacketHandler var1, Packet var2);
}

