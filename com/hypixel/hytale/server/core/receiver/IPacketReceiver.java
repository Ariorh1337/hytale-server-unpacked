/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.receiver;

import com.hypixel.hytale.protocol.Packet;
import javax.annotation.Nonnull;

public interface IPacketReceiver {
    public void write(@Nonnull Packet var1);

    public void writeNoCache(@Nonnull Packet var1);
}

