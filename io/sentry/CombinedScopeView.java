/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Attachment;
import io.sentry.Breadcrumb;
import io.sentry.CombinedContextsView;
import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.IScope;
import io.sentry.ISentryClient;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.NoOpSentryClient;
import io.sentry.PropagationContext;
import io.sentry.Scope;
import io.sentry.ScopeType;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.featureflags.FeatureFlagBuffer;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.EventProcessorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CombinedScopeView
implements IScope {
    private final IScope globalScope;
    private final IScope isolationScope;
    private final IScope scope;

    public CombinedScopeView(@NotNull IScope globalScope, @NotNull IScope isolationScope, @NotNull IScope scope) {
        this.globalScope = globalScope;
        this.isolationScope = isolationScope;
        this.scope = scope;
    }

    @Override
    @Nullable
    public SentryLevel getLevel() {
        @Nullable SentryLevel current = this.scope.getLevel();
        if (current != null) {
            return current;
        }
        @Nullable SentryLevel isolation = this.isolationScope.getLevel();
        if (isolation != null) {
            return isolation;
        }
        return this.globalScope.getLevel();
    }

    @Override
    public void setLevel(@Nullable SentryLevel level) {
        this.getDefaultWriteScope().setLevel(level);
    }

    @Override
    @Nullable
    public String getTransactionName() {
        @Nullable String current = this.scope.getTransactionName();
        if (current != null) {
            return current;
        }
        @Nullable String isolation = this.isolationScope.getTransactionName();
        if (isolation != null) {
            return isolation;
        }
        return this.globalScope.getTransactionName();
    }

    @Override
    public void setTransaction(@NotNull String transaction) {
        this.getDefaultWriteScope().setTransaction(transaction);
    }

    @Override
    @Nullable
    public ISpan getSpan() {
        @Nullable ISpan current = this.scope.getSpan();
        if (current != null) {
            return current;
        }
        @Nullable ISpan isolation = this.isolationScope.getSpan();
        if (isolation != null) {
            return isolation;
        }
        return this.globalScope.getSpan();
    }

    @Override
    public void setActiveSpan(@Nullable ISpan span) {
        this.scope.setActiveSpan(span);
    }

    @Override
    public void setTransaction(@Nullable ITransaction transaction) {
        this.getDefaultWriteScope().setTransaction(transaction);
    }

    @Override
    @Nullable
    public User getUser() {
        @Nullable User current = this.scope.getUser();
        if (current != null) {
            return current;
        }
        @Nullable User isolation = this.isolationScope.getUser();
        if (isolation != null) {
            return isolation;
        }
        return this.globalScope.getUser();
    }

    @Override
    public void setUser(@Nullable User user) {
        this.getDefaultWriteScope().setUser(user);
    }

    @Override
    @Nullable
    public String getScreen() {
        @Nullable String current = this.scope.getScreen();
        if (current != null) {
            return current;
        }
        @Nullable String isolation = this.isolationScope.getScreen();
        if (isolation != null) {
            return isolation;
        }
        return this.globalScope.getScreen();
    }

    @Override
    public void setScreen(@Nullable String screen) {
        this.getDefaultWriteScope().setScreen(screen);
    }

    @Override
    @Nullable
    public Request getRequest() {
        @Nullable Request current = this.scope.getRequest();
        if (current != null) {
            return current;
        }
        @Nullable Request isolation = this.isolationScope.getRequest();
        if (isolation != null) {
            return isolation;
        }
        return this.globalScope.getRequest();
    }

    @Override
    public void setRequest(@Nullable Request request) {
        this.getDefaultWriteScope().setRequest(request);
    }

    @Override
    @NotNull
    public List<String> getFingerprint() {
        @Nullable List<String> current = this.scope.getFingerprint();
        if (!current.isEmpty()) {
            return current;
        }
        @Nullable List<String> isolation = this.isolationScope.getFingerprint();
        if (!isolation.isEmpty()) {
            return isolation;
        }
        return this.globalScope.getFingerprint();
    }

    @Override
    public void setFingerprint(@NotNull List<String> fingerprint) {
        this.getDefaultWriteScope().setFingerprint(fingerprint);
    }

    @Override
    @NotNull
    public Queue<Breadcrumb> getBreadcrumbs() {
        @NotNull ArrayList<Breadcrumb> allBreadcrumbs = new ArrayList<Breadcrumb>();
        allBreadcrumbs.addAll(this.globalScope.getBreadcrumbs());
        allBreadcrumbs.addAll(this.isolationScope.getBreadcrumbs());
        allBreadcrumbs.addAll(this.scope.getBreadcrumbs());
        Collections.sort(allBreadcrumbs);
        @NotNull Queue<Breadcrumb> breadcrumbs = Scope.createBreadcrumbsList(this.scope.getOptions().getMaxBreadcrumbs());
        breadcrumbs.addAll(allBreadcrumbs);
        return breadcrumbs;
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
        this.getDefaultWriteScope().addBreadcrumb(breadcrumb, hint);
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
        this.getDefaultWriteScope().addBreadcrumb(breadcrumb);
    }

    @Override
    public void clearBreadcrumbs() {
        this.getDefaultWriteScope().clearBreadcrumbs();
    }

    @Override
    public void clearTransaction() {
        this.getDefaultWriteScope().clearTransaction();
    }

    @Override
    @Nullable
    public ITransaction getTransaction() {
        @Nullable ITransaction current = this.scope.getTransaction();
        if (current != null) {
            return current;
        }
        @Nullable ITransaction isolation = this.isolationScope.getTransaction();
        if (isolation != null) {
            return isolation;
        }
        return this.globalScope.getTransaction();
    }

    @Override
    public void clear() {
        this.getDefaultWriteScope().clear();
    }

    @Override
    @NotNull
    public Map<String, String> getTags() {
        @NotNull ConcurrentHashMap<String, String> allTags = new ConcurrentHashMap<String, String>();
        allTags.putAll(this.globalScope.getTags());
        allTags.putAll(this.isolationScope.getTags());
        allTags.putAll(this.scope.getTags());
        return allTags;
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
        this.getDefaultWriteScope().setTag(key, value);
    }

    @Override
    public void removeTag(@Nullable String key) {
        this.getDefaultWriteScope().removeTag(key);
    }

    @Override
    @NotNull
    public Map<String, Object> getExtras() {
        @NotNull ConcurrentHashMap<String, Object> allTags = new ConcurrentHashMap<String, Object>();
        allTags.putAll(this.globalScope.getExtras());
        allTags.putAll(this.isolationScope.getExtras());
        allTags.putAll(this.scope.getExtras());
        return allTags;
    }

    @Override
    public void setExtra(@Nullable String key, @Nullable String value) {
        this.getDefaultWriteScope().setExtra(key, value);
    }

    @Override
    public void removeExtra(@Nullable String key) {
        this.getDefaultWriteScope().removeExtra(key);
    }

    @Override
    @NotNull
    public Contexts getContexts() {
        return new CombinedContextsView(this.globalScope.getContexts(), this.isolationScope.getContexts(), this.scope.getContexts(), this.getOptions().getDefaultScopeType());
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Object value) {
        this.getDefaultWriteScope().setContexts(key, value);
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Boolean value) {
        this.getDefaultWriteScope().setContexts(key, value);
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable String value) {
        this.getDefaultWriteScope().setContexts(key, value);
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Number value) {
        this.getDefaultWriteScope().setContexts(key, value);
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Collection<?> value) {
        this.getDefaultWriteScope().setContexts(key, value);
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Object[] value) {
        this.getDefaultWriteScope().setContexts(key, value);
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Character value) {
        this.getDefaultWriteScope().setContexts(key, value);
    }

    @Override
    public void removeContexts(@Nullable String key) {
        this.getDefaultWriteScope().removeContexts(key);
    }

    @NotNull
    private IScope getDefaultWriteScope() {
        return this.getSpecificScope(null);
    }

    IScope getSpecificScope(@Nullable ScopeType scopeType) {
        if (scopeType != null) {
            switch (scopeType) {
                case CURRENT: {
                    return this.scope;
                }
                case ISOLATION: {
                    return this.isolationScope;
                }
                case GLOBAL: {
                    return this.globalScope;
                }
                case COMBINED: {
                    return this;
                }
            }
        }
        switch (this.getOptions().getDefaultScopeType()) {
            case CURRENT: {
                return this.scope;
            }
            case ISOLATION: {
                return this.isolationScope;
            }
            case GLOBAL: {
                return this.globalScope;
            }
        }
        return this.scope;
    }

    @Override
    @NotNull
    public List<Attachment> getAttachments() {
        @NotNull CopyOnWriteArrayList<Attachment> allAttachments = new CopyOnWriteArrayList<Attachment>();
        allAttachments.addAll(this.globalScope.getAttachments());
        allAttachments.addAll(this.isolationScope.getAttachments());
        allAttachments.addAll(this.scope.getAttachments());
        return allAttachments;
    }

    @Override
    public void addAttachment(@NotNull Attachment attachment) {
        this.getDefaultWriteScope().addAttachment(attachment);
    }

    @Override
    public void clearAttachments() {
        this.getDefaultWriteScope().clearAttachments();
    }

    @Override
    @NotNull
    public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
        @NotNull CopyOnWriteArrayList<EventProcessorAndOrder> allEventProcessors = new CopyOnWriteArrayList<EventProcessorAndOrder>();
        allEventProcessors.addAll(this.globalScope.getEventProcessorsWithOrder());
        allEventProcessors.addAll(this.isolationScope.getEventProcessorsWithOrder());
        allEventProcessors.addAll(this.scope.getEventProcessorsWithOrder());
        Collections.sort(allEventProcessors);
        return allEventProcessors;
    }

    @Override
    @NotNull
    public List<EventProcessor> getEventProcessors() {
        return EventProcessorUtils.unwrap(this.getEventProcessorsWithOrder());
    }

    @Override
    public void addEventProcessor(@NotNull EventProcessor eventProcessor) {
        this.getDefaultWriteScope().addEventProcessor(eventProcessor);
    }

    @Override
    @Nullable
    public Session withSession(@NotNull Scope.IWithSession sessionCallback) {
        return this.getDefaultWriteScope().withSession(sessionCallback);
    }

    @Override
    @Nullable
    public Scope.SessionPair startSession() {
        return this.getDefaultWriteScope().startSession();
    }

    @Override
    @Nullable
    public Session endSession() {
        return this.getDefaultWriteScope().endSession();
    }

    @Override
    public void withTransaction(@NotNull Scope.IWithTransaction callback) {
        this.getDefaultWriteScope().withTransaction(callback);
    }

    @Override
    @NotNull
    public SentryOptions getOptions() {
        return this.globalScope.getOptions();
    }

    @Override
    @Nullable
    public Session getSession() {
        @Nullable Session current = this.scope.getSession();
        if (current != null) {
            return current;
        }
        @Nullable Session isolation = this.isolationScope.getSession();
        if (isolation != null) {
            return isolation;
        }
        return this.globalScope.getSession();
    }

    @Override
    public void clearSession() {
        this.getDefaultWriteScope().clearSession();
    }

    @Override
    public void setPropagationContext(@NotNull PropagationContext propagationContext) {
        this.getDefaultWriteScope().setPropagationContext(propagationContext);
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public PropagationContext getPropagationContext() {
        return this.getDefaultWriteScope().getPropagationContext();
    }

    @Override
    @NotNull
    public PropagationContext withPropagationContext(@NotNull Scope.IWithPropagationContext callback) {
        return this.getDefaultWriteScope().withPropagationContext(callback);
    }

    @Override
    @NotNull
    public IScope clone() {
        return new CombinedScopeView(this.globalScope, this.isolationScope.clone(), this.scope.clone());
    }

    @Override
    public void setLastEventId(@NotNull SentryId lastEventId) {
        this.globalScope.setLastEventId(lastEventId);
        this.isolationScope.setLastEventId(lastEventId);
        this.scope.setLastEventId(lastEventId);
    }

    @Override
    @NotNull
    public SentryId getLastEventId() {
        return this.globalScope.getLastEventId();
    }

    @Override
    public void bindClient(@NotNull ISentryClient client) {
        this.getDefaultWriteScope().bindClient(client);
    }

    @Override
    @NotNull
    public ISentryClient getClient() {
        @Nullable ISentryClient current = this.scope.getClient();
        if (!(current instanceof NoOpSentryClient)) {
            return current;
        }
        @Nullable ISentryClient isolation = this.isolationScope.getClient();
        if (!(isolation instanceof NoOpSentryClient)) {
            return isolation;
        }
        return this.globalScope.getClient();
    }

    @Override
    public void assignTraceContext(@NotNull SentryEvent event) {
        this.globalScope.assignTraceContext(event);
    }

    @Override
    public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
        this.globalScope.setSpanContext(throwable, span, transactionName);
    }

    @Override
    @ApiStatus.Internal
    public void replaceOptions(@NotNull SentryOptions options) {
        this.globalScope.replaceOptions(options);
    }

    @Override
    @NotNull
    public SentryId getReplayId() {
        @NotNull SentryId current = this.scope.getReplayId();
        if (!SentryId.EMPTY_ID.equals(current)) {
            return current;
        }
        @Nullable SentryId isolation = this.isolationScope.getReplayId();
        if (!SentryId.EMPTY_ID.equals(isolation)) {
            return isolation;
        }
        return this.globalScope.getReplayId();
    }

    @Override
    public void setReplayId(@NotNull SentryId replayId) {
        this.getDefaultWriteScope().setReplayId(replayId);
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
        this.getDefaultWriteScope().addFeatureFlag(flag, result);
        @Nullable ISpan span = this.getSpan();
        if (span != null) {
            span.addFeatureFlag(flag, result);
        }
    }

    @Override
    @Nullable
    public FeatureFlags getFeatureFlags() {
        return this.getFeatureFlagBuffer().getFeatureFlags();
    }

    @Override
    @NotNull
    public IFeatureFlagBuffer getFeatureFlagBuffer() {
        return FeatureFlagBuffer.merged(this.getOptions(), this.globalScope.getFeatureFlagBuffer(), this.isolationScope.getFeatureFlagBuffer(), this.scope.getFeatureFlagBuffer());
    }
}

