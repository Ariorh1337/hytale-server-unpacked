/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.AbstractChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.util.concurrent.EventExecutor;

final class DefaultChannelHandlerContext
extends AbstractChannelHandlerContext {
    private final ChannelHandler handler;

    DefaultChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name, ChannelHandler handler) {
        super(pipeline, executor, name, handler.getClass());
        this.handler = handler;
    }

    @Override
    public ChannelHandler handler() {
        return this.handler;
    }
}

