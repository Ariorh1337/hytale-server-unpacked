/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoHandle;
import io.netty.channel.IoHandler;
import io.netty.channel.IoRegistration;
import io.netty.util.concurrent.Future;

public interface IoEventLoopGroup
extends EventLoopGroup {
    @Override
    public IoEventLoop next();

    @Override
    @Deprecated
    default public ChannelFuture register(Channel channel) {
        return this.next().register(channel);
    }

    @Override
    @Deprecated
    default public ChannelFuture register(ChannelPromise promise) {
        return this.next().register(promise);
    }

    default public Future<IoRegistration> register(IoHandle handle) {
        return this.next().register(handle);
    }

    default public boolean isCompatible(Class<? extends IoHandle> handleType) {
        return this.next().isCompatible(handleType);
    }

    default public boolean isIoType(Class<? extends IoHandler> handlerType) {
        return this.next().isIoType(handlerType);
    }
}

