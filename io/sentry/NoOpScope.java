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
import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.IScope;
import io.sentry.ISentryClient;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.NoOpSentryClient;
import io.sentry.PropagationContext;
import io.sentry.Scope;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.featureflags.NoOpFeatureFlagBuffer;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.LazyEvaluator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpScope
implements IScope {
    private static final NoOpScope instance = new NoOpScope();
    @NotNull
    private final LazyEvaluator<SentryOptions> emptyOptions = new LazyEvaluator<SentryOptions>(() -> SentryOptions.empty());

    private NoOpScope() {
    }

    public static NoOpScope getInstance() {
        return instance;
    }

    @Override
    @Nullable
    public SentryLevel getLevel() {
        return null;
    }

    @Override
    public void setLevel(@Nullable SentryLevel level) {
    }

    @Override
    @Nullable
    public String getTransactionName() {
        return null;
    }

    @Override
    public void setTransaction(@NotNull String transaction) {
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
    public void setTransaction(@Nullable ITransaction transaction) {
    }

    @Override
    @Nullable
    public User getUser() {
        return null;
    }

    @Override
    public void setUser(@Nullable User user) {
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public String getScreen() {
        return null;
    }

    @Override
    @ApiStatus.Internal
    public void setScreen(@Nullable String screen) {
    }

    @Override
    @NotNull
    public SentryId getReplayId() {
        return SentryId.EMPTY_ID;
    }

    @Override
    public void setReplayId(@Nullable SentryId replayId) {
    }

    @Override
    @Nullable
    public Request getRequest() {
        return null;
    }

    @Override
    public void setRequest(@Nullable Request request) {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public List<String> getFingerprint() {
        return new ArrayList<String>();
    }

    @Override
    public void setFingerprint(@NotNull List<String> fingerprint) {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public Queue<Breadcrumb> getBreadcrumbs() {
        return new ArrayDeque<Breadcrumb>();
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
    }

    @Override
    public void clearBreadcrumbs() {
    }

    @Override
    public void clearTransaction() {
    }

    @Override
    @Nullable
    public ITransaction getTransaction() {
        return null;
    }

    @Override
    public void clear() {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public Map<String, String> getTags() {
        return new HashMap<String, String>();
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
    }

    @Override
    public void removeTag(@Nullable String key) {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public Map<String, Object> getExtras() {
        return new HashMap<String, Object>();
    }

    @Override
    public void setExtra(@Nullable String key, @Nullable String value) {
    }

    @Override
    public void removeExtra(@Nullable String key) {
    }

    @Override
    @NotNull
    public Contexts getContexts() {
        return new Contexts();
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Object value) {
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Boolean value) {
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable String value) {
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Number value) {
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Collection<?> value) {
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Object[] value) {
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Character value) {
    }

    @Override
    public void removeContexts(@Nullable String key) {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public List<Attachment> getAttachments() {
        return new ArrayList<Attachment>();
    }

    @Override
    public void addAttachment(@NotNull Attachment attachment) {
    }

    @Override
    public void clearAttachments() {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public List<EventProcessor> getEventProcessors() {
        return new ArrayList<EventProcessor>();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
        return new ArrayList<EventProcessorAndOrder>();
    }

    @Override
    public void addEventProcessor(@NotNull EventProcessor eventProcessor) {
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public Session withSession( @NotNull Scope.IWithSession sessionCallback) {
        return null;
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public Scope.SessionPair startSession() {
        return null;
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public Session endSession() {
        return null;
    }

    @Override
    @ApiStatus.Internal
    public void withTransaction( @NotNull Scope.IWithTransaction callback) {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryOptions getOptions() {
        return this.emptyOptions.getValue();
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public Session getSession() {
        return null;
    }

    @Override
    @ApiStatus.Internal
    public void clearSession() {
    }

    @Override
    @ApiStatus.Internal
    public void setPropagationContext(@NotNull PropagationContext propagationContext) {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public PropagationContext getPropagationContext() {
        return new PropagationContext();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public PropagationContext withPropagationContext( @NotNull Scope.IWithPropagationContext callback) {
        return new PropagationContext();
    }

    @Override
    public void setLastEventId(@NotNull SentryId lastEventId) {
    }

    @Override
    @NotNull
    public IScope clone() {
        return NoOpScope.getInstance();
    }

    @Override
    @NotNull
    public SentryId getLastEventId() {
        return SentryId.EMPTY_ID;
    }

    @Override
    public void bindClient(@NotNull ISentryClient client) {
    }

    @Override
    @NotNull
    public ISentryClient getClient() {
        return NoOpSentryClient.getInstance();
    }

    @Override
    public void assignTraceContext(@NotNull SentryEvent event) {
    }

    @Override
    public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
    }

    @Override
    public void replaceOptions(@NotNull SentryOptions options) {
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
    }

    @Override
    @Nullable
    public FeatureFlags getFeatureFlags() {
        return null;
    }

    @Override
    @NotNull
    public IFeatureFlagBuffer getFeatureFlagBuffer() {
        return NoOpFeatureFlagBuffer.getInstance();
    }
}

