/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.handlers;

import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.ProtocolVersion;
import com.hypixel.hytale.server.core.io.handlers.SubPacketHandler;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public abstract class GenericPacketHandler
extends PacketHandler {
    private static final Consumer<ToServerPacket> EMPTY_CONSUMER = packet -> {};
    @Nonnull
    protected final List<SubPacketHandler> packetHandlers = new ObjectArrayList<SubPacketHandler>();
    private Consumer<ToServerPacket>[] handlers = GenericPacketHandler.newHandlerArray(0);

    @Nonnull
    public static Consumer<ToServerPacket>[] newHandlerArray(int size) {
        return new Consumer[size];
    }

    public GenericPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion) {
        super(channel, protocolVersion);
    }

    public void registerSubPacketHandler(SubPacketHandler subPacketHandler) {
        this.packetHandlers.add(subPacketHandler);
    }

    public void registerHandler(int packetId, @Nonnull Consumer<ToServerPacket> handler) {
        if (packetId >= this.handlers.length) {
            Consumer<ToServerPacket>[] newHandlers = GenericPacketHandler.newHandlerArray(packetId + 1);
            System.arraycopy(this.handlers, 0, newHandlers, 0, this.handlers.length);
            this.handlers = newHandlers;
        }
        this.handlers[packetId] = handler;
    }

    public void registerNoOpHandlers(int ... packetIds) {
        for (int packetId : packetIds) {
            this.registerHandler(packetId, EMPTY_CONSUMER);
        }
    }

    @Override
    public final void accept(@Nonnull ToServerPacket packet) {
        Consumer<ToServerPacket> handler;
        int packetId = packet.getId();
        Consumer<ToServerPacket> consumer = handler = this.handlers.length > packetId ? this.handlers[packetId] : null;
        if (handler != null) {
            try {
                handler.accept(packet);
            }
            catch (Throwable e) {
                throw new RuntimeException("Could not handle packet (" + packetId + "): " + String.valueOf(packet), e);
            }
        } else {
            throw new RuntimeException("No handler is registered for (" + packetId + "): " + String.valueOf(packet));
        }
    }
}

