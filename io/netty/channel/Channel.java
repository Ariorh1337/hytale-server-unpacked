/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.AttributeMap;
import java.net.SocketAddress;

public interface Channel
extends AttributeMap,
ChannelOutboundInvoker,
Comparable<Channel> {
    public ChannelId id();

    public EventLoop eventLoop();

    public Channel parent();

    public ChannelConfig config();

    public boolean isOpen();

    public boolean isRegistered();

    public boolean isActive();

    public ChannelMetadata metadata();

    public SocketAddress localAddress();

    public SocketAddress remoteAddress();

    public ChannelFuture closeFuture();

    default public boolean isWritable() {
        ChannelOutboundBuffer buf = this.unsafe().outboundBuffer();
        return buf != null && buf.isWritable();
    }

    default public long bytesBeforeUnwritable() {
        ChannelOutboundBuffer buf = this.unsafe().outboundBuffer();
        return buf != null ? buf.bytesBeforeUnwritable() : 0L;
    }

    default public long bytesBeforeWritable() {
        ChannelOutboundBuffer buf = this.unsafe().outboundBuffer();
        return buf != null ? buf.bytesBeforeWritable() : Long.MAX_VALUE;
    }

    public Unsafe unsafe();

    public ChannelPipeline pipeline();

    default public ByteBufAllocator alloc() {
        return this.config().getAllocator();
    }

    default public <T> T getOption(ChannelOption<T> option) {
        return this.config().getOption(option);
    }

    default public <T> boolean setOption(ChannelOption<T> option, T value) {
        return this.config().setOption(option, value);
    }

    @Override
    default public Channel read() {
        this.pipeline().read();
        return this;
    }

    @Override
    default public Channel flush() {
        this.pipeline().flush();
        return this;
    }

    @Override
    default public ChannelFuture writeAndFlush(Object msg) {
        return this.pipeline().writeAndFlush(msg);
    }

    @Override
    default public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.pipeline().writeAndFlush(msg, promise);
    }

    @Override
    default public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.pipeline().write(msg, promise);
    }

    @Override
    default public ChannelFuture write(Object msg) {
        return this.pipeline().write(msg);
    }

    @Override
    default public ChannelFuture deregister(ChannelPromise promise) {
        return this.pipeline().deregister(promise);
    }

    @Override
    default public ChannelFuture close(ChannelPromise promise) {
        return this.pipeline().close(promise);
    }

    @Override
    default public ChannelFuture disconnect(ChannelPromise promise) {
        return this.pipeline().disconnect(promise);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline().connect(remoteAddress, localAddress, promise);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.pipeline().connect(remoteAddress, promise);
    }

    @Override
    default public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline().bind(localAddress, promise);
    }

    @Override
    default public ChannelFuture deregister() {
        return this.pipeline().deregister();
    }

    @Override
    default public ChannelFuture close() {
        return this.pipeline().close();
    }

    @Override
    default public ChannelFuture disconnect() {
        return this.pipeline().disconnect();
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.pipeline().connect(remoteAddress, localAddress);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.pipeline().connect(remoteAddress);
    }

    @Override
    default public ChannelFuture bind(SocketAddress localAddress) {
        return this.pipeline().bind(localAddress);
    }

    @Override
    default public ChannelPromise newPromise() {
        return this.pipeline().newPromise();
    }

    @Override
    default public ChannelProgressivePromise newProgressivePromise() {
        return this.pipeline().newProgressivePromise();
    }

    @Override
    default public ChannelFuture newSucceededFuture() {
        return this.pipeline().newSucceededFuture();
    }

    @Override
    default public ChannelFuture newFailedFuture(Throwable cause) {
        return this.pipeline().newFailedFuture(cause);
    }

    @Override
    default public ChannelPromise voidPromise() {
        return this.pipeline().voidPromise();
    }

    public static interface Unsafe {
        public RecvByteBufAllocator.Handle recvBufAllocHandle();

        public SocketAddress localAddress();

        public SocketAddress remoteAddress();

        public void register(EventLoop var1, ChannelPromise var2);

        public void bind(SocketAddress var1, ChannelPromise var2);

        public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

        public void disconnect(ChannelPromise var1);

        public void close(ChannelPromise var1);

        public void closeForcibly();

        public void deregister(ChannelPromise var1);

        public void beginRead();

        public void write(Object var1, ChannelPromise var2);

        public void flush();

        public ChannelPromise voidPromise();

        public ChannelOutboundBuffer outboundBuffer();
    }
}

