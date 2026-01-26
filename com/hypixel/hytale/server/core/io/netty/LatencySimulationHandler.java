/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class LatencySimulationHandler
extends ChannelDuplexHandler {
    public static final String PIPELINE_KEY = "latencySimulator";
    private static final AtomicInteger counter = new AtomicInteger();
    private final DelayQueue<DelayedHandler> delayedQueue = new DelayQueue();
    @Nonnull
    private final Thread taskThread;
    private final long delayNanos;

    public LatencySimulationHandler(long delay, @Nonnull TimeUnit unit) {
        this.delayNanos = unit.toNanos(delay);
        this.taskThread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    DelayedHandler handler = (DelayedHandler)this.delayedQueue.take();
                    handler.ctx.executor().execute(handler);
                }
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }, "latency-simulator-" + counter.getAndIncrement());
        this.taskThread.setDaemon(true);
        this.taskThread.start();
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        this.delayedQueue.offer(new DelayedRead(ctx, System.nanoTime() + this.delayNanos));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.delayedQueue.offer(new DelayedWrite(ctx, System.nanoTime() + this.delayNanos, msg, promise));
    }

    @Override
    public void flush(ChannelHandlerContext ctx) {
        this.delayedQueue.offer(new DelayedFlush(ctx, System.nanoTime() + this.delayNanos));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        this.taskThread.interrupt();
        ObjectArrayList<DelayedHandler> list = new ObjectArrayList<DelayedHandler>(this.delayedQueue);
        list.sort(Comparator.comparingLong(value -> value.executeAtNanos));
        for (DelayedHandler handler : list) {
            handler.run();
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
        this.taskThread.interrupt();
    }

    public static void setLatency(@Nonnull Channel channel, long delay, @Nonnull TimeUnit unit) {
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get(PIPELINE_KEY) == null) {
            if (delay <= 0L) {
                return;
            }
            pipeline.addAfter("packetArrayEncoder", PIPELINE_KEY, new LatencySimulationHandler(delay, unit));
            return;
        }
        if (delay <= 0L) {
            pipeline.remove(PIPELINE_KEY);
            return;
        }
        pipeline.replace(PIPELINE_KEY, PIPELINE_KEY, (ChannelHandler)new LatencySimulationHandler(delay, unit));
    }

    private static class DelayedRead
    extends DelayedHandler {
        private DelayedRead(ChannelHandlerContext ctx, long executeAtNanos) {
            super(ctx, executeAtNanos);
        }

        @Override
        public void run() {
            this.ctx.read();
        }
    }

    private static class DelayedWrite
    extends DelayedHandler {
        private final Object msg;
        private final ChannelPromise promise;

        public DelayedWrite(ChannelHandlerContext ctx, long executeAtNanos, Object msg, ChannelPromise promise) {
            super(ctx, executeAtNanos);
            this.msg = msg;
            this.promise = promise;
        }

        @Override
        public void run() {
            this.ctx.write(this.msg, this.promise);
        }
    }

    private static class DelayedFlush
    extends DelayedHandler {
        public DelayedFlush(ChannelHandlerContext ctx, long executeAtNanos) {
            super(ctx, executeAtNanos);
        }

        @Override
        public void run() {
            this.ctx.flush();
        }
    }

    private static abstract class DelayedHandler
    implements Delayed,
    Runnable {
        protected final ChannelHandlerContext ctx;
        protected final long executeAtNanos;

        protected DelayedHandler(ChannelHandlerContext ctx, long executeAtNanos) {
            this.ctx = ctx;
            this.executeAtNanos = executeAtNanos;
        }

        @Override
        public long getDelay(@Nonnull TimeUnit unit) {
            return unit.convert(this.executeAtNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(@Nonnull Delayed o) {
            return Long.compare(this.executeAtNanos, ((DelayedHandler)o).executeAtNanos);
        }
    }
}

