/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.io.netty;

import com.hypixel.hytale.protocol.CachedPacket;
import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.annotation.Nonnull;

@ChannelHandler.Sharable
public class PacketEncoder
extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(@Nonnull ChannelHandlerContext ctx, @Nonnull Packet packet, @Nonnull ByteBuf out) {
        Class<Object> packetClass;
        if (packet instanceof CachedPacket) {
            CachedPacket cached = (CachedPacket)packet;
            packetClass = cached.getPacketType();
        } else {
            packetClass = packet.getClass();
        }
        NetworkChannel channelAttr = ctx.channel().attr(ProtocolUtil.STREAM_CHANNEL_KEY).get();
        if (channelAttr != null && channelAttr != packet.getChannel()) {
            throw new IllegalArgumentException("Packet channel " + String.valueOf((Object)packet.getChannel()) + " does not match stream channel " + String.valueOf((Object)channelAttr));
        }
        PacketStatsRecorder statsRecorder = ctx.channel().attr(PacketStatsRecorder.CHANNEL_KEY).get();
        if (statsRecorder == null) {
            statsRecorder = PacketStatsRecorder.NOOP;
        }
        PacketIO.writeFramedPacket(packet, packetClass, out, statsRecorder);
    }
}

