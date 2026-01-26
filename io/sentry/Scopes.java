/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Baggage;
import io.sentry.BaggageHeader;
import io.sentry.Breadcrumb;
import io.sentry.CheckIn;
import io.sentry.CombinedScopeView;
import io.sentry.CompositePerformanceCollector;
import io.sentry.DataCategory;
import io.sentry.Hint;
import io.sentry.HubScopesWrapper;
import io.sentry.IHub;
import io.sentry.IScope;
import io.sentry.IScopes;
import io.sentry.ISentryClient;
import io.sentry.ISentryExecutorService;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.ISpanFactory;
import io.sentry.ITransaction;
import io.sentry.ITransactionProfiler;
import io.sentry.Integration;
import io.sentry.NoOpScope;
import io.sentry.NoOpScopesLifecycleToken;
import io.sentry.NoOpSentryClient;
import io.sentry.NoOpTransaction;
import io.sentry.ProfileChunk;
import io.sentry.ProfileLifecycle;
import io.sentry.ProfilingTraceData;
import io.sentry.PropagationContext;
import io.sentry.SamplingContext;
import io.sentry.Scope;
import io.sentry.ScopeCallback;
import io.sentry.ScopeType;
import io.sentry.Sentry;
import io.sentry.SentryCrashLastRunState;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.SentryReplayEvent;
import io.sentry.SentryTraceHeader;
import io.sentry.Session;
import io.sentry.TraceContext;
import io.sentry.TracesSampler;
import io.sentry.TracesSamplingDecision;
import io.sentry.TransactionContext;
import io.sentry.TransactionOptions;
import io.sentry.UserFeedback;
import io.sentry.clientreport.DiscardReason;
import io.sentry.hints.SessionEndHint;
import io.sentry.hints.SessionStartHint;
import io.sentry.logger.ILoggerApi;
import io.sentry.logger.LoggerApi;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import io.sentry.util.SpanUtils;
import io.sentry.util.TracingUtils;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Scopes
implements IScopes {
    @NotNull
    private final IScope scope;
    @NotNull
    private final IScope isolationScope;
    @NotNull
    private final IScope globalScope;
    @Nullable
    private final Scopes parentScopes;
    @NotNull
    private final String creator;
    @NotNull
    private final CompositePerformanceCollector compositePerformanceCollector;
    @NotNull
    private final CombinedScopeView combinedScope;
    @NotNull
    private final ILoggerApi logger;

    public Scopes(@NotNull IScope scope, @NotNull IScope isolationScope, @NotNull IScope globalScope, @NotNull String creator) {
        this(scope, isolationScope, globalScope, null, creator);
    }

    private Scopes(@NotNull IScope scope, @NotNull IScope isolationScope, @NotNull IScope globalScope, @Nullable Scopes parentScopes, @NotNull String creator) {
        this.combinedScope = new CombinedScopeView(globalScope, isolationScope, scope);
        this.scope = scope;
        this.isolationScope = isolationScope;
        this.globalScope = globalScope;
        this.parentScopes = parentScopes;
        this.creator = creator;
        @NotNull SentryOptions options = this.getOptions();
        Scopes.validateOptions(options);
        this.compositePerformanceCollector = options.getCompositePerformanceCollector();
        this.logger = new LoggerApi(this);
    }

    @NotNull
    public String getCreator() {
        return this.creator;
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getScope() {
        return this.scope;
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getIsolationScope() {
        return this.isolationScope;
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public IScope getGlobalScope() {
        return this.globalScope;
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public IScopes getParentScopes() {
        return this.parentScopes;
    }

    @Override
    @ApiStatus.Internal
    public boolean isAncestorOf(@Nullable IScopes otherScopes) {
        if (otherScopes == null) {
            return false;
        }
        if (this == otherScopes) {
            return true;
        }
        if (otherScopes.getParentScopes() != null) {
            return this.isAncestorOf(otherScopes.getParentScopes());
        }
        return false;
    }

    @Override
    @NotNull
    public IScopes forkedScopes(@NotNull String creator) {
        return new Scopes(this.scope.clone(), this.isolationScope.clone(), this.globalScope, this, creator);
    }

    @Override
    @NotNull
    public IScopes forkedCurrentScope(@NotNull String creator) {
        return new Scopes(this.scope.clone(), this.isolationScope, this.globalScope, this, creator);
    }

    @Override
    @NotNull
    public IScopes forkedRootScopes(@NotNull String creator) {
        return Sentry.forkedRootScopes(creator);
    }

    @Override
    public boolean isEnabled() {
        return this.getClient().isEnabled();
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
        return this.captureEventInternal(event, hint, null);
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return this.captureEventInternal(event, hint, callback);
    }

    @NotNull
    private SentryId captureEventInternal(@NotNull SentryEvent event, @Nullable Hint hint, @Nullable ScopeCallback scopeCallback) {
        SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureEvent' call is a no-op.", new Object[0]);
        } else if (event == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "captureEvent called with null parameter.", new Object[0]);
        } else {
            try {
                this.assignTraceContext(event);
                IScope localScope = this.buildLocalScope(this.getCombinedScopeView(), scopeCallback);
                sentryId = this.getClient().captureEvent(event, localScope, hint);
                this.updateLastEventId(sentryId);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing event with id: " + event.getEventId(), e);
            }
        }
        return sentryId;
    }

    @ApiStatus.Internal
    @NotNull
    public ISentryClient getClient() {
        return this.getCombinedScopeView().getClient();
    }

    private void assignTraceContext(@NotNull SentryEvent event) {
        this.getCombinedScopeView().assignTraceContext(event);
    }

    @NotNull
    private IScope buildLocalScope(@NotNull IScope parentScope, @Nullable ScopeCallback callback) {
        if (callback != null) {
            try {
                IScope localScope = parentScope.clone();
                callback.run(localScope);
                return localScope;
            }
            catch (Throwable t) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'ScopeCallback' callback.", t);
            }
        }
        return parentScope;
    }

    @Override
    @NotNull
    public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
        return this.captureMessageInternal(message, level, null);
    }

    @Override
    @NotNull
    public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
        return this.captureMessageInternal(message, level, callback);
    }

    @NotNull
    private SentryId captureMessageInternal(@NotNull String message, @NotNull SentryLevel level, @Nullable ScopeCallback scopeCallback) {
        SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureMessage' call is a no-op.", new Object[0]);
        } else if (message == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "captureMessage called with null parameter.", new Object[0]);
        } else {
            try {
                IScope localScope = this.buildLocalScope(this.getCombinedScopeView(), scopeCallback);
                sentryId = this.getClient().captureMessage(message, level, localScope);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing message: " + message, e);
            }
        }
        this.updateLastEventId(sentryId);
        return sentryId;
    }

    @Override
    @NotNull
    public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback scopeCallback) {
        SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureFeedback' call is a no-op.", new Object[0]);
        } else if (feedback.getMessage().isEmpty()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "captureFeedback called with empty message.", new Object[0]);
        } else {
            try {
                @NotNull IScope localScope = this.buildLocalScope(this.getCombinedScopeView(), scopeCallback);
                sentryId = this.getClient().captureFeedback(feedback, hint, localScope);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing feedback: " + feedback.getMessage(), e);
            }
        }
        return sentryId;
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
        Objects.requireNonNull(envelope, "SentryEnvelope is required.");
        SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureEnvelope' call is a no-op.", new Object[0]);
        } else {
            try {
                SentryId capturedEnvelopeId = this.getClient().captureEnvelope(envelope, hint);
                if (capturedEnvelopeId != null) {
                    sentryId = capturedEnvelopeId;
                }
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing envelope.", e);
            }
        }
        return sentryId;
    }

    @Override
    @NotNull
    public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
        return this.captureExceptionInternal(throwable, hint, null);
    }

    @Override
    @NotNull
    public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return this.captureExceptionInternal(throwable, hint, callback);
    }

    @NotNull
    private SentryId captureExceptionInternal(@NotNull Throwable throwable, @Nullable Hint hint, @Nullable ScopeCallback scopeCallback) {
        SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureException' call is a no-op.", new Object[0]);
        } else if (throwable == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "captureException called with null parameter.", new Object[0]);
        } else {
            try {
                SentryEvent event = new SentryEvent(throwable);
                this.assignTraceContext(event);
                IScope localScope = this.buildLocalScope(this.getCombinedScopeView(), scopeCallback);
                sentryId = this.getClient().captureEvent(event, localScope, hint);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing exception: " + throwable.getMessage(), e);
            }
        }
        this.updateLastEventId(sentryId);
        return sentryId;
    }

    @Override
    public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureUserFeedback' call is a no-op.", new Object[0]);
        } else {
            try {
                this.getClient().captureUserFeedback(userFeedback);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing captureUserFeedback: " + userFeedback.toString(), e);
            }
        }
    }

    @Override
    public void startSession() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'startSession' call is a no-op.", new Object[0]);
        } else {
            Scope.SessionPair pair = this.getCombinedScopeView().startSession();
            if (pair != null) {
                Hint hint;
                if (pair.getPrevious() != null) {
                    hint = HintUtils.createWithTypeCheckHint(new SessionEndHint());
                    this.getClient().captureSession(pair.getPrevious(), hint);
                }
                hint = HintUtils.createWithTypeCheckHint(new SessionStartHint());
                this.getClient().captureSession(pair.getCurrent(), hint);
            } else {
                this.getOptions().getLogger().log(SentryLevel.WARNING, "Session could not be started.", new Object[0]);
            }
        }
    }

    @Override
    public void endSession() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'endSession' call is a no-op.", new Object[0]);
        } else {
            Session previousSession = this.getCombinedScopeView().endSession();
            if (previousSession != null) {
                Hint hint = HintUtils.createWithTypeCheckHint(new SessionEndHint());
                this.getClient().captureSession(previousSession, hint);
            }
        }
    }

    @ApiStatus.Internal
    @NotNull
    public IScope getCombinedScopeView() {
        return this.combinedScope;
    }

    @Override
    public void close() {
        this.close(false);
    }

    @Override
    public void close(boolean isRestarting) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'close' call is a no-op.", new Object[0]);
        } else {
            try {
                for (Integration integration : this.getOptions().getIntegrations()) {
                    if (!(integration instanceof Closeable)) continue;
                    try {
                        ((Closeable)((Object)integration)).close();
                    }
                    catch (Throwable e) {
                        this.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to close the integration {}.", integration, e);
                    }
                }
                this.configureScope(scope -> scope.clear());
                this.configureScope(ScopeType.ISOLATION, scope -> scope.clear());
                this.getOptions().getBackpressureMonitor().close();
                this.getOptions().getTransactionProfiler().close();
                this.getOptions().getContinuousProfiler().close(true);
                this.getOptions().getCompositePerformanceCollector().close();
                this.getOptions().getConnectionStatusProvider().close();
                @NotNull ISentryExecutorService executorService = this.getOptions().getExecutorService();
                if (isRestarting) {
                    try {
                        executorService.submit(() -> executorService.close(this.getOptions().getShutdownTimeoutMillis()));
                    }
                    catch (RejectedExecutionException e) {
                        this.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to submit executor service shutdown task during restart. Shutting down synchronously.", e);
                        executorService.close(this.getOptions().getShutdownTimeoutMillis());
                    }
                } else {
                    executorService.close(this.getOptions().getShutdownTimeoutMillis());
                }
                this.configureScope(ScopeType.CURRENT, scope -> scope.getClient().close(isRestarting));
                this.configureScope(ScopeType.ISOLATION, scope -> scope.getClient().close(isRestarting));
                this.configureScope(ScopeType.GLOBAL, scope -> scope.getClient().close(isRestarting));
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while closing the Scopes.", e);
            }
        }
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'addBreadcrumb' call is a no-op.", new Object[0]);
        } else if (breadcrumb == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "addBreadcrumb called with null parameter.", new Object[0]);
        } else {
            this.getCombinedScopeView().addBreadcrumb(breadcrumb, hint);
        }
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
        this.addBreadcrumb(breadcrumb, new Hint());
    }

    @Override
    public void setLevel(@Nullable SentryLevel level) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setLevel' call is a no-op.", new Object[0]);
        } else {
            this.getCombinedScopeView().setLevel(level);
        }
    }

    @Override
    public void setTransaction(@Nullable String transaction) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setTransaction' call is a no-op.", new Object[0]);
        } else if (transaction != null) {
            this.getCombinedScopeView().setTransaction(transaction);
        } else {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Transaction cannot be null", new Object[0]);
        }
    }

    @Override
    public void setUser(@Nullable User user) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setUser' call is a no-op.", new Object[0]);
        } else {
            this.getCombinedScopeView().setUser(user);
        }
    }

    @Override
    public void setFingerprint(@NotNull List<String> fingerprint) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setFingerprint' call is a no-op.", new Object[0]);
        } else if (fingerprint == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "setFingerprint called with null parameter.", new Object[0]);
        } else {
            this.getCombinedScopeView().setFingerprint(fingerprint);
        }
    }

    @Override
    public void clearBreadcrumbs() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'clearBreadcrumbs' call is a no-op.", new Object[0]);
        } else {
            this.getCombinedScopeView().clearBreadcrumbs();
        }
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setTag' call is a no-op.", new Object[0]);
        } else if (key == null || value == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "setTag called with null parameter.", new Object[0]);
        } else {
            this.getCombinedScopeView().setTag(key, value);
        }
    }

    @Override
    public void removeTag(@Nullable String key) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'removeTag' call is a no-op.", new Object[0]);
        } else if (key == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "removeTag called with null parameter.", new Object[0]);
        } else {
            this.getCombinedScopeView().removeTag(key);
        }
    }

    @Override
    public void setExtra(@Nullable String key, @Nullable String value) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setExtra' call is a no-op.", new Object[0]);
        } else if (key == null || value == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "setExtra called with null parameter.", new Object[0]);
        } else {
            this.getCombinedScopeView().setExtra(key, value);
        }
    }

    @Override
    public void removeExtra(@Nullable String key) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'removeExtra' call is a no-op.", new Object[0]);
        } else if (key == null) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "removeExtra called with null parameter.", new Object[0]);
        } else {
            this.getCombinedScopeView().removeExtra(key);
        }
    }

    private void updateLastEventId(@NotNull SentryId lastEventId) {
        this.getCombinedScopeView().setLastEventId(lastEventId);
    }

    @Override
    @NotNull
    public SentryId getLastEventId() {
        return this.getCombinedScopeView().getLastEventId();
    }

    @Override
    public ISentryLifecycleToken pushScope() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'pushScope' call is a no-op.", new Object[0]);
            return NoOpScopesLifecycleToken.getInstance();
        }
        @NotNull IScopes scopes = this.forkedCurrentScope("pushScope");
        return scopes.makeCurrent();
    }

    @Override
    public ISentryLifecycleToken pushIsolationScope() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'pushIsolationScope' call is a no-op.", new Object[0]);
            return NoOpScopesLifecycleToken.getInstance();
        }
        @NotNull IScopes scopes = this.forkedScopes("pushIsolationScope");
        return scopes.makeCurrent();
    }

    @Override
    @NotNull
    public ISentryLifecycleToken makeCurrent() {
        return Sentry.setCurrentScopes(this);
    }

    @Override
    @Deprecated
    public void popScope() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'popScope' call is a no-op.", new Object[0]);
        } else {
            @Nullable Scopes parent = this.parentScopes;
            if (parent != null) {
                parent.makeCurrent();
            }
        }
    }

    @Override
    public void withScope(@NotNull ScopeCallback callback) {
        if (!this.isEnabled()) {
            try {
                callback.run(NoOpScope.getInstance());
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withScope' callback.", e);
            }
        } else {
            @NotNull IScopes forkedScopes = this.forkedCurrentScope("withScope");
            try (@NotNull ISentryLifecycleToken ignored = forkedScopes.makeCurrent();){
                callback.run(forkedScopes.getScope());
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withScope' callback.", e);
            }
        }
    }

    @Override
    public void withIsolationScope(@NotNull ScopeCallback callback) {
        if (!this.isEnabled()) {
            try {
                callback.run(NoOpScope.getInstance());
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withIsolationScope' callback.", e);
            }
        } else {
            @NotNull IScopes forkedScopes = this.forkedScopes("withIsolationScope");
            try (@NotNull ISentryLifecycleToken ignored = forkedScopes.makeCurrent();){
                callback.run(forkedScopes.getIsolationScope());
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withIsolationScope' callback.", e);
            }
        }
    }

    @Override
    public void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'configureScope' call is a no-op.", new Object[0]);
        } else {
            try {
                callback.run(this.combinedScope.getSpecificScope(scopeType));
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'configureScope' callback.", e);
            }
        }
    }

    @Override
    public void bindClient(@NotNull ISentryClient client) {
        if (client != null) {
            this.getOptions().getLogger().log(SentryLevel.DEBUG, "New client bound to scope.", new Object[0]);
            this.getCombinedScopeView().bindClient(client);
        } else {
            this.getOptions().getLogger().log(SentryLevel.DEBUG, "NoOp client bound to scope.", new Object[0]);
            this.getCombinedScopeView().bindClient(NoOpSentryClient.getInstance());
        }
    }

    @Override
    public boolean isHealthy() {
        return this.getClient().isHealthy();
    }

    @Override
    public void flush(long timeoutMillis) {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'flush' call is a no-op.", new Object[0]);
        } else {
            try {
                this.getClient().flush(timeoutMillis);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'client.flush'.", e);
            }
        }
    }

    @Override
    @Deprecated
    @NotNull
    public IHub clone() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Disabled Scopes cloned.", new Object[0]);
        }
        return new HubScopesWrapper(this.forkedScopes("scopes clone"));
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData) {
        Objects.requireNonNull(transaction, "transaction is required");
        SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureTransaction' call is a no-op.", new Object[0]);
        } else if (!transaction.isFinished()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Transaction: %s is not finished and this 'captureTransaction' call is a no-op.", transaction.getEventId());
        } else if (!Boolean.TRUE.equals(transaction.isSampled())) {
            this.getOptions().getLogger().log(SentryLevel.DEBUG, "Transaction %s was dropped due to sampling decision.", transaction.getEventId());
            if (this.getOptions().getBackpressureMonitor().getDownsampleFactor() > 0) {
                this.getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.BACKPRESSURE, DataCategory.Transaction);
                this.getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.BACKPRESSURE, DataCategory.Span, transaction.getSpans().size() + 1);
            } else {
                this.getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.SAMPLE_RATE, DataCategory.Transaction);
                this.getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.SAMPLE_RATE, DataCategory.Span, transaction.getSpans().size() + 1);
            }
        } else {
            try {
                sentryId = this.getClient().captureTransaction(transaction, traceContext, this.getCombinedScopeView(), hint, profilingTraceData);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing transaction with id: " + transaction.getEventId(), e);
            }
        }
        return sentryId;
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryId captureProfileChunk(@NotNull ProfileChunk profilingContinuousData) {
        Objects.requireNonNull(profilingContinuousData, "profilingContinuousData is required");
        @NotNull SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureTransaction' call is a no-op.", new Object[0]);
        } else {
            try {
                sentryId = this.getClient().captureProfileChunk(profilingContinuousData, this.getScope());
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing profile chunk with id: " + profilingContinuousData.getChunkId(), e);
            }
        }
        return sentryId;
    }

    @Override
    @NotNull
    public ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
        return this.createTransaction(transactionContext, transactionOptions);
    }

    @NotNull
    private ITransaction createTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
        ITransaction transaction;
        Objects.requireNonNull(transactionContext, "transactionContext is required");
        transactionContext.setOrigin(transactionOptions.getOrigin());
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'startTransaction' returns a no-op.", new Object[0]);
            transaction = NoOpTransaction.getInstance();
        } else if (SpanUtils.isIgnored(this.getOptions().getIgnoredSpanOrigins(), transactionContext.getOrigin())) {
            this.getOptions().getLogger().log(SentryLevel.DEBUG, "Returning no-op for span origin %s as the SDK has been configured to ignore it", transactionContext.getOrigin());
            transaction = NoOpTransaction.getInstance();
        } else if (!this.getOptions().getInstrumenter().equals((Object)transactionContext.getInstrumenter())) {
            this.getOptions().getLogger().log(SentryLevel.DEBUG, "Returning no-op for instrumenter %s as the SDK has been configured to use instrumenter %s", new Object[]{transactionContext.getInstrumenter(), this.getOptions().getInstrumenter()});
            transaction = NoOpTransaction.getInstance();
        } else if (!this.getOptions().isTracingEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.INFO, "Tracing is disabled and this 'startTransaction' returns a no-op.", new Object[0]);
            transaction = NoOpTransaction.getInstance();
        } else {
            ISpanFactory spanFactory;
            Double sampleRand = this.getSampleRand(transactionContext);
            SamplingContext samplingContext = new SamplingContext(transactionContext, transactionOptions.getCustomSamplingContext(), sampleRand, null);
            @NotNull TracesSampler tracesSampler = this.getOptions().getInternalTracesSampler();
            @NotNull TracesSamplingDecision samplingDecision = tracesSampler.sample(samplingContext);
            transactionContext.setSamplingDecision(samplingDecision);
            @Nullable ISpanFactory maybeSpanFactory = transactionOptions.getSpanFactory();
            ISpanFactory iSpanFactory = spanFactory = maybeSpanFactory == null ? this.getOptions().getSpanFactory() : maybeSpanFactory;
            if (samplingDecision.getSampled().booleanValue() && this.getOptions().isContinuousProfilingEnabled() && this.getOptions().getProfileLifecycle() == ProfileLifecycle.TRACE && transactionContext.getProfilerId().equals(SentryId.EMPTY_ID)) {
                this.getOptions().getContinuousProfiler().startProfiler(ProfileLifecycle.TRACE, this.getOptions().getInternalTracesSampler());
            }
            transaction = spanFactory.createTransaction(transactionContext, this, transactionOptions, this.compositePerformanceCollector);
            if (samplingDecision.getSampled().booleanValue() && samplingDecision.getProfileSampled().booleanValue()) {
                ITransactionProfiler transactionProfiler = this.getOptions().getTransactionProfiler();
                if (!transactionProfiler.isRunning()) {
                    transactionProfiler.start();
                    transactionProfiler.bindTransaction(transaction);
                } else if (transactionOptions.isAppStartTransaction()) {
                    transactionProfiler.bindTransaction(transaction);
                }
            }
        }
        if (transactionOptions.isBindToScope()) {
            transaction.makeCurrent();
        }
        return transaction;
    }

    @NotNull
    private Double getSampleRand(@NotNull TransactionContext transactionContext) {
        Double sampleRandFromBaggageMaybe;
        @Nullable Baggage baggage = transactionContext.getBaggage();
        if (baggage != null && (sampleRandFromBaggageMaybe = baggage.getSampleRand()) != null) {
            return sampleRandFromBaggageMaybe;
        }
        return this.getCombinedScopeView().getPropagationContext().getSampleRand();
    }

    @Override
    public void startProfiler() {
        if (this.getOptions().isContinuousProfilingEnabled()) {
            if (this.getOptions().getProfileLifecycle() != ProfileLifecycle.MANUAL) {
                this.getOptions().getLogger().log(SentryLevel.WARNING, "Profiling lifecycle is %s. Profiling cannot be started manually.", this.getOptions().getProfileLifecycle().name());
                return;
            }
            this.getOptions().getContinuousProfiler().startProfiler(ProfileLifecycle.MANUAL, this.getOptions().getInternalTracesSampler());
        } else if (this.getOptions().isProfilingEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Continuous Profiling is not enabled. Set profilesSampleRate and profilesSampler to null to enable it.", new Object[0]);
        }
    }

    @Override
    public void stopProfiler() {
        if (this.getOptions().isContinuousProfilingEnabled()) {
            if (this.getOptions().getProfileLifecycle() != ProfileLifecycle.MANUAL) {
                this.getOptions().getLogger().log(SentryLevel.WARNING, "Profiling lifecycle is %s. Profiling cannot be stopped manually.", this.getOptions().getProfileLifecycle().name());
                return;
            }
            this.getOptions().getLogger().log(SentryLevel.DEBUG, "Stopped continuous Profiling.", new Object[0]);
            this.getOptions().getContinuousProfiler().stopProfiler(ProfileLifecycle.MANUAL);
        } else {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Continuous Profiling is not enabled. Set profilesSampleRate and profilesSampler to null to enable it.", new Object[0]);
        }
    }

    @Override
    @ApiStatus.Internal
    public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
        this.getCombinedScopeView().setSpanContext(throwable, span, transactionName);
    }

    @Override
    @Nullable
    public ISpan getSpan() {
        if (this.isEnabled()) {
            return this.getCombinedScopeView().getSpan();
        }
        this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getSpan' call is a no-op.", new Object[0]);
        return null;
    }

    @Override
    public void setActiveSpan(@Nullable ISpan span) {
        this.getCombinedScopeView().setActiveSpan(span);
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public ITransaction getTransaction() {
        ITransaction span = null;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getTransaction' call is a no-op.", new Object[0]);
        } else {
            span = this.getCombinedScopeView().getTransaction();
        }
        return span;
    }

    @Override
    @NotNull
    public SentryOptions getOptions() {
        return this.combinedScope.getOptions();
    }

    @Override
    @Nullable
    public Boolean isCrashedLastRun() {
        return SentryCrashLastRunState.getInstance().isCrashedLastRun(this.getOptions().getCacheDirPath(), !this.getOptions().isEnableAutoSessionTracking());
    }

    @Override
    public void reportFullyDisplayed() {
        if (this.getOptions().isEnableTimeToFullDisplayTracing()) {
            this.getOptions().getFullyDisplayedReporter().reportFullyDrawn();
        }
    }

    @Override
    @Nullable
    public TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
        @NotNull PropagationContext propagationContext = PropagationContext.fromHeaders(this.getOptions().getLogger(), sentryTrace, baggageHeaders);
        this.configureScope(scope -> scope.withPropagationContext(oldPropagationContext -> scope.setPropagationContext(propagationContext)));
        if (this.getOptions().isTracingEnabled()) {
            return TransactionContext.fromPropagationContext(propagationContext);
        }
        return null;
    }

    @Override
    @Nullable
    public SentryTraceHeader getTraceparent() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getTraceparent' call is a no-op.", new Object[0]);
        } else {
            @Nullable TracingUtils.TracingHeaders headers = TracingUtils.trace(this, null, this.getSpan());
            if (headers != null) {
                return headers.getSentryTraceHeader();
            }
        }
        return null;
    }

    @Override
    @Nullable
    public BaggageHeader getBaggage() {
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getBaggage' call is a no-op.", new Object[0]);
        } else {
            @Nullable TracingUtils.TracingHeaders headers = TracingUtils.trace(this, null, this.getSpan());
            if (headers != null) {
                return headers.getBaggageHeader();
            }
        }
        return null;
    }

    @Override
    @NotNull
    public SentryId captureCheckIn(@NotNull CheckIn checkIn) {
        SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureCheckIn' call is a no-op.", new Object[0]);
        } else {
            try {
                sentryId = this.getClient().captureCheckIn(checkIn, this.getCombinedScopeView(), null);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing check-in for slug", e);
            }
        }
        this.updateLastEventId(sentryId);
        return sentryId;
    }

    @Override
    @NotNull
    public SentryId captureReplay(@NotNull SentryReplayEvent replay, @Nullable Hint hint) {
        SentryId sentryId = SentryId.EMPTY_ID;
        if (!this.isEnabled()) {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureReplay' call is a no-op.", new Object[0]);
        } else {
            try {
                sentryId = this.getClient().captureReplayEvent(replay, this.getCombinedScopeView(), hint);
            }
            catch (Throwable e) {
                this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing replay", e);
            }
        }
        return sentryId;
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public RateLimiter getRateLimiter() {
        return this.getClient().getRateLimiter();
    }

    @Override
    @NotNull
    public ILoggerApi logger() {
        return this.logger;
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
        this.combinedScope.addFeatureFlag(flag, result);
    }

    private static void validateOptions(@NotNull SentryOptions options) {
        Objects.requireNonNull(options, "SentryOptions is required.");
        if (options.getDsn() == null || options.getDsn().isEmpty()) {
            throw new IllegalArgumentException("Scopes requires a DSN to be instantiated. Considering using the NoOpScopes if no DSN is available.");
        }
    }
}

