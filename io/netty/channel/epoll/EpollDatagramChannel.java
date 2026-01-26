/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannelConfig;
import io.netty.channel.epoll.EpollIoOps;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeArrays;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.SegmentedDatagramPacket;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.UnresolvedAddressException;

public final class EpollDatagramChannel
extends AbstractEpollChannel
implements DatagramChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollDatagramChannel.class);
    private static final boolean IP_MULTICAST_ALL = SystemPropertyUtil.getBoolean("io.netty.channel.epoll.ipMulticastAll", false);
    private static final ChannelMetadata METADATA = new ChannelMetadata(true, 16);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    private final EpollDatagramChannelConfig config;
    private volatile boolean connected;

    public static boolean isSegmentedDatagramPacketSupported() {
        return Epoll.isAvailable() && Native.IS_SUPPORTING_SENDMMSG && Native.IS_SUPPORTING_UDP_SEGMENT;
    }

    public EpollDatagramChannel() {
        this((SocketProtocolFamily)null);
    }

    @Deprecated
    public EpollDatagramChannel(InternetProtocolFamily family) {
        this(LinuxSocket.newSocketDgram(family), false);
    }

    public EpollDatagramChannel(SocketProtocolFamily family) {
        this(LinuxSocket.newSocketDgram(family), false);
    }

    public EpollDatagramChannel(int fd) {
        this(new LinuxSocket(fd), true);
    }

    private EpollDatagramChannel(LinuxSocket fd, boolean active) {
        super(null, fd, active, EpollIoOps.valueOf(0));
        try {
            fd.setIpMulticastAll(IP_MULTICAST_ALL);
        }
        catch (IOException e) {
            logger.debug("Failed to set IP_MULTICAST_ALL to {}", (Object)IP_MULTICAST_ALL, (Object)e);
        }
        this.config = new EpollDatagramChannelConfig(this);
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
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public boolean isActive() {
        return this.socket.isOpen() && (this.config.getActiveOnOpen() && this.isRegistered() || this.active);
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress) {
        return this.joinGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            NetworkInterface iface = this.config().getNetworkInterface();
            if (iface == null) {
                iface = NetworkInterface.getByInetAddress(this.localAddress().getAddress());
            }
            return this.joinGroup(multicastAddress, iface, null, promise);
        }
        catch (IOException e) {
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

    @Override
    public ChannelFuture joinGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        if (this.eventLoop().inEventLoop()) {
            this.joinGroup0(multicastAddress, networkInterface, source, promise);
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    EpollDatagramChannel.this.joinGroup0(multicastAddress, networkInterface, source, promise);
                }
            });
        }
        return promise;
    }

    private void joinGroup0(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        assert (this.eventLoop().inEventLoop());
        try {
            this.socket.joinGroup(multicastAddress, networkInterface, source);
            promise.setSuccess();
        }
        catch (IOException e) {
            promise.setFailure(e);
        }
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
        catch (IOException e) {
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

    @Override
    public ChannelFuture leaveGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        if (this.eventLoop().inEventLoop()) {
            this.leaveGroup0(multicastAddress, networkInterface, source, promise);
        } else {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    EpollDatagramChannel.this.leaveGroup0(multicastAddress, networkInterface, source, promise);
                }
            });
        }
        return promise;
    }

    private void leaveGroup0(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        assert (this.eventLoop().inEventLoop());
        try {
            this.socket.leaveGroup(multicastAddress, networkInterface, source);
            promise.setSuccess();
        }
        catch (IOException e) {
            promise.setFailure(e);
        }
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
        return this.block(multicastAddress, networkInterface, sourceToBlock, this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(sourceToBlock, "sourceToBlock");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        promise.setFailure(new UnsupportedOperationException("Multicast block not supported"));
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
        catch (Throwable e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollDatagramChannelUnsafe();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        InetSocketAddress socketAddress;
        if (localAddress instanceof InetSocketAddress && (socketAddress = (InetSocketAddress)localAddress).getAddress().isAnyLocalAddress() && socketAddress.getAddress() instanceof Inet4Address && this.socket.family() == SocketProtocolFamily.INET6) {
            localAddress = new InetSocketAddress(Native.INET6_ANY, socketAddress.getPort());
        }
        super.doBind(localAddress);
        this.active = true;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        int maxMessagesPerWrite = this.maxMessagesPerWrite();
        while (maxMessagesPerWrite > 0 && (msg = in.current()) != null) {
            try {
                if (Native.IS_SUPPORTING_SENDMMSG && in.size() > 1 || in.current() instanceof SegmentedDatagramPacket) {
                    NativeDatagramPacketArray array = this.cleanDatagramPacketArray();
                    array.add(in, this.isConnected(), maxMessagesPerWrite);
                    int cnt = array.count();
                    if (cnt >= 1) {
                        int offset = 0;
                        NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
                        int send = this.socket.sendmmsg(packets, offset, cnt);
                        if (send == 0) break;
                        for (int i = 0; i < send; ++i) {
                            in.remove();
                        }
                        maxMessagesPerWrite -= send;
                        continue;
                    }
                }
                boolean done = false;
                for (int i = this.config().getWriteSpinCount(); i > 0; --i) {
                    if (!this.doWriteMessage(msg)) continue;
                    done = true;
                    break;
                }
                if (!done) break;
                in.remove();
                --maxMessagesPerWrite;
            }
            catch (IOException e) {
                --maxMessagesPerWrite;
                in.remove(e);
            }
        }
        if (in.isEmpty()) {
            this.clearFlag(Native.EPOLLOUT);
        } else {
            this.setFlag(Native.EPOLLOUT);
        }
    }

    private boolean doWriteMessage(Object msg) throws Exception {
        InetSocketAddress remoteAddress;
        ByteBuf data;
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope envelope = (AddressedEnvelope)msg;
            data = (ByteBuf)envelope.content();
            remoteAddress = (InetSocketAddress)envelope.recipient();
        } else {
            data = (ByteBuf)msg;
            remoteAddress = null;
        }
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        return this.doWriteOrSendBytes(data, remoteAddress, false) > 0L;
    }

    private static void checkUnresolved(AddressedEnvelope<?, ?> envelope) {
        if (envelope.recipient() instanceof InetSocketAddress && ((InetSocketAddress)envelope.recipient()).isUnresolved()) {
            throw new UnresolvedAddressException();
        }
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (msg instanceof SegmentedDatagramPacket) {
            if (!Native.IS_SUPPORTING_UDP_SEGMENT) {
                throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
            }
            SegmentedDatagramPacket packet = (SegmentedDatagramPacket)msg;
            EpollDatagramChannel.checkUnresolved(packet);
            ByteBuf content = (ByteBuf)packet.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? packet.replace(this.newDirectBuffer(packet, content)) : msg;
        }
        if (msg instanceof DatagramPacket) {
            DatagramPacket packet = (DatagramPacket)msg;
            EpollDatagramChannel.checkUnresolved(packet);
            ByteBuf content = (ByteBuf)packet.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DatagramPacket(this.newDirectBuffer(packet, content), (InetSocketAddress)packet.recipient()) : msg;
        }
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
        }
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope<ByteBuf, InetSocketAddress> e = (AddressedEnvelope<ByteBuf, InetSocketAddress>)msg;
            EpollDatagramChannel.checkUnresolved(e);
            if (e.content() instanceof ByteBuf && (e.recipient() == null || e.recipient() instanceof InetSocketAddress)) {
                ByteBuf content = (ByteBuf)e.content();
                return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DefaultAddressedEnvelope<ByteBuf, InetSocketAddress>(this.newDirectBuffer(e, content), (InetSocketAddress)e.recipient()) : e;
            }
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    @Override
    public EpollDatagramChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.socket.disconnect();
        this.active = false;
        this.connected = false;
        this.resetCachedAddresses();
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (super.doConnect(remoteAddress, localAddress)) {
            this.connected = true;
            return true;
        }
        return false;
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();
        this.connected = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean connectedRead(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int maxDatagramPacketSize) throws Exception {
        try {
            int localReadAmount;
            int writable = maxDatagramPacketSize != 0 ? Math.min(byteBuf.writableBytes(), maxDatagramPacketSize) : byteBuf.writableBytes();
            allocHandle.attemptedBytesRead(writable);
            int writerIndex = byteBuf.writerIndex();
            if (byteBuf.hasMemoryAddress()) {
                localReadAmount = this.socket.recvAddress(byteBuf.memoryAddress(), writerIndex, writerIndex + writable);
            } else {
                ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, writable);
                localReadAmount = this.socket.recv(buf, buf.position(), buf.limit());
            }
            if (localReadAmount <= 0) {
                allocHandle.lastBytesRead(localReadAmount);
                boolean buf = false;
                return buf;
            }
            byteBuf.writerIndex(writerIndex + localReadAmount);
            allocHandle.lastBytesRead(maxDatagramPacketSize <= 0 ? localReadAmount : writable);
            DatagramPacket packet = new DatagramPacket(byteBuf, this.localAddress(), this.remoteAddress());
            allocHandle.incMessagesRead(1);
            this.pipeline().fireChannelRead(packet);
            byteBuf = null;
            boolean bl = true;
            return bl;
        }
        finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
        }
    }

    private IOException translateForConnected(Errors.NativeIoException e) {
        if (e.expectedErr() == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
            PortUnreachableException error = new PortUnreachableException(e.getMessage());
            error.initCause(e);
            return error;
        }
        return e;
    }

    private static void addDatagramPacketToOut(DatagramPacket packet, RecyclableArrayList out) {
        if (packet instanceof SegmentedDatagramPacket) {
            SegmentedDatagramPacket segmentedDatagramPacket = (SegmentedDatagramPacket)packet;
            ByteBuf content = (ByteBuf)segmentedDatagramPacket.content();
            InetSocketAddress recipient = (InetSocketAddress)segmentedDatagramPacket.recipient();
            InetSocketAddress sender = (InetSocketAddress)segmentedDatagramPacket.sender();
            int segmentSize = segmentedDatagramPacket.segmentSize();
            do {
                out.add(new DatagramPacket(content.readRetainedSlice(Math.min(content.readableBytes(), segmentSize)), recipient, sender));
            } while (content.isReadable());
            segmentedDatagramPacket.release();
        } else {
            out.add(packet);
        }
    }

    private static void releaseAndRecycle(ByteBuf byteBuf, RecyclableArrayList packetList) {
        if (byteBuf != null) {
            byteBuf.release();
        }
        if (packetList != null) {
            for (int i = 0; i < packetList.size(); ++i) {
                ReferenceCountUtil.release(packetList.get(i));
            }
            packetList.recycle();
        }
    }

    private static void processPacket(ChannelPipeline pipeline, EpollRecvByteAllocatorHandle handle, int bytesRead, DatagramPacket packet) {
        handle.lastBytesRead(Math.max(1, bytesRead));
        handle.incMessagesRead(1);
        pipeline.fireChannelRead(packet);
    }

    private static void processPacketList(ChannelPipeline pipeline, EpollRecvByteAllocatorHandle handle, int bytesRead, RecyclableArrayList packetList) {
        int messagesRead = packetList.size();
        handle.lastBytesRead(Math.max(1, bytesRead));
        handle.incMessagesRead(messagesRead);
        for (int i = 0; i < messagesRead; ++i) {
            pipeline.fireChannelRead(packetList.set(i, Unpooled.EMPTY_BUFFER));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean recvmsg(EpollRecvByteAllocatorHandle allocHandle, NativeDatagramPacketArray array, ByteBuf byteBuf) throws IOException {
        int bytesReceived;
        NativeDatagramPacketArray.NativeDatagramPacket msg;
        RecyclableArrayList datagramPackets;
        block6: {
            boolean bl;
            datagramPackets = null;
            try {
                int writable = byteBuf.writableBytes();
                boolean added = array.addWritable(byteBuf, byteBuf.writerIndex(), writable);
                assert (added);
                allocHandle.attemptedBytesRead(writable);
                msg = array.packets()[0];
                bytesReceived = this.socket.recvmsg(msg);
                if (msg.hasSender()) break block6;
                allocHandle.lastBytesRead(-1);
                bl = false;
            }
            catch (Throwable throwable) {
                EpollDatagramChannel.releaseAndRecycle(byteBuf, datagramPackets);
                throw throwable;
            }
            EpollDatagramChannel.releaseAndRecycle(byteBuf, datagramPackets);
            return bl;
        }
        byteBuf.writerIndex(bytesReceived);
        InetSocketAddress local = this.localAddress();
        DatagramPacket packet = msg.newDatagramPacket(byteBuf, local);
        if (!(packet instanceof SegmentedDatagramPacket)) {
            EpollDatagramChannel.processPacket(this.pipeline(), allocHandle, bytesReceived, packet);
        } else {
            datagramPackets = RecyclableArrayList.newInstance();
            EpollDatagramChannel.addDatagramPacketToOut(packet, datagramPackets);
            EpollDatagramChannel.processPacketList(this.pipeline(), allocHandle, bytesReceived, datagramPackets);
            datagramPackets.recycle();
            datagramPackets = null;
        }
        boolean bl = true;
        EpollDatagramChannel.releaseAndRecycle(byteBuf, datagramPackets);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean scatteringRead(EpollRecvByteAllocatorHandle allocHandle, NativeDatagramPacketArray array, ByteBuf byteBuf, int datagramSize, int numDatagram) throws IOException {
        boolean bl;
        int bytesReceived;
        InetSocketAddress local;
        int received;
        NativeDatagramPacketArray.NativeDatagramPacket[] packets;
        RecyclableArrayList datagramPackets;
        block7: {
            DatagramPacket packet;
            block6: {
                datagramPackets = null;
                int offset = byteBuf.writerIndex();
                int i = 0;
                while (i < numDatagram && array.addWritable(byteBuf, offset, datagramSize)) {
                    ++i;
                    offset += datagramSize;
                }
                allocHandle.attemptedBytesRead(offset - byteBuf.writerIndex());
                packets = array.packets();
                received = this.socket.recvmmsg(packets, 0, array.count());
                if (received != 0) break block6;
                allocHandle.lastBytesRead(-1);
                boolean bl2 = false;
                EpollDatagramChannel.releaseAndRecycle(byteBuf, datagramPackets);
                return bl2;
            }
            local = this.localAddress();
            bytesReceived = received * datagramSize;
            byteBuf.writerIndex(byteBuf.writerIndex() + bytesReceived);
            if (received != 1 || (packet = packets[0].newDatagramPacket(byteBuf, local)) instanceof SegmentedDatagramPacket) break block7;
            EpollDatagramChannel.processPacket(this.pipeline(), allocHandle, datagramSize, packet);
            boolean bl3 = true;
            EpollDatagramChannel.releaseAndRecycle(byteBuf, datagramPackets);
            return bl3;
        }
        try {
            datagramPackets = RecyclableArrayList.newInstance();
            for (int i = 0; i < received; ++i) {
                DatagramPacket packet = packets[i].newDatagramPacket(byteBuf, local);
                byteBuf.skipBytes(datagramSize);
                EpollDatagramChannel.addDatagramPacketToOut(packet, datagramPackets);
            }
            byteBuf.release();
            byteBuf = null;
            EpollDatagramChannel.processPacketList(this.pipeline(), allocHandle, bytesReceived, datagramPackets);
            datagramPackets.recycle();
            datagramPackets = null;
            bl = true;
        }
        catch (Throwable throwable) {
            EpollDatagramChannel.releaseAndRecycle(byteBuf, datagramPackets);
            throw throwable;
        }
        EpollDatagramChannel.releaseAndRecycle(byteBuf, datagramPackets);
        return bl;
    }

    private NativeDatagramPacketArray cleanDatagramPacketArray() {
        return ((NativeArrays)this.registration().attachment()).cleanDatagramPacketArray();
    }

    static /* synthetic */ NativeDatagramPacketArray access$200(EpollDatagramChannel x0) {
        return x0.cleanDatagramPacketArray();
    }

    static /* synthetic */ boolean access$300(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, NativeDatagramPacketArray x2, ByteBuf x3) throws IOException {
        return x0.recvmsg(x1, x2, x3);
    }

    static /* synthetic */ boolean access$400(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, ByteBuf x2, int x3) throws Exception {
        return x0.connectedRead(x1, x2, x3);
    }

    static /* synthetic */ boolean access$500(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, NativeDatagramPacketArray x2, ByteBuf x3, int x4, int x5) throws IOException {
        return x0.scatteringRead(x1, x2, x3, x4, x5);
    }

    static /* synthetic */ IOException access$600(EpollDatagramChannel x0, Errors.NativeIoException x1) {
        return x0.translateForConnected(x1);
    }

    static {
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.channel.epoll.ipMulticastAll: {}", (Object)IP_MULTICAST_ALL);
        }
    }

    final class EpollDatagramChannelUnsafe
    extends AbstractEpollChannel.AbstractEpollUnsafe {
        EpollDatagramChannelUnsafe() {
            super(EpollDatagramChannel.this);
        }

        /*
         * Exception decompiling
         */
        @Override
        void epollInReady() {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [7[DOLOOP]], but top level block is 13[SIMPLE_IF_TAKEN]
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        }
    }
}

