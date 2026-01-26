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
import io.sentry.ISentryClient;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.PropagationContext;
import io.sentry.Scope;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScope {
    @Nullable
    public SentryLevel getLevel();

    public void setLevel(@Nullable SentryLevel var1);

    @Nullable
    public String getTransactionName();

    public void setTransaction(@NotNull String var1);

    @Nullable
    public ISpan getSpan();

    @ApiStatus.Internal
    public void setActiveSpan(@Nullable ISpan var1);

    public void setTransaction(@Nullable ITransaction var1);

    @Nullable
    public User getUser();

    public void setUser(@Nullable User var1);

    @ApiStatus.Internal
    @Nullable
    public String getScreen();

    @ApiStatus.Internal
    public void setScreen(@Nullable String var1);

    @ApiStatus.Internal
    @NotNull
    public SentryId getReplayId();

    @ApiStatus.Internal
    public void setReplayId(@NotNull SentryId var1);

    @Nullable
    public Request getRequest();

    public void setRequest(@Nullable Request var1);

    @ApiStatus.Internal
    @NotNull
    public List<String> getFingerprint();

    public void setFingerprint(@NotNull List<String> var1);

    @ApiStatus.Internal
    @NotNull
    public Queue<Breadcrumb> getBreadcrumbs();

    public void addBreadcrumb(@NotNull Breadcrumb var1, @Nullable Hint var2);

    public void addBreadcrumb(@NotNull Breadcrumb var1);

    public void clearBreadcrumbs();

    public void clearTransaction();

    @Nullable
    public ITransaction getTransaction();

    public void clear();

    @ApiStatus.Internal
    @NotNull
    public Map<String, String> getTags();

    public void setTag(@Nullable String var1, @Nullable String var2);

    public void removeTag(@Nullable String var1);

    @ApiStatus.Internal
    @NotNull
    public Map<String, Object> getExtras();

    public void setExtra(@Nullable String var1, @Nullable String var2);

    public void removeExtra(@Nullable String var1);

    @NotNull
    public Contexts getContexts();

    public void setContexts(@Nullable String var1, @Nullable Object var2);

    public void setContexts(@Nullable String var1, @Nullable Boolean var2);

    public void setContexts(@Nullable String var1, @Nullable String var2);

    public void setContexts(@Nullable String var1, @Nullable Number var2);

    public void setContexts(@Nullable String var1, @Nullable Collection<?> var2);

    public void setContexts(@Nullable String var1, @Nullable Object[] var2);

    public void setContexts(@Nullable String var1, @Nullable Character var2);

    public void removeContexts(@Nullable String var1);

    @NotNull
    public List<Attachment> getAttachments();

    public void addAttachment(@NotNull Attachment var1);

    public void clearAttachments();

    @ApiStatus.Internal
    @NotNull
    public List<EventProcessor> getEventProcessors();

    @ApiStatus.Internal
    @NotNull
    public List<EventProcessorAndOrder> getEventProcessorsWithOrder();

    public void addEventProcessor(@NotNull EventProcessor var1);

    @Nullable
    public Session withSession(@NotNull Scope.IWithSession var1);

    @Nullable
    public Scope.SessionPair startSession();

    @Nullable
    public Session endSession();

    @ApiStatus.Internal
    public void withTransaction(@NotNull Scope.IWithTransaction var1);

    @NotNull
    public SentryOptions getOptions();

    @ApiStatus.Internal
    @Nullable
    public Session getSession();

    @ApiStatus.Internal
    public void clearSession();

    @ApiStatus.Internal
    public void setPropagationContext(@NotNull PropagationContext var1);

    @ApiStatus.Internal
    @NotNull
    public PropagationContext getPropagationContext();

    @ApiStatus.Internal
    @NotNull
    public PropagationContext withPropagationContext(@NotNull Scope.IWithPropagationContext var1);

    @NotNull
    public IScope clone();

    public void setLastEventId(@NotNull SentryId var1);

    @NotNull
    public SentryId getLastEventId();

    public void bindClient(@NotNull ISentryClient var1);

    @NotNull
    public ISentryClient getClient();

    @ApiStatus.Internal
    public void assignTraceContext(@NotNull SentryEvent var1);

    @ApiStatus.Internal
    public void setSpanContext(@NotNull Throwable var1, @NotNull ISpan var2, @NotNull String var3);

    @ApiStatus.Internal
    public void replaceOptions(@NotNull SentryOptions var1);

    public void addFeatureFlag(@Nullable String var1, @Nullable Boolean var2);

    @ApiStatus.Internal
    @Nullable
    public FeatureFlags getFeatureFlags();

    @ApiStatus.Internal
    @NotNull
    public IFeatureFlagBuffer getFeatureFlagBuffer();
}

