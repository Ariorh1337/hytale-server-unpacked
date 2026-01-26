/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import io.sentry.Sentry;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class SentryWrapper {
    public static <U> Callable<U> wrapCallable(@NotNull Callable<U> callable) {
        IScopes newScopes = Sentry.getCurrentScopes().forkedScopes("SentryWrapper.wrapCallable");
        return () -> {
            try (ISentryLifecycleToken ignored = newScopes.makeCurrent();){
                Object v = callable.call();
                return v;
            }
        };
    }

    public static <U> Supplier<U> wrapSupplier(@NotNull Supplier<U> supplier) {
        IScopes newScopes = Sentry.forkedScopes("SentryWrapper.wrapSupplier");
        return () -> {
            try (ISentryLifecycleToken ignore = newScopes.makeCurrent();){
                Object t = supplier.get();
                return t;
            }
        };
    }

    public static Runnable wrapRunnable(@NotNull Runnable runnable) {
        IScopes newScopes = Sentry.forkedScopes("SentryWrapper.wrapRunnable");
        return () -> {
            try (ISentryLifecycleToken ignore = newScopes.makeCurrent();){
                runnable.run();
            }
        };
    }
}

