/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.ToClientPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import javax.annotation.Nonnull;

public final class CachedPacket<T extends ToClientPacket>
implements ToClientPacket,
AutoCloseable {
    private final Class<T> packetType;
    private final int packetId;
    private final NetworkChannel packetChannel;
    private final ByteBuf cachedBytes;

    private CachedPacket(Class<T> packetType, int packetId, NetworkChannel packetChannel, ByteBuf cachedBytes) {
        this.packetType = packetType;
        this.packetId = packetId;
        this.packetChannel = packetChannel;
        this.cachedBytes = cachedBytes;
    }

    public static <T extends ToClientPacket> CachedPacket<T> cache(@Nonnull T packet) {
        if (packet instanceof CachedPacket) {
            throw new IllegalArgumentException("Cannot cache a CachedPacket");
        }
        ByteBuf buf = Unpooled.buffer();
        packet.serialize(buf);
        return new CachedPacket(packet.getClass(), packet.getId(), packet.getChannel(), buf);
    }

    @Override
    public int getId() {
        return this.packetId;
    }

    @Override
    public NetworkChannel getChannel() {
        return this.packetChannel;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        if (this.cachedBytes.refCnt() <= 0) {
            throw new IllegalStateException("CachedPacket buffer was released before serialization completed");
        }
        buf.writeBytes(this.cachedBytes, this.cachedBytes.readerIndex(), this.cachedBytes.readableBytes());
    }

    @Override
    public int computeSize() {
        return this.cachedBytes.readableBytes();
    }

    public Class<T> getPacketType() {
        return this.packetType;
    }

    public int getCachedSize() {
        return this.cachedBytes.readableBytes();
    }

    @Override
    public void close() {
        if (this.cachedBytes.refCnt() > 0) {
            this.cachedBytes.release();
        }
    }
}

