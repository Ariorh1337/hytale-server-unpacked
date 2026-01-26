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

import io.sentry.Baggage;
import io.sentry.BaggageHeader;
import io.sentry.CompositePerformanceCollector;
import io.sentry.Hint;
import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Instrumenter;
import io.sentry.MeasurementUnit;
import io.sentry.NoOpScopesLifecycleToken;
import io.sentry.NoOpSpan;
import io.sentry.ProfileContext;
import io.sentry.ProfileLifecycle;
import io.sentry.ProfilingTraceData;
import io.sentry.SentryDate;
import io.sentry.SentryLevel;
import io.sentry.SentryTraceHeader;
import io.sentry.Span;
import io.sentry.SpanContext;
import io.sentry.SpanFinishedCallback;
import io.sentry.SpanId;
import io.sentry.SpanOptions;
import io.sentry.SpanStatus;
import io.sentry.TraceContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.TransactionContext;
import io.sentry.TransactionFinishedCallback;
import io.sentry.TransactionOptions;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.util.SpanUtils;
import io.sentry.util.thread.IThreadChecker;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@ApiStatus.Internal
public final class SentryTracer
implements ITransaction {
    @NotNull
    private final SentryId eventId = new SentryId();
    @NotNull
    private final Span root;
    @NotNull
    private final List<Span> children = new CopyOnWriteArrayList<Span>();
    @NotNull
    private final IScopes scopes;
    @NotNull
    private String name;
    @NotNull
    private FinishStatus finishStatus = FinishStatus.NOT_FINISHED;
    @Nullable
    private volatile TimerTask idleTimeoutTask;
    @Nullable
    private volatile TimerTask deadlineTimeoutTask;
    @Nullable
    private volatile Timer timer = null;
    @NotNull
    private final AutoClosableReentrantLock timerLock = new AutoClosableReentrantLock();
    @NotNull
    private final AutoClosableReentrantLock tracerLock = new AutoClosableReentrantLock();
    @NotNull
    private final AtomicBoolean isIdleFinishTimerRunning = new AtomicBoolean(false);
    @NotNull
    private final AtomicBoolean isDeadlineTimerRunning = new AtomicBoolean(false);
    @NotNull
    private TransactionNameSource transactionNameSource;
    @NotNull
    private final Instrumenter instrumenter;
    @NotNull
    private final Contexts contexts = new Contexts();
    @Nullable
    private final CompositePerformanceCollector compositePerformanceCollector;
    @NotNull
    private final TransactionOptions transactionOptions;

    public SentryTracer(@NotNull TransactionContext context, @NotNull IScopes scopes) {
        this(context, scopes, new TransactionOptions(), null);
    }

    public SentryTracer(@NotNull TransactionContext context, @NotNull IScopes scopes, @NotNull TransactionOptions transactionOptions) {
        this(context, scopes, transactionOptions, null);
    }

    SentryTracer(@NotNull TransactionContext context, @NotNull IScopes scopes, @NotNull TransactionOptions transactionOptions, @Nullable CompositePerformanceCollector compositePerformanceCollector) {
        Objects.requireNonNull(context, "context is required");
        Objects.requireNonNull(scopes, "scopes are required");
        this.root = new Span(context, this, scopes, transactionOptions);
        this.name = context.getName();
        this.instrumenter = context.getInstrumenter();
        this.scopes = scopes;
        this.compositePerformanceCollector = Boolean.TRUE.equals(this.isSampled()) ? compositePerformanceCollector : null;
        this.transactionNameSource = context.getTransactionNameSource();
        this.transactionOptions = transactionOptions;
        this.setDefaultSpanData(this.root);
        @NotNull SentryId continuousProfilerId = this.getProfilerId();
        if (!continuousProfilerId.equals(SentryId.EMPTY_ID) && Boolean.TRUE.equals(this.isSampled())) {
            this.contexts.setProfile(new ProfileContext(continuousProfilerId));
        }
        if (this.compositePerformanceCollector != null) {
            this.compositePerformanceCollector.start(this);
        }
        if (transactionOptions.getIdleTimeout() != null || transactionOptions.getDeadlineTimeout() != null) {
            this.timer = new Timer(true);
            this.scheduleDeadlineTimeout();
            this.scheduleFinish();
        }
    }

    @Override
    public void scheduleFinish() {
        try (@NotNull ISentryLifecycleToken ignored = this.timerLock.acquire();){
            Long idleTimeout;
            if (this.timer != null && (idleTimeout = this.transactionOptions.getIdleTimeout()) != null) {
                this.cancelIdleTimer();
                this.isIdleFinishTimerRunning.set(true);
                this.idleTimeoutTask = new TimerTask(){

                    @Override
                    public void run() {
                        SentryTracer.this.onIdleTimeoutReached();
                    }
                };
                try {
                    this.timer.schedule(this.idleTimeoutTask, idleTimeout);
                }
                catch (Throwable e) {
                    this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to schedule finish timer", e);
                    this.onIdleTimeoutReached();
                }
            }
        }
    }

    private void onIdleTimeoutReached() {
        @Nullable SpanStatus status = this.getStatus();
        this.finish(status != null ? status : SpanStatus.OK);
        this.isIdleFinishTimerRunning.set(false);
    }

    private void onDeadlineTimeoutReached() {
        @Nullable SpanStatus status = this.getStatus();
        this.forceFinish(status != null ? status : SpanStatus.DEADLINE_EXCEEDED, this.transactionOptions.getIdleTimeout() != null, null);
        this.isDeadlineTimerRunning.set(false);
    }

    @Override
    @NotNull
    public void forceFinish(@NotNull SpanStatus status, boolean dropIfNoChildren, @Nullable Hint hint) {
        if (this.isFinished()) {
            return;
        }
        @NotNull SentryDate finishTimestamp = this.scopes.getOptions().getDateProvider().now();
        @NotNull ListIterator<T> iterator = CollectionUtils.reverseListIterator((CopyOnWriteArrayList)this.children);
        while (iterator.hasPrevious()) {
            @NotNull Span span = (Span)iterator.previous();
            span.setSpanFinishedCallback(null);
            span.finish(status, finishTimestamp);
        }
        this.finish(status, finishTimestamp, dropIfNoChildren, hint);
    }

    @Override
    public void finish(@Nullable SpanStatus status, @Nullable SentryDate finishDate, boolean dropIfNoChildren, @Nullable Hint hint) {
        SentryDate finishTimestamp = this.root.getFinishDate();
        if (finishDate != null) {
            finishTimestamp = finishDate;
        }
        if (finishTimestamp == null) {
            finishTimestamp = this.scopes.getOptions().getDateProvider().now();
        }
        for (Span span2 : this.children) {
            if (!span2.getOptions().isIdle()) continue;
            span2.finish(status != null ? status : this.getSpanContext().status, finishTimestamp);
        }
        this.finishStatus = FinishStatus.finishing(status);
        if (!(this.root.isFinished() || this.transactionOptions.isWaitForChildren() && !this.hasAllChildrenFinished())) {
            @NotNull AtomicReference<V> performanceCollectionData = new AtomicReference();
            @Nullable SpanFinishedCallback oldCallback = this.root.getSpanFinishedCallback();
            this.root.setSpanFinishedCallback(span -> {
                TransactionFinishedCallback finishedCallback;
                if (oldCallback != null) {
                    oldCallback.execute(span);
                }
                if ((finishedCallback = this.transactionOptions.getTransactionFinishedCallback()) != null) {
                    finishedCallback.execute(this);
                }
                if (this.compositePerformanceCollector != null) {
                    performanceCollectionData.set(this.compositePerformanceCollector.stop(this));
                }
            });
            this.root.finish(this.finishStatus.spanStatus, finishTimestamp);
            ProfilingTraceData profilingTraceData = null;
            if (Boolean.TRUE.equals(this.isSampled()) && Boolean.TRUE.equals(this.isProfileSampled())) {
                profilingTraceData = this.scopes.getOptions().getTransactionProfiler().onTransactionFinish(this, (List)performanceCollectionData.get(), this.scopes.getOptions());
            }
            if (this.scopes.getOptions().isContinuousProfilingEnabled() && this.scopes.getOptions().getProfileLifecycle() == ProfileLifecycle.TRACE && this.root.getSpanContext().getProfilerId().equals(SentryId.EMPTY_ID)) {
                this.scopes.getOptions().getContinuousProfiler().stopProfiler(ProfileLifecycle.TRACE);
            }
            if (performanceCollectionData.get() != null) {
                ((List)performanceCollectionData.get()).clear();
            }
            this.scopes.configureScope(scope -> scope.withTransaction(transaction -> {
                if (transaction == this) {
                    scope.clearTransaction();
                }
            }));
            SentryTransaction transaction = new SentryTransaction(this);
            if (this.timer != null) {
                try (@NotNull ISentryLifecycleToken ignored = this.timerLock.acquire();){
                    if (this.timer != null) {
                        this.cancelIdleTimer();
                        this.cancelDeadlineTimer();
                        this.timer.cancel();
                        this.timer = null;
                    }
                }
            }
            if (dropIfNoChildren && this.children.isEmpty() && this.transactionOptions.getIdleTimeout() != null) {
                this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "Dropping idle transaction %s because it has no child spans", this.name);
                return;
            }
            transaction.getMeasurements().putAll(this.root.getMeasurements());
            this.scopes.captureTransaction(transaction, this.traceContext(), hint, profilingTraceData);
        }
    }

    private void cancelIdleTimer() {
        try (@NotNull ISentryLifecycleToken ignored = this.timerLock.acquire();){
            if (this.idleTimeoutTask != null) {
                this.idleTimeoutTask.cancel();
                this.isIdleFinishTimerRunning.set(false);
                this.idleTimeoutTask = null;
            }
        }
    }

    private void scheduleDeadlineTimeout() {
        @Nullable Long deadlineTimeOut = this.transactionOptions.getDeadlineTimeout();
        if (deadlineTimeOut != null) {
            try (@NotNull ISentryLifecycleToken ignored = this.timerLock.acquire();){
                if (this.timer != null) {
                    this.cancelDeadlineTimer();
                    this.isDeadlineTimerRunning.set(true);
                    this.deadlineTimeoutTask = new TimerTask(){

                        @Override
                        public void run() {
                            SentryTracer.this.onDeadlineTimeoutReached();
                        }
                    };
                    try {
                        this.timer.schedule(this.deadlineTimeoutTask, deadlineTimeOut);
                    }
                    catch (Throwable e) {
                        this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to schedule finish timer", e);
                        this.onDeadlineTimeoutReached();
                    }
                }
            }
        }
    }

    private void cancelDeadlineTimer() {
        try (@NotNull ISentryLifecycleToken ignored = this.timerLock.acquire();){
            if (this.deadlineTimeoutTask != null) {
                this.deadlineTimeoutTask.cancel();
                this.isDeadlineTimerRunning.set(false);
                this.deadlineTimeoutTask = null;
            }
        }
    }

    @NotNull
    public List<Span> getChildren() {
        return this.children;
    }

    @Override
    @NotNull
    public SentryDate getStartDate() {
        return this.root.getStartDate();
    }

    @Override
    @Nullable
    public SentryDate getFinishDate() {
        return this.root.getFinishDate();
    }

    @NotNull
    ISpan startChild(@NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description) {
        return this.startChild(parentSpanId, operation, description, new SpanOptions());
    }

    @NotNull
    ISpan startChild(@NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description, @NotNull SpanOptions spanOptions) {
        return this.createChild(parentSpanId, operation, description, spanOptions);
    }

    @NotNull
    ISpan startChild(@NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter) {
        @NotNull SpanContext spanContext = this.getSpanContext().copyForChild(operation, parentSpanId, null);
        spanContext.setDescription(description);
        spanContext.setInstrumenter(instrumenter);
        @NotNull SpanOptions spanOptions = new SpanOptions();
        spanOptions.setStartTimestamp(timestamp);
        return this.createChild(spanContext, spanOptions);
    }

    @NotNull
    ISpan startChild(@NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter, @NotNull SpanOptions spanOptions) {
        @NotNull SpanContext spanContext = this.getSpanContext().copyForChild(operation, parentSpanId, null);
        spanContext.setDescription(description);
        spanContext.setInstrumenter(instrumenter);
        spanOptions.setStartTimestamp(timestamp);
        return this.createChild(spanContext, spanOptions);
    }

    @NotNull
    private ISpan createChild(@NotNull SpanId parentSpanId, @NotNull String operation, @Nullable String description, @NotNull SpanOptions options) {
        @NotNull SpanContext spanContext = this.getSpanContext().copyForChild(operation, parentSpanId, null);
        spanContext.setDescription(description);
        spanContext.setInstrumenter(Instrumenter.SENTRY);
        return this.createChild(spanContext, options);
    }

    @NotNull
    private ISpan createChild(@NotNull SpanContext spanContext, @NotNull SpanOptions spanOptions) {
        if (this.root.isFinished()) {
            return NoOpSpan.getInstance();
        }
        if (!this.instrumenter.equals((Object)spanContext.getInstrumenter())) {
            return NoOpSpan.getInstance();
        }
        if (SpanUtils.isIgnored(this.scopes.getOptions().getIgnoredSpanOrigins(), spanOptions.getOrigin())) {
            return NoOpSpan.getInstance();
        }
        @Nullable SpanId parentSpanId = spanContext.getParentSpanId();
        @NotNull String operation = spanContext.getOperation();
        @Nullable String description = spanContext.getDescription();
        if (this.children.size() < this.scopes.getOptions().getMaxSpans()) {
            Objects.requireNonNull(parentSpanId, "parentSpanId is required");
            Objects.requireNonNull(operation, "operation is required");
            this.cancelIdleTimer();
            Span span = new Span(this, this.scopes, spanContext, spanOptions, finishingSpan -> {
                if (this.compositePerformanceCollector != null) {
                    this.compositePerformanceCollector.onSpanFinished(finishingSpan);
                }
                FinishStatus finishStatus = this.finishStatus;
                if (this.transactionOptions.getIdleTimeout() != null) {
                    if (!this.transactionOptions.isWaitForChildren() || this.hasAllChildrenFinished()) {
                        this.scheduleFinish();
                    }
                } else if (finishStatus.isFinishing) {
                    this.finish(finishStatus.spanStatus);
                }
            });
            this.setDefaultSpanData(span);
            this.children.add(span);
            if (this.compositePerformanceCollector != null) {
                this.compositePerformanceCollector.onSpanStarted(span);
            }
            return span;
        }
        this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Span operation: %s, description: %s dropped due to limit reached. Returning NoOpSpan.", operation, description);
        return NoOpSpan.getInstance();
    }

    private void setDefaultSpanData(@NotNull ISpan span) {
        @NotNull IThreadChecker threadChecker = this.scopes.getOptions().getThreadChecker();
        @NotNull SentryId profilerId = this.getProfilerId();
        if (!profilerId.equals(SentryId.EMPTY_ID) && Boolean.TRUE.equals(span.isSampled())) {
            span.setData("profiler_id", profilerId.toString());
        }
        span.setData("thread.id", String.valueOf(threadChecker.currentThreadSystemId()));
        span.setData("thread.name", threadChecker.getCurrentThreadName());
    }

    @NotNull
    private SentryId getProfilerId() {
        return !this.root.getSpanContext().getProfilerId().equals(SentryId.EMPTY_ID) ? this.root.getSpanContext().getProfilerId() : this.scopes.getOptions().getContinuousProfiler().getProfilerId();
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation) {
        return this.startChild(operation, (String)null);
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter) {
        return this.startChild(operation, description, timestamp, instrumenter, new SpanOptions());
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter, @NotNull SpanOptions spanOptions) {
        return this.createChild(operation, description, timestamp, instrumenter, spanOptions);
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp) {
        return this.createChild(operation, description, timestamp, Instrumenter.SENTRY, new SpanOptions());
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description) {
        return this.startChild(operation, description, null, Instrumenter.SENTRY, new SpanOptions());
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @NotNull SpanOptions spanOptions) {
        return this.createChild(operation, description, null, Instrumenter.SENTRY, spanOptions);
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull SpanContext spanContext, @NotNull SpanOptions spanOptions) {
        return this.createChild(spanContext, spanOptions);
    }

    @NotNull
    private ISpan createChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter, @NotNull SpanOptions spanOptions) {
        if (this.root.isFinished()) {
            return NoOpSpan.getInstance();
        }
        if (!this.instrumenter.equals((Object)instrumenter)) {
            return NoOpSpan.getInstance();
        }
        if (this.children.size() < this.scopes.getOptions().getMaxSpans()) {
            return this.root.startChild(operation, description, timestamp, instrumenter, spanOptions);
        }
        this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Span operation: %s, description: %s dropped due to limit reached. Returning NoOpSpan.", operation, description);
        return NoOpSpan.getInstance();
    }

    @Override
    @NotNull
    public SentryTraceHeader toSentryTrace() {
        return this.root.toSentryTrace();
    }

    @Override
    public void finish() {
        this.finish(this.getStatus());
    }

    @Override
    public void finish(@Nullable SpanStatus status) {
        this.finish(status, null);
    }

    @Override
    @ApiStatus.Internal
    public void finish(@Nullable SpanStatus status, @Nullable SentryDate finishDate) {
        this.finish(status, finishDate, true, null);
    }

    @Override
    @Nullable
    public TraceContext traceContext() {
        Baggage baggage;
        if (this.scopes.getOptions().isTraceSampling() && (baggage = this.getSpanContext().getBaggage()) != null) {
            this.updateBaggageValues(baggage);
            return baggage.toTraceContext();
        }
        return null;
    }

    private void updateBaggageValues(@NotNull Baggage baggage) {
        try (@NotNull ISentryLifecycleToken ignored = this.tracerLock.acquire();){
            if (baggage.isMutable()) {
                AtomicReference replayId = new AtomicReference();
                this.scopes.configureScope(scope -> replayId.set(scope.getReplayId()));
                baggage.setValuesFromTransaction(this.getSpanContext().getTraceId(), (SentryId)replayId.get(), this.scopes.getOptions(), this.getSamplingDecision(), this.getName(), this.getTransactionNameSource());
                baggage.freeze();
            }
        }
    }

    @Override
    @Nullable
    public BaggageHeader toBaggageHeader(@Nullable List<String> thirdPartyBaggageHeaders) {
        Baggage baggage;
        if (this.scopes.getOptions().isTraceSampling() && (baggage = this.getSpanContext().getBaggage()) != null) {
            this.updateBaggageValues(baggage);
            return BaggageHeader.fromBaggageAndOutgoingHeader(baggage, thirdPartyBaggageHeaders);
        }
        return null;
    }

    private boolean hasAllChildrenFinished() {
        @NotNull ListIterator<Span> iterator = this.children.listIterator();
        while (iterator.hasNext()) {
            @NotNull Span span = iterator.next();
            if (span.isFinished() || span.getFinishDate() != null) continue;
            return false;
        }
        return true;
    }

    @Override
    public void setOperation(@NotNull String operation) {
        if (this.root.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Operation %s cannot be set", operation);
            return;
        }
        this.root.setOperation(operation);
    }

    @Override
    @NotNull
    public String getOperation() {
        return this.root.getOperation();
    }

    @Override
    public void setDescription(@Nullable String description) {
        if (this.root.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Description %s cannot be set", description);
            return;
        }
        this.root.setDescription(description);
    }

    @Override
    @Nullable
    public String getDescription() {
        return this.root.getDescription();
    }

    @Override
    public void setStatus(@Nullable SpanStatus status) {
        if (this.root.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Status %s cannot be set", status == null ? "null" : status.name());
            return;
        }
        this.root.setStatus(status);
    }

    @Override
    @Nullable
    public SpanStatus getStatus() {
        return this.root.getStatus();
    }

    @Override
    public void setThrowable(@Nullable Throwable throwable) {
        if (this.root.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Throwable cannot be set", new Object[0]);
            return;
        }
        this.root.setThrowable(throwable);
    }

    @Override
    @Nullable
    public Throwable getThrowable() {
        return this.root.getThrowable();
    }

    @Override
    @NotNull
    public SpanContext getSpanContext() {
        return this.root.getSpanContext();
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
        if (this.root.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Tag %s cannot be set", key);
            return;
        }
        this.root.setTag(key, value);
    }

    @Override
    @Nullable
    public String getTag(@Nullable String key) {
        return this.root.getTag(key);
    }

    @Override
    public boolean isFinished() {
        return this.root.isFinished();
    }

    @Override
    public void setData(@Nullable String key, @Nullable Object value) {
        if (this.root.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Data %s cannot be set", key);
            return;
        }
        this.root.setData(key, value);
    }

    @Override
    @Nullable
    public Object getData(@Nullable String key) {
        return this.root.getData(key);
    }

    @ApiStatus.Internal
    public void setMeasurementFromChild(@NotNull String name, @NotNull Number value) {
        if (!this.root.getMeasurements().containsKey(name)) {
            this.setMeasurement(name, value);
        }
    }

    @ApiStatus.Internal
    public void setMeasurementFromChild(@NotNull String name, @NotNull Number value, @NotNull MeasurementUnit unit) {
        if (!this.root.getMeasurements().containsKey(name)) {
            this.setMeasurement(name, value, unit);
        }
    }

    @Override
    public void setMeasurement(@NotNull String name, @NotNull Number value) {
        this.root.setMeasurement(name, value);
    }

    @Override
    public void setMeasurement(@NotNull String name, @NotNull Number value, @NotNull MeasurementUnit unit) {
        this.root.setMeasurement(name, value, unit);
    }

    @Nullable
    public Map<String, Object> getData() {
        return this.root.getData();
    }

    @Override
    @Nullable
    public Boolean isSampled() {
        return this.root.isSampled();
    }

    @Override
    @Nullable
    public Boolean isProfileSampled() {
        return this.root.isProfileSampled();
    }

    @Override
    @Nullable
    public TracesSamplingDecision getSamplingDecision() {
        return this.root.getSamplingDecision();
    }

    @Override
    public void setName(@NotNull String name) {
        this.setName(name, TransactionNameSource.CUSTOM);
    }

    @Override
    @ApiStatus.Internal
    public void setName(@NotNull String name, @NotNull TransactionNameSource transactionNameSource) {
        if (this.root.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Name %s cannot be set", name);
            return;
        }
        this.name = name;
        this.transactionNameSource = transactionNameSource;
    }

    @Override
    @NotNull
    public String getName() {
        return this.name;
    }

    @Override
    @NotNull
    public TransactionNameSource getTransactionNameSource() {
        return this.transactionNameSource;
    }

    @Override
    @NotNull
    public List<Span> getSpans() {
        return this.children;
    }

    @Override
    @Nullable
    public ISpan getLatestActiveSpan() {
        @NotNull ListIterator<T> iterator = CollectionUtils.reverseListIterator((CopyOnWriteArrayList)this.children);
        while (iterator.hasPrevious()) {
            @NotNull Span span = (Span)iterator.previous();
            if (span.isFinished()) continue;
            return span;
        }
        return null;
    }

    @Override
    @NotNull
    public SentryId getEventId() {
        return this.eventId;
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public ISentryLifecycleToken makeCurrent() {
        this.scopes.configureScope(scope -> scope.setTransaction(this));
        return NoOpScopesLifecycleToken.getInstance();
    }

    @NotNull
    Span getRoot() {
        return this.root;
    }

    @TestOnly
    @Nullable
    TimerTask getIdleTimeoutTask() {
        return this.idleTimeoutTask;
    }

    @TestOnly
    @Nullable
    TimerTask getDeadlineTimeoutTask() {
        return this.deadlineTimeoutTask;
    }

    @TestOnly
    @Nullable
    Timer getTimer() {
        return this.timer;
    }

    @TestOnly
    @NotNull
    AtomicBoolean isFinishTimerRunning() {
        return this.isIdleFinishTimerRunning;
    }

    @TestOnly
    @NotNull
    AtomicBoolean isDeadlineTimerRunning() {
        return this.isDeadlineTimerRunning;
    }

    @Override
    @ApiStatus.Internal
    public void setContext(@Nullable String key, @Nullable Object context) {
        this.contexts.put(key, context);
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public Contexts getContexts() {
        return this.contexts;
    }

    @Override
    public boolean updateEndDate(@NotNull SentryDate date) {
        return this.root.updateEndDate(date);
    }

    @Override
    public boolean isNoOp() {
        return false;
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
        this.root.addFeatureFlag(flag, result);
    }

    private static final class FinishStatus {
        static final FinishStatus NOT_FINISHED = FinishStatus.notFinished();
        private final boolean isFinishing;
        @Nullable
        private final SpanStatus spanStatus;

        @NotNull
        static FinishStatus finishing(@Nullable SpanStatus finishStatus) {
            return new FinishStatus(true, finishStatus);
        }

        @NotNull
        private static FinishStatus notFinished() {
            return new FinishStatus(false, null);
        }

        private FinishStatus(boolean isFinishing, @Nullable SpanStatus spanStatus) {
            this.isFinishing = isFinishing;
            this.spanStatus = spanStatus;
        }
    }
}

