/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.OrderedEventExecutor;

public interface EventLoop
extends OrderedEventExecutor,
EventLoopGroup {
    @Override
    public EventLoopGroup parent();
}

