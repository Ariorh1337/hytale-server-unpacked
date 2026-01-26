/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.channel.ChannelOption;
import io.netty.handler.codec.quic.QLogConfiguration;
import io.netty.handler.codec.quic.SegmentedDatagramPacketAllocator;

public final class QuicChannelOption<T>
extends ChannelOption<T> {
    public static final ChannelOption<Boolean> READ_FRAMES = QuicChannelOption.valueOf(QuicChannelOption.class, "READ_FRAMES");
    public static final ChannelOption<QLogConfiguration> QLOG = QuicChannelOption.valueOf(QuicChannelOption.class, "QLOG");
    public static final ChannelOption<SegmentedDatagramPacketAllocator> SEGMENTED_DATAGRAM_PACKET_ALLOCATOR = QuicChannelOption.valueOf(QuicChannelOption.class, "SEGMENTED_DATAGRAM_PACKET_ALLOCATOR");

    private QuicChannelOption() {
        super(null);
    }
}

