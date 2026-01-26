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

import io.sentry.Hint;
import io.sentry.ILogger;
import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ITransaction;
import io.sentry.Integration;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.UncaughtExceptionHandler;
import io.sentry.exception.ExceptionMechanismException;
import io.sentry.hints.BlockingFlushHint;
import io.sentry.hints.EventDropReason;
import io.sentry.hints.SessionEnd;
import io.sentry.hints.TransactionEnd;
import io.sentry.protocol.Mechanism;
import io.sentry.protocol.SentryId;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.HintUtils;
import io.sentry.util.IntegrationUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public final class UncaughtExceptionHandlerIntegration
implements Integration,
Thread.UncaughtExceptionHandler,
Closeable {
    @Nullable
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;
    @NotNull
    private static final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
    @Nullable
    private IScopes scopes;
    @Nullable
    private SentryOptions options;
    private boolean registered = false;
    @NotNull
    private final UncaughtExceptionHandler threadAdapter;

    public UncaughtExceptionHandlerIntegration() {
        this(UncaughtExceptionHandler.Adapter.getInstance());
    }

    UncaughtExceptionHandlerIntegration(@NotNull UncaughtExceptionHandler threadAdapter) {
        this.threadAdapter = Objects.requireNonNull(threadAdapter, "threadAdapter is required.");
    }

    @Override
    public final void register(@NotNull IScopes scopes, @NotNull SentryOptions options) {
        if (this.registered) {
            options.getLogger().log(SentryLevel.ERROR, "Attempt to register a UncaughtExceptionHandlerIntegration twice.", new Object[0]);
            return;
        }
        this.registered = true;
        this.scopes = Objects.requireNonNull(scopes, "Scopes are required");
        this.options = Objects.requireNonNull(options, "SentryOptions is required");
        this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration enabled: %s", this.options.isEnableUncaughtExceptionHandler());
        if (this.options.isEnableUncaughtExceptionHandler()) {
            try (@NotNull ISentryLifecycleToken ignored = lock.acquire();){
                Thread.UncaughtExceptionHandler currentHandler = this.threadAdapter.getDefaultUncaughtExceptionHandler();
                if (currentHandler != null) {
                    this.options.getLogger().log(SentryLevel.DEBUG, "default UncaughtExceptionHandler class='" + currentHandler.getClass().getName() + "'", new Object[0]);
                    if (currentHandler instanceof UncaughtExceptionHandlerIntegration) {
                        UncaughtExceptionHandlerIntegration currentHandlerIntegration = (UncaughtExceptionHandlerIntegration)currentHandler;
                        this.defaultExceptionHandler = currentHandlerIntegration.scopes != null && scopes.getGlobalScope() == currentHandlerIntegration.scopes.getGlobalScope() ? currentHandlerIntegration.defaultExceptionHandler : currentHandler;
                    } else {
                        this.defaultExceptionHandler = currentHandler;
                    }
                }
                this.threadAdapter.setDefaultUncaughtExceptionHandler(this);
            }
            this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration installed.", new Object[0]);
            IntegrationUtils.addIntegrationToSdkVersion("UncaughtExceptionHandler");
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable thrown) {
        if (this.options != null && this.scopes != null) {
            this.options.getLogger().log(SentryLevel.INFO, "Uncaught exception received.", new Object[0]);
            try {
                UncaughtExceptionHint exceptionHint = new UncaughtExceptionHint(this.options.getFlushTimeoutMillis(), this.options.getLogger());
                Throwable throwable = UncaughtExceptionHandlerIntegration.getUnhandledThrowable(thread, thrown);
                SentryEvent event = new SentryEvent(throwable);
                event.setLevel(SentryLevel.FATAL);
                ITransaction transaction = this.scopes.getTransaction();
                if (transaction == null && event.getEventId() != null) {
                    exceptionHint.setFlushable(event.getEventId());
                }
                Hint hint = HintUtils.createWithTypeCheckHint(exceptionHint);
                @NotNull SentryId sentryId = this.scopes.captureEvent(event, hint);
                boolean isEventDropped = sentryId.equals(SentryId.EMPTY_ID);
                EventDropReason eventDropReason = HintUtils.getEventDropReason(hint);
                if (!(isEventDropped && !EventDropReason.MULTITHREADED_DEDUPLICATION.equals((Object)eventDropReason) || exceptionHint.waitFlush())) {
                    this.options.getLogger().log(SentryLevel.WARNING, "Timed out waiting to flush event to disk before crashing. Event: %s", event.getEventId());
                }
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "Error sending uncaught exception to Sentry.", e);
            }
            if (this.defaultExceptionHandler != null) {
                this.options.getLogger().log(SentryLevel.INFO, "Invoking inner uncaught exception handler.", new Object[0]);
                this.defaultExceptionHandler.uncaughtException(thread, thrown);
            } else if (this.options.isPrintUncaughtStackTrace()) {
                thrown.printStackTrace();
            }
        }
    }

    @TestOnly
    @NotNull
    static Throwable getUnhandledThrowable(@NotNull Thread thread, @NotNull Throwable thrown) {
        Mechanism mechanism = new Mechanism();
        mechanism.setHandled(false);
        mechanism.setType("UncaughtExceptionHandler");
        return new ExceptionMechanismException(mechanism, thrown, thread);
    }

    @Override
    public void close() {
        try (@NotNull ISentryLifecycleToken ignored = lock.acquire();){
            if (this == this.threadAdapter.getDefaultUncaughtExceptionHandler()) {
                this.threadAdapter.setDefaultUncaughtExceptionHandler(this.defaultExceptionHandler);
                if (this.options != null) {
                    this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration removed.", new Object[0]);
                }
            } else {
                this.removeFromHandlerTree(this.threadAdapter.getDefaultUncaughtExceptionHandler());
            }
        }
    }

    private void removeFromHandlerTree(@Nullable Thread.UncaughtExceptionHandler currentHandler) {
        this.removeFromHandlerTree(currentHandler, new HashSet<Thread.UncaughtExceptionHandler>());
    }

    private void removeFromHandlerTree(@Nullable Thread.UncaughtExceptionHandler currentHandler, @NotNull Set<Thread.UncaughtExceptionHandler> visited) {
        if (currentHandler == null) {
            if (this.options != null) {
                this.options.getLogger().log(SentryLevel.DEBUG, "Found no UncaughtExceptionHandler to remove.", new Object[0]);
            }
            return;
        }
        if (!visited.add(currentHandler)) {
            if (this.options != null) {
                this.options.getLogger().log(SentryLevel.WARNING, "Cycle detected in UncaughtExceptionHandler chain while removing handler.", new Object[0]);
            }
            return;
        }
        if (!(currentHandler instanceof UncaughtExceptionHandlerIntegration)) {
            return;
        }
        UncaughtExceptionHandlerIntegration currentHandlerIntegration = (UncaughtExceptionHandlerIntegration)currentHandler;
        if (this == currentHandlerIntegration.defaultExceptionHandler) {
            currentHandlerIntegration.defaultExceptionHandler = this.defaultExceptionHandler;
            if (this.options != null) {
                this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration removed.", new Object[0]);
            }
        } else {
            this.removeFromHandlerTree(currentHandlerIntegration.defaultExceptionHandler, visited);
        }
    }

    @ApiStatus.Internal
    public static class UncaughtExceptionHint
    extends BlockingFlushHint
    implements SessionEnd,
    TransactionEnd {
        private final AtomicReference<SentryId> flushableEventId = new AtomicReference();

        public UncaughtExceptionHint(long flushTimeoutMillis, @NotNull ILogger logger) {
            super(flushTimeoutMillis, logger);
        }

        @Override
        public boolean isFlushable(@Nullable SentryId eventId) {
            SentryId unwrapped = this.flushableEventId.get();
            return unwrapped != null && unwrapped.equals(eventId);
        }

        @Override
        public void setFlushable(@NotNull SentryId eventId) {
            this.flushableEventId.set(eventId);
        }
    }
}

