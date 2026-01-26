/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.address;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;

public abstract class DynamicAddressConnectHandler
extends ChannelOutboundHandlerAdapter {
    @Override
    public final void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        SocketAddress local;
        SocketAddress remote;
        try {
            remote = this.remoteAddress(remoteAddress, localAddress);
            local = this.localAddress(remoteAddress, localAddress);
        }
        catch (Exception e) {
            promise.setFailure(e);
            return;
        }
        ctx.connect(remote, local, promise).addListener((GenericFutureListener)new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    future.channel().pipeline().remove(DynamicAddressConnectHandler.this);
                }
            }
        });
    }

    protected SocketAddress localAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        return localAddress;
    }

    protected SocketAddress remoteAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        return remoteAddress;
    }
}

