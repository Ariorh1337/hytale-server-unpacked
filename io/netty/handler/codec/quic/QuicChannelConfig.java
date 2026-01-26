/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

public interface QuicChannelConfig
extends ChannelConfig {
    @Override
    @Deprecated
    public QuicChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public QuicChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public QuicChannelConfig setWriteSpinCount(int var1);

    @Override
    public QuicChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public QuicChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public QuicChannelConfig setAutoRead(boolean var1);

    @Override
    public QuicChannelConfig setAutoClose(boolean var1);

    @Override
    public QuicChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public QuicChannelConfig setWriteBufferLowWaterMark(int var1);

    @Override
    public QuicChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

    @Override
    public QuicChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

