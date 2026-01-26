/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundInvoker;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelProgressivePromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.FailedChannelFuture;
import io.netty.util.concurrent.EventExecutorGroup;
import java.util.List;
import java.util.Map;

public interface ChannelPipeline
extends ChannelInboundInvoker,
ChannelOutboundInvoker,
Iterable<Map.Entry<String, ChannelHandler>> {
    public ChannelPipeline addFirst(String var1, ChannelHandler var2);

    @Deprecated
    public ChannelPipeline addFirst(EventExecutorGroup var1, String var2, ChannelHandler var3);

    public ChannelPipeline addLast(String var1, ChannelHandler var2);

    @Deprecated
    public ChannelPipeline addLast(EventExecutorGroup var1, String var2, ChannelHandler var3);

    public ChannelPipeline addBefore(String var1, String var2, ChannelHandler var3);

    @Deprecated
    public ChannelPipeline addBefore(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4);

    public ChannelPipeline addAfter(String var1, String var2, ChannelHandler var3);

    @Deprecated
    public ChannelPipeline addAfter(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4);

    public ChannelPipeline addFirst(ChannelHandler ... var1);

    @Deprecated
    public ChannelPipeline addFirst(EventExecutorGroup var1, ChannelHandler ... var2);

    public ChannelPipeline addLast(ChannelHandler ... var1);

    @Deprecated
    public ChannelPipeline addLast(EventExecutorGroup var1, ChannelHandler ... var2);

    public ChannelPipeline remove(ChannelHandler var1);

    public ChannelHandler remove(String var1);

    public <T extends ChannelHandler> T remove(Class<T> var1);

    public ChannelHandler removeFirst();

    public ChannelHandler removeLast();

    public ChannelPipeline replace(ChannelHandler var1, String var2, ChannelHandler var3);

    public ChannelHandler replace(String var1, String var2, ChannelHandler var3);

    public <T extends ChannelHandler> T replace(Class<T> var1, String var2, ChannelHandler var3);

    public ChannelHandler first();

    public ChannelHandlerContext firstContext();

    public ChannelHandler last();

    public ChannelHandlerContext lastContext();

    public ChannelHandler get(String var1);

    public <T extends ChannelHandler> T get(Class<T> var1);

    public ChannelHandlerContext context(ChannelHandler var1);

    public ChannelHandlerContext context(String var1);

    public ChannelHandlerContext context(Class<? extends ChannelHandler> var1);

    public Channel channel();

    public List<String> names();

    public Map<String, ChannelHandler> toMap();

    @Override
    public ChannelPipeline fireChannelRegistered();

    @Override
    public ChannelPipeline fireChannelUnregistered();

    @Override
    public ChannelPipeline fireChannelActive();

    @Override
    public ChannelPipeline fireChannelInactive();

    @Override
    public ChannelPipeline fireExceptionCaught(Throwable var1);

    @Override
    public ChannelPipeline fireUserEventTriggered(Object var1);

    @Override
    public ChannelPipeline fireChannelRead(Object var1);

    @Override
    public ChannelPipeline fireChannelReadComplete();

    @Override
    public ChannelPipeline fireChannelWritabilityChanged();

    @Override
    public ChannelPipeline flush();

    @Override
    default public ChannelPromise newPromise() {
        return new DefaultChannelPromise(this.channel());
    }

    @Override
    default public ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise(this.channel());
    }

    @Override
    default public ChannelFuture newFailedFuture(Throwable cause) {
        return new FailedChannelFuture(this.channel(), null, cause);
    }
}

