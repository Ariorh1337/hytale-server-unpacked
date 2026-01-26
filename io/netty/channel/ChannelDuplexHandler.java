/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerMask;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;

public class ChannelDuplexHandler
extends ChannelInboundHandlerAdapter
implements ChannelOutboundHandler {
    @Override
    @ChannelHandlerMask.Skip
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    @ChannelHandlerMask.Skip
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    @ChannelHandlerMask.Skip
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    @ChannelHandlerMask.Skip
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close(promise);
    }

    @Override
    @ChannelHandlerMask.Skip
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    @ChannelHandlerMask.Skip
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    @ChannelHandlerMask.Skip
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }

    @Override
    @ChannelHandlerMask.Skip
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}

