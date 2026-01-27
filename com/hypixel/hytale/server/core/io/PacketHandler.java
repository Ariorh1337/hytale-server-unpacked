/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io;

import com.google.common.flogger.LazyArgs;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.common.util.NetworkUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.metrics.metric.Metric;
import com.hypixel.hytale.protocol.CachedPacket;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
import com.hypixel.hytale.protocol.packets.connection.Ping;
import com.hypixel.hytale.protocol.packets.connection.Pong;
import com.hypixel.hytale.protocol.packets.connection.PongType;
import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
import com.hypixel.hytale.server.core.io.ProtocolVersion;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.handlers.login.AuthenticationPacketHandler;
import com.hypixel.hytale.server.core.io.handlers.login.PasswordPacketHandler;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.receiver.IPacketReceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongPriorityQueue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PacketHandler
implements IPacketReceiver {
    public static final int MAX_PACKET_ID = 512;
    private static final HytaleLogger LOGIN_TIMING_LOGGER = HytaleLogger.get("LoginTiming");
    private static final AttributeKey<Long> LOGIN_START_ATTRIBUTE_KEY;
    @Nonnull
    protected final Channel channel;
    @Nonnull
    protected final ProtocolVersion protocolVersion;
    @Nullable
    protected PlayerAuthentication auth;
    protected boolean queuePackets;
    protected final AtomicInteger queuedPackets = new AtomicInteger();
    protected final SecureRandom pingIdRandom = new SecureRandom();
    @Nonnull
    protected final PingInfo[] pingInfo;
    private float pingTimer;
    protected boolean registered;
    private ScheduledFuture<?> timeoutTask;
    @Nullable
    protected Throwable clientReadyForChunksFutureStack;
    @Nullable
    protected CompletableFuture<Void> clientReadyForChunksFuture;
    @Nonnull
    protected final DisconnectReason disconnectReason = new DisconnectReason();

    public PacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion) {
        this.channel = channel;
        this.protocolVersion = protocolVersion;
        this.pingInfo = new PingInfo[PongType.VALUES.length];
        for (PongType pingType : PongType.VALUES) {
            this.pingInfo[pingType.ordinal()] = new PingInfo(pingType);
        }
    }

    @Nonnull
    public Channel getChannel() {
        return this.channel;
    }

    @Deprecated(forRemoval=true)
    public void setCompressionEnabled(boolean compressionEnabled) {
        HytaleLogger.getLogger().at(Level.INFO).log(this.getIdentifier() + " compression now handled by encoder");
    }

    @Deprecated(forRemoval=true)
    public boolean isCompressionEnabled() {
        return true;
    }

    @Nonnull
    public abstract String getIdentifier();

    @Nonnull
    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }

    public final void registered(@Nullable PacketHandler oldHandler) {
        this.registered = true;
        this.registered0(oldHandler);
    }

    protected void registered0(@Nullable PacketHandler oldHandler) {
    }

    public final void unregistered(@Nullable PacketHandler newHandler) {
        this.registered = false;
        this.clearTimeout();
        this.unregistered0(newHandler);
    }

    protected void unregistered0(@Nullable PacketHandler newHandler) {
    }

    public void handle(@Nonnull Packet packet) {
        this.accept(packet);
    }

    public abstract void accept(@Nonnull Packet var1);

    public void logCloseMessage() {
        HytaleLogger.getLogger().at(Level.INFO).log("%s was closed.", this.getIdentifier());
    }

    public void closed(ChannelHandlerContext ctx) {
        this.clearTimeout();
    }

    public void setQueuePackets(boolean queuePackets) {
        this.queuePackets = queuePackets;
    }

    public void tryFlush() {
        if (this.queuedPackets.getAndSet(0) > 0) {
            this.channel.flush();
        }
    }

    public void write(Packet ... packets) {
        Packet[] cachedPackets = new Packet[packets.length];
        this.handleOutboundAndCachePackets(packets, cachedPackets);
        if (this.queuePackets) {
            this.channel.write(cachedPackets, this.channel.voidPromise());
            this.queuedPackets.getAndIncrement();
        } else {
            this.channel.writeAndFlush(cachedPackets, this.channel.voidPromise());
        }
    }

    public void write(@Nonnull Packet[] packets, @Nonnull Packet finalPacket) {
        Packet[] cachedPackets = new Packet[packets.length + 1];
        this.handleOutboundAndCachePackets(packets, cachedPackets);
        cachedPackets[cachedPackets.length - 1] = this.handleOutboundAndCachePacket(finalPacket);
        if (this.queuePackets) {
            this.channel.write(cachedPackets, this.channel.voidPromise());
            this.queuedPackets.getAndIncrement();
        } else {
            this.channel.writeAndFlush(cachedPackets, this.channel.voidPromise());
        }
    }

    @Override
    public void write(@Nonnull Packet packet) {
        this.writePacket(packet, true);
    }

    @Override
    public void writeNoCache(@Nonnull Packet packet) {
        this.writePacket(packet, false);
    }

    public void writePacket(@Nonnull Packet packet, boolean cache) {
        if (PacketAdapters.__handleOutbound(this, packet)) {
            return;
        }
        Packet toSend = cache ? this.handleOutboundAndCachePacket(packet) : packet;
        if (this.queuePackets) {
            this.channel.write(toSend, this.channel.voidPromise());
            this.queuedPackets.getAndIncrement();
        } else {
            this.channel.writeAndFlush(toSend, this.channel.voidPromise());
        }
    }

    private void handleOutboundAndCachePackets(@Nonnull Packet[] packets, @Nonnull Packet[] cachedPackets) {
        for (int i = 0; i < packets.length; ++i) {
            Packet packet = packets[i];
            if (PacketAdapters.__handleOutbound(this, packet)) continue;
            cachedPackets[i] = this.handleOutboundAndCachePacket(packet);
        }
    }

    @Nonnull
    private Packet handleOutboundAndCachePacket(@Nonnull Packet packet) {
        if (packet instanceof CachedPacket) {
            return packet;
        }
        return CachedPacket.cache(packet);
    }

    public void disconnect(@Nonnull String message) {
        this.disconnectReason.setServerDisconnectReason(message);
        HytaleLogger.getLogger().at(Level.INFO).log("Disconnecting %s with the message: %s", (Object)NettyUtil.formatRemoteAddress(this.channel), (Object)message);
        this.disconnect0(message);
    }

    protected void disconnect0(@Nonnull String message) {
        this.channel.writeAndFlush(new Disconnect(message, DisconnectType.Disconnect)).addListener((GenericFutureListener)ProtocolUtil.CLOSE_ON_COMPLETE);
    }

    @Nullable
    public PacketStatsRecorder getPacketStatsRecorder() {
        return this.channel.attr(PacketStatsRecorder.CHANNEL_KEY).get();
    }

    @Nonnull
    public PingInfo getPingInfo(@Nonnull PongType pongType) {
        return this.pingInfo[pongType.ordinal()];
    }

    public long getOperationTimeoutThreshold() {
        double average = this.getPingInfo(PongType.Tick).getPingMetricSet().getAverage(0);
        return PingInfo.TIME_UNIT.toMillis(Math.round(average * 2.0)) + 3000L;
    }

    public void tickPing(float dt) {
        this.pingTimer -= dt;
        if (this.pingTimer <= 0.0f) {
            this.pingTimer = 1.0f;
            this.sendPing();
        }
    }

    public void sendPing() {
        int id = this.pingIdRandom.nextInt();
        Instant nowInstant = Instant.now();
        long nowTimestamp = System.nanoTime();
        for (PingInfo info : this.pingInfo) {
            info.recordSent(id, nowTimestamp);
        }
        this.writeNoCache(new Ping(id, WorldTimeResource.instantToInstantData(nowInstant), (int)this.getPingInfo(PongType.Raw).getPingMetricSet().getLastValue(), (int)this.getPingInfo(PongType.Direct).getPingMetricSet().getLastValue(), (int)this.getPingInfo(PongType.Tick).getPingMetricSet().getLastValue()));
    }

    public void handlePong(@Nonnull Pong packet) {
        this.pingInfo[packet.type.ordinal()].handlePacket(packet);
    }

    protected void initStage(@Nonnull String stage, @Nonnull Duration timeout, @Nonnull BooleanSupplier condition) {
        NettyUtil.TimeoutContext.init(this.channel, stage, this.getIdentifier());
        this.setStageTimeout(stage, timeout, condition);
    }

    protected void enterStage(@Nonnull String stage, @Nonnull Duration timeout, @Nonnull BooleanSupplier condition) {
        NettyUtil.TimeoutContext.update(this.channel, stage, this.getIdentifier());
        this.updatePacketTimeout(timeout);
        this.setStageTimeout(stage, timeout, condition);
    }

    protected void enterStage(@Nonnull String stage, @Nonnull Duration timeout) {
        NettyUtil.TimeoutContext.update(this.channel, stage, this.getIdentifier());
        this.updatePacketTimeout(timeout);
    }

    protected void continueStage(@Nonnull String stage, @Nonnull Duration timeout, @Nonnull BooleanSupplier condition) {
        NettyUtil.TimeoutContext.update(this.channel, stage);
        this.updatePacketTimeout(timeout);
        this.setStageTimeout(stage, timeout, condition);
    }

    private void setStageTimeout(@Nonnull String stageId, @Nonnull Duration timeout, @Nonnull BooleanSupplier meets) {
        if (this.timeoutTask != null) {
            this.timeoutTask.cancel(false);
        }
        if (!(this instanceof AuthenticationPacketHandler) && this instanceof PasswordPacketHandler && this.auth == null) {
            return;
        }
        PacketHandler.logConnectionTimings(this.channel, "Entering stage '" + stageId + "'", Level.FINEST);
        long timeoutMillis = timeout.toMillis();
        this.timeoutTask = this.channel.eventLoop().schedule(() -> {
            if (!this.channel.isOpen()) {
                return;
            }
            if (!meets.getAsBoolean()) {
                NettyUtil.TimeoutContext context = this.channel.attr(NettyUtil.TimeoutContext.KEY).get();
                String duration = context != null ? FormatUtil.nanosToString(System.nanoTime() - context.connectionStartNs()) : "unknown";
                HytaleLogger.getLogger().at(Level.WARNING).log("Stage timeout for %s at stage '%s' after %s connected", this.getIdentifier(), stageId, duration);
                this.disconnect("Either you took too long to login or we took too long to process your request! Retry again in a moment.");
            }
        }, timeoutMillis, TimeUnit.MILLISECONDS);
    }

    private void updatePacketTimeout(@Nonnull Duration timeout) {
        this.channel.attr(ProtocolUtil.PACKET_TIMEOUT_KEY).set(timeout);
    }

    protected void clearTimeout() {
        if (this.timeoutTask != null) {
            this.timeoutTask.cancel(false);
        }
        if (this.clientReadyForChunksFuture != null) {
            this.clientReadyForChunksFuture.cancel(true);
            this.clientReadyForChunksFuture = null;
            this.clientReadyForChunksFutureStack = null;
        }
    }

    @Nullable
    public PlayerAuthentication getAuth() {
        return this.auth;
    }

    public boolean stillActive() {
        return this.channel.isActive();
    }

    public int getQueuedPacketsCount() {
        return this.queuedPackets.get();
    }

    public boolean isLocalConnection() {
        SocketAddress socketAddress;
        Channel channel = this.channel;
        if (channel instanceof QuicStreamChannel) {
            QuicStreamChannel quicStreamChannel = (QuicStreamChannel)channel;
            socketAddress = quicStreamChannel.parent().remoteSocketAddress();
        } else {
            socketAddress = this.channel.remoteAddress();
        }
        if (socketAddress instanceof InetSocketAddress) {
            InetAddress address = ((InetSocketAddress)socketAddress).getAddress();
            return NetworkUtil.addressMatchesAny(address, NetworkUtil.AddressType.ANY_LOCAL, NetworkUtil.AddressType.LOOPBACK);
        }
        return socketAddress instanceof DomainSocketAddress || socketAddress instanceof LocalAddress;
    }

    public boolean isLANConnection() {
        SocketAddress socketAddress;
        Channel channel = this.channel;
        if (channel instanceof QuicStreamChannel) {
            QuicStreamChannel quicStreamChannel = (QuicStreamChannel)channel;
            socketAddress = quicStreamChannel.parent().remoteSocketAddress();
        } else {
            socketAddress = this.channel.remoteAddress();
        }
        if (socketAddress instanceof InetSocketAddress) {
            InetAddress address = ((InetSocketAddress)socketAddress).getAddress();
            return NetworkUtil.addressMatchesAny(address);
        }
        return socketAddress instanceof DomainSocketAddress || socketAddress instanceof LocalAddress;
    }

    @Nonnull
    public DisconnectReason getDisconnectReason() {
        return this.disconnectReason;
    }

    public void setClientReadyForChunksFuture(@Nonnull CompletableFuture<Void> clientReadyFuture) {
        if (this.clientReadyForChunksFuture != null) {
            throw new IllegalStateException("Tried to hook client ready but something is already waiting for it!", this.clientReadyForChunksFutureStack);
        }
        HytaleLogger.getLogger().at(Level.WARNING).log("%s Added future for ClientReady packet?", this.getIdentifier());
        this.clientReadyForChunksFutureStack = new Throwable();
        this.clientReadyForChunksFuture = clientReadyFuture;
    }

    @Nullable
    public CompletableFuture<Void> getClientReadyForChunksFuture() {
        return this.clientReadyForChunksFuture;
    }

    public static void logConnectionTimings(@Nonnull Channel channel, @Nonnull String message, @Nonnull Level level) {
        String identifier;
        Attribute<Long> loginStartAttribute = channel.attr(LOGIN_START_ATTRIBUTE_KEY);
        long now = System.nanoTime();
        Long before = loginStartAttribute.getAndSet(now);
        NettyUtil.TimeoutContext context = channel.attr(NettyUtil.TimeoutContext.KEY).get();
        String string = identifier = context != null ? context.playerIdentifier() : NettyUtil.formatRemoteAddress(channel);
        if (before == null) {
            LOGIN_TIMING_LOGGER.at(level).log("[%s] %s", (Object)identifier, (Object)message);
        } else {
            LOGIN_TIMING_LOGGER.at(level).log("[%s] %s took %s", identifier, message, LazyArgs.lazy(() -> FormatUtil.nanosToString(now - before)));
        }
    }

    static {
        LOGIN_TIMING_LOGGER.setLevel(Level.ALL);
        LOGIN_START_ATTRIBUTE_KEY = AttributeKey.newInstance("LOGIN_START");
    }

    public static class DisconnectReason {
        @Nullable
        private String serverDisconnectReason;
        @Nullable
        private DisconnectType clientDisconnectType;

        protected DisconnectReason() {
        }

        @Nullable
        public String getServerDisconnectReason() {
            return this.serverDisconnectReason;
        }

        public void setServerDisconnectReason(String serverDisconnectReason) {
            this.serverDisconnectReason = serverDisconnectReason;
            this.clientDisconnectType = null;
        }

        @Nullable
        public DisconnectType getClientDisconnectType() {
            return this.clientDisconnectType;
        }

        public void setClientDisconnectType(DisconnectType clientDisconnectType) {
            this.clientDisconnectType = clientDisconnectType;
            this.serverDisconnectReason = null;
        }

        @Nonnull
        public String toString() {
            return "DisconnectReason{serverDisconnectReason='" + this.serverDisconnectReason + "', clientDisconnectType=" + String.valueOf((Object)this.clientDisconnectType) + "}";
        }
    }

    public static class PingInfo {
        public static final MetricsRegistry<PingInfo> METRICS_REGISTRY = new MetricsRegistry<PingInfo>().register("PingType", pingInfo -> pingInfo.pingType, new EnumCodec<PongType>(PongType.class)).register("PingMetrics", PingInfo::getPingMetricSet, HistoricMetric.METRICS_CODEC).register("PacketQueueMin", pingInfo -> pingInfo.packetQueueMetric.getMin(), Codec.LONG).register("PacketQueueAvg", pingInfo -> pingInfo.packetQueueMetric.getAverage(), Codec.DOUBLE).register("PacketQueueMax", pingInfo -> pingInfo.packetQueueMetric.getMax(), Codec.LONG);
        public static final TimeUnit TIME_UNIT = TimeUnit.MICROSECONDS;
        public static final int ONE_SECOND_INDEX = 0;
        public static final int ONE_MINUTE_INDEX = 1;
        public static final int FIVE_MINUTE_INDEX = 2;
        public static final double PERCENTILE = (double)0.99f;
        public static final int PING_FREQUENCY = 1;
        public static final TimeUnit PING_FREQUENCY_UNIT = TimeUnit.SECONDS;
        public static final int PING_FREQUENCY_MILLIS = 1000;
        public static final int PING_HISTORY_MILLIS = 15000;
        public static final int PING_HISTORY_LENGTH = 15;
        protected final PongType pingType;
        protected final Lock queueLock = new ReentrantLock();
        protected final IntPriorityQueue pingIdQueue = new IntArrayFIFOQueue(15);
        protected final LongPriorityQueue pingTimestampQueue = new LongArrayFIFOQueue(15);
        protected final Lock pingLock = new ReentrantLock();
        @Nonnull
        protected final HistoricMetric pingMetricSet;
        protected final Metric packetQueueMetric = new Metric();

        public PingInfo(PongType pingType) {
            this.pingType = pingType;
            this.pingMetricSet = HistoricMetric.builder(1000L, TimeUnit.MILLISECONDS).addPeriod(1L, TimeUnit.SECONDS).addPeriod(1L, TimeUnit.MINUTES).addPeriod(5L, TimeUnit.MINUTES).build();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void recordSent(int id, long timestamp) {
            this.queueLock.lock();
            try {
                this.pingIdQueue.enqueue(id);
                this.pingTimestampQueue.enqueue(timestamp);
            }
            finally {
                this.queueLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void handlePacket(@Nonnull Pong packet) {
            long sentTimestamp;
            int nextIdToHandle;
            if (packet.type != this.pingType) {
                throw new IllegalArgumentException("Got packet for " + String.valueOf((Object)packet.type) + " but expected " + String.valueOf((Object)this.pingType));
            }
            this.queueLock.lock();
            try {
                nextIdToHandle = this.pingIdQueue.dequeueInt();
                sentTimestamp = this.pingTimestampQueue.dequeueLong();
            }
            finally {
                this.queueLock.unlock();
            }
            if (packet.id != nextIdToHandle) {
                throw new IllegalArgumentException(String.valueOf(packet.id));
            }
            long nanoTime = System.nanoTime();
            long pingValue = nanoTime - sentTimestamp;
            if (pingValue <= 0L) {
                throw new IllegalArgumentException(String.format("Ping must be received after its sent! %s", pingValue));
            }
            this.pingLock.lock();
            try {
                this.pingMetricSet.add(nanoTime, TIME_UNIT.convert(pingValue, TimeUnit.NANOSECONDS));
                this.packetQueueMetric.add(packet.packetQueueSize);
            }
            finally {
                this.pingLock.unlock();
            }
        }

        public PongType getPingType() {
            return this.pingType;
        }

        @Nonnull
        public Metric getPacketQueueMetric() {
            return this.packetQueueMetric;
        }

        @Nonnull
        public HistoricMetric getPingMetricSet() {
            return this.pingMetricSet;
        }

        public void clear() {
            this.pingLock.lock();
            try {
                this.packetQueueMetric.clear();
                this.pingMetricSet.clear();
            }
            finally {
                this.pingLock.unlock();
            }
        }
    }
}

