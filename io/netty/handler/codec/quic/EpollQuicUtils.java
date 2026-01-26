/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.unix.SegmentedDatagramPacket;
import io.netty.handler.codec.quic.SegmentedDatagramPacketAllocator;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;

public final class EpollQuicUtils {
    private EpollQuicUtils() {
    }

    public static SegmentedDatagramPacketAllocator newSegmentedAllocator(int maxNumSegments) {
        ObjectUtil.checkInRange(maxNumSegments, 1, 64, "maxNumSegments");
        if (io.netty.channel.epoll.SegmentedDatagramPacket.isSupported()) {
            return new EpollSegmentedDatagramPacketAllocator(maxNumSegments);
        }
        return SegmentedDatagramPacketAllocator.NONE;
    }

    private static final class EpollSegmentedDatagramPacketAllocator
    implements SegmentedDatagramPacketAllocator {
        private final int maxNumSegments;

        EpollSegmentedDatagramPacketAllocator(int maxNumSegments) {
            this.maxNumSegments = maxNumSegments;
        }

        @Override
        public int maxNumSegments() {
            return this.maxNumSegments;
        }

        @Override
        public DatagramPacket newPacket(ByteBuf buffer, int segmentSize, InetSocketAddress remoteAddress) {
            return new SegmentedDatagramPacket(buffer, segmentSize, remoteAddress);
        }
    }
}

