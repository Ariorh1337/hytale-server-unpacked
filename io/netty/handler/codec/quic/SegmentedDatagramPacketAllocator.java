/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;

@FunctionalInterface
public interface SegmentedDatagramPacketAllocator {
    public static final SegmentedDatagramPacketAllocator NONE = new SegmentedDatagramPacketAllocator(){

        @Override
        public int maxNumSegments() {
            return 0;
        }

        @Override
        public DatagramPacket newPacket(ByteBuf buffer, int segmentSize, InetSocketAddress remoteAddress) {
            throw new UnsupportedOperationException();
        }
    };

    default public int maxNumSegments() {
        return 10;
    }

    public DatagramPacket newPacket(ByteBuf var1, int var2, InetSocketAddress var3);
}

