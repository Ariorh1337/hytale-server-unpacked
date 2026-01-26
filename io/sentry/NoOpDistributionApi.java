/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IDistributionApi;
import io.sentry.UpdateInfo;
import io.sentry.UpdateStatus;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public final class NoOpDistributionApi
implements IDistributionApi {
    private static final NoOpDistributionApi instance = new NoOpDistributionApi();

    private NoOpDistributionApi() {
    }

    public static NoOpDistributionApi getInstance() {
        return instance;
    }

    @Override
    @NotNull
    public UpdateStatus checkForUpdateBlocking() {
        return UpdateStatus.UpToDate.getInstance();
    }

    @Override
    @NotNull
    public Future<UpdateStatus> checkForUpdate() {
        return new CompletedFuture<UpdateStatus>(UpdateStatus.UpToDate.getInstance());
    }

    @Override
    public void downloadUpdate(@NotNull UpdateInfo info) {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    private static final class CompletedFuture<T>
    implements Future<T> {
        private final T result;

        CompletedFuture(T result) {
            this.result = result;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public T get() throws ExecutionException {
            return this.result;
        }

        @Override
        public T get(long timeout, @NotNull TimeUnit unit) throws ExecutionException, TimeoutException {
            return this.result;
        }
    }
}

