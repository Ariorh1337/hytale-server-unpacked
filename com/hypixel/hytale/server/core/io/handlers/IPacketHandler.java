/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.handlers;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface IPacketHandler {
    public void registerHandler(int var1, @Nonnull Consumer<Packet> var2);

    public void registerNoOpHandlers(int ... var1);

    @Nonnull
    public PlayerRef getPlayerRef();

    @Nonnull
    public String getIdentifier();
}

