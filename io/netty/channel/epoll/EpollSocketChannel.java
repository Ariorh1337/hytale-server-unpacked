/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.AbstractEpollStreamChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannelConfig;
import io.netty.channel.epoll.EpollTcpInfo;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.TcpMd5Util;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;

public final class EpollSocketChannel
extends AbstractEpollStreamChannel
implements SocketChannel {
    private final EpollSocketChannelConfig config;
    private volatile Collection<InetAddress> tcpMd5SigAddresses = Collections.emptyList();

    public EpollSocketChannel() {
        super(LinuxSocket.newSocketStream(), false);
        this.config = new EpollSocketChannelConfig(this);
    }

    @Deprecated
    public EpollSocketChannel(InternetProtocolFamily protocol) {
        super(LinuxSocket.newSocketStream(protocol), false);
        this.config = new EpollSocketChannelConfig(this);
    }

    public EpollSocketChannel(SocketProtocolFamily protocol) {
        super(LinuxSocket.newSocketStream(protocol), false);
        this.config = new EpollSocketChannelConfig(this);
    }

    public EpollSocketChannel(int fd) {
        super(fd);
        this.config = new EpollSocketChannelConfig(this);
    }

    EpollSocketChannel(LinuxSocket fd, boolean active) {
        super(fd, active);
        this.config = new EpollSocketChannelConfig(this);
    }

    EpollSocketChannel(Channel parent, LinuxSocket fd, InetSocketAddress remoteAddress) {
        super(parent, fd, remoteAddress);
        this.config = new EpollSocketChannelConfig(this);
        if (parent instanceof EpollServerSocketChannel) {
            this.tcpMd5SigAddresses = ((EpollServerSocketChannel)parent).tcpMd5SigAddresses();
        }
    }

    public EpollTcpInfo tcpInfo() {
        return this.tcpInfo(new EpollTcpInfo());
    }

    public EpollTcpInfo tcpInfo(EpollTcpInfo info) {
        try {
            this.socket.getTcpInfo(info);
            return info;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public EpollSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollSocketChannelUnsafe();
    }

    @Override
    boolean doConnect0(SocketAddress remote) throws Exception {
        if (Native.IS_SUPPORTING_TCP_FASTOPEN_CLIENT && this.config.isTcpFastOpenConnect()) {
            ByteBuf initialData;
            long localFlushedAmount;
            ChannelOutboundBuffer outbound = this.unsafe().outboundBuffer();
            outbound.addFlush();
            Object curr = outbound.current();
            if (curr instanceof ByteBuf && (localFlushedAmount = this.doWriteOrSendBytes(initialData = (ByteBuf)curr, (InetSocketAddress)remote, true)) > 0L) {
                outbound.removeBytes(localFlushedAmount);
                return true;
            }
        }
        return super.doConnect0(remote);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setTcpMd5Sig(Map<InetAddress, byte[]> keys) throws IOException {
        EpollSocketChannel epollSocketChannel = this;
        synchronized (epollSocketChannel) {
            this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, keys);
        }
    }

    private final class EpollSocketChannelUnsafe
    extends AbstractEpollStreamChannel.EpollStreamUnsafe {
        private EpollSocketChannelUnsafe() {
            super(EpollSocketChannel.this);
        }

        @Override
        protected Executor prepareToClose() {
            try {
                if (EpollSocketChannel.this.isOpen() && EpollSocketChannel.this.config().getSoLinger() > 0) {
                    EpollSocketChannel.this.registration().cancel();
                    return GlobalEventExecutor.INSTANCE;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return null;
        }
    }
}

