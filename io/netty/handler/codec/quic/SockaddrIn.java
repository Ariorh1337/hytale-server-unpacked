/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.Quiche;
import io.netty.util.internal.PlatformDependent;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.Nullable;

final class SockaddrIn {
    static final byte[] IPV4_MAPPED_IPV6_PREFIX = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1};
    static final int IPV4_ADDRESS_LENGTH = 4;
    static final int IPV6_ADDRESS_LENGTH = 16;
    static final byte[] SOCKADDR_IN6_EMPTY_ARRAY = new byte[Quiche.SIZEOF_SOCKADDR_IN6];
    static final byte[] SOCKADDR_IN_EMPTY_ARRAY = new byte[Quiche.SIZEOF_SOCKADDR_IN];

    private SockaddrIn() {
    }

    static int cmp(long memory, long memory2) {
        return Quiche.sockaddr_cmp(memory, memory2);
    }

    static int setAddress(ByteBuffer memory, InetSocketAddress address) {
        InetAddress addr = address.getAddress();
        return SockaddrIn.setAddress(addr instanceof Inet6Address, memory, address);
    }

    static int setAddress(boolean ipv6, ByteBuffer memory, InetSocketAddress address) {
        if (ipv6) {
            return SockaddrIn.setIPv6(memory, address.getAddress(), address.getPort());
        }
        return SockaddrIn.setIPv4(memory, address.getAddress(), address.getPort());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int setIPv4(ByteBuffer memory, InetAddress address, int port) {
        int position = memory.position();
        try {
            memory.put(SOCKADDR_IN_EMPTY_ARRAY);
            memory.putShort(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_FAMILY, Quiche.AF_INET);
            memory.putShort(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_PORT, (short)port);
            byte[] bytes = address.getAddress();
            int offset = 0;
            if (bytes.length == 16) {
                offset = IPV4_MAPPED_IPV6_PREFIX.length;
            }
            assert (bytes.length == offset + 4);
            memory.position(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_ADDR + Quiche.IN_ADDRESS_OFFSETOF_S_ADDR);
            memory.put(bytes, offset, 4);
            int n = Quiche.SIZEOF_SOCKADDR_IN;
            return n;
        }
        finally {
            memory.position(position);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int setIPv6(ByteBuffer memory, InetAddress address, int port) {
        int position = memory.position();
        try {
            memory.put(SOCKADDR_IN6_EMPTY_ARRAY);
            memory.putShort(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_FAMILY, Quiche.AF_INET6);
            memory.putShort(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_PORT, (short)port);
            byte[] bytes = address.getAddress();
            int offset = Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_ADDR + Quiche.IN6_ADDRESS_OFFSETOF_S6_ADDR;
            if (bytes.length == 4) {
                memory.position(position + offset);
                memory.put(IPV4_MAPPED_IPV6_PREFIX);
                memory.position(position + offset + IPV4_MAPPED_IPV6_PREFIX.length);
                memory.put(bytes, 0, 4);
            } else {
                memory.position(position + offset);
                memory.put(bytes, 0, 16);
                memory.putInt(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_SCOPE_ID, ((Inet6Address)address).getScopeId());
            }
            int n = Quiche.SIZEOF_SOCKADDR_IN6;
            return n;
        }
        finally {
            memory.position(position);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    static InetSocketAddress getIPv4(ByteBuffer memory, byte[] tmpArray) {
        assert (tmpArray.length == 4);
        int position = memory.position();
        try {
            int port = memory.getShort(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_PORT) & 0xFFFF;
            memory.position(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_ADDR + Quiche.IN_ADDRESS_OFFSETOF_S_ADDR);
            memory.get(tmpArray);
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByAddress(tmpArray), port);
                return inetSocketAddress;
            }
            catch (UnknownHostException ignore) {
                InetSocketAddress inetSocketAddress = null;
                memory.position(position);
                return inetSocketAddress;
            }
        }
        finally {
            memory.position(position);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Nullable
    static InetSocketAddress getIPv6(ByteBuffer memory, byte[] ipv6Array, byte[] ipv4Array) {
        assert (ipv6Array.length == 16);
        assert (ipv4Array.length == 4);
        int position = memory.position();
        try {
            int port = memory.getShort(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_PORT) & 0xFFFF;
            memory.position(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_ADDR + Quiche.IN6_ADDRESS_OFFSETOF_S6_ADDR);
            memory.get(ipv6Array);
            if (PlatformDependent.equals(ipv6Array, 0, IPV4_MAPPED_IPV6_PREFIX, 0, IPV4_MAPPED_IPV6_PREFIX.length)) {
                System.arraycopy(ipv6Array, IPV4_MAPPED_IPV6_PREFIX.length, ipv4Array, 0, 4);
                try {
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(Inet4Address.getByAddress(ipv4Array), port);
                    return inetSocketAddress;
                }
                catch (UnknownHostException ignore) {
                    InetSocketAddress inetSocketAddress = null;
                    memory.position(position);
                    return inetSocketAddress;
                }
            }
            int scopeId = memory.getInt(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_SCOPE_ID);
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(Inet6Address.getByAddress(null, ipv6Array, scopeId), port);
                return inetSocketAddress;
            }
            catch (UnknownHostException ignore) {
                InetSocketAddress inetSocketAddress = null;
                memory.position(position);
                {
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                }
                return inetSocketAddress;
            }
        }
        finally {
            memory.position(position);
        }
    }
}

