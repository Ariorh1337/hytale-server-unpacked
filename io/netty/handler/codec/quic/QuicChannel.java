/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.quic.QuicChannelBootstrap;
import io.netty.handler.codec.quic.QuicChannelConfig;
import io.netty.handler.codec.quic.QuicConnectionAddress;
import io.netty.handler.codec.quic.QuicConnectionPathStats;
import io.netty.handler.codec.quic.QuicConnectionStats;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamChannelBootstrap;
import io.netty.handler.codec.quic.QuicStreamType;
import io.netty.handler.codec.quic.QuicTransportParameters;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.net.SocketAddress;
import javax.net.ssl.SSLEngine;
import org.jetbrains.annotations.Nullable;

public interface QuicChannel
extends Channel {
    @Override
    default public ChannelFuture bind(SocketAddress localAddress) {
        return this.pipeline().bind(localAddress);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.pipeline().connect(remoteAddress);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.pipeline().connect(remoteAddress, localAddress);
    }

    @Override
    default public ChannelFuture disconnect() {
        return this.pipeline().disconnect();
    }

    @Override
    default public ChannelFuture close() {
        return this.pipeline().close();
    }

    @Override
    default public ChannelFuture deregister() {
        return this.pipeline().deregister();
    }

    @Override
    default public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline().bind(localAddress, promise);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.pipeline().connect(remoteAddress, promise);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline().connect(remoteAddress, localAddress, promise);
    }

    @Override
    default public ChannelFuture disconnect(ChannelPromise promise) {
        return this.pipeline().disconnect(promise);
    }

    @Override
    default public ChannelFuture close(ChannelPromise promise) {
        return this.pipeline().close(promise);
    }

    @Override
    default public ChannelFuture deregister(ChannelPromise promise) {
        return this.pipeline().deregister(promise);
    }

    @Override
    default public ChannelFuture write(Object msg) {
        return this.pipeline().write(msg);
    }

    @Override
    default public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.pipeline().write(msg, promise);
    }

    @Override
    default public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.pipeline().writeAndFlush(msg, promise);
    }

    @Override
    default public ChannelFuture writeAndFlush(Object msg) {
        return this.pipeline().writeAndFlush(msg);
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

    @Override
    public QuicChannel read();

    @Override
    public QuicChannel flush();

    @Override
    public QuicChannelConfig config();

    @Nullable
    public SSLEngine sslEngine();

    public long peerAllowedStreams(QuicStreamType var1);

    public boolean isTimedOut();

    @Nullable
    public QuicTransportParameters peerTransportParameters();

    @Override
    @Nullable
    public QuicConnectionAddress localAddress();

    @Override
    @Nullable
    public QuicConnectionAddress remoteAddress();

    @Nullable
    public SocketAddress localSocketAddress();

    @Nullable
    public SocketAddress remoteSocketAddress();

    default public Future<QuicStreamChannel> createStream(QuicStreamType type, @Nullable ChannelHandler handler) {
        return this.createStream(type, handler, this.eventLoop().newPromise());
    }

    public Future<QuicStreamChannel> createStream(QuicStreamType var1, @Nullable ChannelHandler var2, Promise<QuicStreamChannel> var3);

    default public QuicStreamChannelBootstrap newStreamBootstrap() {
        return new QuicStreamChannelBootstrap(this);
    }

    default public ChannelFuture close(boolean applicationClose, int error, ByteBuf reason) {
        return this.close(applicationClose, error, reason, this.newPromise());
    }

    public ChannelFuture close(boolean var1, int var2, ByteBuf var3, ChannelPromise var4);

    default public Future<QuicConnectionStats> collectStats() {
        return this.collectStats(this.eventLoop().newPromise());
    }

    public Future<QuicConnectionStats> collectStats(Promise<QuicConnectionStats> var1);

    default public Future<QuicConnectionPathStats> collectPathStats(int pathIdx) {
        return this.collectPathStats(pathIdx, this.eventLoop().newPromise());
    }

    public Future<QuicConnectionPathStats> collectPathStats(int var1, Promise<QuicConnectionPathStats> var2);

    public static QuicChannelBootstrap newBootstrap(Channel channel) {
        return new QuicChannelBootstrap(channel);
    }
}

