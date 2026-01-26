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

@Deprecated
public final class HubScopesWrapper
implements IHub {
    @NotNull
    private final IScopes scopes;

    public HubScopesWrapper(@NotNull IScopes scopes) {
        this.scopes = scopes;
    }

    @NotNull
    public IScopes getScopes() {
        return this.scopes;
    }

    @Override
    public boolean isEnabled() {
        return this.scopes.isEnabled();
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
        return this.scopes.captureEvent(event, hint);
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return this.scopes.captureEvent(event, hint, callback);
    }

    @Override
    @NotNull
    public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
        return this.scopes.captureMessage(message, level);
    }

    @Override
    @NotNull
    public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
        return this.scopes.captureMessage(message, level, callback);
    }

    @Override
    @NotNull
    public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback callback) {
        return this.scopes.captureFeedback(feedback, hint, callback);
    }

    @Override
    @NotNull
    public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
        return this.scopes.captureEnvelope(envelope, hint);
    }

    @Override
    @NotNull
    public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
        return this.scopes.captureException(throwable, hint);
    }

    @Override
    @NotNull
    public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return this.scopes.captureException(throwable, hint, callback);
    }

    @Override
    public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
        this.scopes.captureUserFeedback(userFeedback);
    }

    @Override
    public void startSession() {
        this.scopes.startSession();
    }

    @Override
    public void endSession() {
        this.scopes.endSession();
    }

    @Override
    public void close() {
        this.scopes.close();
    }

    @Override
    public void close(boolean isRestarting) {
        this.scopes.close(isRestarting);
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
        this.scopes.addBreadcrumb(breadcrumb, hint);
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
        this.scopes.addBreadcrumb(breadcrumb);
    }

    @Override
    public void setLevel(@Nullable SentryLevel level) {
        this.scopes.setLevel(level);
    }

    @Override
    public void setTransaction(@Nullable String transaction) {
        this.scopes.setTransaction(transaction);
    }

    @Override
    public void setUser(@Nullable User user) {
        this.scopes.setUser(user);
    }

    @Override
    public void setFingerprint(@NotNull List<String> fingerprint) {
        this.scopes.setFingerprint(fingerprint);
    }

    @Override
    public void clearBreadcrumbs() {
        this.scopes.clearBreadcrumbs();
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
        this.scopes.setTag(key, value);
    }

    @Override
    public void removeTag(@Nullable String key) {
        this.scopes.removeTag(key);
    }

    @Override
    public void setExtra(@Nullable String key, @Nullable String value) {
        this.scopes.setExtra(key, value);
    }

    @Override
    public void removeExtra(@Nullable String key) {
        this.scopes.removeExtra(key);
    }

    @Override
    @NotNull
    public SentryId getLastEventId() {
        return this.scopes.getLastEventId();
    }

    @Override
    @NotNull
    public ISentryLifecycleToken pushScope() {
        return this.scopes.pushScope();
    }

    @Override
    @NotNull
    public ISentryLifecycleToken pushIsolationScope() {
        return this.scopes.pushIsolationScope();
    }

    @Override
    @Deprecated
    public void popScope() {
        this.scopes.popScope();
    }

    @Override
    public void withScope(@NotNull ScopeCallback callback) {
        this.scopes.withScope(callback);
    }

    @Override
    public void withIsolationScope(@NotNull ScopeCallback callback) {
        this.scopes.withIsolationScope(callback);
    }

    @Override
    public void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
        this.scopes.configureScope(scopeType, callback);
    }

    @Override
    public void bindClient(@NotNull ISentryClient client) {
        this.scopes.bindClient(client);
    }

    @Override
    public boolean isHealthy() {
        return this.scopes.isHealthy();
    }

    @Override
    public void flush(long timeoutMillis) {
        this.scopes.flush(timeoutMillis);
    }

    @Override
    @Deprecated
    @NotNull
    public IHub clone() {
        return this.scopes.clone();
    }

    @Override
    @NotNull
    public IScopes forkedScopes(@NotNull String creator) {
        return this.scopes.forkedScopes(creator);
    }

    @Override
    @NotNull
    public IScopes forkedCurrentScope(@NotNull String creator) {
        return this.scopes.forkedCurrentScope(creator);
    }

    @Override
    @NotNull
    public IScopes forkedRootScopes(@NotNull String creator) {
        return Sentry.forkedRootScopes(creator);
    }

    @Override
    @NotNull
    public ISentryLifecycleToken makeCurrent() {
        return this.scopes.makeCurrent();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getScope() {
        return this.scopes.getScope();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getIsolationScope() {
        return this.scopes.getIsolationScope();
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
        return this.scopes.getParentScopes();
    }

    @Override
    public boolean isAncestorOf(@Nullable IScopes otherScopes) {
        return this.scopes.isAncestorOf(otherScopes);
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData) {
        return this.scopes.captureTransaction(transaction, traceContext, hint, profilingTraceData);
    }

    @Override
    @NotNull
    public SentryId captureProfileChunk(@NotNull ProfileChunk profileChunk) {
        return this.scopes.captureProfileChunk(profileChunk);
    }

    @Override
    @NotNull
    public ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
        return this.scopes.startTransaction(transactionContext, transactionOptions);
    }

    @Override
    public void startProfiler() {
        this.scopes.startProfiler();
    }

    @Override
    public void stopProfiler() {
        this.scopes.stopProfiler();
    }

    @Override
    @ApiStatus.Internal
    public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
        this.scopes.setSpanContext(throwable, span, transactionName);
    }

    @Override
    @Nullable
    public ISpan getSpan() {
        return this.scopes.getSpan();
    }

    @Override
    public void setActiveSpan(@Nullable ISpan span) {
        this.scopes.setActiveSpan(span);
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public ITransaction getTransaction() {
        return this.scopes.getTransaction();
    }

    @Override
    @NotNull
    public SentryOptions getOptions() {
        return this.scopes.getOptions();
    }

    @Override
    @Nullable
    public Boolean isCrashedLastRun() {
        return this.scopes.isCrashedLastRun();
    }

    @Override
    public void reportFullyDisplayed() {
        this.scopes.reportFullyDisplayed();
    }

    @Override
    @Nullable
    public TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
        return this.scopes.continueTrace(sentryTrace, baggageHeaders);
    }

    @Override
    @Nullable
    public SentryTraceHeader getTraceparent() {
        return this.scopes.getTraceparent();
    }

    @Override
    @Nullable
    public BaggageHeader getBaggage() {
        return this.scopes.getBaggage();
    }

    @Override
    @NotNull
    public SentryId captureCheckIn(@NotNull CheckIn checkIn) {
        return this.scopes.captureCheckIn(checkIn);
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public RateLimiter getRateLimiter() {
        return this.scopes.getRateLimiter();
    }

    @Override
    @NotNull
    public SentryId captureReplay(@NotNull SentryReplayEvent replay, @Nullable Hint hint) {
        return this.scopes.captureReplay(replay, hint);
    }

    @Override
    @NotNull
    public ILoggerApi logger() {
        return this.scopes.logger();
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
        this.scopes.addFeatureFlag(flag, result);
    }
}

