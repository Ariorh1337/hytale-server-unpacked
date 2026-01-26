/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.transport;

import com.hypixel.hytale.server.core.io.transport.TransportType;
import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;

public interface Transport {
    public TransportType getType();

    public ChannelFuture bind(InetSocketAddress var1) throws InterruptedException;

    public void shutdown();
}

