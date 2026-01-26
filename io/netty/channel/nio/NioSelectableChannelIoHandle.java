/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.IoEvent;
import io.netty.channel.IoHandle;
import io.netty.channel.IoRegistration;
import io.netty.channel.nio.NioIoHandle;
import io.netty.util.internal.ObjectUtil;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.AbstractInterruptibleChannel;

public abstract class NioSelectableChannelIoHandle<S extends SelectableChannel>
implements IoHandle,
NioIoHandle {
    private final S channel;

    public NioSelectableChannelIoHandle(S channel) {
        this.channel = (SelectableChannel)ObjectUtil.checkNotNull(channel, "channel");
    }

    @Override
    public void handle(IoRegistration registration, IoEvent ioEvent) {
        SelectionKey key = (SelectionKey)registration.attachment();
        this.handle(this.channel, key);
    }

    @Override
    public void close() throws Exception {
        ((AbstractInterruptibleChannel)this.channel).close();
    }

    @Override
    public SelectableChannel selectableChannel() {
        return this.channel;
    }

    protected abstract void handle(S var1, SelectionKey var2);

    protected void deregister(S channel) {
    }
}

