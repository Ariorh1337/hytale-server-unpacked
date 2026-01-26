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
import java.util.List;
import javax.annotation.Nonnull;

public class PacketDecoder
extends ByteToMessageDecoder {
    private static final int LENGTH_PREFIX_SIZE = 4;
    private static final int PACKET_ID_SIZE = 4;
    private static final int MIN_FRAME_SIZE = 8;

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

