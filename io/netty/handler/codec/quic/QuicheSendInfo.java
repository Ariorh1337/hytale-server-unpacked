/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.Quiche;
import io.netty.handler.codec.quic.SockaddrIn;
import io.netty.util.concurrent.FastThreadLocal;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;

final class QuicheSendInfo {
    private static final FastThreadLocal<byte[]> IPV4_ARRAYS = new FastThreadLocal<byte[]>(){

        @Override
        protected byte[] initialValue() {
            return new byte[4];
        }
    };
    private static final FastThreadLocal<byte[]> IPV6_ARRAYS = new FastThreadLocal<byte[]>(){

        @Override
        protected byte[] initialValue() {
            return new byte[16];
        }
    };
    private static final byte[] TIMESPEC_ZEROOUT = new byte[Quiche.SIZEOF_TIMESPEC];

    private QuicheSendInfo() {
    }

    @Nullable
    static InetSocketAddress getToAddress(ByteBuffer memory) {
        return QuicheSendInfo.getAddress(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO_LEN, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO);
    }

    @Nullable
    static InetSocketAddress getFromAddress(ByteBuffer memory) {
        return QuicheSendInfo.getAddress(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_FROM_LEN, Quiche.QUICHE_SEND_INFO_OFFSETOF_FROM);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private static InetSocketAddress getAddress(ByteBuffer memory, int lenOffset, int addressOffset) {
        int position = memory.position();
        try {
            long len = QuicheSendInfo.getLen(memory, position + lenOffset);
            memory.position(position + addressOffset);
            if (len == (long)Quiche.SIZEOF_SOCKADDR_IN) {
                InetSocketAddress inetSocketAddress = SockaddrIn.getIPv4(memory, IPV4_ARRAYS.get());
                return inetSocketAddress;
            }
            assert (len == (long)Quiche.SIZEOF_SOCKADDR_IN6);
            InetSocketAddress inetSocketAddress = SockaddrIn.getIPv6(memory, IPV6_ARRAYS.get(), IPV4_ARRAYS.get());
            return inetSocketAddress;
        }
        finally {
            memory.position(position);
        }
    }

    private static long getLen(ByteBuffer memory, int index) {
        return Quiche.getPrimitiveValue(memory, index, Quiche.SIZEOF_SOCKLEN_T);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void setSendInfo(ByteBuffer memory, InetSocketAddress from, InetSocketAddress to) {
        int position = memory.position();
        try {
            QuicheSendInfo.setAddress(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_FROM, Quiche.QUICHE_SEND_INFO_OFFSETOF_FROM_LEN, from);
            QuicheSendInfo.setAddress(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO_LEN, to);
            memory.position(position + Quiche.QUICHE_SEND_INFO_OFFSETOF_AT);
            memory.put(TIMESPEC_ZEROOUT);
        }
        finally {
            memory.position(position);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void setAddress(ByteBuffer memory, int addrOffset, int lenOffset, InetSocketAddress addr) {
        int position = memory.position();
        try {
            memory.position(position + addrOffset);
            int len = SockaddrIn.setAddress(memory, addr);
            Quiche.setPrimitiveValue(memory, position + lenOffset, Quiche.SIZEOF_SOCKLEN_T, len);
        }
        finally {
            memory.position(position);
        }
    }

    static long getAtNanos(ByteBuffer memory) {
        long sec = Quiche.getPrimitiveValue(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_AT + Quiche.TIMESPEC_OFFSETOF_TV_SEC, Quiche.SIZEOF_TIME_T);
        long nsec = Quiche.getPrimitiveValue(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_AT + Quiche.TIMESPEC_OFFSETOF_TV_SEC, Quiche.SIZEOF_LONG);
        return TimeUnit.SECONDS.toNanos(sec) + nsec;
    }

    static boolean isSameAddress(ByteBuffer memory, ByteBuffer memory2) {
        return Quiche.isSameAddress(memory, memory2, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO);
    }
}

