/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.ServerChannel;
import io.netty.channel.ServerChannelRecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioDomainSocketChannel;
import io.netty.channel.socket.nio.NioDomainSocketUtil;
import io.netty.channel.socket.nio.SelectorProviderUtil;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NioServerDomainSocketChannel
extends AbstractNioMessageChannel
implements ServerChannel {
    private static final Method OPEN_SERVER_SOCKET_CHANNEL_WITH_FAMILY = SelectorProviderUtil.findOpenMethod("openServerSocketChannel");
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioServerDomainSocketChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    private final NioDomainServerSocketChannelConfig config;
    private volatile boolean bound;

    static ServerSocketChannel newChannel(SelectorProvider provider) {
        if (PlatformDependent.javaVersion() < 16) {
            throw new UnsupportedOperationException("Only supported with Java 16+");
        }
        try {
            ServerSocketChannel channel = (ServerSocketChannel)SelectorProviderUtil.newDomainSocketChannel(OPEN_SERVER_SOCKET_CHANNEL_WITH_FAMILY, provider);
            if (channel == null) {
                throw new ChannelException("Failed to open a socket.");
            }
            return channel;
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }

    @Override
    protected ServerSocketChannel javaChannel() {
        return (ServerSocketChannel)super.javaChannel();
    }

    public NioServerDomainSocketChannel() {
        this(DEFAULT_SELECTOR_PROVIDER);
    }

    public NioServerDomainSocketChannel(SelectorProvider provider) {
        this(NioServerDomainSocketChannel.newChannel(provider));
    }

    public NioServerDomainSocketChannel(ServerSocketChannel channel) {
        super(null, (SelectableChannel)channel, 16);
        if (PlatformDependent.javaVersion() < 16) {
            throw new UnsupportedOperationException("Only supported with Java 16+");
        }
        this.config = new NioDomainServerSocketChannelConfig(this);
        try {
            this.bound = channel.getLocalAddress() != null;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && this.bound;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.javaChannel().bind(localAddress, this.config.getBacklog());
        this.bound = true;
    }

    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = SocketUtils.accept(this.javaChannel());
        try {
            if (ch != null) {
                buf.add(new NioDomainSocketChannel((Channel)this, ch));
                return 1;
            }
        }
        catch (Throwable t) {
            logger.warn("Failed to create a new channel from an accepted socket.", t);
            try {
                ch.close();
            }
            catch (Throwable t2) {
                logger.warn("Failed to close a socket.", t2);
            }
        }
        return 0;
    }

    @Override
    protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doClose() throws Exception {
        SocketAddress local = this.localAddress();
        try {
            super.doClose();
        }
        finally {
            this.javaChannel().close();
            if (local != null) {
                NioDomainSocketUtil.deleteSocketFile(local);
            }
        }
    }

    @Override
    protected SocketAddress localAddress0() {
        try {
            return this.javaChannel().getLocalAddress();
        }
        catch (Exception exception) {
            return null;
        }
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected boolean closeOnReadError(Throwable cause) {
        return super.closeOnReadError(cause);
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    private final class NioDomainServerSocketChannelConfig
    extends DefaultChannelConfig {
        private volatile int backlog;

        private NioDomainServerSocketChannelConfig(NioServerDomainSocketChannel channel) {
            super(channel, new ServerChannelRecvByteBufAllocator());
            this.backlog = NetUtil.SOMAXCONN;
        }

        @Override
        protected void autoReadCleared() {
            NioServerDomainSocketChannel.this.clearReadPending();
        }

        @Override
        public Map<ChannelOption<?>, Object> getOptions() {
            ArrayList options = new ArrayList();
            options.add(ChannelOption.SO_BACKLOG);
            for (ChannelOption<?> opt : NioChannelOption.getOptions(this.jdkChannel())) {
                options.add(opt);
            }
            return this.getOptions(super.getOptions(), options.toArray(new ChannelOption[0]));
        }

        @Override
        public <T> T getOption(ChannelOption<T> option) {
            if (option == ChannelOption.SO_BACKLOG) {
                return (T)Integer.valueOf(this.getBacklog());
            }
            if (option instanceof NioChannelOption) {
                return NioChannelOption.getOption(this.jdkChannel(), (NioChannelOption)option);
            }
            return super.getOption(option);
        }

        @Override
        public <T> boolean setOption(ChannelOption<T> option, T value) {
            if (option != ChannelOption.SO_BACKLOG) {
                if (option instanceof NioChannelOption) {
                    return NioChannelOption.setOption(this.jdkChannel(), (NioChannelOption)option, value);
                }
                return super.setOption(option, value);
            }
            this.validate(option, value);
            this.setBacklog((Integer)value);
            return true;
        }

        private int getBacklog() {
            return this.backlog;
        }

        private NioDomainServerSocketChannelConfig setBacklog(int backlog) {
            ObjectUtil.checkPositiveOrZero(backlog, "backlog");
            this.backlog = backlog;
            return this;
        }

        private ServerSocketChannel jdkChannel() {
            return ((NioServerDomainSocketChannel)this.channel).javaChannel();
        }
    }
}

