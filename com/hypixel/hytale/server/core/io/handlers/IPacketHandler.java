/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.handlers;

import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface IPacketHandler {
    public void registerHandler(int var1, @Nonnull Consumer<ToServerPacket> var2);

    public void registerNoOpHandlers(int ... var1);

    @Nonnull
    public PlayerRef getPlayerRef();

    @Nonnull
    public String getIdentifier();
}

