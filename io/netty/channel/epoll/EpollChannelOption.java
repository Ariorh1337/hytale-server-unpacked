/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.unix.UnixChannelOption;
import java.net.InetAddress;
import java.util.Map;

public final class EpollChannelOption<T>
extends UnixChannelOption<T> {
    public static final ChannelOption<Boolean> TCP_CORK = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_CORK");
    public static final ChannelOption<Long> TCP_NOTSENT_LOWAT = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_NOTSENT_LOWAT");
    public static final ChannelOption<Integer> TCP_KEEPIDLE = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPIDLE");
    public static final ChannelOption<Integer> TCP_KEEPINTVL = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPINTVL");
    public static final ChannelOption<Integer> TCP_KEEPCNT = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPCNT");
    public static final ChannelOption<Integer> TCP_USER_TIMEOUT = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_USER_TIMEOUT");
    public static final ChannelOption<Boolean> IP_FREEBIND = EpollChannelOption.valueOf("IP_FREEBIND");
    public static final ChannelOption<Boolean> IP_BIND_ADDRESS_NO_PORT = EpollChannelOption.valueOf("IP_BIND_ADDRESS_NO_PORT");
    public static final ChannelOption<Boolean> IP_MULTICAST_ALL = EpollChannelOption.valueOf("IP_MULTICAST_ALL");
    public static final ChannelOption<Boolean> IP_TRANSPARENT = EpollChannelOption.valueOf("IP_TRANSPARENT");
    public static final ChannelOption<Boolean> IP_RECVORIGDSTADDR = EpollChannelOption.valueOf("IP_RECVORIGDSTADDR");
    @Deprecated
    public static final ChannelOption<Integer> TCP_FASTOPEN = ChannelOption.TCP_FASTOPEN;
    @Deprecated
    public static final ChannelOption<Boolean> TCP_FASTOPEN_CONNECT = ChannelOption.TCP_FASTOPEN_CONNECT;
    public static final ChannelOption<Integer> TCP_DEFER_ACCEPT = ChannelOption.valueOf(EpollChannelOption.class, "TCP_DEFER_ACCEPT");
    public static final ChannelOption<Boolean> TCP_QUICKACK = EpollChannelOption.valueOf(EpollChannelOption.class, "TCP_QUICKACK");
    public static final ChannelOption<Integer> SO_BUSY_POLL = EpollChannelOption.valueOf(EpollChannelOption.class, "SO_BUSY_POLL");
    @Deprecated
    public static final ChannelOption<EpollMode> EPOLL_MODE = ChannelOption.valueOf(EpollChannelOption.class, "EPOLL_MODE");
    public static final ChannelOption<Map<InetAddress, byte[]>> TCP_MD5SIG = EpollChannelOption.valueOf("TCP_MD5SIG");
    public static final ChannelOption<Integer> MAX_DATAGRAM_PAYLOAD_SIZE = EpollChannelOption.valueOf("MAX_DATAGRAM_PAYLOAD_SIZE");
    public static final ChannelOption<Boolean> UDP_GRO = EpollChannelOption.valueOf("UDP_GRO");

    private EpollChannelOption() {
    }
}

