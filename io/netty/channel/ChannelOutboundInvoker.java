/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;

public interface ChannelOutboundInvoker {
    default public ChannelFuture bind(SocketAddress localAddress) {
        return this.bind(localAddress, this.newPromise());
    }

    default public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.connect(remoteAddress, this.newPromise());
    }

    default public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.connect(remoteAddress, localAddress, this.newPromise());
    }

    default public ChannelFuture disconnect() {
        return this.disconnect(this.newPromise());
    }

    default public ChannelFuture close() {
        return this.close(this.newPromise());
    }

    default public ChannelFuture deregister() {
        return this.deregister(this.newPromise());
    }

    public ChannelFuture bind(SocketAddress var1, ChannelPromise var2);

    public ChannelFuture connect(SocketAddress var1, ChannelPromise var2);

    public ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

    public ChannelFuture disconnect(ChannelPromise var1);

    public ChannelFuture close(ChannelPromise var1);

    public ChannelFuture deregister(ChannelPromise var1);

    public ChannelOutboundInvoker read();

    default public ChannelFuture write(Object msg) {
        return this.write(msg, this.newPromise());
    }

    public ChannelFuture write(Object var1, ChannelPromise var2);

    public ChannelOutboundInvoker flush();

    public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

    default public ChannelFuture writeAndFlush(Object msg) {
        return this.writeAndFlush(msg, this.newPromise());
    }

    public ChannelPromise newPromise();

    public ChannelProgressivePromise newProgressivePromise();

    public ChannelFuture newSucceededFuture();

    public ChannelFuture newFailedFuture(Throwable var1);

    public ChannelPromise voidPromise();
}

