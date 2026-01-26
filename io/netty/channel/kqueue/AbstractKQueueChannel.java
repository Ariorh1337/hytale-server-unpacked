/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.IoEvent;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoRegistration;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.kqueue.BsdSocket;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.channel.kqueue.KQueueDomainSocketChannelConfig;
import io.netty.channel.kqueue.KQueueIoEvent;
import io.netty.channel.kqueue.KQueueIoHandle;
import io.netty.channel.kqueue.KQueueIoOps;
import io.netty.channel.kqueue.KQueueRecvByteAllocatorHandle;
import io.netty.channel.kqueue.Native;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.TimeUnit;

abstract class AbstractKQueueChannel
extends AbstractChannel
implements UnixChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private ChannelPromise connectPromise;
    private Future<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;
    final BsdSocket socket;
    private IoRegistration registration;
    private boolean readFilterEnabled;
    private boolean writeFilterEnabled;
    boolean readReadyRunnablePending;
    boolean inputClosedSeenErrorOnRead;
    protected volatile boolean active;
    private volatile SocketAddress local;
    private volatile SocketAddress remote;

    AbstractKQueueChannel(Channel parent, BsdSocket fd, boolean active) {
        super(parent);
        this.socket = ObjectUtil.checkNotNull(fd, "fd");
        this.active = active;
        if (active) {
            this.local = fd.localAddress();
            this.remote = fd.remoteAddress();
        }
    }

    AbstractKQueueChannel(Channel parent, BsdSocket fd, SocketAddress remote) {
        super(parent);
        this.socket = ObjectUtil.checkNotNull(fd, "fd");
        this.active = true;
        this.remote = remote;
        this.local = fd.localAddress();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof IoEventLoop && ((IoEventLoop)loop).isCompatible(AbstractKQueueUnsafe.class);
    }

    static boolean isSoErrorZero(BsdSocket fd) {
        try {
            return fd.getSoError() == 0;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    protected final IoRegistration registration() {
        assert (this.registration != null);
        return this.registration;
    }

    @Override
    public final FileDescriptor fd() {
        return this.socket;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    protected void doClose() throws Exception {
        this.active = false;
        this.inputClosedSeenErrorOnRead = true;
        this.socket.close();
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    void resetCachedAddresses() {
        this.local = this.socket.localAddress();
        this.remote = this.socket.remoteAddress();
    }

    @Override
    public boolean isOpen() {
        return this.socket.isOpen();
    }

    @Override
    protected void doDeregister() throws Exception {
        this.readFilter(false);
        this.writeFilter(false);
        this.clearRdHup0();
        IoRegistration registration = this.registration;
        if (registration != null) {
            registration.cancel();
        }
    }

    private void clearRdHup0() {
        this.submit(KQueueIoOps.newOps(Native.EVFILT_SOCK, Native.EV_DELETE_DISABLE, Native.NOTE_RDHUP));
    }

    private void submit(KQueueIoOps ops) {
        try {
            this.registration.submit(ops);
        }
        catch (Exception e) {
            throw new ChannelException(e);
        }
    }

    @Override
    protected final void doBeginRead() throws Exception {
        AbstractKQueueUnsafe unsafe = (AbstractKQueueUnsafe)this.unsafe();
        unsafe.readPending = true;
        this.readFilter(true);
    }

    @Override
    protected void doRegister(ChannelPromise promise) {
        ((IoEventLoop)this.eventLoop()).register((AbstractKQueueUnsafe)this.unsafe()).addListener(f -> {
            if (f.isSuccess()) {
                this.registration = (IoRegistration)f.getNow();
                this.readReadyRunnablePending = false;
                this.submit(KQueueIoOps.newOps(Native.EVFILT_SOCK, Native.EV_ADD, Native.NOTE_RDHUP));
                if (this.writeFilterEnabled) {
                    this.submit(Native.WRITE_ENABLED_OPS);
                }
                if (this.readFilterEnabled) {
                    this.submit(Native.READ_ENABLED_OPS);
                }
                promise.setSuccess();
            } else {
                promise.setFailure(f.cause());
            }
        });
    }

    @Override
    protected abstract AbstractKQueueUnsafe newUnsafe();

    @Override
    public abstract KQueueChannelConfig config();

    protected final ByteBuf newDirectBuffer(ByteBuf buf) {
        return this.newDirectBuffer(buf, buf);
    }

    protected final ByteBuf newDirectBuffer(Object holder, ByteBuf buf) {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.release(holder);
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            return AbstractKQueueChannel.newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf == null) {
            return AbstractKQueueChannel.newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }

    private static ByteBuf newDirectBuffer0(Object holder, ByteBuf buf, ByteBufAllocator alloc, int capacity) {
        ByteBuf directBuf = alloc.directBuffer(capacity);
        directBuf.writeBytes(buf, buf.readerIndex(), capacity);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }

    protected static void checkResolvable(InetSocketAddress addr) {
        if (addr.isUnresolved()) {
            throw new UnresolvedAddressException();
        }
    }

    protected final int doReadBytes(ByteBuf byteBuf) throws Exception {
        int localReadAmount;
        int writerIndex = byteBuf.writerIndex();
        this.unsafe().recvBufAllocHandle().attemptedBytesRead(byteBuf.writableBytes());
        if (byteBuf.hasMemoryAddress()) {
            localReadAmount = this.socket.readAddress(byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
        } else {
            ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
            localReadAmount = this.socket.read(buf, buf.position(), buf.limit());
        }
        if (localReadAmount > 0) {
            byteBuf.writerIndex(writerIndex + localReadAmount);
        }
        return localReadAmount;
    }

    protected final int doWriteBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
        if (buf.hasMemoryAddress()) {
            int localFlushedAmount = this.socket.writeAddress(buf.memoryAddress(), buf.readerIndex(), buf.writerIndex());
            if (localFlushedAmount > 0) {
                in.removeBytes(localFlushedAmount);
                return 1;
            }
        } else {
            ByteBuffer nioBuf = buf.nioBufferCount() == 1 ? buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes()) : buf.nioBuffer();
            int localFlushedAmount = this.socket.write(nioBuf, nioBuf.position(), nioBuf.limit());
            if (localFlushedAmount > 0) {
                nioBuf.position(nioBuf.position() + localFlushedAmount);
                in.removeBytes(localFlushedAmount);
                return 1;
            }
        }
        return Integer.MAX_VALUE;
    }

    final boolean shouldBreakReadReady(ChannelConfig config) {
        return this.socket.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !AbstractKQueueChannel.isAllowHalfClosure(config));
    }

    private static boolean isAllowHalfClosure(ChannelConfig config) {
        if (config instanceof KQueueDomainSocketChannelConfig) {
            return ((KQueueDomainSocketChannelConfig)config).isAllowHalfClosure();
        }
        return config instanceof SocketChannelConfig && ((SocketChannelConfig)config).isAllowHalfClosure();
    }

    final void clearReadFilter() {
        if (this.isRegistered()) {
            EventLoop loop = this.eventLoop();
            final AbstractKQueueUnsafe unsafe = (AbstractKQueueUnsafe)this.unsafe();
            if (loop.inEventLoop()) {
                unsafe.clearReadFilter0();
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        if (!unsafe.readPending && !AbstractKQueueChannel.this.config().isAutoRead()) {
                            unsafe.clearReadFilter0();
                        }
                    }
                });
            }
        } else {
            this.readFilterEnabled = false;
        }
    }

    void readFilter(boolean readFilterEnabled) throws IOException {
        if (this.readFilterEnabled != readFilterEnabled) {
            this.readFilterEnabled = readFilterEnabled;
            this.submit(readFilterEnabled ? Native.READ_ENABLED_OPS : Native.READ_DISABLED_OPS);
        }
    }

    void writeFilter(boolean writeFilterEnabled) throws IOException {
        if (this.writeFilterEnabled != writeFilterEnabled) {
            this.writeFilterEnabled = writeFilterEnabled;
            this.submit(writeFilterEnabled ? Native.WRITE_ENABLED_OPS : Native.WRITE_DISABLED_OPS);
        }
    }

    @Override
    protected void doBind(SocketAddress local) throws Exception {
        if (local instanceof InetSocketAddress) {
            AbstractKQueueChannel.checkResolvable((InetSocketAddress)local);
        }
        this.socket.bind(local);
        this.local = this.socket.localAddress();
    }

    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        boolean connected;
        InetSocketAddress remoteSocketAddr;
        if (localAddress instanceof InetSocketAddress) {
            AbstractKQueueChannel.checkResolvable((InetSocketAddress)localAddress);
        }
        InetSocketAddress inetSocketAddress = remoteSocketAddr = remoteAddress instanceof InetSocketAddress ? (InetSocketAddress)remoteAddress : null;
        if (remoteSocketAddr != null) {
            AbstractKQueueChannel.checkResolvable(remoteSocketAddr);
        }
        if (this.remote != null) {
            throw new AlreadyConnectedException();
        }
        if (localAddress != null) {
            this.socket.bind(localAddress);
        }
        if (connected = this.doConnect0(remoteAddress, localAddress)) {
            this.remote = remoteSocketAddr == null ? remoteAddress : UnixChannelUtil.computeRemoteAddr(remoteSocketAddr, this.socket.remoteAddress());
        }
        this.local = this.socket.localAddress();
        return connected;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean doConnect0(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        boolean success = false;
        try {
            boolean connected = this.socket.connect(remoteAddress);
            if (!connected) {
                this.writeFilter(true);
            }
            success = true;
            boolean bl = connected;
            return bl;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.remote;
    }

    public abstract class AbstractKQueueUnsafe
    extends AbstractChannel.AbstractUnsafe
    implements KQueueIoHandle {
        boolean readPending;
        private KQueueRecvByteAllocatorHandle allocHandle;

        Channel channel() {
            return AbstractKQueueChannel.this;
        }

        @Override
        public int ident() {
            return AbstractKQueueChannel.this.fd().intValue();
        }

        @Override
        public void close() {
            this.close(this.voidPromise());
        }

        @Override
        public void handle(IoRegistration registration, IoEvent event) {
            KQueueIoEvent kqueueEvent = (KQueueIoEvent)event;
            short filter = kqueueEvent.filter();
            short flags = kqueueEvent.flags();
            int fflags = kqueueEvent.fflags();
            long data = kqueueEvent.data();
            if (filter == Native.EVFILT_WRITE) {
                this.writeReady();
            } else if (filter == Native.EVFILT_READ) {
                KQueueRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
                this.readReady(allocHandle);
            } else if (filter == Native.EVFILT_SOCK && (fflags & Native.NOTE_RDHUP) != 0) {
                this.readEOF();
                return;
            }
            if ((flags & Native.EV_EOF) != 0) {
                this.readEOF();
            }
        }

        abstract void readReady(KQueueRecvByteAllocatorHandle var1);

        final boolean shouldStopReading(ChannelConfig config) {
            return !this.readPending && !config.isAutoRead();
        }

        final boolean failConnectPromise(Throwable cause) {
            if (AbstractKQueueChannel.this.connectPromise != null) {
                ChannelPromise connectPromise = AbstractKQueueChannel.this.connectPromise;
                AbstractKQueueChannel.this.connectPromise = null;
                if (connectPromise.tryFailure(cause instanceof ConnectException ? cause : new ConnectException("failed to connect").initCause(cause))) {
                    this.closeIfClosed();
                    return true;
                }
            }
            return false;
        }

        private void writeReady() {
            if (AbstractKQueueChannel.this.connectPromise != null) {
                this.finishConnect();
            } else if (!AbstractKQueueChannel.this.socket.isOutputShutdown()) {
                super.flush0();
            }
        }

        void shutdownInput(boolean readEOF) {
            if (readEOF && AbstractKQueueChannel.this.connectPromise != null) {
                this.finishConnect();
            }
            if (!AbstractKQueueChannel.this.socket.isInputShutdown()) {
                if (AbstractKQueueChannel.isAllowHalfClosure(AbstractKQueueChannel.this.config())) {
                    try {
                        AbstractKQueueChannel.this.socket.shutdown(true, false);
                    }
                    catch (IOException ignored) {
                        this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
                        return;
                    }
                    catch (NotYetConnectedException notYetConnectedException) {
                        // empty catch block
                    }
                    if (this.shouldStopReading(AbstractKQueueChannel.this.config())) {
                        this.clearReadFilter0();
                    }
                    AbstractKQueueChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                } else {
                    this.close(this.voidPromise());
                    return;
                }
            }
            if (!readEOF && !AbstractKQueueChannel.this.inputClosedSeenErrorOnRead) {
                AbstractKQueueChannel.this.inputClosedSeenErrorOnRead = true;
                AbstractKQueueChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
            }
        }

        private void readEOF() {
            KQueueRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.readEOF();
            if (AbstractKQueueChannel.this.isActive()) {
                this.readReady(allocHandle);
            } else {
                this.shutdownInput(true);
            }
            AbstractKQueueChannel.this.clearRdHup0();
        }

        @Override
        public KQueueRecvByteAllocatorHandle recvBufAllocHandle() {
            if (this.allocHandle == null) {
                this.allocHandle = new KQueueRecvByteAllocatorHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle());
            }
            return this.allocHandle;
        }

        @Override
        protected final void flush0() {
            if (!AbstractKQueueChannel.this.writeFilterEnabled) {
                super.flush0();
            }
        }

        protected final void clearReadFilter0() {
            assert (AbstractKQueueChannel.this.eventLoop().inEventLoop());
            try {
                this.readPending = false;
                AbstractKQueueChannel.this.readFilter(false);
            }
            catch (IOException e) {
                AbstractKQueueChannel.this.pipeline().fireExceptionCaught(e);
                AbstractKQueueChannel.this.unsafe().close(AbstractKQueueChannel.this.unsafe().voidPromise());
            }
        }

        private void fireEventAndClose(Object evt) {
            AbstractKQueueChannel.this.pipeline().fireUserEventTriggered(evt);
            this.close(this.voidPromise());
        }

        @Override
        public void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            if (promise.isDone() || !this.ensureOpen(promise)) {
                return;
            }
            try {
                if (AbstractKQueueChannel.this.connectPromise != null) {
                    throw new ConnectionPendingException();
                }
                boolean wasActive = AbstractKQueueChannel.this.isActive();
                if (AbstractKQueueChannel.this.doConnect(remoteAddress, localAddress)) {
                    this.fulfillConnectPromise(promise, wasActive);
                } else {
                    AbstractKQueueChannel.this.connectPromise = promise;
                    AbstractKQueueChannel.this.requestedRemoteAddress = remoteAddress;
                    final int connectTimeoutMillis = AbstractKQueueChannel.this.config().getConnectTimeoutMillis();
                    if (connectTimeoutMillis > 0) {
                        AbstractKQueueChannel.this.connectTimeoutFuture = (Future)((Object)AbstractKQueueChannel.this.eventLoop().schedule(new Runnable(){

                            @Override
                            public void run() {
                                ChannelPromise connectPromise = AbstractKQueueChannel.this.connectPromise;
                                if (connectPromise != null && !connectPromise.isDone() && connectPromise.tryFailure(new ConnectTimeoutException("connection timed out after " + connectTimeoutMillis + " ms: " + remoteAddress))) {
                                    AbstractKQueueUnsafe.this.close(AbstractKQueueUnsafe.this.voidPromise());
                                }
                            }
                        }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS));
                    }
                    promise.addListener((GenericFutureListener)new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) {
                            if (future.isCancelled()) {
                                if (AbstractKQueueChannel.this.connectTimeoutFuture != null) {
                                    AbstractKQueueChannel.this.connectTimeoutFuture.cancel(false);
                                }
                                AbstractKQueueChannel.this.connectPromise = null;
                                AbstractKQueueUnsafe.this.close(AbstractKQueueUnsafe.this.voidPromise());
                            }
                        }
                    });
                }
            }
            catch (Throwable t) {
                this.closeIfClosed();
                promise.tryFailure(this.annotateConnectException(t, remoteAddress));
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
            if (promise == null) {
                return;
            }
            AbstractKQueueChannel.this.active = true;
            boolean active = AbstractKQueueChannel.this.isActive();
            boolean promiseSet = promise.trySuccess();
            if (!wasActive && active) {
                AbstractKQueueChannel.this.pipeline().fireChannelActive();
            }
            if (!promiseSet) {
                this.close(this.voidPromise());
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
            if (promise == null) {
                return;
            }
            promise.tryFailure(cause);
            this.closeIfClosed();
        }

        private void finishConnect() {
            assert (AbstractKQueueChannel.this.eventLoop().inEventLoop());
            boolean connectStillInProgress = false;
            try {
                boolean wasActive = AbstractKQueueChannel.this.isActive();
                if (!this.doFinishConnect()) {
                    connectStillInProgress = true;
                    return;
                }
                this.fulfillConnectPromise(AbstractKQueueChannel.this.connectPromise, wasActive);
            }
            catch (Throwable t) {
                this.fulfillConnectPromise(AbstractKQueueChannel.this.connectPromise, this.annotateConnectException(t, AbstractKQueueChannel.this.requestedRemoteAddress));
            }
            finally {
                if (!connectStillInProgress) {
                    if (AbstractKQueueChannel.this.connectTimeoutFuture != null) {
                        AbstractKQueueChannel.this.connectTimeoutFuture.cancel(false);
                    }
                    AbstractKQueueChannel.this.connectPromise = null;
                }
            }
        }

        private boolean doFinishConnect() throws Exception {
            if (AbstractKQueueChannel.this.socket.finishConnect()) {
                AbstractKQueueChannel.this.writeFilter(false);
                if (AbstractKQueueChannel.this.requestedRemoteAddress instanceof InetSocketAddress) {
                    AbstractKQueueChannel.this.remote = UnixChannelUtil.computeRemoteAddr((InetSocketAddress)AbstractKQueueChannel.this.requestedRemoteAddress, AbstractKQueueChannel.this.socket.remoteAddress());
                }
                AbstractKQueueChannel.this.requestedRemoteAddress = null;
                return true;
            }
            AbstractKQueueChannel.this.writeFilter(true);
            return false;
        }
    }
}

