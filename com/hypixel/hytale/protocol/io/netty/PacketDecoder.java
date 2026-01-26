/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.io.netty;

import com.hypixel.hytale.protocol.PacketRegistry;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class PacketDecoder
extends ByteToMessageDecoder {
    private static final int LENGTH_PREFIX_SIZE = 4;
    private static final int PACKET_ID_SIZE = 4;
    private static final int MIN_FRAME_SIZE = 8;
    private static final long CHECK_INTERVAL_MS = 1000L;
    private volatile long lastPacketTimeNanos;
    private ScheduledFuture<?> timeoutCheckFuture;

    @Override
    public void handlerAdded(@Nonnull ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            this.initialize(ctx);
        }
        super.handlerAdded(ctx);
    }

    @Override
    public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        this.initialize(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        this.cancelTimeoutCheck();
        super.channelInactive(ctx);
    }

    private void initialize(@Nonnull ChannelHandlerContext ctx) {
        if (this.timeoutCheckFuture != null) {
            return;
        }
        this.lastPacketTimeNanos = System.nanoTime();
        this.timeoutCheckFuture = ctx.executor().scheduleAtFixedRate(() -> this.checkTimeout(ctx), 1000L, 1000L, TimeUnit.MILLISECONDS);
    }

    private void cancelTimeoutCheck() {
        if (this.timeoutCheckFuture != null) {
            this.timeoutCheckFuture.cancel(false);
            this.timeoutCheckFuture = null;
        }
    }

    private void checkTimeout(@Nonnull ChannelHandlerContext ctx) {
        if (!ctx.channel().isActive()) {
            this.cancelTimeoutCheck();
            return;
        }
        Duration timeout = ctx.channel().attr(ProtocolUtil.PACKET_TIMEOUT_KEY).get();
        if (timeout == null) {
            return;
        }
        long elapsedNanos = System.nanoTime() - this.lastPacketTimeNanos;
        if (elapsedNanos >= timeout.toNanos()) {
            this.cancelTimeoutCheck();
            ctx.fireExceptionCaught(ReadTimeoutException.INSTANCE);
            ctx.close();
        }
    }

    @Override
    protected void decode(@Nonnull ChannelHandlerContext ctx, @Nonnull ByteBuf in, @Nonnull List<Object> out) {
        if (in.readableBytes() < 8) {
            return;
        }
        in.markReaderIndex();
        int payloadLength = in.readIntLE();
        if (payloadLength < 0 || payloadLength > 0x64000000) {
            in.skipBytes(in.readableBytes());
            ProtocolUtil.closeConnection(ctx.channel());
            return;
        }
        int packetId = in.readIntLE();
        PacketRegistry.PacketInfo packetInfo = PacketRegistry.getById(packetId);
        if (packetInfo == null) {
            in.skipBytes(in.readableBytes());
            ProtocolUtil.closeConnection(ctx.channel());
            return;
        }
        if (payloadLength > packetInfo.maxSize()) {
            in.skipBytes(in.readableBytes());
            ProtocolUtil.closeConnection(ctx.channel());
            return;
        }
        if (in.readableBytes() < payloadLength) {
            in.resetReaderIndex();
            return;
        }
        PacketStatsRecorder statsRecorder = ctx.channel().attr(PacketStatsRecorder.CHANNEL_KEY).get();
        if (statsRecorder == null) {
            statsRecorder = PacketStatsRecorder.NOOP;
        }
        try {
            out.add(PacketIO.readFramedPacketWithInfo(in, payloadLength, packetInfo, statsRecorder));
            this.lastPacketTimeNanos = System.nanoTime();
        }
        catch (ProtocolException e) {
            in.skipBytes(in.readableBytes());
            ProtocolUtil.closeConnection(ctx.channel());
        }
        catch (IndexOutOfBoundsException e) {
            in.skipBytes(in.readableBytes());
            ProtocolUtil.closeConnection(ctx.channel());
        }
    }
}

