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
import io.sentry.IScopes;
import io.sentry.ISentryClient;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.NoOpScope;
import io.sentry.NoOpScopes;
import io.sentry.NoOpScopesLifecycleToken;
import io.sentry.NoOpTransaction;
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
import io.sentry.logger.NoOpLoggerApi;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class NoOpHub
implements IHub {
    private static final NoOpHub instance = new NoOpHub();
    @NotNull
    private final SentryOptions emptyOptions = SentryOptions.empty();

    private NoOpHub() {
    }

    @Deprecated
    public static NoOpHub getInstance() {
        return instance;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback callback) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return SentryId.EMPTY_ID;
    }

    @Override
    public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
    }

    @Override
    public void startSession() {
    }

    @Override
    public void endSession() {
    }

    @Override
    public void close() {
    }

    @Override
    public void close(boolean isRestarting) {
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
    }

    @Override
    public void setLevel(@Nullable SentryLevel level) {
    }

    @Override
    public void setTransaction(@Nullable String transaction) {
    }

    @Override
    public void setUser(@Nullable User user) {
    }

    @Override
    public void setFingerprint(@NotNull List<String> fingerprint) {
    }

    @Override
    public void clearBreadcrumbs() {
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
    }

    @Override
    public void removeTag(@Nullable String key) {
    }

    @Override
    public void setExtra(@Nullable String key, @Nullable String value) {
    }

    @Override
    public void removeExtra(@Nullable String key) {
    }

    @Override
    @NotNull
    public SentryId getLastEventId() {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public ISentryLifecycleToken pushScope() {
        return NoOpScopesLifecycleToken.getInstance();
    }

    @Override
    @NotNull
    public ISentryLifecycleToken pushIsolationScope() {
        return NoOpScopesLifecycleToken.getInstance();
    }

    @Override
    @Deprecated
    public void popScope() {
    }

    @Override
    public void withScope(@NotNull ScopeCallback callback) {
        callback.run(NoOpScope.getInstance());
    }

    @Override
    public void withIsolationScope(@NotNull ScopeCallback callback) {
        callback.run(NoOpScope.getInstance());
    }

    @Override
    public void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
    }

    @Override
    public void bindClient(@NotNull ISentryClient client) {
    }

    @Override
    public boolean isHealthy() {
        return true;
    }

    @Override
    public void flush(long timeoutMillis) {
    }

    @Override
    @Deprecated
    @NotNull
    public IHub clone() {
        return instance;
    }

    @Override
    @NotNull
    public IScopes forkedScopes(@NotNull String creator) {
        return NoOpScopes.getInstance();
    }

    @Override
    @NotNull
    public IScopes forkedCurrentScope(@NotNull String creator) {
        return NoOpScopes.getInstance();
    }

    @Override
    @NotNull
    public ISentryLifecycleToken makeCurrent() {
        return NoOpScopesLifecycleToken.getInstance();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getScope() {
        return NoOpScope.getInstance();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getIsolationScope() {
        return NoOpScope.getInstance();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getGlobalScope() {
        return NoOpScope.getInstance();
    }

    @Override
    @Nullable
    public IScopes getParentScopes() {
        return null;
    }

    @Override
    public boolean isAncestorOf(@Nullable IScopes otherScopes) {
        return false;
    }

    @Override
    @NotNull
    public IScopes forkedRootScopes(@NotNull String creator) {
        return NoOpScopes.getInstance();
    }

    @Override
    @NotNull
    public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureProfileChunk(@NotNull ProfileChunk profileChunk) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
        return NoOpTransaction.getInstance();
    }

    @Override
    public void startProfiler() {
    }

    @Override
    public void stopProfiler() {
    }

    @Override
    public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan spanContext, @NotNull String transactionName) {
    }

    @Override
    @Nullable
    public ISpan getSpan() {
        return null;
    }

    @Override
    public void setActiveSpan(@Nullable ISpan span) {
    }

    @Override
    @Nullable
    public ITransaction getTransaction() {
        return null;
    }

    @Override
    @NotNull
    public SentryOptions getOptions() {
        return this.emptyOptions;
    }

    @Override
    @Nullable
    public Boolean isCrashedLastRun() {
        return null;
    }

    @Override
    public void reportFullyDisplayed() {
    }

    @Override
    @Nullable
    public TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
        return null;
    }

    @Override
    @Nullable
    public SentryTraceHeader getTraceparent() {
        return null;
    }

    @Override
    @Nullable
    public BaggageHeader getBaggage() {
        return null;
    }

    @Override
    @NotNull
    public SentryId captureCheckIn(@NotNull CheckIn checkIn) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureReplay(@NotNull SentryReplayEvent replay, @Nullable Hint hint) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @Nullable
    public RateLimiter getRateLimiter() {
        return null;
    }

    @Override
    public boolean isNoOp() {
        return true;
    }

    @Override
    @NotNull
    public ILoggerApi logger() {
        return NoOpLoggerApi.getInstance();
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
    }
}

