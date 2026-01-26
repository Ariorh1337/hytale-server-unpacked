/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DuplexChannelConfig;

public interface QuicStreamChannelConfig
extends DuplexChannelConfig {
    public QuicStreamChannelConfig setReadFrames(boolean var1);

    public boolean isReadFrames();

    @Override
    public QuicStreamChannelConfig setAllowHalfClosure(boolean var1);

    @Override
    public QuicStreamChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public QuicStreamChannelConfig setWriteSpinCount(int var1);

    @Override
    public QuicStreamChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public QuicStreamChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public QuicStreamChannelConfig setAutoRead(boolean var1);

    @Override
    public QuicStreamChannelConfig setAutoClose(boolean var1);

    @Override
    public QuicStreamChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

    @Override
    public QuicStreamChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

    @Override
    public QuicStreamChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public QuicStreamChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public QuicStreamChannelConfig setWriteBufferLowWaterMark(int var1);
}

