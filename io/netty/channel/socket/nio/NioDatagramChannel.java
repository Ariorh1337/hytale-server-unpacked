/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannelConfig;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.MembershipKey;
import java.nio.channels.SelectableChannel;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class NioDatagramChannel
extends AbstractNioMessageChannel
implements DatagramChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(true, 16);
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(SocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    private final DatagramChannelConfig config;
    private Map<InetAddress, List<MembershipKey>> memberships;

    private static java.nio.channels.DatagramChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openDatagramChannel();
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }

    private static java.nio.channels.DatagramChannel newSocket(SelectorProvider provider, SocketProtocolFamily ipFamily) {
        if (ipFamily == null) {
            return NioDatagramChannel.newSocket(provider);
        }
        try {
            return provider.openDatagramChannel(ipFamily.toJdkFamily());
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }

    public NioDatagramChannel() {
        this(NioDatagramChannel.newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    public NioDatagramChannel(SelectorProvider provider) {
        this(NioDatagramChannel.newSocket(provider));
    }

    @Deprecated
    public NioDatagramChannel(InternetProtocolFamily ipFamily) {
        this(ipFamily == null ? null : ipFamily.toSocketProtocolFamily());
    }

    public NioDatagramChannel(SocketProtocolFamily protocolFamily) {
        this(NioDatagramChannel.newSocket(DEFAULT_SELECTOR_PROVIDER, protocolFamily));
    }

    @Deprecated
    public NioDatagramChannel(SelectorProvider provider, InternetProtocolFamily ipFamily) {
        this(provider, ipFamily == null ? null : ipFamily.toSocketProtocolFamily());
    }

    public NioDatagramChannel(SelectorProvider provider, SocketProtocolFamily protocolFamily) {
        this(NioDatagramChannel.newSocket(provider, protocolFamily));
    }

    public NioDatagramChannel(java.nio.channels.DatagramChannel socket) {
        super(null, (SelectableChannel)socket, 1);
        this.config = new NioDatagramChannelConfig(this, socket);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public DatagramChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isActive() {
        java.nio.channels.DatagramChannel ch = this.javaChannel();
        return ch.isOpen() && (this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) != false && this.isRegistered() || ch.socket().isBound());
    }

    @Override
    public boolean isConnected() {
        return this.javaChannel().isConnected();
    }

    @Override
    protected java.nio.channels.DatagramChannel javaChannel() {
        return (java.nio.channels.DatagramChannel)super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.doBind0(localAddress);
    }

    private void doBind0(SocketAddress localAddress) throws Exception {
        SocketUtils.bind(this.javaChannel(), localAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.doBind0(localAddress);
        }
        boolean success = false;
        try {
            this.javaChannel().connect(remoteAddress);
            success = true;
            boolean bl = true;
            return bl;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new UnsupportedOperationException("finishConnect is not supported for " + this.getClass().getName());
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.javaChannel().disconnect();
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        java.nio.channels.DatagramChannel ch = this.javaChannel();
        DatagramChannelConfig config = this.config();
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        ByteBuf data = allocHandle.allocate(config.getAllocator());
        allocHandle.attemptedBytesRead(data.writableBytes());
        boolean free = true;
        try {
            ByteBuffer nioData = data.internalNioBuffer(data.writerIndex(), data.writableBytes());
            int pos = nioData.position();
            InetSocketAddress remoteAddress = (InetSocketAddress)ch.receive(nioData);
            if (remoteAddress == null) {
                int n = 0;
                return n;
            }
            allocHandle.lastBytesRead(nioData.position() - pos);
            buf.add(new DatagramPacket(data.writerIndex(data.writerIndex() + allocHandle.lastBytesRead()), this.localAddress(), remoteAddress));
            free = false;
            int n = 1;
            return n;
        }
        catch (Throwable cause) {
            PlatformDependent.throwException(cause);
            int n = -1;
            return n;
        }
        finally {
            if (free) {
                data.release();
            }
        }
    }

    @Override
    protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
        ByteBuf data;
        SocketAddress remoteAddress;
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope envelope = (AddressedEnvelope)msg;
            remoteAddress = (SocketAddress)envelope.recipient();
            data = (ByteBuf)envelope.content();
        } else {
            data = (ByteBuf)msg;
            remoteAddress = null;
        }
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        ByteBuffer nioData = data.nioBufferCount() == 1 ? data.internalNioBuffer(data.readerIndex(), dataLen) : data.nioBuffer(data.readerIndex(), dataLen);
        int writtenBytes = remoteAddress != null ? this.javaChannel().send(nioData, remoteAddress) : this.javaChannel().write(nioData);
        return writtenBytes > 0;
    }

    private static void checkUnresolved(AddressedEnvelope<?, ?> envelope) {
        if (envelope.recipient() instanceof InetSocketAddress && ((InetSocketAddress)envelope.recipient()).isUnresolved()) {
            throw new UnresolvedAddressException();
        }
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (msg instanceof DatagramPacket) {
            DatagramPacket p = (DatagramPacket)msg;
            NioDatagramChannel.checkUnresolved(p);
            ByteBuf content = (ByteBuf)p.content();
            if (NioDatagramChannel.isSingleDirectBuffer(content)) {
                return p;
            }
            return new DatagramPacket(this.newDirectBuffer(p, content), (InetSocketAddress)p.recipient());
        }
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (NioDatagramChannel.isSingleDirectBuffer(buf)) {
                return buf;
            }
            return this.newDirectBuffer(buf);
        }
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope e = (AddressedEnvelope)msg;
            NioDatagramChannel.checkUnresolved(e);
            if (e.content() instanceof ByteBuf) {
                ByteBuf content = (ByteBuf)e.content();
                if (NioDatagramChannel.isSingleDirectBuffer(content)) {
                    return e;
                }
                return new DefaultAddressedEnvelope(this.newDirectBuffer(e, content), e.recipient());
            }
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    private static boolean isSingleDirectBuffer(ByteBuf buf) {
        return buf.isDirect() && buf.nioBufferCount() == 1;
    }

    @Override
    protected boolean continueOnWriteError() {
        return true;
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress) {
        return this.joinGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            NetworkInterface iface = this.config.getNetworkInterface();
            if (iface == null) {
                iface = NetworkInterface.getByInetAddress(this.localAddress().getAddress());
            }
            return this.joinGroup(multicastAddress, iface, null, promise);
        }
        catch (SocketException e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.joinGroup(multicastAddress, networkInterface, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        return this.joinGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.joinGroup(multicastAddress, networkInterface, source, this.newPromise());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        try {
            MembershipKey key = source == null ? this.javaChannel().join(multicastAddress, networkInterface) : this.javaChannel().join(multicastAddress, networkInterface, source);
            NioDatagramChannel nioDatagramChannel = this;
            synchronized (nioDatagramChannel) {
                List<MembershipKey> keys = null;
                if (this.memberships == null) {
                    this.memberships = new HashMap<InetAddress, List<MembershipKey>>();
                } else {
                    keys = this.memberships.get(multicastAddress);
                }
                if (keys == null) {
                    keys = new ArrayList<MembershipKey>();
                    this.memberships.put(multicastAddress, keys);
                }
                keys.add(key);
            }
            promise.setSuccess();
        }
        catch (Throwable e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress) {
        return this.leaveGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return this.leaveGroup(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, promise);
        }
        catch (SocketException e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.leaveGroup(multicastAddress, networkInterface, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        return this.leaveGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.leaveGroup(multicastAddress, networkInterface, source, this.newPromise());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        NioDatagramChannel nioDatagramChannel = this;
        synchronized (nioDatagramChannel) {
            List<MembershipKey> keys;
            if (this.memberships != null && (keys = this.memberships.get(multicastAddress)) != null) {
                Iterator<MembershipKey> keyIt = keys.iterator();
                while (keyIt.hasNext()) {
                    MembershipKey key = keyIt.next();
                    if (!networkInterface.equals(key.networkInterface()) || (source != null || key.sourceAddress() != null) && (source == null || !source.equals(key.sourceAddress()))) continue;
                    key.drop();
                    keyIt.remove();
                }
                if (keys.isEmpty()) {
                    this.memberships.remove(multicastAddress);
                }
            }
        }
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
        return this.block(multicastAddress, networkInterface, sourceToBlock, this.newPromise());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(sourceToBlock, "sourceToBlock");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        NioDatagramChannel nioDatagramChannel = this;
        synchronized (nioDatagramChannel) {
            if (this.memberships != null) {
                List<MembershipKey> keys = this.memberships.get(multicastAddress);
                for (MembershipKey key : keys) {
                    if (!networkInterface.equals(key.networkInterface())) continue;
                    try {
                        key.block(sourceToBlock);
                    }
                    catch (IOException e) {
                        promise.setFailure(e);
                    }
                }
            }
        }
        promise.setSuccess();
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
        return this.block(multicastAddress, sourceToBlock, this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
        try {
            return this.block(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), sourceToBlock, promise);
        }
        catch (SocketException e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    @Deprecated
    protected void setReadPending(boolean readPending) {
        super.setReadPending(readPending);
    }

    void clearReadPending0() {
        this.clearReadPending();
    }

    @Override
    protected boolean closeOnReadError(Throwable cause) {
        if (cause instanceof SocketException) {
            return false;
        }
        return super.closeOnReadError(cause);
    }

    @Override
    protected boolean continueReading(RecvByteBufAllocator.Handle allocHandle) {
        if (allocHandle instanceof RecvByteBufAllocator.ExtendedHandle) {
            return ((RecvByteBufAllocator.ExtendedHandle)allocHandle).continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER);
        }
        return allocHandle.continueReading();
    }
}

