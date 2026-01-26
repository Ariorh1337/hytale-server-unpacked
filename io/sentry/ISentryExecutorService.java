/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface ISentryExecutorService {
    @NotNull
    public Future<?> submit(@NotNull Runnable var1) throws RejectedExecutionException;

    @NotNull
    public <T> Future<T> submit(@NotNull Callable<T> var1) throws RejectedExecutionException;

    @NotNull
    public Future<?> schedule(@NotNull Runnable var1, long var2) throws RejectedExecutionException;

    public void close(long var1);

    public boolean isClosed();

    public void prewarm();
}

