/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DataCategory;
import io.sentry.DirectoryProcessor;
import io.sentry.IConnectionStatusProvider;
import io.sentry.ILogger;
import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import io.sentry.Integration;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.transport.RateLimiter;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.IntegrationUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SendCachedEnvelopeFireAndForgetIntegration
implements Integration,
IConnectionStatusProvider.IConnectionStatusObserver,
Closeable {
    @NotNull
    private final SendFireAndForgetFactory factory;
    @Nullable
    private IConnectionStatusProvider connectionStatusProvider;
    @Nullable
    private IScopes scopes;
    @Nullable
    private SentryOptions options;
    @Nullable
    private SendFireAndForget sender;
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    @NotNull
    private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

    public SendCachedEnvelopeFireAndForgetIntegration(@NotNull SendFireAndForgetFactory factory) {
        this.factory = Objects.requireNonNull(factory, "SendFireAndForgetFactory is required");
    }

    @Override
    public void register(@NotNull IScopes scopes, @NotNull SentryOptions options) {
        this.scopes = Objects.requireNonNull(scopes, "Scopes are required");
        this.options = Objects.requireNonNull(options, "SentryOptions is required");
        String cachedDir = options.getCacheDirPath();
        if (!this.factory.hasValidPath(cachedDir, options.getLogger())) {
            options.getLogger().log(SentryLevel.ERROR, "No cache dir path is defined in options.", new Object[0]);
            return;
        }
        options.getLogger().log(SentryLevel.DEBUG, "SendCachedEventFireAndForgetIntegration installed.", new Object[0]);
        IntegrationUtils.addIntegrationToSdkVersion("SendCachedEnvelopeFireAndForget");
        this.sendCachedEnvelopes(scopes, options);
    }

    @Override
    public void close() throws IOException {
        this.isClosed.set(true);
        if (this.connectionStatusProvider != null) {
            this.connectionStatusProvider.removeConnectionStatusObserver(this);
        }
    }

    @Override
    public void onConnectionStatusChanged(@NotNull IConnectionStatusProvider.ConnectionStatus status) {
        if (this.scopes != null && this.options != null && status != IConnectionStatusProvider.ConnectionStatus.DISCONNECTED) {
            this.sendCachedEnvelopes(this.scopes, this.options);
        }
    }

    private void sendCachedEnvelopes(@NotNull IScopes scopes, @NotNull SentryOptions options) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            options.getExecutorService().submit(() -> {
                try {
                    if (this.isClosed.get()) {
                        options.getLogger().log(SentryLevel.INFO, "SendCachedEnvelopeFireAndForgetIntegration, not trying to send after closing.", new Object[0]);
                        return;
                    }
                    if (!this.isInitialized.getAndSet(true)) {
                        this.connectionStatusProvider = options.getConnectionStatusProvider();
                        this.connectionStatusProvider.addConnectionStatusObserver(this);
                        this.sender = this.factory.create(scopes, options);
                    }
                    if (this.connectionStatusProvider != null && this.connectionStatusProvider.getConnectionStatus() == IConnectionStatusProvider.ConnectionStatus.DISCONNECTED) {
                        options.getLogger().log(SentryLevel.INFO, "SendCachedEnvelopeFireAndForgetIntegration, no connection.", new Object[0]);
                        return;
                    }
                    @Nullable RateLimiter rateLimiter = scopes.getRateLimiter();
                    if (rateLimiter != null && rateLimiter.isActiveForCategory(DataCategory.All)) {
                        options.getLogger().log(SentryLevel.INFO, "SendCachedEnvelopeFireAndForgetIntegration, rate limiting active.", new Object[0]);
                        return;
                    }
                    if (this.sender == null) {
                        options.getLogger().log(SentryLevel.ERROR, "SendFireAndForget factory is null.", new Object[0]);
                        return;
                    }
                    this.sender.send();
                }
                catch (Throwable e) {
                    options.getLogger().log(SentryLevel.ERROR, "Failed trying to send cached events.", e);
                }
            });
        }
        catch (RejectedExecutionException e) {
            options.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. Cached events will not be sent. Did you call Sentry.close()?", e);
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. Cached events will not be sent", e);
        }
    }

    public static interface SendFireAndForgetFactory {
        @Nullable
        public SendFireAndForget create(@NotNull IScopes var1, @NotNull SentryOptions var2);

        default public boolean hasValidPath(@Nullable String dirPath, @NotNull ILogger logger) {
            if (dirPath == null || dirPath.isEmpty()) {
                logger.log(SentryLevel.INFO, "No cached dir path is defined in options.", new Object[0]);
                return false;
            }
            return true;
        }

        @NotNull
        default public SendFireAndForget processDir(@NotNull DirectoryProcessor directoryProcessor, @NotNull String dirPath, @NotNull ILogger logger) {
            File dirFile = new File(dirPath);
            return () -> {
                logger.log(SentryLevel.DEBUG, "Started processing cached files from %s", dirPath);
                directoryProcessor.processDirectory(dirFile);
                logger.log(SentryLevel.DEBUG, "Finished processing cached files from %s", dirPath);
            };
        }
    }

    public static interface SendFireAndForget {
        public void send();
    }

    public static interface SendFireAndForgetDirPath {
        @Nullable
        public String getDirPath();
    }
}

