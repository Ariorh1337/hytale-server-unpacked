/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.embedded;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.MockTicker;
import io.netty.util.concurrent.Ticker;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EmbeddedEventLoop
extends AbstractScheduledEventExecutor
implements EventLoop {
    private final Ticker ticker;
    private final Queue<Runnable> tasks = new ArrayDeque<Runnable>(2);

    EmbeddedEventLoop(Ticker ticker) {
        this.ticker = ticker;
    }

    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup)super.parent();
    }

    @Override
    public EventLoop next() {
        return (EventLoop)super.next();
    }

    @Override
    public void execute(Runnable command) {
        this.tasks.add(ObjectUtil.checkNotNull(command, "command"));
    }

    void runTasks() {
        Runnable task;
        while ((task = this.tasks.poll()) != null) {
            task.run();
        }
    }

    boolean hasPendingNormalTasks() {
        return !this.tasks.isEmpty();
    }

    long runScheduledTasks() {
        long time = this.getCurrentTimeNanos();
        Runnable task;
        while ((task = this.pollScheduledTask(time)) != null) {
            task.run();
        }
        return this.nextScheduledTaskNano();
    }

    long nextScheduledTask() {
        return this.nextScheduledTaskNano();
    }

    @Override
    public Ticker ticker() {
        return this.ticker;
    }

    @Override
    protected long getCurrentTimeNanos() {
        return this.ticker.nanoTime();
    }

    @Override
    protected void cancelScheduledTasks() {
        super.cancelScheduledTasks();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> terminationFuture() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return this.register(new DefaultChannelPromise(channel, this));
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        promise.channel().unsafe().register(this, promise);
        return promise;
    }

    @Override
    @Deprecated
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        channel.unsafe().register(this, promise);
        return promise;
    }

    @Override
    public boolean inEventLoop() {
        return true;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return true;
    }

    static final class FreezableTicker
    implements MockTicker {
        private final Ticker unfrozen = Ticker.systemTicker();
        private long startTime;
        private long frozenTimestamp;
        private boolean timeFrozen;

        FreezableTicker() {
        }

        @Override
        public void advance(long amount, TimeUnit unit) {
            long nanos = unit.toNanos(amount);
            if (this.timeFrozen) {
                this.frozenTimestamp += nanos;
            } else {
                this.startTime -= nanos;
            }
        }

        @Override
        public long nanoTime() {
            if (this.timeFrozen) {
                return this.frozenTimestamp;
            }
            return this.unfrozen.nanoTime() - this.startTime;
        }

        @Override
        public void sleep(long delay, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException("Sleeping is not supported by the default ticker for EmbeddedEventLoop. Please use a different ticker implementation if you require sleep support.");
        }

        public void freezeTime() {
            if (!this.timeFrozen) {
                this.frozenTimestamp = this.nanoTime();
                this.timeFrozen = true;
            }
        }

        public void unfreezeTime() {
            if (this.timeFrozen) {
                this.startTime = this.unfrozen.nanoTime() - this.frozenTimestamp;
                this.timeFrozen = false;
            }
        }
    }
}

