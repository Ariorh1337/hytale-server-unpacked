/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.embedded;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannelId;
import io.netty.channel.embedded.EmbeddedEventLoop;
import io.netty.channel.embedded.EmbeddedSocketAddress;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Ticker;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class EmbeddedChannel
extends AbstractChannel {
    private static final SocketAddress LOCAL_ADDRESS = new EmbeddedSocketAddress();
    private static final SocketAddress REMOTE_ADDRESS = new EmbeddedSocketAddress();
    private static final ChannelHandler[] EMPTY_HANDLERS = new ChannelHandler[0];
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EmbeddedChannel.class);
    private static final ChannelMetadata METADATA_NO_DISCONNECT = new ChannelMetadata(false);
    private static final ChannelMetadata METADATA_DISCONNECT = new ChannelMetadata(true);
    private final EmbeddedEventLoop loop;
    private final ChannelFutureListener recordExceptionListener = new ChannelFutureListener(){

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            EmbeddedChannel.this.recordException(future);
        }
    };
    private final ChannelMetadata metadata;
    private final ChannelConfig config;
    private Queue<Object> inboundMessages;
    private Queue<Object> outboundMessages;
    private Throwable lastException;
    private State state;
    private int executingStackCnt;
    private boolean cancelRemainingScheduledTasks;

    public EmbeddedChannel() {
        this(EmbeddedChannel.builder());
    }

    public EmbeddedChannel(ChannelId channelId) {
        this(EmbeddedChannel.builder().channelId(channelId));
    }

    public EmbeddedChannel(ChannelHandler ... handlers) {
        this(EmbeddedChannel.builder().handlers(handlers));
    }

    public EmbeddedChannel(boolean hasDisconnect, ChannelHandler ... handlers) {
        this(EmbeddedChannel.builder().hasDisconnect(hasDisconnect).handlers(handlers));
    }

    public EmbeddedChannel(boolean register, boolean hasDisconnect, ChannelHandler ... handlers) {
        this(EmbeddedChannel.builder().register(register).hasDisconnect(hasDisconnect).handlers(handlers));
    }

    public EmbeddedChannel(ChannelId channelId, ChannelHandler ... handlers) {
        this(EmbeddedChannel.builder().channelId(channelId).handlers(handlers));
    }

    public EmbeddedChannel(ChannelId channelId, boolean hasDisconnect, ChannelHandler ... handlers) {
        this(EmbeddedChannel.builder().channelId(channelId).hasDisconnect(hasDisconnect).handlers(handlers));
    }

    public EmbeddedChannel(ChannelId channelId, boolean register, boolean hasDisconnect, ChannelHandler ... handlers) {
        this(EmbeddedChannel.builder().channelId(channelId).register(register).hasDisconnect(hasDisconnect).handlers(handlers));
    }

    public EmbeddedChannel(Channel parent, ChannelId channelId, boolean register, boolean hasDisconnect, ChannelHandler ... handlers) {
        this(EmbeddedChannel.builder().parent(parent).channelId(channelId).register(register).hasDisconnect(hasDisconnect).handlers(handlers));
    }

    public EmbeddedChannel(ChannelId channelId, boolean hasDisconnect, ChannelConfig config, ChannelHandler ... handlers) {
        this(EmbeddedChannel.builder().channelId(channelId).hasDisconnect(hasDisconnect).config(config).handlers(handlers));
    }

    protected EmbeddedChannel(Builder builder) {
        super(builder.parent, builder.channelId);
        this.loop = new EmbeddedEventLoop(builder.ticker == null ? new EmbeddedEventLoop.FreezableTicker() : builder.ticker);
        this.metadata = EmbeddedChannel.metadata(builder.hasDisconnect);
        ChannelConfig channelConfig = this.config = builder.config == null ? new DefaultChannelConfig(this) : builder.config;
        if (builder.handler == null) {
            this.setup(builder.register, builder.handlers);
        } else {
            this.setup(builder.register, builder.handler);
        }
    }

    private static ChannelMetadata metadata(boolean hasDisconnect) {
        return hasDisconnect ? METADATA_DISCONNECT : METADATA_NO_DISCONNECT;
    }

    private void setup(boolean register, final ChannelHandler ... handlers) {
        ChannelPipeline p = this.pipeline();
        p.addLast(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                for (ChannelHandler h : handlers) {
                    if (h == null) break;
                    pipeline.addLast(h);
                }
            }
        });
        if (register) {
            ChannelFuture future = this.loop.register(this);
            assert (future.isDone());
        }
    }

    private void setup(boolean register, ChannelHandler handler) {
        ChannelPipeline p = this.pipeline();
        p.addLast(handler);
        if (register) {
            ChannelFuture future = this.loop.register(this);
            assert (future.isDone());
        }
    }

    public void register() throws Exception {
        ChannelFuture future = this.loop.register(this);
        assert (future.isDone());
        Throwable cause = future.cause();
        if (cause != null) {
            PlatformDependent.throwException(cause);
        }
    }

    @Override
    protected final DefaultChannelPipeline newChannelPipeline() {
        return new EmbeddedChannelPipeline(this);
    }

    @Override
    public ChannelMetadata metadata() {
        return this.metadata;
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return this.state != State.CLOSED;
    }

    @Override
    public boolean isActive() {
        return this.state == State.ACTIVE;
    }

    public Queue<Object> inboundMessages() {
        if (this.inboundMessages == null) {
            this.inboundMessages = new ArrayDeque<Object>();
        }
        return this.inboundMessages;
    }

    @Deprecated
    public Queue<Object> lastInboundBuffer() {
        return this.inboundMessages();
    }

    public Queue<Object> outboundMessages() {
        if (this.outboundMessages == null) {
            this.outboundMessages = new ArrayDeque<Object>();
        }
        return this.outboundMessages;
    }

    @Deprecated
    public Queue<Object> lastOutboundBuffer() {
        return this.outboundMessages();
    }

    public <T> T readInbound() {
        Object message = EmbeddedChannel.poll(this.inboundMessages);
        if (message != null) {
            ReferenceCountUtil.touch(message, "Caller of readInbound() will handle the message from this point");
        }
        return (T)message;
    }

    public <T> T readOutbound() {
        Object message = EmbeddedChannel.poll(this.outboundMessages);
        if (message != null) {
            ReferenceCountUtil.touch(message, "Caller of readOutbound() will handle the message from this point.");
        }
        return (T)message;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean writeInbound(Object ... msgs) {
        this.ensureOpen();
        if (msgs.length == 0) {
            return EmbeddedChannel.isNotEmpty(this.inboundMessages);
        }
        ++this.executingStackCnt;
        try {
            ChannelPipeline p = this.pipeline();
            for (Object m : msgs) {
                p.fireChannelRead(m);
            }
            this.flushInbound(false, this.voidPromise());
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
        return EmbeddedChannel.isNotEmpty(this.inboundMessages);
    }

    public ChannelFuture writeOneInbound(Object msg) {
        return this.writeOneInbound(msg, this.newPromise());
    }

    public ChannelFuture writeOneInbound(Object msg, ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            if (this.checkOpen(true)) {
                this.pipeline().fireChannelRead(msg);
            }
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
        return this.checkException(promise);
    }

    public EmbeddedChannel flushInbound() {
        this.flushInbound(true, this.voidPromise());
        return this;
    }

    private ChannelFuture flushInbound(boolean recordException, ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            if (this.checkOpen(recordException)) {
                this.pipeline().fireChannelReadComplete();
                this.runPendingTasks();
            }
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
        return this.checkException(promise);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean writeOutbound(Object ... msgs) {
        this.ensureOpen();
        if (msgs.length == 0) {
            return EmbeddedChannel.isNotEmpty(this.outboundMessages);
        }
        ++this.executingStackCnt;
        RecyclableArrayList futures = RecyclableArrayList.newInstance(msgs.length);
        try {
            try {
                for (Object m : msgs) {
                    if (m == null) break;
                    futures.add(this.write(m));
                }
                this.flushOutbound0();
                int size = futures.size();
                for (int i = 0; i < size; ++i) {
                    ChannelFuture future = (ChannelFuture)futures.get(i);
                    if (future.isDone()) {
                        this.recordException(future);
                        continue;
                    }
                    future.addListener((GenericFutureListener)this.recordExceptionListener);
                }
            }
            finally {
                --this.executingStackCnt;
                this.maybeRunPendingTasks();
            }
            this.checkException();
            boolean bl = EmbeddedChannel.isNotEmpty(this.outboundMessages);
            return bl;
        }
        finally {
            futures.recycle();
        }
    }

    public ChannelFuture writeOneOutbound(Object msg) {
        return this.writeOneOutbound(msg, this.newPromise());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChannelFuture writeOneOutbound(Object msg, ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            if (this.checkOpen(true)) {
                ChannelFuture channelFuture = this.write(msg, promise);
                return channelFuture;
            }
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
        return this.checkException(promise);
    }

    public EmbeddedChannel flushOutbound() {
        ++this.executingStackCnt;
        try {
            if (this.checkOpen(true)) {
                this.flushOutbound0();
            }
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
        this.checkException(this.voidPromise());
        return this;
    }

    private void flushOutbound0() {
        this.runPendingTasks();
        this.flush();
    }

    public boolean finish() {
        return this.finish(false);
    }

    public boolean finishAndReleaseAll() {
        return this.finish(true);
    }

    private boolean finish(boolean releaseAll) {
        ++this.executingStackCnt;
        try {
            this.close();
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
        try {
            this.checkException();
            boolean bl = EmbeddedChannel.isNotEmpty(this.inboundMessages) || EmbeddedChannel.isNotEmpty(this.outboundMessages);
            return bl;
        }
        finally {
            if (releaseAll) {
                EmbeddedChannel.releaseAll(this.inboundMessages);
                EmbeddedChannel.releaseAll(this.outboundMessages);
            }
        }
    }

    public boolean releaseInbound() {
        return EmbeddedChannel.releaseAll(this.inboundMessages);
    }

    public boolean releaseOutbound() {
        return EmbeddedChannel.releaseAll(this.outboundMessages);
    }

    private static boolean releaseAll(Queue<Object> queue) {
        if (EmbeddedChannel.isNotEmpty(queue)) {
            Object msg;
            while ((msg = queue.poll()) != null) {
                ReferenceCountUtil.release(msg);
            }
            return true;
        }
        return false;
    }

    @Override
    public final ChannelFuture close() {
        return this.close(this.newPromise());
    }

    @Override
    public final ChannelFuture disconnect() {
        return this.disconnect(this.newPromise());
    }

    @Override
    public final ChannelFuture close(ChannelPromise promise) {
        ChannelFuture future;
        ++this.executingStackCnt;
        try {
            this.runPendingTasks();
            future = super.close(promise);
            this.cancelRemainingScheduledTasks = true;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
        return future;
    }

    @Override
    public final ChannelFuture disconnect(ChannelPromise promise) {
        ChannelFuture future;
        ++this.executingStackCnt;
        try {
            future = super.disconnect(promise);
            if (!this.metadata.hasDisconnect()) {
                this.cancelRemainingScheduledTasks = true;
            }
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
        return future;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.bind(localAddress);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.connect(remoteAddress);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.connect(remoteAddress, localAddress);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    @Override
    public ChannelFuture deregister() {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.deregister();
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    @Override
    public Channel flush() {
        ++this.executingStackCnt;
        try {
            Channel channel = super.flush();
            return channel;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.bind(localAddress, promise);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.connect(remoteAddress, promise);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.connect(remoteAddress, localAddress, promise);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.deregister(promise);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    @Override
    public Channel read() {
        ++this.executingStackCnt;
        try {
            Channel channel = super.read();
            return channel;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    @Override
    public ChannelFuture write(Object msg) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.write(msg);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.write(msg, promise);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.writeAndFlush(msg);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        ++this.executingStackCnt;
        try {
            ChannelFuture channelFuture = super.writeAndFlush(msg, promise);
            return channelFuture;
        }
        finally {
            --this.executingStackCnt;
            this.maybeRunPendingTasks();
        }
    }

    private static boolean isNotEmpty(Queue<Object> queue) {
        return queue != null && !queue.isEmpty();
    }

    private static Object poll(Queue<Object> queue) {
        return queue != null ? queue.poll() : null;
    }

    private void maybeRunPendingTasks() {
        if (this.executingStackCnt == 0) {
            this.runPendingTasks();
            if (this.cancelRemainingScheduledTasks) {
                this.embeddedEventLoop().cancelScheduledTasks();
            }
        }
    }

    public void runPendingTasks() {
        try {
            this.embeddedEventLoop().runTasks();
        }
        catch (Exception e) {
            this.recordException(e);
        }
        try {
            this.embeddedEventLoop().runScheduledTasks();
        }
        catch (Exception e) {
            this.recordException(e);
        }
    }

    public boolean hasPendingTasks() {
        return this.embeddedEventLoop().hasPendingNormalTasks() || this.embeddedEventLoop().nextScheduledTask() == 0L;
    }

    public long runScheduledPendingTasks() {
        try {
            return this.embeddedEventLoop().runScheduledTasks();
        }
        catch (Exception e) {
            this.recordException(e);
            return this.embeddedEventLoop().nextScheduledTask();
        }
    }

    private void recordException(ChannelFuture future) {
        if (!future.isSuccess()) {
            this.recordException(future.cause());
        }
    }

    private void recordException(Throwable cause) {
        if (this.lastException == null) {
            this.lastException = cause;
        } else {
            logger.warn("More than one exception was raised. Will report only the first one and log others.", cause);
        }
    }

    private EmbeddedEventLoop.FreezableTicker freezableTicker() {
        Ticker ticker = this.eventLoop().ticker();
        if (ticker instanceof EmbeddedEventLoop.FreezableTicker) {
            return (EmbeddedEventLoop.FreezableTicker)ticker;
        }
        throw new IllegalStateException("EmbeddedChannel constructed with custom ticker, time manipulation methods are unavailable.");
    }

    public void advanceTimeBy(long duration, TimeUnit unit) {
        this.freezableTicker().advance(duration, unit);
    }

    public void freezeTime() {
        this.freezableTicker().freezeTime();
    }

    public void unfreezeTime() {
        this.freezableTicker().unfreezeTime();
    }

    private ChannelFuture checkException(ChannelPromise promise) {
        Throwable t = this.lastException;
        if (t != null) {
            this.lastException = null;
            if (promise.isVoid()) {
                PlatformDependent.throwException(t);
            }
            return promise.setFailure(t);
        }
        return promise.setSuccess();
    }

    public void checkException() {
        this.checkException(this.voidPromise());
    }

    private boolean checkOpen(boolean recordException) {
        if (!this.isOpen()) {
            if (recordException) {
                this.recordException(new ClosedChannelException());
            }
            return false;
        }
        return true;
    }

    private EmbeddedEventLoop embeddedEventLoop() {
        if (this.isRegistered()) {
            return (EmbeddedEventLoop)super.eventLoop();
        }
        return this.loop;
    }

    protected final void ensureOpen() {
        if (!this.checkOpen(true)) {
            this.checkException();
        }
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EmbeddedEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.isActive() ? LOCAL_ADDRESS : null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.isActive() ? REMOTE_ADDRESS : null;
    }

    @Override
    protected void doRegister() throws Exception {
        this.state = State.ACTIVE;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
    }

    @Override
    protected void doDisconnect() throws Exception {
        if (!this.metadata.hasDisconnect()) {
            this.doClose();
        }
    }

    @Override
    protected void doClose() throws Exception {
        this.state = State.CLOSED;
    }

    @Override
    protected void doBeginRead() throws Exception {
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new EmbeddedUnsafe();
    }

    @Override
    public Channel.Unsafe unsafe() {
        return ((EmbeddedUnsafe)super.unsafe()).wrapped;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        while ((msg = in.current()) != null) {
            ReferenceCountUtil.retain(msg);
            this.handleOutboundMessage(msg);
            in.remove();
        }
    }

    protected void handleOutboundMessage(Object msg) {
        this.outboundMessages().add(msg);
    }

    protected void handleInboundMessage(Object msg) {
        this.inboundMessages().add(msg);
    }

    public static Builder builder() {
        return new Builder();
    }

    static /* synthetic */ ChannelHandler[] access$300() {
        return EMPTY_HANDLERS;
    }

    private final class EmbeddedChannelPipeline
    extends DefaultChannelPipeline {
        EmbeddedChannelPipeline(EmbeddedChannel channel) {
            super(channel);
        }

        @Override
        protected void onUnhandledInboundException(Throwable cause) {
            EmbeddedChannel.this.recordException(cause);
        }

        @Override
        protected void onUnhandledInboundMessage(ChannelHandlerContext ctx, Object msg) {
            EmbeddedChannel.this.handleInboundMessage(msg);
        }
    }

    private final class EmbeddedUnsafe
    extends AbstractChannel.AbstractUnsafe {
        final Channel.Unsafe wrapped;

        private EmbeddedUnsafe() {
            super(EmbeddedChannel.this);
            this.wrapped = new Channel.Unsafe(){

                @Override
                public RecvByteBufAllocator.Handle recvBufAllocHandle() {
                    return EmbeddedUnsafe.this.recvBufAllocHandle();
                }

                @Override
                public SocketAddress localAddress() {
                    return EmbeddedUnsafe.this.localAddress();
                }

                @Override
                public SocketAddress remoteAddress() {
                    return EmbeddedUnsafe.this.remoteAddress();
                }

                @Override
                public void register(EventLoop eventLoop, ChannelPromise promise) {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.register(eventLoop, promise);
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public void bind(SocketAddress localAddress, ChannelPromise promise) {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.bind(localAddress, promise);
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.connect(remoteAddress, localAddress, promise);
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public void disconnect(ChannelPromise promise) {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.disconnect(promise);
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public void close(ChannelPromise promise) {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.close(promise);
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public void closeForcibly() {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.closeForcibly();
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public void deregister(ChannelPromise promise) {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.deregister(promise);
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public void beginRead() {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.beginRead();
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public void write(Object msg, ChannelPromise promise) {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.write(msg, promise);
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public void flush() {
                    EmbeddedChannel.this.executingStackCnt++;
                    try {
                        EmbeddedUnsafe.this.flush();
                    }
                    finally {
                        EmbeddedChannel.this.executingStackCnt--;
                        EmbeddedChannel.this.maybeRunPendingTasks();
                    }
                }

                @Override
                public ChannelPromise voidPromise() {
                    return EmbeddedUnsafe.this.voidPromise();
                }

                @Override
                public ChannelOutboundBuffer outboundBuffer() {
                    return EmbeddedUnsafe.this.outboundBuffer();
                }
            };
        }

        @Override
        public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            this.safeSetSuccess(promise);
        }
    }

    public static final class Builder {
        Channel parent;
        ChannelId channelId = EmbeddedChannelId.INSTANCE;
        boolean register = true;
        boolean hasDisconnect;
        ChannelHandler[] handlers = EmbeddedChannel.access$300();
        ChannelHandler handler;
        ChannelConfig config;
        Ticker ticker;

        private Builder() {
        }

        public Builder parent(Channel parent) {
            this.parent = parent;
            return this;
        }

        public Builder channelId(ChannelId channelId) {
            this.channelId = Objects.requireNonNull(channelId, "channelId");
            return this;
        }

        public Builder register(boolean register) {
            this.register = register;
            return this;
        }

        public Builder hasDisconnect(boolean hasDisconnect) {
            this.hasDisconnect = hasDisconnect;
            return this;
        }

        public Builder handlers(ChannelHandler ... handlers) {
            this.handlers = Objects.requireNonNull(handlers, "handlers");
            this.handler = null;
            return this;
        }

        public Builder handlers(ChannelHandler handler) {
            this.handler = Objects.requireNonNull(handler, "handler");
            this.handlers = null;
            return this;
        }

        public Builder config(ChannelConfig config) {
            this.config = Objects.requireNonNull(config, "config");
            return this;
        }

        public Builder ticker(Ticker ticker) {
            this.ticker = ticker;
            return this;
        }

        public EmbeddedChannel build() {
            return new EmbeddedChannel(this);
        }
    }

    private static enum State {
        OPEN,
        ACTIVE,
        CLOSED;

    }
}

