/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.NetworkChannel;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public interface Packet {
    public int getId();

    public NetworkChannel getChannel();

    public void serialize(@Nonnull ByteBuf var1);

    public int computeSize();
}

