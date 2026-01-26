/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.codec.quic.DirectIoByteBufAllocator;
import io.netty.handler.codec.quic.QuicChannelOption;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamChannelConfig;
import io.netty.handler.codec.quic.QuicStreamType;
import java.util.Map;

final class QuicheQuicStreamChannelConfig
extends DefaultChannelConfig
implements QuicStreamChannelConfig {
    private volatile boolean allowHalfClosure = true;
    private volatile boolean readFrames;
    volatile DirectIoByteBufAllocator allocator = new DirectIoByteBufAllocator(super.getAllocator());

    QuicheQuicStreamChannelConfig(QuicStreamChannel channel) {
        super(channel);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        if (this.isHalfClosureSupported()) {
            return this.getOptions(super.getOptions(), ChannelOption.ALLOW_HALF_CLOSURE, QuicChannelOption.READ_FRAMES);
        }
        return super.getOptions();
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            return (T)Boolean.valueOf(this.isAllowHalfClosure());
        }
        if (option == QuicChannelOption.READ_FRAMES) {
            return (T)Boolean.valueOf(this.isReadFrames());
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            if (this.isHalfClosureSupported()) {
                this.setAllowHalfClosure((Boolean)value);
                return true;
            }
            return false;
        }
        if (option == QuicChannelOption.READ_FRAMES) {
            this.setReadFrames((Boolean)value);
        }
        return super.setOption(option, value);
    }

    @Override
    public QuicStreamChannelConfig setReadFrames(boolean readFrames) {
        this.readFrames = readFrames;
        return this;
    }

    @Override
    public boolean isReadFrames() {
        return this.readFrames;
    }

    @Override
    public QuicStreamChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setAllocator(ByteBufAllocator allocator) {
        this.allocator = new DirectIoByteBufAllocator(allocator);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    @Override
    public QuicStreamChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
        if (!this.isHalfClosureSupported()) {
            throw new UnsupportedOperationException("Undirectional streams don't support half-closure");
        }
        this.allowHalfClosure = allowHalfClosure;
        return this;
    }

    @Override
    public ByteBufAllocator getAllocator() {
        return this.allocator.wrapped();
    }

    @Override
    public boolean isAllowHalfClosure() {
        return this.allowHalfClosure;
    }

    private boolean isHalfClosureSupported() {
        return ((QuicStreamChannel)this.channel).type() == QuicStreamType.BIDIRECTIONAL;
    }
}

