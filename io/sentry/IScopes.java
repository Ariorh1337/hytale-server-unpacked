/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.BaggageHeader;
import io.sentry.Breadcrumb;
import io.sentry.CheckIn;
import io.sentry.Hint;
import io.sentry.IHub;
import io.sentry.IScope;
import io.sentry.ISentryClient;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.ProfileChunk;
import io.sentry.ProfilingTraceData;
import io.sentry.ScopeCallback;
import io.sentry.ScopeType;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.SentryReplayEvent;
import io.sentry.SentryTraceHeader;
import io.sentry.TraceContext;
import io.sentry.TransactionContext;
import io.sentry.TransactionOptions;
import io.sentry.UserFeedback;
import io.sentry.logger.ILoggerApi;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScopes {
    public boolean isEnabled();

    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent var1, @Nullable Hint var2);

    @NotNull
    default public SentryId captureEvent(@NotNull SentryEvent event) {
        return this.captureEvent(event, new Hint());
    }

    @NotNull
    default public SentryId captureEvent(@NotNull SentryEvent event, @NotNull ScopeCallback callback) {
        return this.captureEvent(event, new Hint(), callback);
    }

    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent var1, @Nullable Hint var2, @NotNull ScopeCallback var3);

    @NotNull
    default public SentryId captureMessage(@NotNull String message) {
        return this.captureMessage(message, SentryLevel.INFO);
    }

    @NotNull
    public SentryId captureMessage(@NotNull String var1, @NotNull SentryLevel var2);

    @NotNull
    public SentryId captureMessage(@NotNull String var1, @NotNull SentryLevel var2, @NotNull ScopeCallback var3);

    @NotNull
    default public SentryId captureMessage(@NotNull String message, @NotNull ScopeCallback callback) {
        return this.captureMessage(message, SentryLevel.INFO, callback);
    }

    @NotNull
    default public SentryId captureFeedback(@NotNull Feedback feedback) {
        return this.captureFeedback(feedback, null);
    }

    @NotNull
    default public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint) {
        return this.captureFeedback(feedback, hint, null);
    }

    @NotNull
    public SentryId captureFeedback(@NotNull Feedback var1, @Nullable Hint var2, @Nullable ScopeCallback var3);

    @NotNull
    public SentryId captureEnvelope(@NotNull SentryEnvelope var1, @Nullable Hint var2);

    @NotNull
    default public SentryId captureEnvelope(@NotNull SentryEnvelope envelope) {
        return this.captureEnvelope(envelope, new Hint());
    }

    @NotNull
    public SentryId captureException(@NotNull Throwable var1, @Nullable Hint var2);

    @NotNull
    default public SentryId captureException(@NotNull Throwable throwable) {
        return this.captureException(throwable, new Hint());
    }

    @NotNull
    default public SentryId captureException(@NotNull Throwable throwable, @NotNull ScopeCallback callback) {
        return this.captureException(throwable, new Hint(), callback);
    }

    @NotNull
    public SentryId captureException(@NotNull Throwable var1, @Nullable Hint var2, @NotNull ScopeCallback var3);

    public void captureUserFeedback(@NotNull UserFeedback var1);

    public void startSession();

    public void endSession();

    public void close();

    public void close(boolean var1);

    public void addBreadcrumb(@NotNull Breadcrumb var1, @Nullable Hint var2);

    public void addBreadcrumb(@NotNull Breadcrumb var1);

    default public void addBreadcrumb(@NotNull String message) {
        this.addBreadcrumb(new Breadcrumb(message));
    }

    default public void addBreadcrumb(@NotNull String message, @NotNull String category) {
        Breadcrumb breadcrumb = new Breadcrumb(message);
        breadcrumb.setCategory(category);
        this.addBreadcrumb(breadcrumb);
    }

    public void setLevel(@Nullable SentryLevel var1);

    public void setTransaction(@Nullable String var1);

    public void setUser(@Nullable User var1);

    public void setFingerprint(@NotNull List<String> var1);

    public void clearBreadcrumbs();

    public void setTag(@Nullable String var1, @Nullable String var2);

    public void removeTag(@Nullable String var1);

    public void setExtra(@Nullable String var1, @Nullable String var2);

    public void removeExtra(@Nullable String var1);

    @NotNull
    public SentryId getLastEventId();

    @NotNull
    public ISentryLifecycleToken pushScope();

    @NotNull
    public ISentryLifecycleToken pushIsolationScope();

    @Deprecated
    public void popScope();

    public void withScope(@NotNull ScopeCallback var1);

    public void withIsolationScope(@NotNull ScopeCallback var1);

    default public void configureScope(@NotNull ScopeCallback callback) {
        this.configureScope(null, callback);
    }

    public void configureScope(@Nullable ScopeType var1, @NotNull ScopeCallback var2);

    public void bindClient(@NotNull ISentryClient var1);

    public boolean isHealthy();

    public void flush(long var1);

    @Deprecated
    @NotNull
    public IHub clone();

    @NotNull
    public IScopes forkedScopes(@NotNull String var1);

    @NotNull
    public IScopes forkedCurrentScope(@NotNull String var1);

    @NotNull
    public IScopes forkedRootScopes(@NotNull String var1);

    @NotNull
    public ISentryLifecycleToken makeCurrent();

    @ApiStatus.Internal
    @NotNull
    public IScope getScope();

    @ApiStatus.Internal
    @NotNull
    public IScope getIsolationScope();

    @ApiStatus.Internal
    @NotNull
    public IScope getGlobalScope();

    @ApiStatus.Internal
    @Nullable
    public IScopes getParentScopes();

    @ApiStatus.Internal
    public boolean isAncestorOf(@Nullable IScopes var1);

    @ApiStatus.Internal
    @NotNull
    public SentryId captureTransaction(@NotNull SentryTransaction var1, @Nullable TraceContext var2, @Nullable Hint var3, @Nullable ProfilingTraceData var4);

    @ApiStatus.Internal
    @NotNull
    default public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint) {
        return this.captureTransaction(transaction, traceContext, hint, null);
    }

    @ApiStatus.Internal
    @NotNull
    default public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable Hint hint) {
        return this.captureTransaction(transaction, null, hint);
    }

    @ApiStatus.Internal
    @NotNull
    default public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext) {
        return this.captureTransaction(transaction, traceContext, null);
    }

    @ApiStatus.Internal
    @NotNull
    public SentryId captureProfileChunk(@NotNull ProfileChunk var1);

    @NotNull
    default public ITransaction startTransaction(@NotNull TransactionContext transactionContexts) {
        return this.startTransaction(transactionContexts, new TransactionOptions());
    }

    @NotNull
    default public ITransaction startTransaction(@NotNull String name, @NotNull String operation) {
        return this.startTransaction(name, operation, new TransactionOptions());
    }

    @NotNull
    default public ITransaction startTransaction(@NotNull String name, @NotNull String operation, @NotNull TransactionOptions transactionOptions) {
        return this.startTransaction(new TransactionContext(name, operation), transactionOptions);
    }

    @NotNull
    public ITransaction startTransaction(@NotNull TransactionContext var1, @NotNull TransactionOptions var2);

    public void startProfiler();

    public void stopProfiler();

    @ApiStatus.Internal
    public void setSpanContext(@NotNull Throwable var1, @NotNull ISpan var2, @NotNull String var3);

    @Nullable
    public ISpan getSpan();

    @ApiStatus.Internal
    public void setActiveSpan(@Nullable ISpan var1);

    @ApiStatus.Internal
    @Nullable
    public ITransaction getTransaction();

    @NotNull
    public SentryOptions getOptions();

    @Nullable
    public Boolean isCrashedLastRun();

    public void reportFullyDisplayed();

    @Nullable
    public TransactionContext continueTrace(@Nullable String var1, @Nullable List<String> var2);

    @Nullable
    public SentryTraceHeader getTraceparent();

    @Nullable
    public BaggageHeader getBaggage();

    @NotNull
    public SentryId captureCheckIn(@NotNull CheckIn var1);

    @ApiStatus.Internal
    @Nullable
    public RateLimiter getRateLimiter();

    default public boolean isNoOp() {
        return false;
    }

    @NotNull
    public SentryId captureReplay(@NotNull SentryReplayEvent var1, @Nullable Hint var2);

    @NotNull
    public ILoggerApi logger();

    public void addFeatureFlag(@Nullable String var1, @Nullable Boolean var2);
}

