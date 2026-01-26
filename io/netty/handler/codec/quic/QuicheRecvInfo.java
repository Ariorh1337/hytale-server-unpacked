/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.Quiche;
import io.netty.handler.codec.quic.SockaddrIn;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

final class QuicheRecvInfo {
    private QuicheRecvInfo() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void setRecvInfo(ByteBuffer memory, InetSocketAddress from, InetSocketAddress to) {
        int position = memory.position();
        try {
            QuicheRecvInfo.setAddress(memory, Quiche.SIZEOF_QUICHE_RECV_INFO, Quiche.QUICHE_RECV_INFO_OFFSETOF_FROM, Quiche.QUICHE_RECV_INFO_OFFSETOF_FROM_LEN, from);
            QuicheRecvInfo.setAddress(memory, Quiche.SIZEOF_QUICHE_RECV_INFO + Quiche.SIZEOF_SOCKADDR_STORAGE, Quiche.QUICHE_RECV_INFO_OFFSETOF_TO, Quiche.QUICHE_RECV_INFO_OFFSETOF_TO_LEN, to);
        }
        finally {
            memory.position(position);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void setAddress(ByteBuffer memory, int socketAddressOffset, int addrOffset, int lenOffset, InetSocketAddress address) {
        int position = memory.position();
        try {
            int sockaddrPosition = position + socketAddressOffset;
            memory.position(sockaddrPosition);
            long sockaddrMemoryAddress = Quiche.memoryAddressWithPosition(memory);
            int len = SockaddrIn.setAddress(memory, address);
            if (Quiche.SIZEOF_SIZE_T == 4) {
                memory.putInt(position + addrOffset, (int)sockaddrMemoryAddress);
            } else {
                memory.putLong(position + addrOffset, sockaddrMemoryAddress);
            }
            Quiche.setPrimitiveValue(memory, position + lenOffset, Quiche.SIZEOF_SOCKLEN_T, len);
        }
        finally {
            memory.position(position);
        }
    }
}

