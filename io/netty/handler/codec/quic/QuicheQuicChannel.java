/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.quic.QLogConfiguration;
import io.netty.handler.codec.quic.Quic;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicChannelConfig;
import io.netty.handler.codec.quic.QuicClientSessionCache;
import io.netty.handler.codec.quic.QuicClosedChannelException;
import io.netty.handler.codec.quic.QuicConnectionAddress;
import io.netty.handler.codec.quic.QuicConnectionCloseEvent;
import io.netty.handler.codec.quic.QuicConnectionIdGenerator;
import io.netty.handler.codec.quic.QuicConnectionPathStats;
import io.netty.handler.codec.quic.QuicConnectionStats;
import io.netty.handler.codec.quic.QuicDatagramExtensionEvent;
import io.netty.handler.codec.quic.QuicPathEvent;
import io.netty.handler.codec.quic.QuicResetTokenGenerator;
import io.netty.handler.codec.quic.QuicSslEngine;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamIdGenerator;
import io.netty.handler.codec.quic.QuicStreamLimitChangedEvent;
import io.netty.handler.codec.quic.QuicStreamType;
import io.netty.handler.codec.quic.QuicTimeoutClosedChannelException;
import io.netty.handler.codec.quic.QuicTransportParameters;
import io.netty.handler.codec.quic.Quiche;
import io.netty.handler.codec.quic.QuicheQuicChannelAddress;
import io.netty.handler.codec.quic.QuicheQuicChannelConfig;
import io.netty.handler.codec.quic.QuicheQuicConnection;
import io.netty.handler.codec.quic.QuicheQuicConnectionPathStats;
import io.netty.handler.codec.quic.QuicheQuicConnectionStats;
import io.netty.handler.codec.quic.QuicheQuicSslEngine;
import io.netty.handler.codec.quic.QuicheQuicStreamChannel;
import io.netty.handler.codec.quic.QuicheRecvInfo;
import io.netty.handler.codec.quic.QuicheSendInfo;
import io.netty.handler.codec.quic.SegmentedDatagramPacketAllocator;
import io.netty.handler.codec.quic.SockaddrIn;
import io.netty.handler.codec.quic.SslEarlyDataReadyEvent;
import io.netty.handler.ssl.SniCompletionEvent;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.AttributeKey;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicChannel
extends AbstractChannel
implements QuicChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(QuicheQuicChannel.class);
    private static final String QLOG_FILE_EXTENSION = ".qlog";
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
    private long[] readableStreams = new long[4];
    private long[] writableStreams = new long[4];
    private final LongObjectMap<QuicheQuicStreamChannel> streams = new LongObjectHashMap<QuicheQuicStreamChannel>();
    private final QuicheQuicChannelConfig config;
    private final boolean server;
    private final QuicStreamIdGenerator idGenerator;
    private final ChannelHandler streamHandler;
    private final Map.Entry<ChannelOption<?>, Object>[] streamOptionsArray;
    private final Map.Entry<AttributeKey<?>, Object>[] streamAttrsArray;
    private final TimeoutHandler timeoutHandler;
    private final QuicConnectionIdGenerator connectionIdAddressGenerator;
    private final QuicResetTokenGenerator resetTokenGenerator;
    private final Set<ByteBuffer> sourceConnectionIds = new HashSet<ByteBuffer>();
    private Consumer<QuicheQuicChannel> freeTask;
    private Executor sslTaskExecutor;
    private boolean inFireChannelReadCompleteQueue;
    private boolean fireChannelReadCompletePending;
    private ByteBuf finBuffer;
    private ByteBuf outErrorCodeBuffer;
    private ChannelPromise connectPromise;
    private ScheduledFuture<?> connectTimeoutFuture;
    private QuicConnectionAddress connectAddress;
    private CloseData closeData;
    private QuicConnectionCloseEvent connectionCloseEvent;
    private QuicConnectionStats statsAtClose;
    private boolean supportsDatagram;
    private boolean recvDatagramPending;
    private boolean datagramReadable;
    private boolean recvStreamPending;
    private boolean streamReadable;
    private boolean handshakeCompletionNotified;
    private boolean earlyDataReadyNotified;
    private int reantranceGuard;
    private static final int IN_RECV = 2;
    private static final int IN_CONNECTION_SEND = 4;
    private static final int IN_HANDLE_WRITABLE_STREAMS = 8;
    private volatile ChannelState state = ChannelState.OPEN;
    private volatile boolean timedOut;
    private volatile String traceId;
    private volatile QuicheQuicConnection connection;
    private volatile InetSocketAddress local;
    private volatile InetSocketAddress remote;
    private final ChannelFutureListener continueSendingListener = f -> {
        if (this.connectionSend(this.connection) != SendResult.NONE) {
            this.flushParent();
        }
    };
    private static final AtomicLongFieldUpdater<QuicheQuicChannel> UNI_STREAMS_LEFT_UPDATER = AtomicLongFieldUpdater.newUpdater(QuicheQuicChannel.class, "uniStreamsLeft");
    private volatile long uniStreamsLeft;
    private static final AtomicLongFieldUpdater<QuicheQuicChannel> BIDI_STREAMS_LEFT_UPDATER = AtomicLongFieldUpdater.newUpdater(QuicheQuicChannel.class, "bidiStreamsLeft");
    private volatile long bidiStreamsLeft;
    private static final int MAX_ARRAY_LEN = 128;

    private QuicheQuicChannel(Channel parent, boolean server, @Nullable ByteBuffer key, InetSocketAddress local, InetSocketAddress remote, boolean supportsDatagram, ChannelHandler streamHandler, Map.Entry<ChannelOption<?>, Object>[] streamOptionsArray, Map.Entry<AttributeKey<?>, Object>[] streamAttrsArray, @Nullable Consumer<QuicheQuicChannel> freeTask, @Nullable Executor sslTaskExecutor, @Nullable QuicConnectionIdGenerator connectionIdAddressGenerator, @Nullable QuicResetTokenGenerator resetTokenGenerator) {
        super(parent);
        this.config = new QuicheQuicChannelConfig(this);
        this.freeTask = freeTask;
        this.server = server;
        this.idGenerator = new QuicStreamIdGenerator(server);
        this.connectionIdAddressGenerator = connectionIdAddressGenerator;
        this.resetTokenGenerator = resetTokenGenerator;
        if (key != null) {
            this.sourceConnectionIds.add(key);
        }
        this.supportsDatagram = supportsDatagram;
        this.local = local;
        this.remote = remote;
        this.streamHandler = streamHandler;
        this.streamOptionsArray = streamOptionsArray;
        this.streamAttrsArray = streamAttrsArray;
        this.timeoutHandler = new TimeoutHandler();
        this.sslTaskExecutor = sslTaskExecutor == null ? ImmediateExecutor.INSTANCE : sslTaskExecutor;
    }

    static QuicheQuicChannel forClient(Channel parent, InetSocketAddress local, InetSocketAddress remote, ChannelHandler streamHandler, Map.Entry<ChannelOption<?>, Object>[] streamOptionsArray, Map.Entry<AttributeKey<?>, Object>[] streamAttrsArray) {
        return new QuicheQuicChannel(parent, false, null, local, remote, false, streamHandler, streamOptionsArray, streamAttrsArray, null, null, null, null);
    }

    static QuicheQuicChannel forServer(Channel parent, ByteBuffer key, InetSocketAddress local, InetSocketAddress remote, boolean supportsDatagram, ChannelHandler streamHandler, Map.Entry<ChannelOption<?>, Object>[] streamOptionsArray, Map.Entry<AttributeKey<?>, Object>[] streamAttrsArray, Consumer<QuicheQuicChannel> freeTask, Executor sslTaskExecutor, QuicConnectionIdGenerator connectionIdAddressGenerator, QuicResetTokenGenerator resetTokenGenerator) {
        return new QuicheQuicChannel(parent, true, key, local, remote, supportsDatagram, streamHandler, streamOptionsArray, streamAttrsArray, freeTask, sslTaskExecutor, connectionIdAddressGenerator, resetTokenGenerator);
    }

    private static long[] growIfNeeded(long[] array, int maxLength) {
        if (maxLength > array.length) {
            if (array.length == 128) {
                return array;
            }
            return new long[Math.min(128, array.length + 4)];
        }
        return array;
    }

    @Override
    public boolean isTimedOut() {
        return this.timedOut;
    }

    @Override
    public SSLEngine sslEngine() {
        QuicheQuicConnection connection = this.connection;
        return connection == null ? null : connection.engine();
    }

    private void notifyAboutHandshakeCompletionIfNeeded(QuicheQuicConnection conn, @Nullable SSLHandshakeException cause) {
        if (this.handshakeCompletionNotified) {
            return;
        }
        if (cause != null) {
            this.pipeline().fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
            return;
        }
        if (conn.isFreed()) {
            return;
        }
        switch (this.connection.engine().getHandshakeStatus()) {
            case NOT_HANDSHAKING: 
            case FINISHED: {
                this.handshakeCompletionNotified = true;
                this.pipeline().fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
                break;
            }
        }
    }

    @Override
    public long peerAllowedStreams(QuicStreamType type) {
        switch (type) {
            case BIDIRECTIONAL: {
                return this.bidiStreamsLeft;
            }
            case UNIDIRECTIONAL: {
                return this.uniStreamsLeft;
            }
        }
        return 0L;
    }

    void attachQuicheConnection(QuicheQuicConnection connection) {
        this.connection = connection;
        byte[] traceId = Quiche.quiche_conn_trace_id(connection.address());
        if (traceId != null) {
            this.traceId = new String(traceId);
        }
        connection.init(this.local, this.remote, sniHostname -> this.pipeline().fireUserEventTriggered(new SniCompletionEvent((String)sniHostname)));
        QLogConfiguration configuration = this.config.getQLogConfiguration();
        if (configuration != null) {
            String fileName;
            File file = new File(configuration.path());
            if (file.isDirectory()) {
                file.mkdir();
                fileName = this.traceId != null ? configuration.path() + File.separatorChar + this.traceId + "-" + this.id().asShortText() + QLOG_FILE_EXTENSION : configuration.path() + File.separatorChar + this.id().asShortText() + QLOG_FILE_EXTENSION;
            } else {
                fileName = configuration.path();
            }
            if (!Quiche.quiche_conn_set_qlog_path(connection.address(), fileName, configuration.logTitle(), configuration.logDescription())) {
                logger.info("Unable to create qlog file: {} ", (Object)fileName);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void connectNow(Function<QuicChannel, ? extends QuicSslEngine> engineProvider, Executor sslTaskExecutor, Consumer<QuicheQuicChannel> freeTask, long configAddr, int localConnIdLength, boolean supportsDatagram, ByteBuffer fromSockaddrMemory, ByteBuffer toSockaddrMemory) throws Exception {
        QuicSslEngine engine;
        ByteBuffer connectId;
        assert (this.connection == null);
        assert (this.traceId == null);
        assert (this.sourceConnectionIds.isEmpty());
        this.sslTaskExecutor = sslTaskExecutor;
        this.freeTask = freeTask;
        QuicConnectionAddress address = this.connectAddress;
        if (address == QuicConnectionAddress.EPHEMERAL) {
            address = QuicConnectionAddress.random(localConnIdLength);
        }
        if ((connectId = address.id()).remaining() != localConnIdLength) {
            this.failConnectPromiseAndThrow(new IllegalArgumentException("connectionAddress has length " + connectId.remaining() + " instead of " + localConnIdLength));
        }
        if (!((engine = engineProvider.apply(this)) instanceof QuicheQuicSslEngine)) {
            this.failConnectPromiseAndThrow(new IllegalArgumentException("QuicSslEngine is not of type " + QuicheQuicSslEngine.class.getSimpleName()));
            return;
        }
        if (!engine.getUseClientMode()) {
            this.failConnectPromiseAndThrow(new IllegalArgumentException("QuicSslEngine is not create in client mode"));
        }
        QuicheQuicSslEngine quicheEngine = (QuicheQuicSslEngine)engine;
        ByteBuf idBuffer = this.alloc().directBuffer(connectId.remaining()).writeBytes(connectId.duplicate());
        try {
            byte[] sessionBytes;
            int fromSockaddrLen = SockaddrIn.setAddress(fromSockaddrMemory, this.local);
            int toSockaddrLen = SockaddrIn.setAddress(toSockaddrMemory, this.remote);
            QuicheQuicConnection connection = quicheEngine.createConnection(ssl -> Quiche.quiche_conn_new_with_tls(Quiche.readerMemoryAddress(idBuffer), idBuffer.readableBytes(), -1L, -1, Quiche.memoryAddressWithPosition(fromSockaddrMemory), fromSockaddrLen, Quiche.memoryAddressWithPosition(toSockaddrMemory), toSockaddrLen, configAddr, ssl, false));
            if (connection == null) {
                this.failConnectPromiseAndThrow(new ConnectException());
                return;
            }
            this.attachQuicheConnection(connection);
            QuicClientSessionCache sessionCache = quicheEngine.ctx.getSessionCache();
            if (sessionCache != null && (sessionBytes = sessionCache.getSession(quicheEngine.getSession().getPeerHost(), quicheEngine.getSession().getPeerPort())) != null) {
                Quiche.quiche_conn_set_session(connection.address(), sessionBytes);
            }
            this.supportsDatagram = supportsDatagram;
            this.sourceConnectionIds.add(connectId);
        }
        finally {
            idBuffer.release();
        }
    }

    private void failConnectPromiseAndThrow(Exception e) throws Exception {
        this.tryFailConnectPromise(e);
        throw e;
    }

    private boolean tryFailConnectPromise(Exception e) {
        ChannelPromise promise = this.connectPromise;
        if (promise != null) {
            this.connectPromise = null;
            promise.tryFailure(e);
            return true;
        }
        return false;
    }

    Set<ByteBuffer> sourceConnectionIds() {
        return this.sourceConnectionIds;
    }

    boolean markInFireChannelReadCompleteQueue() {
        if (this.inFireChannelReadCompleteQueue) {
            return false;
        }
        this.inFireChannelReadCompleteQueue = true;
        return true;
    }

    private void failPendingConnectPromise() {
        ChannelPromise promise = this.connectPromise;
        if (promise != null) {
            this.connectPromise = null;
            promise.tryFailure(new QuicClosedChannelException(this.connectionCloseEvent));
        }
    }

    void forceClose() {
        this.unsafe().close(this.voidPromise());
    }

    @Override
    protected DefaultChannelPipeline newChannelPipeline() {
        return new DefaultChannelPipeline(this){

            @Override
            protected void onUnhandledInboundMessage(ChannelHandlerContext ctx, Object msg) {
                if (msg instanceof QuicStreamChannel) {
                    QuicStreamChannel channel = (QuicStreamChannel)msg;
                    Quic.setupChannel(channel, QuicheQuicChannel.this.streamOptionsArray, QuicheQuicChannel.this.streamAttrsArray, QuicheQuicChannel.this.streamHandler, logger);
                    ctx.channel().eventLoop().register(channel);
                } else {
                    super.onUnhandledInboundMessage(ctx, msg);
                }
            }
        };
    }

    @Override
    public QuicChannel flush() {
        super.flush();
        return this;
    }

    @Override
    public QuicChannel read() {
        super.read();
        return this;
    }

    @Override
    public Future<QuicStreamChannel> createStream(QuicStreamType type, @Nullable ChannelHandler handler, Promise<QuicStreamChannel> promise) {
        if (this.eventLoop().inEventLoop()) {
            ((QuicChannelUnsafe)this.unsafe()).connectStream(type, handler, promise);
        } else {
            this.eventLoop().execute(() -> ((QuicChannelUnsafe)this.unsafe()).connectStream(type, handler, promise));
        }
        return promise;
    }

    @Override
    public ChannelFuture close(boolean applicationClose, int error, ByteBuf reason, ChannelPromise promise) {
        if (this.eventLoop().inEventLoop()) {
            this.close0(applicationClose, error, reason, promise);
        } else {
            this.eventLoop().execute(() -> this.close0(applicationClose, error, reason, promise));
        }
        return promise;
    }

    private void close0(boolean applicationClose, int error, ByteBuf reason, ChannelPromise promise) {
        if (this.closeData == null) {
            if (!reason.hasMemoryAddress()) {
                ByteBuf copy = this.alloc().directBuffer(reason.readableBytes()).writeBytes(reason);
                reason.release();
                reason = copy;
            }
            this.closeData = new CloseData(applicationClose, error, reason);
            promise.addListener((GenericFutureListener)this.closeData);
        } else {
            reason.release();
        }
        this.close(promise);
    }

    @Override
    public String toString() {
        String traceId = this.traceId;
        if (traceId == null) {
            return "()" + super.toString();
        }
        return '(' + traceId + ')' + super.toString();
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new QuicChannelUnsafe();
    }

    @Override
    protected boolean isCompatible(EventLoop eventLoop) {
        return this.parent().eventLoop() == eventLoop;
    }

    @Override
    @Nullable
    protected QuicConnectionAddress localAddress0() {
        QuicheQuicConnection connection = this.connection;
        return connection == null ? null : connection.sourceId();
    }

    @Override
    @Nullable
    protected QuicConnectionAddress remoteAddress0() {
        QuicheQuicConnection connection = this.connection;
        return connection == null ? null : connection.destinationId();
    }

    @Override
    @Nullable
    public QuicConnectionAddress localAddress() {
        return this.localAddress0();
    }

    @Override
    @Nullable
    public QuicConnectionAddress remoteAddress() {
        return this.remoteAddress0();
    }

    @Override
    @Nullable
    public SocketAddress localSocketAddress() {
        return this.local;
    }

    @Override
    @Nullable
    public SocketAddress remoteSocketAddress() {
        return this.remote;
    }

    @Override
    protected void doBind(SocketAddress socketAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doClose() throws Exception {
        ByteBuf reason;
        int err;
        boolean app;
        if (this.state == ChannelState.CLOSED) {
            return;
        }
        this.state = ChannelState.CLOSED;
        QuicheQuicConnection conn = this.connection;
        if (conn == null || conn.isFreed()) {
            if (this.closeData != null) {
                this.closeData.reason.release();
                this.closeData = null;
            }
            this.failPendingConnectPromise();
            return;
        }
        SendResult sendResult = this.connectionSend(conn);
        if (this.closeData == null) {
            app = false;
            err = 0;
            reason = Unpooled.EMPTY_BUFFER;
        } else {
            app = this.closeData.applicationClose;
            err = this.closeData.err;
            reason = this.closeData.reason;
            this.closeData = null;
        }
        this.failPendingConnectPromise();
        try {
            int res = Quiche.quiche_conn_close(conn.address(), app, err, Quiche.readerMemoryAddress(reason), reason.readableBytes());
            if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
                throw Quiche.convertToException(res);
            }
            if (this.connectionSend(conn) == SendResult.SOME) {
                sendResult = SendResult.SOME;
            }
        }
        finally {
            this.statsAtClose = this.collectStats0(conn, this.eventLoop().newPromise());
            try {
                this.timedOut = Quiche.quiche_conn_is_timed_out(conn.address());
                this.closeStreams();
                if (this.finBuffer != null) {
                    this.finBuffer.release();
                    this.finBuffer = null;
                }
                if (this.outErrorCodeBuffer != null) {
                    this.outErrorCodeBuffer.release();
                    this.outErrorCodeBuffer = null;
                }
            }
            finally {
                if (sendResult == SendResult.SOME) {
                    this.forceFlushParent();
                } else {
                    this.flushParent();
                }
                conn.free();
                if (this.freeTask != null) {
                    this.freeTask.accept(this);
                }
                this.timeoutHandler.cancel();
                this.local = null;
                this.remote = null;
            }
        }
    }

    @Override
    protected void doBeginRead() {
        this.recvDatagramPending = true;
        this.recvStreamPending = true;
        if (this.datagramReadable || this.streamReadable) {
            ((QuicChannelUnsafe)this.unsafe()).recv();
        }
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (msg instanceof ByteBuf) {
            return msg;
        }
        throw new UnsupportedOperationException("Unsupported message type: " + StringUtil.simpleClassName(msg));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) throws Exception {
        if (!this.supportsDatagram) {
            throw new UnsupportedOperationException("Datagram extension is not supported");
        }
        boolean sendSomething = false;
        boolean retry = false;
        QuicheQuicConnection conn = this.connection;
        block7: while (true) {
            while (true) {
                int res;
                ByteBuf buffer;
                if ((buffer = (ByteBuf)channelOutboundBuffer.current()) == null) {
                    return;
                }
                int readable = buffer.readableBytes();
                if (readable == 0) {
                    channelOutboundBuffer.remove();
                    continue;
                }
                if (!buffer.isDirect() || buffer.nioBufferCount() > 1) {
                    ByteBuf tmpBuffer = this.alloc().directBuffer(readable);
                    try {
                        tmpBuffer.writeBytes(buffer, buffer.readerIndex(), readable);
                        res = QuicheQuicChannel.sendDatagram(conn, tmpBuffer);
                    }
                    finally {
                        tmpBuffer.release();
                    }
                } else {
                    res = QuicheQuicChannel.sendDatagram(conn, buffer);
                }
                if (res >= 0) {
                    channelOutboundBuffer.remove();
                    sendSomething = true;
                    retry = false;
                    continue block7;
                }
                if (res == Quiche.QUICHE_ERR_BUFFER_TOO_SHORT) {
                    retry = false;
                    channelOutboundBuffer.remove(new BufferUnderflowException());
                    continue block7;
                }
                if (res == Quiche.QUICHE_ERR_INVALID_STATE) {
                    throw new UnsupportedOperationException("Remote peer does not support Datagram extension");
                }
                if (res != Quiche.QUICHE_ERR_DONE) throw Quiche.convertToException(res);
                if (retry) {
                    while (channelOutboundBuffer.remove()) {
                    }
                    return;
                }
                sendSomething = false;
                if (this.connectionSend(conn) != SendResult.NONE) {
                    this.forceFlushParent();
                }
                retry = true;
            }
            break;
        }
        finally {
            if (sendSomething && this.connectionSend(conn) != SendResult.NONE) {
                this.flushParent();
            }
        }
    }

    private static int sendDatagram(QuicheQuicConnection conn, ByteBuf buf) throws ClosedChannelException {
        return Quiche.quiche_conn_dgram_send(QuicheQuicChannel.connectionAddressChecked(conn), Quiche.readerMemoryAddress(buf), buf.readableBytes());
    }

    @Override
    public QuicChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return this.state != ChannelState.CLOSED;
    }

    @Override
    public boolean isActive() {
        return this.state == ChannelState.ACTIVE;
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    private void flushParent() {
        if (!this.inFireChannelReadCompleteQueue) {
            this.forceFlushParent();
        }
    }

    private void forceFlushParent() {
        this.parent().flush();
    }

    private static long connectionAddressChecked(@Nullable QuicheQuicConnection conn) throws ClosedChannelException {
        if (conn == null || conn.isFreed()) {
            throw new ClosedChannelException();
        }
        return conn.address();
    }

    boolean freeIfClosed() {
        QuicheQuicConnection conn = this.connection;
        if (conn == null || conn.isFreed()) {
            return true;
        }
        if (conn.isClosed()) {
            this.unsafe().close(this.newPromise());
            return true;
        }
        return false;
    }

    private void closeStreams() {
        if (this.streams.isEmpty()) {
            return;
        }
        ClosedChannelException closedChannelException = this.isTimedOut() ? new QuicTimeoutClosedChannelException() : new ClosedChannelException();
        for (QuicheQuicStreamChannel stream : this.streams.values().toArray(new QuicheQuicStreamChannel[0])) {
            stream.unsafe().close(closedChannelException, this.voidPromise());
        }
        this.streams.clear();
    }

    void streamPriority(long streamId, byte priority, boolean incremental) throws Exception {
        int res = Quiche.quiche_conn_stream_priority(QuicheQuicChannel.connectionAddressChecked(this.connection), streamId, priority, incremental);
        if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
            throw Quiche.convertToException(res);
        }
    }

    void streamClosed(long streamId) {
        this.streams.remove(streamId);
    }

    boolean isStreamLocalCreated(long streamId) {
        return (streamId & 1L) == (long)(this.server ? 1 : 0);
    }

    QuicStreamType streamType(long streamId) {
        return (streamId & 2L) == 0L ? QuicStreamType.BIDIRECTIONAL : QuicStreamType.UNIDIRECTIONAL;
    }

    void streamShutdown(long streamId, boolean read, boolean write, int err, ChannelPromise promise) {
        long connectionAddress;
        QuicheQuicConnection conn = this.connection;
        try {
            connectionAddress = QuicheQuicChannel.connectionAddressChecked(conn);
        }
        catch (ClosedChannelException e) {
            promise.setFailure(e);
            return;
        }
        int res = 0;
        if (read) {
            res |= Quiche.quiche_conn_stream_shutdown(connectionAddress, streamId, Quiche.QUICHE_SHUTDOWN_READ, err);
        }
        if (write) {
            res |= Quiche.quiche_conn_stream_shutdown(connectionAddress, streamId, Quiche.QUICHE_SHUTDOWN_WRITE, err);
        }
        if (this.connectionSend(conn) != SendResult.NONE) {
            this.forceFlushParent();
        }
        if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
            promise.setFailure(Quiche.convertToException(res));
        } else {
            promise.setSuccess();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void streamSendFin(long streamId) throws Exception {
        QuicheQuicConnection conn = this.connection;
        try {
            int res = this.streamSend0(conn, streamId, Unpooled.EMPTY_BUFFER, true);
            if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
                throw Quiche.convertToException(res);
            }
        }
        finally {
            if (this.connectionSend(conn) != SendResult.NONE) {
                this.flushParent();
            }
        }
    }

    int streamSend(long streamId, ByteBuf buffer, boolean fin) throws ClosedChannelException {
        QuicheQuicConnection conn = this.connection;
        if (buffer.nioBufferCount() == 1) {
            return this.streamSend0(conn, streamId, buffer, fin);
        }
        ByteBuffer[] nioBuffers = buffer.nioBuffers();
        int lastIdx = nioBuffers.length - 1;
        int res = 0;
        for (int i = 0; i < lastIdx; ++i) {
            ByteBuffer nioBuffer = nioBuffers[i];
            while (nioBuffer.hasRemaining()) {
                int localRes = this.streamSend(conn, streamId, nioBuffer, false);
                if (localRes <= 0) {
                    return res;
                }
                res += localRes;
                nioBuffer.position(nioBuffer.position() + localRes);
            }
        }
        int localRes = this.streamSend(conn, streamId, nioBuffers[lastIdx], fin);
        if (localRes > 0) {
            res += localRes;
        }
        return res;
    }

    void connectionSendAndFlush() {
        if (this.inFireChannelReadCompleteQueue || (this.reantranceGuard & 8) != 0) {
            return;
        }
        if (this.connectionSend(this.connection) != SendResult.NONE) {
            this.flushParent();
        }
    }

    private int streamSend0(QuicheQuicConnection conn, long streamId, ByteBuf buffer, boolean fin) throws ClosedChannelException {
        return Quiche.quiche_conn_stream_send(QuicheQuicChannel.connectionAddressChecked(conn), streamId, Quiche.readerMemoryAddress(buffer), buffer.readableBytes(), fin);
    }

    private int streamSend(QuicheQuicConnection conn, long streamId, ByteBuffer buffer, boolean fin) throws ClosedChannelException {
        return Quiche.quiche_conn_stream_send(QuicheQuicChannel.connectionAddressChecked(conn), streamId, Quiche.memoryAddressWithPosition(buffer), buffer.remaining(), fin);
    }

    StreamRecvResult streamRecv(long streamId, ByteBuf buffer) throws Exception {
        QuicheQuicConnection conn = this.connection;
        long connAddr = QuicheQuicChannel.connectionAddressChecked(conn);
        if (this.finBuffer == null) {
            this.finBuffer = this.alloc().directBuffer(1);
        }
        if (this.outErrorCodeBuffer == null) {
            this.outErrorCodeBuffer = this.alloc().directBuffer(8);
        }
        this.outErrorCodeBuffer.setLongLE(0, -1L);
        int writerIndex = buffer.writerIndex();
        int recvLen = Quiche.quiche_conn_stream_recv(connAddr, streamId, Quiche.writerMemoryAddress(buffer), buffer.writableBytes(), Quiche.writerMemoryAddress(this.finBuffer), Quiche.writerMemoryAddress(this.outErrorCodeBuffer));
        long errorCode = this.outErrorCodeBuffer.getLongLE(0);
        if (recvLen == Quiche.QUICHE_ERR_DONE) {
            return StreamRecvResult.DONE;
        }
        if (recvLen < 0) {
            throw Quiche.convertToException(recvLen, errorCode);
        }
        buffer.writerIndex(writerIndex + recvLen);
        return this.finBuffer.getBoolean(0) ? StreamRecvResult.FIN : StreamRecvResult.OK;
    }

    void recv(InetSocketAddress sender, InetSocketAddress recipient, ByteBuf buffer) {
        ((QuicChannelUnsafe)this.unsafe()).connectionRecv(sender, recipient, buffer);
    }

    List<ByteBuffer> retiredSourceConnectionId() {
        byte[] retired;
        QuicheQuicConnection connection = this.connection;
        if (connection == null || connection.isFreed()) {
            return Collections.emptyList();
        }
        long connAddr = connection.address();
        assert (connAddr != -1L);
        ArrayList<ByteBuffer> retiredSourceIds = null;
        while ((retired = Quiche.quiche_conn_retired_scid_next(connAddr)) != null) {
            if (retiredSourceIds == null) {
                retiredSourceIds = new ArrayList<ByteBuffer>();
            }
            ByteBuffer retiredId = ByteBuffer.wrap(retired);
            retiredSourceIds.add(retiredId);
            this.sourceConnectionIds.remove(retiredId);
        }
        if (retiredSourceIds == null) {
            return Collections.emptyList();
        }
        return retiredSourceIds;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    List<ByteBuffer> newSourceConnectionIds() {
        if (this.connectionIdAddressGenerator != null && this.resetTokenGenerator != null) {
            QuicheQuicConnection connection = this.connection;
            if (connection == null || connection.isFreed()) {
                return Collections.emptyList();
            }
            long connAddr = connection.address();
            int left = Quiche.quiche_conn_scids_left(connAddr);
            if (left > 0) {
                QuicConnectionAddress sourceAddr = connection.sourceId();
                if (sourceAddr == null) {
                    return Collections.emptyList();
                }
                ArrayList<ByteBuffer> generatedIds = new ArrayList<ByteBuffer>(left);
                boolean sendAndFlush = false;
                ByteBuffer key = sourceAddr.id();
                ByteBuf connIdBuffer = this.alloc().directBuffer(key.remaining());
                byte[] resetTokenArray = new byte[16];
                try {
                    do {
                        ByteBuffer srcId = this.connectionIdAddressGenerator.newId(key.duplicate(), key.remaining()).asReadOnlyBuffer();
                        connIdBuffer.clear();
                        connIdBuffer.writeBytes(srcId.duplicate());
                        ByteBuffer resetToken = this.resetTokenGenerator.newResetToken(srcId.duplicate());
                        resetToken.get(resetTokenArray);
                        long result = Quiche.quiche_conn_new_scid(connAddr, Quiche.memoryAddress(connIdBuffer, 0, connIdBuffer.readableBytes()), connIdBuffer.readableBytes(), resetTokenArray, false, -1L);
                        if (result < 0L) {
                            break;
                        }
                        sendAndFlush = true;
                        generatedIds.add(srcId.duplicate());
                        this.sourceConnectionIds.add(srcId);
                    } while (--left > 0);
                }
                finally {
                    connIdBuffer.release();
                }
                if (sendAndFlush) {
                    this.connectionSendAndFlush();
                }
                return generatedIds;
            }
        }
        return Collections.emptyList();
    }

    void writable() {
        QuicheQuicConnection conn = this.connection;
        SendResult result = this.connectionSend(conn);
        this.handleWritableStreams(conn);
        if (this.connectionSend(conn) == SendResult.SOME) {
            result = SendResult.SOME;
        }
        if (result == SendResult.SOME) {
            this.forceFlushParent();
        }
        this.freeIfClosed();
    }

    long streamCapacity(long streamId) {
        QuicheQuicConnection conn = this.connection;
        if (conn.isClosed()) {
            return 0L;
        }
        return Quiche.quiche_conn_stream_capacity(conn.address(), streamId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean handleWritableStreams(QuicheQuicConnection conn) {
        if (conn.isFreed()) {
            return false;
        }
        this.reantranceGuard |= 8;
        try {
            long connAddr = conn.address();
            boolean mayNeedWrite = false;
            if (Quiche.quiche_conn_is_established(connAddr) || Quiche.quiche_conn_is_in_early_data(connAddr)) {
                long writableIterator = Quiche.quiche_conn_writable(connAddr);
                int totalWritable = 0;
                try {
                    int writable;
                    do {
                        writable = Quiche.quiche_stream_iter_next(writableIterator, this.writableStreams);
                        for (int i = 0; i < writable; ++i) {
                            long capacity;
                            long streamId = this.writableStreams[i];
                            QuicheQuicStreamChannel streamChannel = this.streams.get(streamId);
                            if (streamChannel == null || !streamChannel.writable(capacity = Quiche.quiche_conn_stream_capacity(connAddr, streamId))) continue;
                            mayNeedWrite = true;
                        }
                        if (writable <= 0) continue;
                        totalWritable += writable;
                    } while (writable >= this.writableStreams.length);
                }
                finally {
                    Quiche.quiche_stream_iter_free(writableIterator);
                }
                this.writableStreams = QuicheQuicChannel.growIfNeeded(this.writableStreams, totalWritable);
            }
            boolean bl = mayNeedWrite;
            return bl;
        }
        finally {
            this.reantranceGuard &= 0xFFFFFFF7;
        }
    }

    void recvComplete() {
        try {
            QuicheQuicConnection conn = this.connection;
            if (conn.isFreed()) {
                this.forceFlushParent();
                return;
            }
            this.fireChannelReadCompleteIfNeeded();
            this.connectionSend(conn);
            this.forceFlushParent();
            this.freeIfClosed();
        }
        finally {
            this.inFireChannelReadCompleteQueue = false;
        }
    }

    private void fireChannelReadCompleteIfNeeded() {
        if (this.fireChannelReadCompletePending) {
            this.fireChannelReadCompletePending = false;
            this.pipeline().fireChannelReadComplete();
        }
    }

    private void fireExceptionEvents(QuicheQuicConnection conn, Throwable cause) {
        if (cause instanceof SSLHandshakeException) {
            this.notifyAboutHandshakeCompletionIfNeeded(conn, (SSLHandshakeException)cause);
        }
        this.pipeline().fireExceptionCaught(cause);
    }

    private boolean runTasksDirectly() {
        return this.sslTaskExecutor == null || this.sslTaskExecutor == ImmediateExecutor.INSTANCE || this.sslTaskExecutor == ImmediateEventExecutor.INSTANCE;
    }

    private void runAllTaskSend(QuicheQuicConnection conn, Runnable task) {
        this.sslTaskExecutor.execute(this.decorateTaskSend(conn, task));
    }

    private void runAll(QuicheQuicConnection conn, Runnable task) {
        do {
            task.run();
        } while ((task = conn.sslTask()) != null);
    }

    private Runnable decorateTaskSend(QuicheQuicConnection conn, Runnable task) {
        return () -> {
            try {
                this.runAll(conn, task);
            }
            finally {
                this.eventLoop().execute(() -> {
                    if (this.connectionSend(conn) != SendResult.NONE) {
                        this.forceFlushParent();
                    }
                    this.freeIfClosed();
                });
            }
        };
    }

    private SendResult connectionSendSegments(QuicheQuicConnection conn, SegmentedDatagramPacketAllocator segmentedDatagramPacketAllocator) {
        if (conn.isClosed()) {
            return SendResult.NONE;
        }
        ArrayList<ByteBuf> bufferList = new ArrayList<ByteBuf>(segmentedDatagramPacketAllocator.maxNumSegments());
        long connAddr = conn.address();
        int maxDatagramSize = Quiche.quiche_conn_max_send_udp_payload_size(connAddr);
        SendResult sendResult = SendResult.NONE;
        boolean close = false;
        while (true) {
            int lastReadable;
            int segmentSize;
            boolean done;
            int len = QuicheQuicChannel.calculateSendBufferLength(connAddr, maxDatagramSize);
            ByteBuf out = this.alloc().directBuffer(len);
            ByteBuffer sendInfo = conn.nextSendInfo();
            InetSocketAddress sendToAddress = this.remote;
            int writerIndex = out.writerIndex();
            int written = Quiche.quiche_conn_send(connAddr, Quiche.writerMemoryAddress(out), out.writableBytes(), Quiche.memoryAddressWithPosition(sendInfo));
            if (written == 0) {
                out.release();
                continue;
            }
            if (written < 0) {
                done = true;
                if (written != Quiche.QUICHE_ERR_DONE) {
                    close = Quiche.shouldClose(written);
                    Exception e = Quiche.convertToException(written);
                    if (!this.tryFailConnectPromise(e)) {
                        this.fireExceptionEvents(conn, e);
                    }
                }
            } else {
                done = false;
            }
            int size = bufferList.size();
            if (done) {
                out.release();
                switch (size) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        this.parent().write(new DatagramPacket((ByteBuf)bufferList.get(0), sendToAddress));
                        sendResult = SendResult.SOME;
                        break;
                    }
                    default: {
                        segmentSize = QuicheQuicChannel.segmentSize(bufferList);
                        ByteBuf compositeBuffer = Unpooled.wrappedBuffer(bufferList.toArray(new ByteBuf[0]));
                        this.parent().write(segmentedDatagramPacketAllocator.newPacket(compositeBuffer, segmentSize, sendToAddress));
                        sendResult = SendResult.SOME;
                    }
                }
                bufferList.clear();
                if (close) {
                    sendResult = SendResult.CLOSE;
                }
                return sendResult;
            }
            out.writerIndex(writerIndex + written);
            segmentSize = -1;
            if (conn.isSendInfoChanged()) {
                this.remote = QuicheSendInfo.getToAddress(sendInfo);
                this.local = QuicheSendInfo.getFromAddress(sendInfo);
                if (size > 0) {
                    segmentSize = QuicheQuicChannel.segmentSize(bufferList);
                }
            } else if (size > 0 && ((lastReadable = QuicheQuicChannel.segmentSize(bufferList)) != out.readableBytes() || size == segmentedDatagramPacketAllocator.maxNumSegments())) {
                segmentSize = lastReadable;
            }
            if (segmentSize != -1) {
                boolean stop;
                if (size == 1) {
                    stop = this.writePacket(new DatagramPacket((ByteBuf)bufferList.get(0), sendToAddress), maxDatagramSize, len);
                } else {
                    ByteBuf compositeBuffer = Unpooled.wrappedBuffer(bufferList.toArray(new ByteBuf[0]));
                    stop = this.writePacket(segmentedDatagramPacketAllocator.newPacket(compositeBuffer, segmentSize, sendToAddress), maxDatagramSize, len);
                }
                bufferList.clear();
                sendResult = SendResult.SOME;
                if (stop) {
                    if (out.isReadable()) {
                        this.parent().write(new DatagramPacket(out, sendToAddress));
                    } else {
                        out.release();
                    }
                    if (close) {
                        sendResult = SendResult.CLOSE;
                    }
                    return sendResult;
                }
            }
            out.touch(bufferList);
            bufferList.add(out);
        }
    }

    private static int segmentSize(List<ByteBuf> bufferList) {
        assert (!bufferList.isEmpty());
        int size = bufferList.size();
        return bufferList.get(size - 1).readableBytes();
    }

    private SendResult connectionSendSimple(QuicheQuicConnection conn) {
        if (conn.isClosed()) {
            return SendResult.NONE;
        }
        long connAddr = conn.address();
        SendResult sendResult = SendResult.NONE;
        boolean close = false;
        int maxDatagramSize = Quiche.quiche_conn_max_send_udp_payload_size(connAddr);
        while (true) {
            ByteBuffer sendInfo = conn.nextSendInfo();
            int len = QuicheQuicChannel.calculateSendBufferLength(connAddr, maxDatagramSize);
            ByteBuf out = this.alloc().directBuffer(len);
            int writerIndex = out.writerIndex();
            int written = Quiche.quiche_conn_send(connAddr, Quiche.writerMemoryAddress(out), out.writableBytes(), Quiche.memoryAddressWithPosition(sendInfo));
            if (written == 0) {
                out.release();
                continue;
            }
            if (written < 0) {
                out.release();
                if (written != Quiche.QUICHE_ERR_DONE) {
                    close = Quiche.shouldClose(written);
                    Exception e = Quiche.convertToException(written);
                    if (!this.tryFailConnectPromise(e)) {
                        this.fireExceptionEvents(conn, e);
                    }
                }
                break;
            }
            if (conn.isSendInfoChanged()) {
                this.remote = QuicheSendInfo.getToAddress(sendInfo);
                this.local = QuicheSendInfo.getFromAddress(sendInfo);
            }
            out.writerIndex(writerIndex + written);
            boolean stop = this.writePacket(new DatagramPacket(out, this.remote), maxDatagramSize, len);
            sendResult = SendResult.SOME;
            if (stop) break;
        }
        if (close) {
            sendResult = SendResult.CLOSE;
        }
        return sendResult;
    }

    private boolean writePacket(DatagramPacket packet, int maxDatagramSize, int len) {
        ChannelFuture future = this.parent().write(packet);
        if (QuicheQuicChannel.isSendWindowUsed(maxDatagramSize, len)) {
            future.addListener((GenericFutureListener)this.continueSendingListener);
            return true;
        }
        return false;
    }

    private static boolean isSendWindowUsed(int maxDatagramSize, int len) {
        return len < maxDatagramSize;
    }

    private static int calculateSendBufferLength(long connAddr, int maxDatagramSize) {
        int len = Math.min(maxDatagramSize, Quiche.quiche_conn_send_quantum(connAddr));
        if (len <= 0) {
            return 8;
        }
        return len;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SendResult connectionSend(final QuicheQuicConnection conn) {
        if (conn.isFreed()) {
            return SendResult.NONE;
        }
        if ((this.reantranceGuard & 4) != 0) {
            this.notifyEarlyDataReadyIfNeeded(conn);
            return SendResult.NONE;
        }
        this.reantranceGuard |= 4;
        try {
            SegmentedDatagramPacketAllocator segmentedDatagramPacketAllocator = this.config.getSegmentedDatagramPacketAllocator();
            SendResult sendResult = segmentedDatagramPacketAllocator.maxNumSegments() > 0 ? this.connectionSendSegments(conn, segmentedDatagramPacketAllocator) : this.connectionSendSimple(conn);
            Runnable task = conn.sslTask();
            if (task != null) {
                if (this.runTasksDirectly()) {
                    do {
                        task.run();
                        this.notifyEarlyDataReadyIfNeeded(conn);
                    } while ((task = conn.sslTask()) != null);
                    this.eventLoop().execute(new Runnable(){

                        @Override
                        public void run() {
                            if (QuicheQuicChannel.this.connectionSend(conn) != SendResult.NONE) {
                                QuicheQuicChannel.this.forceFlushParent();
                            }
                            QuicheQuicChannel.this.freeIfClosed();
                        }
                    });
                } else {
                    this.runAllTaskSend(conn, task);
                }
            } else {
                this.notifyEarlyDataReadyIfNeeded(conn);
            }
            this.timeoutHandler.scheduleTimeout();
            SendResult sendResult2 = sendResult;
            return sendResult2;
        }
        finally {
            this.reantranceGuard &= 0xFFFFFFFB;
        }
    }

    void finishConnect() {
        assert (!this.server);
        assert (this.connection != null);
        if (this.connectionSend(this.connection) != SendResult.NONE) {
            this.flushParent();
        }
    }

    private void notifyEarlyDataReadyIfNeeded(QuicheQuicConnection conn) {
        if (!this.server && !this.earlyDataReadyNotified && !conn.isFreed() && Quiche.quiche_conn_is_in_early_data(conn.address())) {
            this.earlyDataReadyNotified = true;
            this.pipeline().fireUserEventTriggered(SslEarlyDataReadyEvent.INSTANCE);
        }
    }

    @Override
    public Future<QuicConnectionStats> collectStats(Promise<QuicConnectionStats> promise) {
        if (this.eventLoop().inEventLoop()) {
            this.collectStats0(promise);
        } else {
            this.eventLoop().execute(() -> this.collectStats0(promise));
        }
        return promise;
    }

    private void collectStats0(Promise<QuicConnectionStats> promise) {
        QuicheQuicConnection conn = this.connection;
        if (conn.isFreed()) {
            promise.setSuccess(this.statsAtClose);
            return;
        }
        this.collectStats0(this.connection, promise);
    }

    @Nullable
    private QuicConnectionStats collectStats0(QuicheQuicConnection connection, Promise<QuicConnectionStats> promise) {
        long[] stats = Quiche.quiche_conn_stats(connection.address());
        if (stats == null) {
            promise.setFailure(new IllegalStateException("native quiche_conn_stats(...) failed"));
            return null;
        }
        QuicheQuicConnectionStats connStats = new QuicheQuicConnectionStats(stats);
        promise.setSuccess(connStats);
        return connStats;
    }

    @Override
    public Future<QuicConnectionPathStats> collectPathStats(int pathIdx, Promise<QuicConnectionPathStats> promise) {
        if (this.eventLoop().inEventLoop()) {
            this.collectPathStats0(pathIdx, promise);
        } else {
            this.eventLoop().execute(() -> this.collectPathStats0(pathIdx, promise));
        }
        return promise;
    }

    private void collectPathStats0(int pathIdx, Promise<QuicConnectionPathStats> promise) {
        QuicheQuicConnection conn = this.connection;
        if (conn.isFreed()) {
            promise.setFailure(new IllegalStateException("Connection is closed"));
            return;
        }
        Object[] stats = Quiche.quiche_conn_path_stats(this.connection.address(), pathIdx);
        if (stats == null) {
            promise.setFailure(new IllegalStateException("native quiche_conn_path_stats(...) failed"));
            return;
        }
        promise.setSuccess(new QuicheQuicConnectionPathStats(stats));
    }

    @Override
    public QuicTransportParameters peerTransportParameters() {
        return this.connection.peerParameters();
    }

    static /* synthetic */ long[] access$3502(QuicheQuicChannel x0, long[] x1) {
        x0.readableStreams = x1;
        return x1;
    }

    private final class TimeoutHandler
    implements Runnable {
        private ScheduledFuture<?> timeoutFuture;

        private TimeoutHandler() {
        }

        @Override
        public void run() {
            QuicheQuicConnection conn = QuicheQuicChannel.this.connection;
            if (conn.isFreed()) {
                return;
            }
            if (!QuicheQuicChannel.this.freeIfClosed()) {
                long connAddr = conn.address();
                this.timeoutFuture = null;
                Quiche.quiche_conn_on_timeout(connAddr);
                if (!QuicheQuicChannel.this.freeIfClosed()) {
                    boolean closed;
                    if (QuicheQuicChannel.this.connectionSend(conn) != SendResult.NONE) {
                        QuicheQuicChannel.this.flushParent();
                    }
                    if (!(closed = QuicheQuicChannel.this.freeIfClosed())) {
                        this.scheduleTimeout();
                    }
                }
            }
        }

        void scheduleTimeout() {
            QuicheQuicConnection conn = QuicheQuicChannel.this.connection;
            if (conn.isFreed()) {
                this.cancel();
                return;
            }
            if (conn.isClosed()) {
                this.cancel();
                QuicheQuicChannel.this.unsafe().close(QuicheQuicChannel.this.newPromise());
                return;
            }
            long nanos = Quiche.quiche_conn_timeout_as_nanos(conn.address());
            if (nanos < 0L || nanos == Long.MAX_VALUE) {
                this.cancel();
                return;
            }
            if (this.timeoutFuture == null) {
                this.timeoutFuture = QuicheQuicChannel.this.eventLoop().schedule(this, nanos, TimeUnit.NANOSECONDS);
            } else {
                long remaining = this.timeoutFuture.getDelay(TimeUnit.NANOSECONDS);
                if (remaining <= 0L) {
                    this.cancel();
                    this.run();
                } else if (remaining > nanos) {
                    this.cancel();
                    this.timeoutFuture = QuicheQuicChannel.this.eventLoop().schedule(this, nanos, TimeUnit.NANOSECONDS);
                }
            }
        }

        void cancel() {
            if (this.timeoutFuture != null) {
                this.timeoutFuture.cancel(false);
                this.timeoutFuture = null;
            }
        }
    }

    private final class QuicChannelUnsafe
    extends AbstractChannel.AbstractUnsafe {
        private QuicChannelUnsafe() {
            super(QuicheQuicChannel.this);
        }

        void connectStream(QuicStreamType type, @Nullable ChannelHandler handler, Promise<QuicStreamChannel> promise) {
            if (!promise.setUncancellable()) {
                return;
            }
            long streamId = QuicheQuicChannel.this.idGenerator.nextStreamId(type == QuicStreamType.BIDIRECTIONAL);
            try {
                int res = QuicheQuicChannel.this.streamSend0(QuicheQuicChannel.this.connection, streamId, Unpooled.EMPTY_BUFFER, false);
                if (res < 0 && res != Quiche.QUICHE_ERR_DONE) {
                    throw Quiche.convertToException(res);
                }
            }
            catch (Exception e) {
                promise.setFailure(e);
                return;
            }
            if (type == QuicStreamType.UNIDIRECTIONAL) {
                UNI_STREAMS_LEFT_UPDATER.decrementAndGet(QuicheQuicChannel.this);
            } else {
                BIDI_STREAMS_LEFT_UPDATER.decrementAndGet(QuicheQuicChannel.this);
            }
            QuicheQuicStreamChannel streamChannel = this.addNewStreamChannel(streamId);
            if (handler != null) {
                streamChannel.pipeline().addLast(handler);
            }
            QuicheQuicChannel.this.eventLoop().register(streamChannel).addListener(f -> {
                if (f.isSuccess()) {
                    promise.setSuccess(streamChannel);
                } else {
                    promise.setFailure(f.cause());
                    QuicheQuicChannel.this.streams.remove(streamId);
                }
            });
        }

        @Override
        public void connect(SocketAddress remote, SocketAddress local, ChannelPromise channelPromise) {
            assert (QuicheQuicChannel.this.eventLoop().inEventLoop());
            if (!channelPromise.setUncancellable()) {
                return;
            }
            if (QuicheQuicChannel.this.server) {
                channelPromise.setFailure(new UnsupportedOperationException());
                return;
            }
            if (QuicheQuicChannel.this.connectPromise != null) {
                channelPromise.setFailure(new ConnectionPendingException());
                return;
            }
            if (remote instanceof QuicConnectionAddress) {
                if (!QuicheQuicChannel.this.sourceConnectionIds.isEmpty()) {
                    channelPromise.setFailure(new AlreadyConnectedException());
                    return;
                }
                QuicheQuicChannel.this.connectAddress = (QuicConnectionAddress)remote;
                QuicheQuicChannel.this.connectPromise = channelPromise;
                int connectTimeoutMillis = QuicheQuicChannel.this.config().getConnectTimeoutMillis();
                if (connectTimeoutMillis > 0) {
                    QuicheQuicChannel.this.connectTimeoutFuture = QuicheQuicChannel.this.eventLoop().schedule(() -> {
                        ChannelPromise connectPromise = QuicheQuicChannel.this.connectPromise;
                        if (connectPromise != null && !connectPromise.isDone() && connectPromise.tryFailure(new ConnectTimeoutException("connection timed out: " + remote))) {
                            this.close(this.voidPromise());
                        }
                    }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                }
                QuicheQuicChannel.this.connectPromise.addListener(future -> {
                    if (future.isCancelled()) {
                        if (QuicheQuicChannel.this.connectTimeoutFuture != null) {
                            QuicheQuicChannel.this.connectTimeoutFuture.cancel(false);
                        }
                        QuicheQuicChannel.this.connectPromise = null;
                        this.close(this.voidPromise());
                    }
                });
                QuicheQuicChannel.this.parent().connect(new QuicheQuicChannelAddress(QuicheQuicChannel.this)).addListener(f -> {
                    ChannelPromise connectPromise = QuicheQuicChannel.this.connectPromise;
                    if (connectPromise != null && !f.isSuccess()) {
                        connectPromise.tryFailure(f.cause());
                        QuicheQuicChannel.this.unsafe().closeForcibly();
                    }
                });
                return;
            }
            channelPromise.setFailure(new UnsupportedOperationException());
        }

        private void fireConnectCloseEventIfNeeded(QuicheQuicConnection conn) {
            if (QuicheQuicChannel.this.connectionCloseEvent == null && !conn.isFreed()) {
                QuicheQuicChannel.this.connectionCloseEvent = Quiche.quiche_conn_peer_error(conn.address());
                if (QuicheQuicChannel.this.connectionCloseEvent != null) {
                    QuicheQuicChannel.this.pipeline().fireUserEventTriggered(QuicheQuicChannel.this.connectionCloseEvent);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void connectionRecv(InetSocketAddress sender, InetSocketAddress recipient, ByteBuf buffer) {
            QuicheQuicConnection conn = QuicheQuicChannel.this.connection;
            if (conn.isFreed()) {
                return;
            }
            int bufferReadable = buffer.readableBytes();
            if (bufferReadable == 0) {
                return;
            }
            QuicheQuicChannel.this.reantranceGuard |= 2;
            boolean close = false;
            try {
                ByteBuf tmpBuffer = null;
                if (buffer.isReadOnly()) {
                    tmpBuffer = QuicheQuicChannel.this.alloc().directBuffer(buffer.readableBytes());
                    tmpBuffer.writeBytes(buffer);
                    buffer = tmpBuffer;
                }
                long memoryAddress = Quiche.readerMemoryAddress(buffer);
                ByteBuffer recvInfo = conn.nextRecvInfo();
                QuicheRecvInfo.setRecvInfo(recvInfo, sender, recipient);
                QuicheQuicChannel.this.remote = sender;
                QuicheQuicChannel.this.local = recipient;
                try {
                    int res;
                    do {
                        Runnable task;
                        boolean done;
                        if ((res = Quiche.quiche_conn_recv(conn.address(), memoryAddress, bufferReadable, Quiche.memoryAddressWithPosition(recvInfo))) < 0) {
                            done = true;
                            if (res != Quiche.QUICHE_ERR_DONE) {
                                close = Quiche.shouldClose(res);
                                Exception e = Quiche.convertToException(res);
                                if (QuicheQuicChannel.this.tryFailConnectPromise(e)) {
                                    break;
                                }
                                QuicheQuicChannel.this.fireExceptionEvents(conn, e);
                            }
                        } else {
                            done = false;
                        }
                        if ((task = conn.sslTask()) != null) {
                            if (QuicheQuicChannel.this.runTasksDirectly()) {
                                do {
                                    task.run();
                                } while ((task = conn.sslTask()) != null);
                                this.processReceived(conn);
                            } else {
                                this.runAllTaskRecv(conn, task);
                            }
                        } else {
                            this.processReceived(conn);
                        }
                        if (done) {
                            break;
                        }
                        memoryAddress += (long)res;
                    } while ((bufferReadable -= res) > 0 && !conn.isFreed());
                }
                finally {
                    buffer.skipBytes((int)(memoryAddress - Quiche.readerMemoryAddress(buffer)));
                    if (tmpBuffer != null) {
                        tmpBuffer.release();
                    }
                }
                if (close) {
                    QuicheQuicChannel.this.unsafe().close(QuicheQuicChannel.this.newPromise());
                }
            }
            finally {
                QuicheQuicChannel.this.reantranceGuard &= -3;
            }
        }

        private void processReceived(QuicheQuicConnection conn) {
            if (this.handlePendingChannelActive(conn)) {
                return;
            }
            QuicheQuicChannel.this.notifyAboutHandshakeCompletionIfNeeded(conn, null);
            this.fireConnectCloseEventIfNeeded(conn);
            if (conn.isFreed()) {
                return;
            }
            long connAddr = conn.address();
            if (Quiche.quiche_conn_is_established(connAddr) || Quiche.quiche_conn_is_in_early_data(connAddr)) {
                long uniLeftOld = QuicheQuicChannel.this.uniStreamsLeft;
                long bidiLeftOld = QuicheQuicChannel.this.bidiStreamsLeft;
                if (uniLeftOld == 0L || bidiLeftOld == 0L) {
                    long uniLeft = Quiche.quiche_conn_peer_streams_left_uni(connAddr);
                    long bidiLeft = Quiche.quiche_conn_peer_streams_left_bidi(connAddr);
                    QuicheQuicChannel.this.uniStreamsLeft = uniLeft;
                    QuicheQuicChannel.this.bidiStreamsLeft = bidiLeft;
                    if (uniLeftOld != uniLeft || bidiLeftOld != bidiLeft) {
                        QuicheQuicChannel.this.pipeline().fireUserEventTriggered(QuicStreamLimitChangedEvent.INSTANCE);
                    }
                }
                this.handlePathEvents(conn);
                if (QuicheQuicChannel.this.handleWritableStreams(conn)) {
                    QuicheQuicChannel.this.flushParent();
                }
                QuicheQuicChannel.this.datagramReadable = true;
                QuicheQuicChannel.this.streamReadable = true;
                this.recvDatagram(conn);
                this.recvStream(conn);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void handlePathEvents(QuicheQuicConnection conn) {
            long event;
            while (!conn.isFreed() && (event = Quiche.quiche_conn_path_event_next(conn.address())) > 0L) {
                try {
                    InetSocketAddress peer;
                    InetSocketAddress local;
                    Object[] ret;
                    int type = Quiche.quiche_path_event_type(event);
                    if (type == Quiche.QUICHE_PATH_EVENT_NEW) {
                        ret = Quiche.quiche_path_event_new(event);
                        local = (InetSocketAddress)ret[0];
                        peer = (InetSocketAddress)ret[1];
                        QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.New(local, peer));
                        continue;
                    }
                    if (type == Quiche.QUICHE_PATH_EVENT_VALIDATED) {
                        ret = Quiche.quiche_path_event_validated(event);
                        local = (InetSocketAddress)ret[0];
                        peer = (InetSocketAddress)ret[1];
                        QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.Validated(local, peer));
                        continue;
                    }
                    if (type == Quiche.QUICHE_PATH_EVENT_FAILED_VALIDATION) {
                        ret = Quiche.quiche_path_event_failed_validation(event);
                        local = (InetSocketAddress)ret[0];
                        peer = (InetSocketAddress)ret[1];
                        QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.FailedValidation(local, peer));
                        continue;
                    }
                    if (type == Quiche.QUICHE_PATH_EVENT_CLOSED) {
                        ret = Quiche.quiche_path_event_closed(event);
                        local = (InetSocketAddress)ret[0];
                        peer = (InetSocketAddress)ret[1];
                        QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.Closed(local, peer));
                        continue;
                    }
                    if (type == Quiche.QUICHE_PATH_EVENT_REUSED_SOURCE_CONNECTION_ID) {
                        ret = Quiche.quiche_path_event_reused_source_connection_id(event);
                        Long seq = (Long)ret[0];
                        InetSocketAddress localOld = (InetSocketAddress)ret[1];
                        InetSocketAddress peerOld = (InetSocketAddress)ret[2];
                        InetSocketAddress local2 = (InetSocketAddress)ret[3];
                        InetSocketAddress peer2 = (InetSocketAddress)ret[4];
                        QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.ReusedSourceConnectionId(seq, localOld, peerOld, local2, peer2));
                        continue;
                    }
                    if (type != Quiche.QUICHE_PATH_EVENT_PEER_MIGRATED) continue;
                    ret = Quiche.quiche_path_event_peer_migrated(event);
                    local = (InetSocketAddress)ret[0];
                    peer = (InetSocketAddress)ret[1];
                    QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicPathEvent.PeerMigrated(local, peer));
                }
                finally {
                    Quiche.quiche_path_event_free(event);
                }
            }
        }

        private void runAllTaskRecv(QuicheQuicConnection conn, Runnable task) {
            QuicheQuicChannel.this.sslTaskExecutor.execute(this.decorateTaskRecv(conn, task));
        }

        private Runnable decorateTaskRecv(QuicheQuicConnection conn, Runnable task) {
            return () -> {
                try {
                    QuicheQuicChannel.this.runAll(conn, task);
                }
                finally {
                    QuicheQuicChannel.this.eventLoop().execute(() -> {
                        if (!conn.isFreed()) {
                            this.processReceived(conn);
                            if (QuicheQuicChannel.this.connectionSend(conn) != SendResult.NONE) {
                                QuicheQuicChannel.this.forceFlushParent();
                            }
                            QuicheQuicChannel.this.freeIfClosed();
                        }
                    });
                }
            };
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void recv() {
            QuicheQuicConnection conn = QuicheQuicChannel.this.connection;
            if ((QuicheQuicChannel.this.reantranceGuard & 2) != 0 || conn.isFreed()) {
                return;
            }
            long connAddr = conn.address();
            if (!Quiche.quiche_conn_is_established(connAddr) && !Quiche.quiche_conn_is_in_early_data(connAddr)) {
                return;
            }
            QuicheQuicChannel.this.reantranceGuard |= 2;
            try {
                this.recvDatagram(conn);
                this.recvStream(conn);
            }
            finally {
                QuicheQuicChannel.this.fireChannelReadCompleteIfNeeded();
                QuicheQuicChannel.this.reantranceGuard &= -3;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void recvStream(QuicheQuicConnection conn) {
            if (conn.isFreed()) {
                return;
            }
            long connAddr = conn.address();
            long readableIterator = Quiche.quiche_conn_readable(connAddr);
            int totalReadable = 0;
            if (readableIterator != -1L) {
                block9: {
                    try {
                        if (!QuicheQuicChannel.this.recvStreamPending || !QuicheQuicChannel.this.streamReadable) break block9;
                        while (true) {
                            int readable = Quiche.quiche_stream_iter_next(readableIterator, QuicheQuicChannel.this.readableStreams);
                            for (int i = 0; i < readable; ++i) {
                                long streamId = QuicheQuicChannel.this.readableStreams[i];
                                QuicheQuicStreamChannel streamChannel = (QuicheQuicStreamChannel)QuicheQuicChannel.this.streams.get(streamId);
                                if (streamChannel == null) {
                                    QuicheQuicChannel.this.recvStreamPending = false;
                                    QuicheQuicChannel.this.fireChannelReadCompletePending = true;
                                    streamChannel = this.addNewStreamChannel(streamId);
                                    streamChannel.readable();
                                    QuicheQuicChannel.this.pipeline().fireChannelRead(streamChannel);
                                    continue;
                                }
                                streamChannel.readable();
                            }
                            if (readable < QuicheQuicChannel.this.readableStreams.length) {
                                QuicheQuicChannel.this.streamReadable = false;
                                break;
                            }
                            if (readable <= 0) continue;
                            totalReadable += readable;
                        }
                    }
                    finally {
                        Quiche.quiche_stream_iter_free(readableIterator);
                    }
                }
                QuicheQuicChannel.access$3502(QuicheQuicChannel.this, QuicheQuicChannel.growIfNeeded(QuicheQuicChannel.this.readableStreams, totalReadable));
            }
        }

        private void recvDatagram(QuicheQuicConnection conn) {
            if (!QuicheQuicChannel.this.supportsDatagram) {
                return;
            }
            while (QuicheQuicChannel.this.recvDatagramPending && QuicheQuicChannel.this.datagramReadable && !conn.isFreed()) {
                RecvByteBufAllocator.Handle recvHandle = this.recvBufAllocHandle();
                recvHandle.reset(QuicheQuicChannel.this.config());
                int numMessagesRead = 0;
                do {
                    long connAddr;
                    int len;
                    if ((len = Quiche.quiche_conn_dgram_recv_front_len(connAddr = conn.address())) == Quiche.QUICHE_ERR_DONE) {
                        QuicheQuicChannel.this.datagramReadable = false;
                        return;
                    }
                    ByteBuf datagramBuffer = QuicheQuicChannel.this.alloc().directBuffer(len);
                    recvHandle.attemptedBytesRead(datagramBuffer.writableBytes());
                    int writerIndex = datagramBuffer.writerIndex();
                    long memoryAddress = Quiche.writerMemoryAddress(datagramBuffer);
                    int written = Quiche.quiche_conn_dgram_recv(connAddr, memoryAddress, datagramBuffer.writableBytes());
                    if (written < 0) {
                        datagramBuffer.release();
                        if (written == Quiche.QUICHE_ERR_DONE) {
                            QuicheQuicChannel.this.datagramReadable = false;
                            break;
                        }
                        QuicheQuicChannel.this.pipeline().fireExceptionCaught(Quiche.convertToException(written));
                    }
                    recvHandle.lastBytesRead(written);
                    recvHandle.incMessagesRead(1);
                    ++numMessagesRead;
                    datagramBuffer.writerIndex(writerIndex + written);
                    QuicheQuicChannel.this.recvDatagramPending = false;
                    QuicheQuicChannel.this.fireChannelReadCompletePending = true;
                    QuicheQuicChannel.this.pipeline().fireChannelRead(datagramBuffer);
                } while (recvHandle.continueReading() && !conn.isFreed());
                recvHandle.readComplete();
                if (numMessagesRead <= 0) continue;
                QuicheQuicChannel.this.fireChannelReadCompleteIfNeeded();
            }
        }

        private boolean handlePendingChannelActive(QuicheQuicConnection conn) {
            if (conn.isFreed() || QuicheQuicChannel.this.state == ChannelState.CLOSED) {
                return true;
            }
            if (QuicheQuicChannel.this.server) {
                if (QuicheQuicChannel.this.state == ChannelState.OPEN && Quiche.quiche_conn_is_established(conn.address())) {
                    QuicheQuicChannel.this.state = ChannelState.ACTIVE;
                    QuicheQuicChannel.this.pipeline().fireChannelActive();
                    QuicheQuicChannel.this.notifyAboutHandshakeCompletionIfNeeded(conn, null);
                    this.fireDatagramExtensionEvent(conn);
                }
            } else if (QuicheQuicChannel.this.connectPromise != null && Quiche.quiche_conn_is_established(conn.address())) {
                ChannelPromise promise = QuicheQuicChannel.this.connectPromise;
                QuicheQuicChannel.this.connectPromise = null;
                QuicheQuicChannel.this.state = ChannelState.ACTIVE;
                boolean promiseSet = promise.trySuccess();
                QuicheQuicChannel.this.pipeline().fireChannelActive();
                QuicheQuicChannel.this.notifyAboutHandshakeCompletionIfNeeded(conn, null);
                this.fireDatagramExtensionEvent(conn);
                if (!promiseSet) {
                    this.fireConnectCloseEventIfNeeded(conn);
                    this.close(this.voidPromise());
                    return true;
                }
            }
            return false;
        }

        private void fireDatagramExtensionEvent(QuicheQuicConnection conn) {
            if (conn.isClosed()) {
                return;
            }
            long connAddr = conn.address();
            int len = Quiche.quiche_conn_dgram_max_writable_len(connAddr);
            if (len != Quiche.QUICHE_ERR_DONE) {
                QuicheQuicChannel.this.pipeline().fireUserEventTriggered(new QuicDatagramExtensionEvent(len));
            }
        }

        private QuicheQuicStreamChannel addNewStreamChannel(long streamId) {
            QuicheQuicStreamChannel streamChannel = new QuicheQuicStreamChannel(QuicheQuicChannel.this, streamId);
            QuicheQuicStreamChannel old = QuicheQuicChannel.this.streams.put(streamId, streamChannel);
            assert (old == null);
            streamChannel.writable(QuicheQuicChannel.this.streamCapacity(streamId));
            return streamChannel;
        }
    }

    private static final class CloseData
    implements ChannelFutureListener {
        final boolean applicationClose;
        final int err;
        final ByteBuf reason;

        CloseData(boolean applicationClose, int err, ByteBuf reason) {
            this.applicationClose = applicationClose;
            this.err = err;
            this.reason = reason;
        }

        @Override
        public void operationComplete(ChannelFuture future) {
            this.reason.release();
        }
    }

    private static enum SendResult {
        SOME,
        NONE,
        CLOSE;

    }

    private static enum ChannelState {
        OPEN,
        ACTIVE,
        CLOSED;

    }

    static enum StreamRecvResult {
        DONE,
        FIN,
        OK;

    }
}

