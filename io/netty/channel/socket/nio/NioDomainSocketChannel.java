/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.FileRegion;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.socket.DuplexChannel;
import io.netty.channel.socket.DuplexChannelConfig;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.SelectorProviderUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Map;

public final class NioDomainSocketChannel
extends AbstractNioByteChannel
implements DuplexChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioDomainSocketChannel.class);
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    private static final Method OPEN_SOCKET_CHANNEL_WITH_FAMILY = SelectorProviderUtil.findOpenMethod("openSocketChannel");
    private final ChannelConfig config;
    private volatile boolean isInputShutdown;
    private volatile boolean isOutputShutdown;

    static SocketChannel newChannel(SelectorProvider provider) {
        if (PlatformDependent.javaVersion() < 16) {
            throw new UnsupportedOperationException("Only supported on java 16+");
        }
        try {
            SocketChannel channel = (SocketChannel)SelectorProviderUtil.newDomainSocketChannel(OPEN_SOCKET_CHANNEL_WITH_FAMILY, provider);
            if (channel == null) {
                throw new ChannelException("Failed to open a socket.");
            }
            return channel;
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }

    public NioDomainSocketChannel() {
        this(DEFAULT_SELECTOR_PROVIDER);
    }

    public NioDomainSocketChannel(SelectorProvider provider) {
        this(NioDomainSocketChannel.newChannel(provider));
    }

    public NioDomainSocketChannel(SocketChannel socket) {
        this(null, socket);
    }

    public NioDomainSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket);
        if (PlatformDependent.javaVersion() < 16) {
            throw new UnsupportedOperationException("Only supported on java 16+");
        }
        this.config = new NioDomainSocketChannelConfig(this, socket);
    }

    @Override
    public ServerChannel parent() {
        return (ServerChannel)super.parent();
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    protected SocketChannel javaChannel() {
        return (SocketChannel)super.javaChannel();
    }

    @Override
    public boolean isActive() {
        SocketChannel ch = this.javaChannel();
        return ch.isOpen() && ch.isConnected();
    }

    @Override
    public boolean isOutputShutdown() {
        return this.isOutputShutdown || !this.isActive();
    }

    @Override
    public boolean isInputShutdown() {
        return this.isInputShutdown || !this.isActive();
    }

    @Override
    public boolean isShutdown() {
        return this.isInputShutdown() && this.isOutputShutdown() || !this.isActive();
    }

    @Override
    protected void doShutdownOutput() throws Exception {
        this.javaChannel().shutdownOutput();
        this.isOutputShutdown = true;
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }

    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise promise) {
        EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            ((AbstractChannel.AbstractUnsafe)((Object)this.unsafe())).shutdownOutput(promise);
        } else {
            loop.execute(new Runnable(){

                @Override
                public void run() {
                    ((AbstractChannel.AbstractUnsafe)((Object)NioDomainSocketChannel.this.unsafe())).shutdownOutput(promise);
                }
            });
        }
        return promise;
    }

    @Override
    public ChannelFuture shutdownInput() {
        return this.shutdownInput(this.newPromise());
    }

    @Override
    protected boolean isInputShutdown0() {
        return this.isInputShutdown();
    }

    @Override
    public ChannelFuture shutdownInput(final ChannelPromise promise) {
        EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            this.shutdownInput0(promise);
        } else {
            loop.execute(new Runnable(){

                @Override
                public void run() {
                    NioDomainSocketChannel.this.shutdownInput0(promise);
                }
            });
        }
        return promise;
    }

    @Override
    public ChannelFuture shutdown() {
        return this.shutdown(this.newPromise());
    }

    @Override
    public ChannelFuture shutdown(final ChannelPromise promise) {
        ChannelFuture shutdownOutputFuture = this.shutdownOutput();
        if (shutdownOutputFuture.isDone()) {
            this.shutdownOutputDone(shutdownOutputFuture, promise);
        } else {
            shutdownOutputFuture.addListener((GenericFutureListener)new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture shutdownOutputFuture) throws Exception {
                    NioDomainSocketChannel.this.shutdownOutputDone(shutdownOutputFuture, promise);
                }
            });
        }
        return promise;
    }

    private void shutdownOutputDone(final ChannelFuture shutdownOutputFuture, final ChannelPromise promise) {
        ChannelFuture shutdownInputFuture = this.shutdownInput();
        if (shutdownInputFuture.isDone()) {
            NioDomainSocketChannel.shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
        } else {
            shutdownInputFuture.addListener((GenericFutureListener)new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture shutdownInputFuture) throws Exception {
                    NioDomainSocketChannel.shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
                }
            });
        }
    }

    private static void shutdownDone(ChannelFuture shutdownOutputFuture, ChannelFuture shutdownInputFuture, ChannelPromise promise) {
        Throwable shutdownOutputCause = shutdownOutputFuture.cause();
        Throwable shutdownInputCause = shutdownInputFuture.cause();
        if (shutdownOutputCause != null) {
            if (shutdownInputCause != null) {
                logger.debug("Exception suppressed because a previous exception occurred.", shutdownInputCause);
            }
            promise.setFailure(shutdownOutputCause);
        } else if (shutdownInputCause != null) {
            promise.setFailure(shutdownInputCause);
        } else {
            promise.setSuccess();
        }
    }

    private void shutdownInput0(ChannelPromise promise) {
        try {
            this.shutdownInput0();
            promise.setSuccess();
        }
        catch (Throwable t) {
            promise.setFailure(t);
        }
    }

    private void shutdownInput0() throws Exception {
        this.javaChannel().shutdownInput();
        this.isInputShutdown = true;
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
        try {
            return this.javaChannel().getRemoteAddress();
        }
        catch (Exception exception) {
            return null;
        }
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        SocketUtils.bind(this.javaChannel(), localAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.doBind(localAddress);
        }
        boolean success = false;
        try {
            boolean connected = SocketUtils.connect(this.javaChannel(), remoteAddress);
            if (!connected) {
                this.selectionKey().interestOps(8);
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
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new UnsupportedOperationException("finishConnect is not supported for " + this.getClass().getName());
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        try {
            super.doClose();
        }
        finally {
            this.javaChannel().close();
        }
    }

    @Override
    protected int doReadBytes(ByteBuf byteBuf) throws Exception {
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        allocHandle.attemptedBytesRead(byteBuf.writableBytes());
        return byteBuf.writeBytes(this.javaChannel(), allocHandle.attemptedBytesRead());
    }

    @Override
    protected int doWriteBytes(ByteBuf buf) throws Exception {
        int expectedWrittenBytes = buf.readableBytes();
        return buf.readBytes(this.javaChannel(), expectedWrittenBytes);
    }

    @Override
    protected long doWriteFileRegion(FileRegion region) throws Exception {
        long position = region.transferred();
        return region.transferTo(this.javaChannel(), position);
    }

    private void adjustMaxBytesPerGatheringWrite(int attempted, int written, int oldMaxBytesPerGatheringWrite) {
        if (attempted == written) {
            if (attempted << 1 > oldMaxBytesPerGatheringWrite) {
                ((NioDomainSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite(attempted << 1);
            }
        } else if (attempted > 4096 && written < attempted >>> 1) {
            ((NioDomainSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite(attempted >>> 1);
        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        SocketChannel ch = this.javaChannel();
        int writeSpinCount = this.config().getWriteSpinCount();
        do {
            if (in.isEmpty()) {
                this.clearOpWrite();
                return;
            }
            int maxBytesPerGatheringWrite = ((NioDomainSocketChannelConfig)this.config).getMaxBytesPerGatheringWrite();
            ByteBuffer[] nioBuffers = in.nioBuffers(1024, maxBytesPerGatheringWrite);
            int nioBufferCnt = in.nioBufferCount();
            switch (nioBufferCnt) {
                case 0: {
                    writeSpinCount -= this.doWrite0(in);
                    break;
                }
                case 1: {
                    ByteBuffer buffer = nioBuffers[0];
                    int attemptedBytes = buffer.remaining();
                    int localWrittenBytes = ch.write(buffer);
                    if (localWrittenBytes <= 0) {
                        this.incompleteWrite(true);
                        return;
                    }
                    this.adjustMaxBytesPerGatheringWrite(attemptedBytes, localWrittenBytes, maxBytesPerGatheringWrite);
                    in.removeBytes(localWrittenBytes);
                    --writeSpinCount;
                    break;
                }
                default: {
                    long attemptedBytes = in.nioBufferSize();
                    long localWrittenBytes = ch.write(nioBuffers, 0, nioBufferCnt);
                    if (localWrittenBytes <= 0L) {
                        this.incompleteWrite(true);
                        return;
                    }
                    this.adjustMaxBytesPerGatheringWrite((int)attemptedBytes, (int)localWrittenBytes, maxBytesPerGatheringWrite);
                    in.removeBytes(localWrittenBytes);
                    --writeSpinCount;
                    break;
                }
            }
        } while (writeSpinCount > 0);
        this.incompleteWrite(writeSpinCount < 0);
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioSocketChannelUnsafe();
    }

    private final class NioDomainSocketChannelConfig
    extends DefaultChannelConfig
    implements DuplexChannelConfig {
        private volatile boolean allowHalfClosure;
        private volatile int maxBytesPerGatheringWrite;
        private final SocketChannel javaChannel;

        private NioDomainSocketChannelConfig(NioDomainSocketChannel channel, SocketChannel javaChannel) {
            super(channel);
            this.maxBytesPerGatheringWrite = Integer.MAX_VALUE;
            this.javaChannel = javaChannel;
            this.calculateMaxBytesPerGatheringWrite();
        }

        @Override
        public boolean isAllowHalfClosure() {
            return this.allowHalfClosure;
        }

        @Override
        public NioDomainSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
            this.allowHalfClosure = allowHalfClosure;
            return this;
        }

        @Override
        public Map<ChannelOption<?>, Object> getOptions() {
            ArrayList options = new ArrayList();
            options.add(ChannelOption.SO_RCVBUF);
            options.add(ChannelOption.SO_SNDBUF);
            for (ChannelOption<?> opt : NioChannelOption.getOptions(this.jdkChannel())) {
                options.add(opt);
            }
            return this.getOptions(super.getOptions(), options.toArray(new ChannelOption[0]));
        }

        @Override
        public <T> T getOption(ChannelOption<T> option) {
            if (option == ChannelOption.SO_RCVBUF) {
                return (T)Integer.valueOf(this.getReceiveBufferSize());
            }
            if (option == ChannelOption.SO_SNDBUF) {
                return (T)Integer.valueOf(this.getSendBufferSize());
            }
            if (option instanceof NioChannelOption) {
                return NioChannelOption.getOption(this.jdkChannel(), (NioChannelOption)option);
            }
            return super.getOption(option);
        }

        @Override
        public <T> boolean setOption(ChannelOption<T> option, T value) {
            if (option == ChannelOption.SO_RCVBUF) {
                this.validate(option, value);
                this.setReceiveBufferSize((Integer)value);
            } else if (option == ChannelOption.SO_SNDBUF) {
                this.validate(option, value);
                this.setSendBufferSize((Integer)value);
            } else {
                if (option instanceof NioChannelOption) {
                    return NioChannelOption.setOption(this.jdkChannel(), (NioChannelOption)option, value);
                }
                return super.setOption(option, value);
            }
            return true;
        }

        private int getReceiveBufferSize() {
            try {
                return this.javaChannel.getOption(StandardSocketOptions.SO_RCVBUF);
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
        }

        private NioDomainSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
            try {
                this.javaChannel.setOption((SocketOption)StandardSocketOptions.SO_RCVBUF, (Object)receiveBufferSize);
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
            return this;
        }

        private int getSendBufferSize() {
            try {
                return this.javaChannel.getOption(StandardSocketOptions.SO_SNDBUF);
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
        }

        private NioDomainSocketChannelConfig setSendBufferSize(int sendBufferSize) {
            try {
                this.javaChannel.setOption((SocketOption)StandardSocketOptions.SO_SNDBUF, (Object)sendBufferSize);
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
            super.setConnectTimeoutMillis(connectTimeoutMillis);
            return this;
        }

        @Override
        @Deprecated
        public NioDomainSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
            super.setMaxMessagesPerRead(maxMessagesPerRead);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
            super.setWriteSpinCount(writeSpinCount);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
            super.setAllocator(allocator);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
            super.setRecvByteBufAllocator(allocator);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setAutoRead(boolean autoRead) {
            super.setAutoRead(autoRead);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setAutoClose(boolean autoClose) {
            super.setAutoClose(autoClose);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
            super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
            super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
            super.setWriteBufferWaterMark(writeBufferWaterMark);
            return this;
        }

        @Override
        public NioDomainSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
            super.setMessageSizeEstimator(estimator);
            return this;
        }

        @Override
        protected void autoReadCleared() {
            NioDomainSocketChannel.this.clearReadPending();
        }

        void setMaxBytesPerGatheringWrite(int maxBytesPerGatheringWrite) {
            this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
        }

        int getMaxBytesPerGatheringWrite() {
            return this.maxBytesPerGatheringWrite;
        }

        private void calculateMaxBytesPerGatheringWrite() {
            int newSendBufferSize = this.getSendBufferSize() << 1;
            if (newSendBufferSize > 0) {
                this.setMaxBytesPerGatheringWrite(newSendBufferSize);
            }
        }

        private SocketChannel jdkChannel() {
            return this.javaChannel;
        }
    }

    private final class NioSocketChannelUnsafe
    extends AbstractNioByteChannel.NioByteUnsafe {
        private NioSocketChannelUnsafe() {
        }
    }
}

