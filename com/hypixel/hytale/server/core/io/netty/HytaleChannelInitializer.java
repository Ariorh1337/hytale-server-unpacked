/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.netty;

import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.io.netty.PacketDecoder;
import com.hypixel.hytale.protocol.io.netty.PacketEncoder;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.io.PacketStatsRecorderImpl;
import com.hypixel.hytale.server.core.io.handlers.InitialPacketHandler;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import com.hypixel.hytale.server.core.io.netty.PlayerChannelHandler;
import com.hypixel.hytale.server.core.io.netty.RateLimitHandler;
import com.hypixel.hytale.server.core.io.transport.QUICTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.TimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class HytaleChannelInitializer
extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) {
        if (channel instanceof QuicStreamChannel) {
            QuicStreamChannel quicStreamChannel = (QuicStreamChannel)channel;
            HytaleLogger.getLogger().at(Level.INFO).log("Received stream %s to %s", (Object)NettyUtil.formatRemoteAddress(channel), (Object)NettyUtil.formatLocalAddress(channel));
            QuicChannel parentChannel = quicStreamChannel.parent();
            Integer rejectErrorCode = parentChannel.attr(QUICTransport.ALPN_REJECT_ERROR_CODE_ATTR).get();
            if (rejectErrorCode != null) {
                HytaleLogger.getLogger().at(Level.INFO).log("Rejecting stream from %s: client outdated (ALPN mismatch)", NettyUtil.formatRemoteAddress(channel));
                channel.config().setAutoRead(false);
                channel.pipeline().addLast("packetEncoder", (ChannelHandler)new PacketEncoder());
                channel.writeAndFlush(new Disconnect("Your game client needs to be updated.", DisconnectType.Disconnect)).addListener(future -> channel.eventLoop().schedule(() -> ProtocolUtil.closeApplicationConnection(channel, rejectErrorCode), 100L, TimeUnit.MILLISECONDS));
                return;
            }
            X509Certificate clientCert = parentChannel.attr(QUICTransport.CLIENT_CERTIFICATE_ATTR).get();
            if (clientCert != null) {
                channel.attr(QUICTransport.CLIENT_CERTIFICATE_ATTR).set(clientCert);
                HytaleLogger.getLogger().at(Level.FINE).log("Copied client certificate to stream: %s", clientCert.getSubjectX500Principal().getName());
            }
        } else {
            HytaleLogger.getLogger().at(Level.INFO).log("Received connection from %s to %s", (Object)NettyUtil.formatRemoteAddress(channel), (Object)NettyUtil.formatLocalAddress(channel));
        }
        PacketStatsRecorderImpl statsRecorder = new PacketStatsRecorderImpl();
        channel.attr(PacketStatsRecorder.CHANNEL_KEY).set(statsRecorder);
        Duration initialTimeout = HytaleServer.get().getConfig().getConnectionTimeouts().getInitial();
        channel.attr(ProtocolUtil.PACKET_TIMEOUT_KEY).set(initialTimeout);
        channel.pipeline().addLast("packetDecoder", (ChannelHandler)new PacketDecoder());
        HytaleServerConfig.RateLimitConfig rateLimitConfig = HytaleServer.get().getConfig().getRateLimitConfig();
        if (rateLimitConfig.isEnabled()) {
            channel.pipeline().addLast("rateLimit", (ChannelHandler)new RateLimitHandler(rateLimitConfig.getBurstCapacity(), rateLimitConfig.getPacketsPerSecond()));
        }
        channel.pipeline().addLast("packetEncoder", (ChannelHandler)new PacketEncoder());
        channel.pipeline().addLast("packetArrayEncoder", (ChannelHandler)NettyUtil.PACKET_ARRAY_ENCODER_INSTANCE);
        if (NettyUtil.PACKET_LOGGER.getLevel() != Level.OFF) {
            channel.pipeline().addLast("logger", (ChannelHandler)NettyUtil.LOGGER);
        }
        InitialPacketHandler playerConnection = new InitialPacketHandler(channel);
        channel.pipeline().addLast("handler", (ChannelHandler)new PlayerChannelHandler(playerConnection));
        channel.pipeline().addLast(new ExceptionHandler());
        playerConnection.registered(null);
    }

    @Override
    public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, Throwable cause) {
        ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.WARNING).withCause(cause)).log("Got exception from netty pipeline in HytaleChannelInitializer!");
        if (ctx.channel().isWritable()) {
            ctx.channel().writeAndFlush(new Disconnect("Internal server error!", DisconnectType.Crash)).addListener((GenericFutureListener)ProtocolUtil.CLOSE_ON_COMPLETE);
        } else {
            ProtocolUtil.closeApplicationConnection(ctx.channel());
        }
    }

    @Override
    public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        ProtocolUtil.closeApplicationConnection(ctx.channel());
        super.channelInactive(ctx);
    }

    private static class ExceptionHandler
    extends ChannelInboundHandlerAdapter {
        private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
        private final AtomicBoolean handled = new AtomicBoolean();

        private ExceptionHandler() {
        }

        @Override
        public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, Throwable cause) {
            if (cause instanceof ClosedChannelException) {
                return;
            }
            ChannelHandler handler = ctx.pipeline().get("handler");
            String identifier = handler instanceof PlayerChannelHandler ? ((PlayerChannelHandler)handler).getHandler().getIdentifier() : NettyUtil.formatRemoteAddress(ctx.channel());
            if (this.handled.getAndSet(true)) {
                if (cause instanceof IOException && cause.getMessage() != null) {
                    switch (cause.getMessage()) {
                        case "Broken pipe": 
                        case "Connection reset by peer": 
                        case "An existing connection was forcibly closed by the remote host": {
                            return;
                        }
                    }
                }
                ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(cause)).log("Already handled exception in ExceptionHandler but got another!");
                return;
            }
            if (cause instanceof TimeoutException) {
                this.handleTimeout(ctx, cause, identifier);
                return;
            }
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(cause)).log("Got exception from netty pipeline in ExceptionHandler: %s", cause.getMessage());
            this.gracefulDisconnect(ctx, identifier, "Internal server error!");
        }

        private void handleTimeout(@Nonnull ChannelHandlerContext ctx, Throwable cause, String identifier) {
            boolean readTimeout = cause instanceof ReadTimeoutException;
            boolean writeTimeout = cause instanceof WriteTimeoutException;
            String timeoutType = readTimeout ? "Read" : (writeTimeout ? "Write" : "Connection");
            NettyUtil.TimeoutContext context = ctx.channel().attr(NettyUtil.TimeoutContext.KEY).get();
            String stage = context != null ? context.stage() : "unknown";
            String duration = context != null ? FormatUtil.nanosToString(System.nanoTime() - context.connectionStartNs()) : "unknown";
            LOGGER.at(Level.INFO).log("%s timeout for %s at stage '%s' after %s connected", timeoutType, identifier, stage, duration);
            ((HytaleLogger.Api)NettyUtil.CONNECTION_EXCEPTION_LOGGER.at(Level.FINE).withCause(cause)).log("%s timeout for %s at stage '%s' after %s connected", timeoutType, identifier, stage, duration);
            this.gracefulDisconnect(ctx, identifier, timeoutType + " timeout");
        }

        private void gracefulDisconnect(@Nonnull ChannelHandlerContext ctx, String identifier, String reason) {
            Channel channel = ctx.channel();
            if (channel.isWritable()) {
                channel.writeAndFlush(new Disconnect(reason, DisconnectType.Disconnect)).addListener(future -> ProtocolUtil.closeApplicationConnection(channel, 4));
                channel.eventLoop().schedule(() -> {
                    if (channel.isOpen()) {
                        LOGGER.at(Level.FINE).log("Force closing %s after graceful disconnect attempt", identifier);
                        ProtocolUtil.closeApplicationConnection(channel, 4);
                    }
                }, 1L, TimeUnit.SECONDS);
            } else {
                ProtocolUtil.closeApplicationConnection(channel, 4);
            }
        }
    }
}

