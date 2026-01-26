/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.EventLoop;
import io.netty.channel.IoEventLoopGroup;
import io.netty.channel.IoHandle;
import io.netty.channel.IoHandler;
import io.netty.channel.IoRegistration;
import io.netty.util.concurrent.Future;

public interface IoEventLoop
extends EventLoop,
IoEventLoopGroup {
    @Override
    default public IoEventLoop next() {
        return this;
    }

    @Override
    public Future<IoRegistration> register(IoHandle var1);

    @Override
    public boolean isCompatible(Class<? extends IoHandle> var1);

    @Override
    public boolean isIoType(Class<? extends IoHandler> var1);
}

