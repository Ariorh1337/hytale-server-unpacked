/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.unix;

import io.netty.channel.Channel;
import io.netty.channel.unix.DomainDatagramChannelConfig;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.UnixChannel;

public interface DomainDatagramChannel
extends UnixChannel,
Channel {
    @Override
    public DomainDatagramChannelConfig config();

    public boolean isConnected();

    @Override
    public DomainSocketAddress localAddress();

    @Override
    public DomainSocketAddress remoteAddress();
}

