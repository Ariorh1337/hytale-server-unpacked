/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.receiver;

import com.hypixel.hytale.protocol.ToClientPacket;
import javax.annotation.Nonnull;

public interface IPacketReceiver {
    public void write(@Nonnull ToClientPacket var1);

    public void writeNoCache(@Nonnull ToClientPacket var1);
}

