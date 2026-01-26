/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.IoHandle;
import io.netty.channel.IoHandlerContext;
import io.netty.channel.IoRegistration;

public interface IoHandler {
    default public void initialize() {
    }

    public int run(IoHandlerContext var1);

    default public void prepareToDestroy() {
    }

    default public void destroy() {
    }

    public IoRegistration register(IoHandle var1) throws Exception;

    public void wakeup();

    public boolean isCompatible(Class<? extends IoHandle> var1);
}

