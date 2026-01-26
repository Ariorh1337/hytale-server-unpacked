/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
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
import io.sentry.NoOpScopesLifecycleToken;
import io.sentry.ProfileChunk;
import io.sentry.ProfilingTraceData;
import io.sentry.ScopeCallback;
import io.sentry.ScopeType;
import io.sentry.Sentry;
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

public final class ScopesAdapter
implements IScopes {
    private static final ScopesAdapter INSTANCE = new ScopesAdapter();

    private ScopesAdapter() {
    }

    public static ScopesAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isEnabled() {
        return Sentry.isEnabled();
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
        return Sentry.captureEvent(event, hint);
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return Sentry.captureEvent(event, hint, callback);
    }

    @Override
    @NotNull
    public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
        return Sentry.captureMessage(message, level);
    }

    @Override
    @NotNull
    public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
        return Sentry.captureMessage(message, level, callback);
    }

    @Override
    @NotNull
    public SentryId captureFeedback(@NotNull Feedback feedback) {
        return Sentry.captureFeedback(feedback);
    }

    @Override
    @NotNull
    public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint) {
        return Sentry.captureFeedback(feedback, hint);
    }

    @Override
    @NotNull
    public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback callback) {
        return Sentry.captureFeedback(feedback, hint, callback);
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
        return Sentry.getCurrentScopes().captureEnvelope(envelope, hint);
    }

    @Override
    @NotNull
    public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
        return Sentry.captureException(throwable, hint);
    }

    @Override
    @NotNull
    public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return Sentry.captureException(throwable, hint, callback);
    }

    @Override
    public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
        Sentry.captureUserFeedback(userFeedback);
    }

    @Override
    public void startSession() {
        Sentry.startSession();
    }

    @Override
    public void endSession() {
        Sentry.endSession();
    }

    @Override
    public void close(boolean isRestarting) {
        Sentry.close();
    }

    @Override
    public void close() {
        Sentry.close();
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
        Sentry.addBreadcrumb(breadcrumb, hint);
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
        this.addBreadcrumb(breadcrumb, new Hint());
    }

    @Override
    public void setLevel(@Nullable SentryLevel level) {
        Sentry.setLevel(level);
    }

    @Override
    public void setTransaction(@Nullable String transaction) {
        Sentry.setTransaction(transaction);
    }

    @Override
    public void setUser(@Nullable User user) {
        Sentry.setUser(user);
    }

    @Override
    public void setFingerprint(@NotNull List<String> fingerprint) {
        Sentry.setFingerprint(fingerprint);
    }

    @Override
    public void clearBreadcrumbs() {
        Sentry.clearBreadcrumbs();
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
        Sentry.setTag(key, value);
    }

    @Override
    public void removeTag(@Nullable String key) {
        Sentry.removeTag(key);
    }

    @Override
    public void setExtra(@Nullable String key, @Nullable String value) {
        Sentry.setExtra(key, value);
    }

    @Override
    public void removeExtra(@Nullable String key) {
        Sentry.removeExtra(key);
    }

    @Override
    @NotNull
    public SentryId getLastEventId() {
        return Sentry.getLastEventId();
    }

    @Override
    @NotNull
    public ISentryLifecycleToken pushScope() {
        return Sentry.pushScope();
    }

    @Override
    @NotNull
    public ISentryLifecycleToken pushIsolationScope() {
        return Sentry.pushIsolationScope();
    }

    @Override
    @Deprecated
    public void popScope() {
        Sentry.popScope();
    }

    @Override
    public void withScope(@NotNull ScopeCallback callback) {
        Sentry.withScope(callback);
    }

    @Override
    public void withIsolationScope(@NotNull ScopeCallback callback) {
        Sentry.withIsolationScope(callback);
    }

    @Override
    public void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
        Sentry.configureScope(scopeType, callback);
    }

    @Override
    public void bindClient(@NotNull ISentryClient client) {
        Sentry.bindClient(client);
    }

    @Override
    public boolean isHealthy() {
        return Sentry.isHealthy();
    }

    @Override
    public void flush(long timeoutMillis) {
        Sentry.flush(timeoutMillis);
    }

    @Override
    @Deprecated
    @NotNull
    public IHub clone() {
        return Sentry.getCurrentScopes().clone();
    }

    @Override
    @NotNull
    public IScopes forkedScopes(@NotNull String creator) {
        return Sentry.forkedScopes(creator);
    }

    @Override
    @NotNull
    public IScopes forkedCurrentScope(@NotNull String creator) {
        return Sentry.forkedCurrentScope(creator);
    }

    @Override
    @NotNull
    public IScopes forkedRootScopes(@NotNull String creator) {
        return Sentry.forkedRootScopes(creator);
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
        return Sentry.getCurrentScopes().getScope();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getIsolationScope() {
        return Sentry.getCurrentScopes().getIsolationScope();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getGlobalScope() {
        return Sentry.getGlobalScope();
    }

    @Override
    @Nullable
    public IScopes getParentScopes() {
        return Sentry.getCurrentScopes().getParentScopes();
    }

    @Override
    public boolean isAncestorOf(@Nullable IScopes otherScopes) {
        return Sentry.getCurrentScopes().isAncestorOf(otherScopes);
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData) {
        return Sentry.getCurrentScopes().captureTransaction(transaction, traceContext, hint, profilingTraceData);
    }

    @Override
    @NotNull
    public SentryId captureProfileChunk(@NotNull ProfileChunk profileChunk) {
        return Sentry.getCurrentScopes().captureProfileChunk(profileChunk);
    }

    @Override
    @NotNull
    public ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
        return Sentry.startTransaction(transactionContext, transactionOptions);
    }

    @Override
    public void startProfiler() {
        Sentry.startProfiler();
    }

    @Override
    public void stopProfiler() {
        Sentry.stopProfiler();
    }

    @Override
    @ApiStatus.Internal
    public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
        Sentry.getCurrentScopes().setSpanContext(throwable, span, transactionName);
    }

    @Override
    @Nullable
    public ISpan getSpan() {
        return Sentry.getCurrentScopes().getSpan();
    }

    @Override
    public void setActiveSpan(@Nullable ISpan span) {
        Sentry.getCurrentScopes().setActiveSpan(span);
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public ITransaction getTransaction() {
        return Sentry.getCurrentScopes().getTransaction();
    }

    @Override
    @NotNull
    public SentryOptions getOptions() {
        return Sentry.getCurrentScopes().getOptions();
    }

    @Override
    @Nullable
    public Boolean isCrashedLastRun() {
        return Sentry.isCrashedLastRun();
    }

    @Override
    public void reportFullyDisplayed() {
        Sentry.reportFullyDisplayed();
    }

    @Override
    @Nullable
    public TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
        return Sentry.continueTrace(sentryTrace, baggageHeaders);
    }

    @Override
    @Nullable
    public SentryTraceHeader getTraceparent() {
        return Sentry.getTraceparent();
    }

    @Override
    @Nullable
    public BaggageHeader getBaggage() {
        return Sentry.getBaggage();
    }

    @Override
    @ApiStatus.Experimental
    @NotNull
    public SentryId captureCheckIn(@NotNull CheckIn checkIn) {
        return Sentry.captureCheckIn(checkIn);
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public RateLimiter getRateLimiter() {
        return Sentry.getCurrentScopes().getRateLimiter();
    }

    @Override
    @NotNull
    public SentryId captureReplay(@NotNull SentryReplayEvent replay, @Nullable Hint hint) {
        return Sentry.getCurrentScopes().captureReplay(replay, hint);
    }

    @Override
    @NotNull
    public ILoggerApi logger() {
        return Sentry.getCurrentScopes().logger();
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
        Sentry.addFeatureFlag(flag, result);
    }
}

