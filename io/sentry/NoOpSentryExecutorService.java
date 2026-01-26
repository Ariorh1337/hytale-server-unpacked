/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ISentryExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.jetbrains.annotations.NotNull;

final class NoOpSentryExecutorService
implements ISentryExecutorService {
    private static final NoOpSentryExecutorService instance = new NoOpSentryExecutorService();

    private NoOpSentryExecutorService() {
    }

    @NotNull
    public static ISentryExecutorService getInstance() {
        return instance;
    }

    @Override
    @NotNull
    public Future<?> submit(@NotNull Runnable runnable) {
        return new FutureTask<Object>(() -> null);
    }

    @Override
    @NotNull
    public <T> Future<T> submit(@NotNull Callable<T> callable) {
        return new FutureTask<Object>(() -> null);
    }

    @Override
    @NotNull
    public Future<?> schedule(@NotNull Runnable runnable, long delayMillis) {
        return new FutureTask<Object>(() -> null);
    }

    @Override
    public void close(long timeoutMillis) {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void prewarm() {
    }
}

