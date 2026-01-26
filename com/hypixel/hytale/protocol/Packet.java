/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public interface Packet {
    public int getId();

    public void serialize(@Nonnull ByteBuf var1);

    public int computeSize();
}

