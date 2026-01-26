/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import com.hypixel.hytale.logger.HytaleLogger;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class CompletableFutureUtil {
    public static final Function<Throwable, ?> fn = throwable -> {
        if (!(throwable instanceof TailedRuntimeException)) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause((Throwable)throwable)).log("Unhandled exception! " + String.valueOf(Thread.currentThread()));
        }
        throw new TailedRuntimeException((Throwable)throwable);
    };

    @Nonnull
    public static <T> CompletableFuture<T> whenComplete(@Nonnull CompletableFuture<T> future, @Nonnull CompletableFuture<T> callee) {
        return future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                callee.completeExceptionally((Throwable)throwable);
            } else {
                callee.complete(result);
            }
        });
    }

    public static boolean isCanceled(Throwable throwable) {
        return throwable instanceof CancellationException || throwable instanceof CompletionException && throwable.getCause() != null && throwable.getCause() != throwable && CompletableFutureUtil.isCanceled(throwable.getCause());
    }

    @Nonnull
    public static <T> CompletableFuture<T> _catch(@Nonnull CompletableFuture<T> future) {
        return future.exceptionally(fn);
    }

    @Nonnull
    public static <T> CompletableFuture<T> completionCanceled() {
        CompletableFuture out = new CompletableFuture();
        out.cancel(false);
        return out;
    }

    public static void joinWithProgress(@Nonnull List<CompletableFuture<?>> list, @Nonnull ProgressConsumer callback, int millisSleep, int millisProgress) throws InterruptedException {
        CompletableFuture<Void> all = CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
        long last = System.nanoTime();
        long nanosProgress = TimeUnit.MILLISECONDS.toNanos(millisProgress);
        int listSize = list.size();
        while (!all.isDone()) {
            Thread.sleep(millisSleep);
            long now = System.nanoTime();
            if (last + nanosProgress >= now) continue;
            last = now;
            int done = 0;
            for (CompletableFuture<?> c : list) {
                if (!c.isDone()) continue;
                ++done;
            }
            if (done >= listSize) continue;
            callback.accept((double)done / (double)listSize, done, listSize);
        }
        callback.accept(1.0, listSize, listSize);
        all.join();
    }

    @FunctionalInterface
    public static interface ProgressConsumer {
        public void accept(double var1, int var3, int var4);
    }

    static class TailedRuntimeException
    extends RuntimeException {
        public TailedRuntimeException(Throwable cause) {
            super(cause);
        }
    }
}

