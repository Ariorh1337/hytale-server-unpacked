/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DuplexChannel;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamAddress;
import io.netty.handler.codec.quic.QuicStreamChannelConfig;
import io.netty.handler.codec.quic.QuicStreamPriority;
import io.netty.handler.codec.quic.QuicStreamType;
import java.net.SocketAddress;
import org.jetbrains.annotations.Nullable;

public interface QuicStreamChannel
extends DuplexChannel {
    public static final ChannelFutureListener SHUTDOWN_OUTPUT = f -> ((QuicStreamChannel)f.channel()).shutdownOutput();

    @Override
    default public ChannelFuture bind(SocketAddress socketAddress) {
        return this.pipeline().bind(socketAddress);
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
    default public ChannelFuture bind(SocketAddress localAddress, ChannelPromise channelPromise) {
        return this.pipeline().bind(localAddress, channelPromise);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise channelPromise) {
        return this.pipeline().connect(remoteAddress, channelPromise);
    }

    @Override
    default public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise channelPromise) {
        return this.pipeline().connect(remoteAddress, localAddress, channelPromise);
    }

    @Override
    default public ChannelFuture disconnect(ChannelPromise channelPromise) {
        return this.pipeline().disconnect(channelPromise);
    }

    @Override
    default public ChannelFuture close(ChannelPromise channelPromise) {
        return this.pipeline().close(channelPromise);
    }

    @Override
    default public ChannelFuture deregister(ChannelPromise channelPromise) {
        return this.pipeline().deregister(channelPromise);
    }

    @Override
    default public ChannelFuture write(Object msg) {
        return this.pipeline().write(msg);
    }

    @Override
    default public ChannelFuture write(Object msg, ChannelPromise channelPromise) {
        return this.pipeline().write(msg, channelPromise);
    }

    @Override
    default public ChannelFuture writeAndFlush(Object msg, ChannelPromise channelPromise) {
        return this.pipeline().writeAndFlush(msg, channelPromise);
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
    default public ChannelFuture shutdownInput() {
        return this.shutdownInput(this.newPromise());
    }

    @Override
    default public ChannelFuture shutdownInput(ChannelPromise promise) {
        return this.shutdownInput(0, promise);
    }

    @Override
    default public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }

    @Override
    default public ChannelFuture shutdown() {
        return this.shutdown(this.newPromise());
    }

    default public ChannelFuture shutdown(int error) {
        return this.shutdown(error, this.newPromise());
    }

    public ChannelFuture shutdown(int var1, ChannelPromise var2);

    default public ChannelFuture shutdownInput(int error) {
        return this.shutdownInput(error, this.newPromise());
    }

    public ChannelFuture shutdownInput(int var1, ChannelPromise var2);

    default public ChannelFuture shutdownOutput(int error) {
        return this.shutdownOutput(error, this.newPromise());
    }

    public ChannelFuture shutdownOutput(int var1, ChannelPromise var2);

    @Override
    public QuicStreamAddress localAddress();

    @Override
    public QuicStreamAddress remoteAddress();

    public boolean isLocalCreated();

    public QuicStreamType type();

    public long streamId();

    @Nullable
    public QuicStreamPriority priority();

    default public ChannelFuture updatePriority(QuicStreamPriority priority) {
        return this.updatePriority(priority, this.newPromise());
    }

    public ChannelFuture updatePriority(QuicStreamPriority var1, ChannelPromise var2);

    @Override
    public QuicChannel parent();

    @Override
    public QuicStreamChannel read();

    @Override
    public QuicStreamChannel flush();

    @Override
    public QuicStreamChannelConfig config();
}

