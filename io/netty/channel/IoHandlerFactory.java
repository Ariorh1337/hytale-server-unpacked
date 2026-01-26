/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.IoHandler;
import io.netty.util.concurrent.ThreadAwareExecutor;

public interface IoHandlerFactory {
    public IoHandler newHandler(ThreadAwareExecutor var1);

    default public boolean isChangingThreadSupported() {
        return false;
    }
}

