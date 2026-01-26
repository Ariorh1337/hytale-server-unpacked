/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry;

import io.sentry.ISentryExecutorService;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@ApiStatus.Internal
public final class SentryExecutorService
implements ISentryExecutorService {
    private static final int INITIAL_QUEUE_SIZE = 40;
    private static final int MAX_QUEUE_SIZE = 271;
    @NotNull
    private final ScheduledThreadPoolExecutor executorService;
    @NotNull
    private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
    @NotNull
    private final Runnable dummyRunnable = () -> {};
    @Nullable
    private final SentryOptions options;

    @TestOnly
    SentryExecutorService(@NotNull ScheduledThreadPoolExecutor executorService, @Nullable SentryOptions options) {
        this.executorService = executorService;
        this.options = options;
    }

    public SentryExecutorService(@Nullable SentryOptions options) {
        this(new ScheduledThreadPoolExecutor(1, new SentryExecutorServiceThreadFactory()), options);
    }

    public SentryExecutorService() {
        this(new ScheduledThreadPoolExecutor(1, new SentryExecutorServiceThreadFactory()), null);
    }

    private boolean isQueueAvailable() {
        if (this.executorService.getQueue().size() >= 271) {
            this.executorService.purge();
        }
        return this.executorService.getQueue().size() < 271;
    }

    @Override
    @NotNull
    public Future<?> submit(@NotNull Runnable runnable) throws RejectedExecutionException {
        if (this.isQueueAvailable()) {
            return this.executorService.submit(runnable);
        }
        if (this.options != null) {
            this.options.getLogger().log(SentryLevel.WARNING, "Task " + runnable + " rejected from " + this.executorService, new Object[0]);
        }
        return new CancelledFuture();
    }

    @Override
    @NotNull
    public <T> Future<T> submit(@NotNull Callable<T> callable) throws RejectedExecutionException {
        if (this.isQueueAvailable()) {
            return this.executorService.submit(callable);
        }
        if (this.options != null) {
            this.options.getLogger().log(SentryLevel.WARNING, "Task " + callable + " rejected from " + this.executorService, new Object[0]);
        }
        return new CancelledFuture();
    }

    @Override
    @NotNull
    public Future<?> schedule(@NotNull Runnable runnable, long delayMillis) throws RejectedExecutionException {
        return this.executorService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close(long timeoutMillis) {
        block9: {
            try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
                if (this.executorService.isShutdown()) break block9;
                this.executorService.shutdown();
                try {
                    if (!this.executorService.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
                        this.executorService.shutdownNow();
                    }
                }
                catch (InterruptedException e) {
                    this.executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public boolean isClosed() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.executorService.isShutdown();
            return bl;
        }
    }

    @Override
    public void prewarm() {
        block2: {
            try {
                this.executorService.submit(() -> {
                    try {
                        for (int i = 0; i < 40; ++i) {
                            ScheduledFuture<?> future = this.executorService.schedule(this.dummyRunnable, 365L, TimeUnit.DAYS);
                            future.cancel(true);
                        }
                        this.executorService.purge();
                    }
                    catch (RejectedExecutionException rejectedExecutionException) {
                        // empty catch block
                    }
                });
            }
            catch (RejectedExecutionException e) {
                if (this.options == null) break block2;
                this.options.getLogger().log(SentryLevel.WARNING, "Prewarm task rejected from " + this.executorService, e);
            }
        }
    }

    private static final class SentryExecutorServiceThreadFactory
    implements ThreadFactory {
        private int cnt;

        private SentryExecutorServiceThreadFactory() {
        }

        @Override
        @NotNull
        public Thread newThread(@NotNull Runnable r) {
            Thread ret = new Thread(r, "SentryExecutorServiceThreadFactory-" + this.cnt++);
            ret.setDaemon(true);
            return ret;
        }
    }

    private static final class CancelledFuture<T>
    implements Future<T> {
        private CancelledFuture() {
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return true;
        }

        @Override
        public boolean isCancelled() {
            return true;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public T get() {
            throw new CancellationException();
        }

        @Override
        public T get(long timeout, @NotNull TimeUnit unit) {
            throw new CancellationException();
        }
    }
}

