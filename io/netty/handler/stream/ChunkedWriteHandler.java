/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ChunkedWriteHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
    private Queue<PendingWrite> queue;
    private volatile ChannelHandlerContext ctx;

    public ChunkedWriteHandler() {
    }

    @Deprecated
    public ChunkedWriteHandler(int maxPendingWrites) {
        ObjectUtil.checkPositive(maxPendingWrites, "maxPendingWrites");
    }

    private void allocateQueue() {
        if (this.queue == null) {
            this.queue = new ArrayDeque<PendingWrite>();
        }
    }

    private boolean queueIsEmpty() {
        return this.queue == null || this.queue.isEmpty();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    public void resumeTransfer() {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            return;
        }
        if (ctx.executor().inEventLoop()) {
            this.resumeTransfer0(ctx);
        } else {
            ctx.executor().execute(new Runnable(){

                @Override
                public void run() {
                    ChunkedWriteHandler.this.resumeTransfer0(ctx);
                }
            });
        }
    }

    private void resumeTransfer0(ChannelHandlerContext ctx) {
        try {
            this.doFlush(ctx);
        }
        catch (Exception e) {
            logger.warn("Unexpected exception while sending chunks.", e);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!this.queueIsEmpty() || msg instanceof ChunkedInput) {
            this.allocateQueue();
            this.queue.add(new PendingWrite(msg, promise));
        } else {
            ctx.write(msg, promise);
        }
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        this.doFlush(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.doFlush(ctx);
        ctx.fireChannelInactive();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            this.doFlush(ctx);
        }
        ctx.fireChannelWritabilityChanged();
    }

    private void discard(Throwable cause) {
        PendingWrite currentWrite;
        if (this.queueIsEmpty()) {
            return;
        }
        while ((currentWrite = this.queue.poll()) != null) {
            Object message = currentWrite.msg;
            if (message instanceof ChunkedInput) {
                long inputLength;
                boolean endOfInput;
                ChunkedInput in = (ChunkedInput)message;
                try {
                    endOfInput = in.isEndOfInput();
                    inputLength = in.length();
                    ChunkedWriteHandler.closeInput(in);
                }
                catch (Exception e) {
                    ChunkedWriteHandler.closeInput(in);
                    currentWrite.fail(e);
                    logger.warn("ChunkedInput failed", e);
                    continue;
                }
                if (!endOfInput) {
                    if (cause == null) {
                        cause = new ClosedChannelException();
                    }
                    currentWrite.fail(cause);
                    continue;
                }
                currentWrite.success(inputLength);
                continue;
            }
            if (cause == null) {
                cause = new ClosedChannelException();
            }
            currentWrite.fail(cause);
        }
    }

    private void doFlush(ChannelHandlerContext ctx) {
        PendingWrite currentWrite;
        Channel channel = ctx.channel();
        if (!channel.isActive()) {
            this.discard(null);
            ctx.flush();
            return;
        }
        if (this.queueIsEmpty()) {
            ctx.flush();
            return;
        }
        boolean requiresFlush = true;
        ByteBufAllocator allocator = ctx.alloc();
        while (channel.isWritable() && (currentWrite = this.queue.peek()) != null) {
            if (currentWrite.promise.isDone()) {
                this.queue.remove();
                continue;
            }
            Object pendingMessage = currentWrite.msg;
            if (pendingMessage instanceof ChunkedInput) {
                boolean suspend;
                boolean endOfInput;
                final ChunkedInput chunks = (ChunkedInput)pendingMessage;
                ByteBuf message = null;
                try {
                    message = (ByteBuf)chunks.readChunk(allocator);
                    endOfInput = chunks.isEndOfInput();
                    suspend = message == null && !endOfInput;
                }
                catch (Throwable t) {
                    this.queue.remove();
                    if (message != null) {
                        ReferenceCountUtil.release(message);
                    }
                    ChunkedWriteHandler.closeInput(chunks);
                    currentWrite.fail(t);
                    break;
                }
                if (suspend) break;
                if (message == null) {
                    message = Unpooled.EMPTY_BUFFER;
                }
                if (endOfInput) {
                    this.queue.remove();
                }
                ChannelFuture f = ctx.writeAndFlush(message);
                if (endOfInput) {
                    if (f.isDone()) {
                        ChunkedWriteHandler.handleEndOfInputFuture(f, chunks, currentWrite);
                    } else {
                        f.addListener((GenericFutureListener)new ChannelFutureListener(){

                            @Override
                            public void operationComplete(ChannelFuture future) {
                                ChunkedWriteHandler.handleEndOfInputFuture(future, chunks, currentWrite);
                            }
                        });
                    }
                } else {
                    boolean resume;
                    boolean bl = resume = !channel.isWritable();
                    if (f.isDone()) {
                        this.handleFuture(f, chunks, currentWrite, resume);
                    } else {
                        f.addListener((GenericFutureListener)new ChannelFutureListener(){

                            @Override
                            public void operationComplete(ChannelFuture future) {
                                ChunkedWriteHandler.this.handleFuture(future, chunks, currentWrite, resume);
                            }
                        });
                    }
                }
                requiresFlush = false;
            } else {
                this.queue.remove();
                ctx.write(pendingMessage, currentWrite.promise);
                requiresFlush = true;
            }
            if (channel.isActive()) continue;
            this.discard(new ClosedChannelException());
            break;
        }
        if (requiresFlush) {
            ctx.flush();
        }
    }

    private static void handleEndOfInputFuture(ChannelFuture future, ChunkedInput<?> input, PendingWrite currentWrite) {
        if (!future.isSuccess()) {
            ChunkedWriteHandler.closeInput(input);
            currentWrite.fail(future.cause());
        } else {
            long inputProgress = input.progress();
            long inputLength = input.length();
            ChunkedWriteHandler.closeInput(input);
            currentWrite.progress(inputProgress, inputLength);
            currentWrite.success(inputLength);
        }
    }

    private void handleFuture(ChannelFuture future, ChunkedInput<?> input, PendingWrite currentWrite, boolean resume) {
        if (!future.isSuccess()) {
            ChunkedWriteHandler.closeInput(input);
            currentWrite.fail(future.cause());
        } else {
            currentWrite.progress(input.progress(), input.length());
            if (resume && future.channel().isWritable()) {
                this.resumeTransfer();
            }
        }
    }

    private static void closeInput(ChunkedInput<?> chunks) {
        try {
            chunks.close();
        }
        catch (Throwable t) {
            logger.warn("Failed to close a ChunkedInput.", t);
        }
    }

    private static final class PendingWrite {
        final Object msg;
        final ChannelPromise promise;

        PendingWrite(Object msg, ChannelPromise promise) {
            this.msg = msg;
            this.promise = promise;
        }

        void fail(Throwable cause) {
            ReferenceCountUtil.release(this.msg);
            this.promise.tryFailure(cause);
        }

        void success(long total) {
            if (this.promise.isDone()) {
                return;
            }
            this.progress(total, total);
            this.promise.trySuccess();
        }

        void progress(long progress, long total) {
            if (this.promise instanceof ChannelProgressivePromise) {
                ((ChannelProgressivePromise)this.promise).tryProgress(progress, total);
            }
        }
    }
}

