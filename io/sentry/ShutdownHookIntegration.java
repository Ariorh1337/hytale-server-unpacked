/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 *  org.jetbrains.annotations.VisibleForTesting
 */
package io.sentry;

import io.sentry.IScopes;
import io.sentry.Integration;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.IntegrationUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.VisibleForTesting;

public final class ShutdownHookIntegration
implements Integration,
Closeable {
    @NotNull
    private final Runtime runtime;
    @Nullable
    private Thread thread;

    @TestOnly
    public ShutdownHookIntegration(@NotNull Runtime runtime) {
        this.runtime = Objects.requireNonNull(runtime, "Runtime is required");
    }

    public ShutdownHookIntegration() {
        this(Runtime.getRuntime());
    }

    @Override
    public void register(@NotNull IScopes scopes, @NotNull SentryOptions options) {
        Objects.requireNonNull(scopes, "Scopes are required");
        Objects.requireNonNull(options, "SentryOptions is required");
        if (options.isEnableShutdownHook()) {
            this.thread = new Thread(() -> scopes.flush(options.getFlushTimeoutMillis()), "sentry-shutdownhook");
            this.handleShutdownInProgress(() -> {
                this.runtime.addShutdownHook(this.thread);
                options.getLogger().log(SentryLevel.DEBUG, "ShutdownHookIntegration installed.", new Object[0]);
                IntegrationUtils.addIntegrationToSdkVersion("ShutdownHook");
            });
        } else {
            options.getLogger().log(SentryLevel.INFO, "enableShutdownHook is disabled.", new Object[0]);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.thread != null) {
            this.handleShutdownInProgress(() -> this.runtime.removeShutdownHook(this.thread));
        }
    }

    private void handleShutdownInProgress(@NotNull Runnable runnable) {
        block2: {
            try {
                runnable.run();
            }
            catch (IllegalStateException e) {
                @Nullable String message = e.getMessage();
                if (message != null && (message.equals("Shutdown in progress") || message.equals("VM already shutting down"))) break block2;
                throw e;
            }
        }
    }

    @VisibleForTesting
    @Nullable
    Thread getHook() {
        return this.thread;
    }
}

