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
import io.sentry.CircularFifoQueue;
import io.sentry.DisabledQueue;
import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.IScope;
import io.sentry.IScopeObserver;
import io.sentry.ISentryClient;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.NoOpSentryClient;
import io.sentry.PropagationContext;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.SpanContext;
import io.sentry.SynchronizedQueue;
import io.sentry.featureflags.FeatureFlagBuffer;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.App;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.protocol.User;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.CollectionUtils;
import io.sentry.util.EventProcessorUtils;
import io.sentry.util.ExceptionUtils;
import io.sentry.util.Objects;
import io.sentry.util.Pair;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Scope
implements IScope {
    @NotNull
    private volatile SentryId lastEventId;
    @Nullable
    private SentryLevel level;
    @Nullable
    private ITransaction transaction;
    @NotNull
    private WeakReference<ISpan> activeSpan = new WeakReference<Object>(null);
    @Nullable
    private String transactionName;
    @Nullable
    private User user;
    @Nullable
    private String screen;
    @Nullable
    private Request request;
    @NotNull
    private List<String> fingerprint = new ArrayList<String>();
    @NotNull
    private volatile Queue<Breadcrumb> breadcrumbs;
    @NotNull
    private @NotNull Map<String, @NotNull String> tags = new ConcurrentHashMap<String, String>();
    @NotNull
    private @NotNull Map<String, @NotNull Object> extra = new ConcurrentHashMap<String, Object>();
    @NotNull
    private List<EventProcessorAndOrder> eventProcessors = new CopyOnWriteArrayList<EventProcessorAndOrder>();
    @NotNull
    private volatile SentryOptions options;
    @Nullable
    private volatile Session session;
    @NotNull
    private final AutoClosableReentrantLock sessionLock = new AutoClosableReentrantLock();
    @NotNull
    private final AutoClosableReentrantLock transactionLock = new AutoClosableReentrantLock();
    @NotNull
    private final AutoClosableReentrantLock propagationContextLock = new AutoClosableReentrantLock();
    @NotNull
    private Contexts contexts = new Contexts();
    @NotNull
    private List<Attachment> attachments = new CopyOnWriteArrayList<Attachment>();
    @NotNull
    private PropagationContext propagationContext;
    @NotNull
    private SentryId replayId = SentryId.EMPTY_ID;
    @NotNull
    private ISentryClient client = NoOpSentryClient.getInstance();
    @NotNull
    private final Map<Throwable, Pair<WeakReference<ISpan>, String>> throwableToSpan = Collections.synchronizedMap(new WeakHashMap());
    @NotNull
    private final IFeatureFlagBuffer featureFlags;

    public Scope(@NotNull SentryOptions options) {
        this.options = Objects.requireNonNull(options, "SentryOptions is required.");
        this.breadcrumbs = Scope.createBreadcrumbsList(this.options.getMaxBreadcrumbs());
        this.featureFlags = FeatureFlagBuffer.create(options);
        this.propagationContext = new PropagationContext();
        this.lastEventId = SentryId.EMPTY_ID;
    }

    private Scope(@NotNull Scope scope) {
        this.transaction = scope.transaction;
        this.transactionName = scope.transactionName;
        this.activeSpan = scope.activeSpan;
        this.session = scope.session;
        this.options = scope.options;
        this.level = scope.level;
        this.client = scope.client;
        this.lastEventId = scope.getLastEventId();
        User userRef = scope.user;
        this.user = userRef != null ? new User(userRef) : null;
        this.screen = scope.screen;
        this.replayId = scope.replayId;
        Request requestRef = scope.request;
        this.request = requestRef != null ? new Request(requestRef) : null;
        this.fingerprint = new ArrayList<String>(scope.fingerprint);
        this.eventProcessors = new CopyOnWriteArrayList<EventProcessorAndOrder>(scope.eventProcessors);
        Breadcrumb[] breadcrumbsRef = scope.breadcrumbs.toArray(new Breadcrumb[0]);
        Queue<Breadcrumb> breadcrumbsClone = Scope.createBreadcrumbsList(scope.options.getMaxBreadcrumbs());
        for (Breadcrumb breadcrumb : breadcrumbsRef) {
            Breadcrumb breadcrumbClone = new Breadcrumb(breadcrumb);
            breadcrumbsClone.add(breadcrumbClone);
        }
        this.breadcrumbs = breadcrumbsClone;
        Map<String, String> tagsRef = scope.tags;
        ConcurrentHashMap<String, @NotNull String> tagsClone = new ConcurrentHashMap<String, String>();
        for (Map.Entry<String, String> entry : tagsRef.entrySet()) {
            if (entry == null) continue;
            tagsClone.put(entry.getKey(), entry.getValue());
        }
        this.tags = tagsClone;
        Map<String, Object> extraRef = scope.extra;
        ConcurrentHashMap<String, @NotNull Object> concurrentHashMap = new ConcurrentHashMap<String, Object>();
        for (Map.Entry<String, Object> item : extraRef.entrySet()) {
            if (item == null) continue;
            concurrentHashMap.put(item.getKey(), item.getValue());
        }
        this.extra = concurrentHashMap;
        this.contexts = new Contexts(scope.contexts);
        this.attachments = new CopyOnWriteArrayList<Attachment>(scope.attachments);
        this.featureFlags = scope.featureFlags.clone();
        this.propagationContext = new PropagationContext(scope.propagationContext);
    }

    @Override
    @Nullable
    public SentryLevel getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(@Nullable SentryLevel level) {
        this.level = level;
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setLevel(level);
        }
    }

    @Override
    @Nullable
    public String getTransactionName() {
        ITransaction tx = this.transaction;
        return tx != null ? tx.getName() : this.transactionName;
    }

    @Override
    public void setTransaction(@NotNull String transaction) {
        if (transaction != null) {
            ITransaction tx = this.transaction;
            if (tx != null) {
                tx.setName(transaction, TransactionNameSource.CUSTOM);
            }
            this.transactionName = transaction;
            for (IScopeObserver observer : this.options.getScopeObservers()) {
                observer.setTransaction(transaction);
            }
        } else {
            this.options.getLogger().log(SentryLevel.WARNING, "Transaction cannot be null", new Object[0]);
        }
    }

    @Override
    @Nullable
    public ISpan getSpan() {
        ISpan span;
        @Nullable ISpan activeSpan = (ISpan)this.activeSpan.get();
        if (activeSpan != null) {
            return activeSpan;
        }
        ITransaction tx = this.transaction;
        if (tx != null && (span = tx.getLatestActiveSpan()) != null) {
            return span;
        }
        return tx;
    }

    @Override
    public void setActiveSpan(@Nullable ISpan span) {
        this.activeSpan = new WeakReference<ISpan>(span);
    }

    @Override
    public void setTransaction(@Nullable ITransaction transaction) {
        try (@NotNull ISentryLifecycleToken ignored = this.transactionLock.acquire();){
            this.transaction = transaction;
            for (IScopeObserver observer : this.options.getScopeObservers()) {
                if (transaction != null) {
                    observer.setTransaction(transaction.getName());
                    observer.setTrace(transaction.getSpanContext(), this);
                    continue;
                }
                observer.setTransaction(null);
                observer.setTrace(null, this);
            }
        }
    }

    @Override
    @Nullable
    public User getUser() {
        return this.user;
    }

    @Override
    public void setUser(@Nullable User user) {
        this.user = user;
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setUser(user);
        }
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public String getScreen() {
        return this.screen;
    }

    @Override
    @ApiStatus.Internal
    public void setScreen(@Nullable String screen) {
        this.screen = screen;
        @NotNull Contexts contexts = this.getContexts();
        @Nullable App app = contexts.getApp();
        if (app == null) {
            app = new App();
            contexts.setApp(app);
        }
        if (screen == null) {
            app.setViewNames(null);
        } else {
            @NotNull ArrayList<String> viewNames = new ArrayList<String>(1);
            viewNames.add(screen);
            app.setViewNames(viewNames);
        }
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setContexts(contexts);
        }
    }

    @Override
    @NotNull
    public SentryId getReplayId() {
        return this.replayId;
    }

    @Override
    public void setReplayId(@NotNull SentryId replayId) {
        this.replayId = replayId;
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setReplayId(replayId);
        }
    }

    @Override
    @Nullable
    public Request getRequest() {
        return this.request;
    }

    @Override
    public void setRequest(@Nullable Request request) {
        this.request = request;
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setRequest(request);
        }
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public List<String> getFingerprint() {
        return this.fingerprint;
    }

    @Override
    public void setFingerprint(@NotNull List<String> fingerprint) {
        if (fingerprint == null) {
            return;
        }
        this.fingerprint = new ArrayList<String>(fingerprint);
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setFingerprint(fingerprint);
        }
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public Queue<Breadcrumb> getBreadcrumbs() {
        return this.breadcrumbs;
    }

    @Nullable
    private Breadcrumb executeBeforeBreadcrumb(@NotNull SentryOptions.BeforeBreadcrumbCallback callback, @NotNull Breadcrumb breadcrumb, @NotNull Hint hint) {
        block2: {
            try {
                breadcrumb = callback.execute(breadcrumb, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "The BeforeBreadcrumbCallback callback threw an exception. Exception details will be added to the breadcrumb.", e);
                if (e.getMessage() == null) break block2;
                breadcrumb.setData("sentry:message", e.getMessage());
            }
        }
        return breadcrumb;
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
        SentryOptions.BeforeBreadcrumbCallback callback;
        if (breadcrumb == null || this.breadcrumbs instanceof DisabledQueue) {
            return;
        }
        if (hint == null) {
            hint = new Hint();
        }
        if ((callback = this.options.getBeforeBreadcrumb()) != null) {
            breadcrumb = this.executeBeforeBreadcrumb(callback, breadcrumb, hint);
        }
        if (breadcrumb != null) {
            this.breadcrumbs.add(breadcrumb);
            for (IScopeObserver observer : this.options.getScopeObservers()) {
                observer.addBreadcrumb(breadcrumb);
                observer.setBreadcrumbs(this.breadcrumbs);
            }
        } else {
            this.options.getLogger().log(SentryLevel.INFO, "Breadcrumb was dropped by beforeBreadcrumb", new Object[0]);
        }
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
        this.addBreadcrumb(breadcrumb, null);
    }

    @Override
    public void clearBreadcrumbs() {
        this.breadcrumbs.clear();
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setBreadcrumbs(this.breadcrumbs);
        }
    }

    @Override
    public void clearTransaction() {
        try (@NotNull ISentryLifecycleToken ignored = this.transactionLock.acquire();){
            this.transaction = null;
        }
        this.transactionName = null;
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setTransaction(null);
            observer.setTrace(null, this);
        }
    }

    @Override
    @Nullable
    public ITransaction getTransaction() {
        return this.transaction;
    }

    @Override
    public void clear() {
        this.level = null;
        this.user = null;
        this.request = null;
        this.screen = null;
        this.fingerprint.clear();
        this.clearBreadcrumbs();
        this.tags.clear();
        this.extra.clear();
        this.eventProcessors.clear();
        this.clearTransaction();
        this.clearAttachments();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public Map<String, String> getTags() {
        return CollectionUtils.newConcurrentHashMap(this.tags);
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.removeTag(key);
        } else {
            this.tags.put(key, value);
            for (IScopeObserver observer : this.options.getScopeObservers()) {
                observer.setTag(key, value);
                observer.setTags(this.tags);
            }
        }
    }

    @Override
    public void removeTag(@Nullable String key) {
        if (key == null) {
            return;
        }
        this.tags.remove(key);
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.removeTag(key);
            observer.setTags(this.tags);
        }
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public Map<String, Object> getExtras() {
        return this.extra;
    }

    @Override
    public void setExtra(@Nullable String key, @Nullable String value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.removeExtra(key);
        } else {
            this.extra.put(key, value);
            for (IScopeObserver observer : this.options.getScopeObservers()) {
                observer.setExtra(key, value);
                observer.setExtras(this.extra);
            }
        }
    }

    @Override
    public void removeExtra(@Nullable String key) {
        if (key == null) {
            return;
        }
        this.extra.remove(key);
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.removeExtra(key);
            observer.setExtras(this.extra);
        }
    }

    @Override
    @NotNull
    public Contexts getContexts() {
        return this.contexts;
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Object value) {
        if (key == null) {
            return;
        }
        this.contexts.put(key, value);
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setContexts(this.contexts);
        }
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Boolean value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.setContexts(key, (Object)null);
        } else {
            HashMap<String, Boolean> map = new HashMap<String, Boolean>();
            map.put("value", value);
            this.setContexts(key, map);
        }
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable String value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.setContexts(key, (Object)null);
        } else {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("value", value);
            this.setContexts(key, map);
        }
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Number value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.setContexts(key, (Object)null);
        } else {
            HashMap<String, Number> map = new HashMap<String, Number>();
            map.put("value", value);
            this.setContexts(key, map);
        }
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Collection<?> value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.setContexts(key, (Object)null);
        } else {
            HashMap map = new HashMap();
            map.put("value", value);
            this.setContexts(key, map);
        }
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Object[] value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.setContexts(key, (Object)null);
        } else {
            HashMap<String, Object[]> map = new HashMap<String, Object[]>();
            map.put("value", value);
            this.setContexts(key, map);
        }
    }

    @Override
    public void setContexts(@Nullable String key, @Nullable Character value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.setContexts(key, (Object)null);
        } else {
            HashMap<String, Character> map = new HashMap<String, Character>();
            map.put("value", value);
            this.setContexts(key, map);
        }
    }

    @Override
    public void removeContexts(@Nullable String key) {
        if (key == null) {
            return;
        }
        this.contexts.remove(key);
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public List<Attachment> getAttachments() {
        return new CopyOnWriteArrayList<Attachment>(this.attachments);
    }

    @Override
    public void addAttachment(@NotNull Attachment attachment) {
        this.attachments.add(attachment);
    }

    @Override
    public void clearAttachments() {
        this.attachments.clear();
    }

    @NotNull
    static Queue<Breadcrumb> createBreadcrumbsList(int maxBreadcrumb) {
        return maxBreadcrumb > 0 ? SynchronizedQueue.synchronizedQueue(new CircularFifoQueue(maxBreadcrumb)) : new DisabledQueue<Breadcrumb>();
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public List<EventProcessor> getEventProcessors() {
        return EventProcessorUtils.unwrap(this.eventProcessors);
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
        return this.eventProcessors;
    }

    @Override
    public void addEventProcessor(@NotNull EventProcessor eventProcessor) {
        this.eventProcessors.add(new EventProcessorAndOrder(eventProcessor, eventProcessor.getOrder()));
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public Session withSession(@NotNull IWithSession sessionCallback) {
        Session cloneSession = null;
        try (@NotNull ISentryLifecycleToken ignored = this.sessionLock.acquire();){
            sessionCallback.accept(this.session);
            if (this.session != null) {
                cloneSession = this.session.clone();
            }
        }
        return cloneSession;
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public SessionPair startSession() {
        SessionPair pair = null;
        try (@NotNull ISentryLifecycleToken ignored = this.sessionLock.acquire();){
            if (this.session != null) {
                this.session.end();
                this.options.getContinuousProfiler().reevaluateSampling();
            }
            Session previousSession = this.session;
            if (this.options.getRelease() != null) {
                this.session = new Session(this.options.getDistinctId(), this.user, this.options.getEnvironment(), this.options.getRelease());
                Session previousClone = previousSession != null ? previousSession.clone() : null;
                pair = new SessionPair(this.session.clone(), previousClone);
            } else {
                this.options.getLogger().log(SentryLevel.WARNING, "Release is not set on SentryOptions. Session could not be started", new Object[0]);
            }
        }
        return pair;
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public Session endSession() {
        Session previousSession = null;
        try (@NotNull ISentryLifecycleToken ignored = this.sessionLock.acquire();){
            if (this.session != null) {
                this.session.end();
                this.options.getContinuousProfiler().reevaluateSampling();
                previousSession = this.session.clone();
                this.session = null;
            }
        }
        return previousSession;
    }

    @Override
    @ApiStatus.Internal
    public void withTransaction(@NotNull IWithTransaction callback) {
        try (@NotNull ISentryLifecycleToken ignored = this.transactionLock.acquire();){
            callback.accept(this.transaction);
        }
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryOptions getOptions() {
        return this.options;
    }

    @Override
    @ApiStatus.Internal
    @Nullable
    public Session getSession() {
        return this.session;
    }

    @Override
    @ApiStatus.Internal
    public void clearSession() {
        this.session = null;
    }

    @Override
    @ApiStatus.Internal
    public void setPropagationContext(@NotNull PropagationContext propagationContext) {
        this.propagationContext = propagationContext;
        @NotNull SpanContext spanContext = propagationContext.toSpanContext();
        for (IScopeObserver observer : this.options.getScopeObservers()) {
            observer.setTrace(spanContext, this);
        }
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public PropagationContext getPropagationContext() {
        return this.propagationContext;
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public PropagationContext withPropagationContext(@NotNull IWithPropagationContext callback) {
        try (@NotNull ISentryLifecycleToken ignored = this.propagationContextLock.acquire();){
            callback.accept(this.propagationContext);
            PropagationContext propagationContext = new PropagationContext(this.propagationContext);
            return propagationContext;
        }
    }

    @Override
    @NotNull
    public IScope clone() {
        return new Scope(this);
    }

    @Override
    public void setLastEventId(@NotNull SentryId lastEventId) {
        this.lastEventId = lastEventId;
    }

    @Override
    @NotNull
    public SentryId getLastEventId() {
        return this.lastEventId;
    }

    @Override
    public void bindClient(@NotNull ISentryClient client) {
        this.client = client;
    }

    @Override
    @NotNull
    public ISentryClient getClient() {
        return this.client;
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
        this.featureFlags.add(flag, result);
    }

    @Override
    @Nullable
    public FeatureFlags getFeatureFlags() {
        return this.featureFlags.getFeatureFlags();
    }

    @Override
    @NotNull
    public IFeatureFlagBuffer getFeatureFlagBuffer() {
        return this.featureFlags;
    }

    @Override
    @ApiStatus.Internal
    public void assignTraceContext(@NotNull SentryEvent event) {
        Pair<WeakReference<ISpan>, String> pair;
        if (this.options.isTracingEnabled() && event.getThrowable() != null && (pair = this.throwableToSpan.get(ExceptionUtils.findRootCause(event.getThrowable()))) != null) {
            ISpan span;
            WeakReference<ISpan> spanWeakRef = pair.getFirst();
            if (event.getContexts().getTrace() == null && spanWeakRef != null && (span = (ISpan)spanWeakRef.get()) != null) {
                event.getContexts().setTrace(span.getSpanContext());
            }
            String transactionName = pair.getSecond();
            if (event.getTransaction() == null && transactionName != null) {
                event.setTransaction(transactionName);
            }
        }
    }

    @Override
    @ApiStatus.Internal
    public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
        Objects.requireNonNull(throwable, "throwable is required");
        Objects.requireNonNull(span, "span is required");
        Objects.requireNonNull(transactionName, "transactionName is required");
        Throwable rootCause = ExceptionUtils.findRootCause(throwable);
        if (!this.throwableToSpan.containsKey(rootCause)) {
            this.throwableToSpan.put(rootCause, new Pair<WeakReference<ISpan>, String>(new WeakReference<ISpan>(span), transactionName));
        }
    }

    @Override
    @ApiStatus.Internal
    public void replaceOptions(@NotNull SentryOptions options) {
        this.options = options;
        Queue<Breadcrumb> oldBreadcrumbs = this.breadcrumbs;
        this.breadcrumbs = Scope.createBreadcrumbsList(options.getMaxBreadcrumbs());
        for (Breadcrumb breadcrumb : oldBreadcrumbs) {
            this.addBreadcrumb(breadcrumb);
        }
    }

    static interface IWithSession {
        public void accept(@Nullable Session var1);
    }

    static final class SessionPair {
        @Nullable
        private final Session previous;
        @NotNull
        private final Session current;

        public SessionPair(@NotNull Session current, @Nullable Session previous) {
            this.current = current;
            this.previous = previous;
        }

        @Nullable
        public Session getPrevious() {
            return this.previous;
        }

        @NotNull
        public Session getCurrent() {
            return this.current;
        }
    }

    @ApiStatus.Internal
    public static interface IWithTransaction {
        public void accept(@Nullable ITransaction var1);
    }

    @ApiStatus.Internal
    public static interface IWithPropagationContext {
        public void accept(@NotNull PropagationContext var1);
    }
}

