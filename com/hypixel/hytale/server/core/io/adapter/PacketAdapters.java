/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.adapter;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketFilter;
import com.hypixel.hytale.server.core.io.adapter.PacketWatcher;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class PacketAdapters {
    private static final List<PacketFilter> inboundHandlers = new CopyOnWriteArrayList<PacketFilter>();
    private static final List<PacketFilter> outboundHandlers = new CopyOnWriteArrayList<PacketFilter>();

    @Nonnull
    public static PacketFilter registerInbound(@Nonnull PacketWatcher watcher) {
        PacketFilter out = (packetListener, packet) -> {
            watcher.accept(packetListener, packet);
            return false;
        };
        PacketAdapters.registerInbound(out);
        return out;
    }

    public static void registerInbound(PacketFilter predicate) {
        inboundHandlers.add(predicate);
    }

    @Nonnull
    public static PacketFilter registerOutbound(@Nonnull PacketWatcher watcher) {
        PacketFilter out = (packetListener, packet) -> {
            watcher.accept(packetListener, packet);
            return false;
        };
        PacketAdapters.registerOutbound(out);
        return out;
    }

    public static void registerOutbound(PacketFilter predicate) {
        outboundHandlers.add(predicate);
    }

    @Nonnull
    public static PacketFilter registerInbound(@Nonnull PlayerPacketFilter filter) {
        PacketFilter out = (packetHandler, client) -> packetHandler instanceof GamePacketHandler && filter.test(((GamePacketHandler)packetHandler).getPlayerRef(), client);
        PacketAdapters.registerInbound(out);
        return out;
    }

    @Nonnull
    public static PacketFilter registerOutbound(@Nonnull PlayerPacketFilter filter) {
        PacketFilter out = (packetHandler, server) -> packetHandler instanceof GamePacketHandler && filter.test(((GamePacketHandler)packetHandler).getPlayerRef(), server);
        PacketAdapters.registerOutbound(out);
        return out;
    }

    @Nonnull
    public static PacketFilter registerInbound(@Nonnull PlayerPacketWatcher watcher) {
        PacketFilter out = (packetHandler, client) -> {
            if (packetHandler instanceof GamePacketHandler) {
                watcher.accept(((GamePacketHandler)packetHandler).getPlayerRef(), client);
            }
            return false;
        };
        PacketAdapters.registerInbound(out);
        return out;
    }

    @Nonnull
    public static PacketFilter registerOutbound(@Nonnull PlayerPacketWatcher watcher) {
        PacketFilter out = (packetHandler, server) -> {
            if (packetHandler instanceof GamePacketHandler) {
                watcher.accept(((GamePacketHandler)packetHandler).getPlayerRef(), server);
            }
            return false;
        };
        PacketAdapters.registerOutbound(out);
        return out;
    }

    public static void deregisterInbound(PacketFilter predicate) {
        if (!inboundHandlers.remove(predicate)) {
            throw new IllegalArgumentException("That handler was not registered to inbound!");
        }
    }

    public static void deregisterOutbound(PacketFilter predicate) {
        if (!outboundHandlers.remove(predicate)) {
            throw new IllegalArgumentException("That handler was not registered to outbound!");
        }
    }

    public static boolean __handleInbound(PacketHandler player, Packet packet) {
        return PacketAdapters.handle(inboundHandlers, player, packet);
    }

    private static <T extends Packet> boolean handle(@Nonnull List<PacketFilter> list, PacketHandler player, T packet) {
        for (int i = 0; i < list.size(); ++i) {
            try {
                if (!list.get(i).test(player, packet)) continue;
                return true;
            }
            catch (Throwable t) {
                ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(t)).log("Failed to test packet %s against %s:", packet, (Object)player);
            }
        }
        return false;
    }

    public static boolean __handleOutbound(PacketHandler player, Packet packet) {
        return PacketAdapters.handle(outboundHandlers, player, packet);
    }
}

