/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.IoEvent;
import io.netty.channel.IoRegistration;

public interface IoHandle
extends AutoCloseable {
    public void handle(IoRegistration var1, IoEvent var2);

    default public void registered() {
    }

    default public void unregistered() {
    }

    @Override
    public void close() throws Exception;
}

