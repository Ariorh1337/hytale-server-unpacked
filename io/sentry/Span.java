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
import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.Instrumenter;
import io.sentry.MeasurementUnit;
import io.sentry.NoOpScopesLifecycleToken;
import io.sentry.NoOpSpan;
import io.sentry.SentryDate;
import io.sentry.SentryLevel;
import io.sentry.SentryTraceHeader;
import io.sentry.SentryTracer;
import io.sentry.SpanContext;
import io.sentry.SpanFinishedCallback;
import io.sentry.SpanId;
import io.sentry.SpanOptions;
import io.sentry.SpanStatus;
import io.sentry.TraceContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.TransactionContext;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.MeasurementValue;
import io.sentry.protocol.SentryId;
import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class Span
implements ISpan {
    @NotNull
    private SentryDate startTimestamp;
    @Nullable
    private SentryDate timestamp;
    @NotNull
    private final SpanContext context;
    @NotNull
    private final SentryTracer transaction;
    @Nullable
    private Throwable throwable;
    @NotNull
    private final IScopes scopes;
    private boolean finished = false;
    @NotNull
    private final AtomicBoolean isFinishing = new AtomicBoolean(false);
    @NotNull
    private final SpanOptions options;
    @Nullable
    private SpanFinishedCallback spanFinishedCallback;
    @NotNull
    private final Map<String, Object> data = new ConcurrentHashMap<String, Object>();
    @NotNull
    private final Map<String, MeasurementValue> measurements = new ConcurrentHashMap<String, MeasurementValue>();
    @NotNull
    private final Contexts contexts = new Contexts();

    Span(@NotNull SentryTracer transaction, @NotNull IScopes scopes, @NotNull SpanContext spanContext, @NotNull SpanOptions options, @Nullable SpanFinishedCallback spanFinishedCallback) {
        this.context = spanContext;
        this.context.setOrigin(options.getOrigin());
        this.transaction = Objects.requireNonNull(transaction, "transaction is required");
        this.scopes = Objects.requireNonNull(scopes, "Scopes are required");
        this.options = options;
        this.spanFinishedCallback = spanFinishedCallback;
        @Nullable SentryDate startTimestamp = options.getStartTimestamp();
        this.startTimestamp = startTimestamp != null ? startTimestamp : scopes.getOptions().getDateProvider().now();
    }

    public Span(@NotNull TransactionContext context, @NotNull SentryTracer sentryTracer, @NotNull IScopes scopes, @NotNull SpanOptions options) {
        this.context = Objects.requireNonNull(context, "context is required");
        this.context.setOrigin(options.getOrigin());
        this.transaction = Objects.requireNonNull(sentryTracer, "sentryTracer is required");
        this.scopes = Objects.requireNonNull(scopes, "scopes are required");
        this.spanFinishedCallback = null;
        @Nullable SentryDate startTimestamp = options.getStartTimestamp();
        this.startTimestamp = startTimestamp != null ? startTimestamp : scopes.getOptions().getDateProvider().now();
        this.options = options;
    }

    @Override
    @NotNull
    public SentryDate getStartDate() {
        return this.startTimestamp;
    }

    @Override
    @Nullable
    public SentryDate getFinishDate() {
        return this.timestamp;
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation) {
        return this.startChild(operation, (String)null);
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter, @NotNull SpanOptions spanOptions) {
        if (this.finished) {
            return NoOpSpan.getInstance();
        }
        return this.transaction.startChild(this.context.getSpanId(), operation, description, timestamp, instrumenter, spanOptions);
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description) {
        if (this.finished) {
            return NoOpSpan.getInstance();
        }
        return this.transaction.startChild(this.context.getSpanId(), operation, description);
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @NotNull SpanOptions spanOptions) {
        if (this.finished) {
            return NoOpSpan.getInstance();
        }
        return this.transaction.startChild(this.context.getSpanId(), operation, description, spanOptions);
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull SpanContext spanContext, @NotNull SpanOptions spanOptions) {
        return this.transaction.startChild(spanContext, spanOptions);
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter) {
        return this.startChild(operation, description, timestamp, instrumenter, new SpanOptions());
    }

    @Override
    @NotNull
    public SentryTraceHeader toSentryTrace() {
        return new SentryTraceHeader(this.context.getTraceId(), this.context.getSpanId(), this.context.getSampled());
    }

    @Override
    @Nullable
    public TraceContext traceContext() {
        return this.transaction.traceContext();
    }

    @Override
    @Nullable
    public BaggageHeader toBaggageHeader(@Nullable List<String> thirdPartyBaggageHeaders) {
        return this.transaction.toBaggageHeader(thirdPartyBaggageHeaders);
    }

    @Override
    public void finish() {
        this.finish(this.context.getStatus());
    }

    @Override
    public void finish(@Nullable SpanStatus status) {
        this.finish(status, this.scopes.getOptions().getDateProvider().now());
    }

    @Override
    public void finish(@Nullable SpanStatus status, @Nullable SentryDate timestamp) {
        if (this.finished || !this.isFinishing.compareAndSet(false, true)) {
            return;
        }
        this.context.setStatus(status);
        SentryDate sentryDate = this.timestamp = timestamp == null ? this.scopes.getOptions().getDateProvider().now() : timestamp;
        if (this.options.isTrimStart() || this.options.isTrimEnd()) {
            @Nullable SentryDate minChildStart = null;
            SentryDate maxChildEnd = null;
            @NotNull List<Span> children = this.transaction.getRoot().getSpanId().equals(this.getSpanId()) ? this.transaction.getChildren() : this.getDirectChildren();
            for (Span child : children) {
                if (minChildStart == null || child.getStartDate().isBefore(minChildStart)) {
                    minChildStart = child.getStartDate();
                }
                if (maxChildEnd != null && (child.getFinishDate() == null || !child.getFinishDate().isAfter(maxChildEnd))) continue;
                maxChildEnd = child.getFinishDate();
            }
            if (this.options.isTrimStart() && minChildStart != null && this.startTimestamp.isBefore(minChildStart)) {
                this.updateStartDate(minChildStart);
            }
            if (this.options.isTrimEnd() && maxChildEnd != null && (this.timestamp == null || this.timestamp.isAfter(maxChildEnd))) {
                this.updateEndDate(maxChildEnd);
            }
        }
        if (this.throwable != null) {
            this.scopes.setSpanContext(this.throwable, this, this.transaction.getName());
        }
        if (this.spanFinishedCallback != null) {
            this.spanFinishedCallback.execute(this);
        }
        this.finished = true;
    }

    @Override
    public void setOperation(@NotNull String operation) {
        this.context.setOperation(operation);
    }

    @Override
    @NotNull
    public String getOperation() {
        return this.context.getOperation();
    }

    @Override
    public void setDescription(@Nullable String description) {
        this.context.setDescription(description);
    }

    @Override
    @Nullable
    public String getDescription() {
        return this.context.getDescription();
    }

    @Override
    public void setStatus(@Nullable SpanStatus status) {
        this.context.setStatus(status);
    }

    @Override
    @Nullable
    public SpanStatus getStatus() {
        return this.context.getStatus();
    }

    @Override
    @NotNull
    public SpanContext getSpanContext() {
        return this.context;
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
        this.context.setTag(key, value);
    }

    @Override
    @Nullable
    public String getTag(@Nullable String key) {
        if (key == null) {
            return null;
        }
        return this.context.getTags().get(key);
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @NotNull
    public Map<String, Object> getData() {
        return this.data;
    }

    @Override
    @Nullable
    public Boolean isSampled() {
        return this.context.getSampled();
    }

    @Nullable
    public Boolean isProfileSampled() {
        return this.context.getProfileSampled();
    }

    @Override
    @Nullable
    public TracesSamplingDecision getSamplingDecision() {
        return this.context.getSamplingDecision();
    }

    @Override
    public void setThrowable(@Nullable Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    @Nullable
    public Throwable getThrowable() {
        return this.throwable;
    }

    @NotNull
    public SentryId getTraceId() {
        return this.context.getTraceId();
    }

    @NotNull
    public SpanId getSpanId() {
        return this.context.getSpanId();
    }

    @Nullable
    public SpanId getParentSpanId() {
        return this.context.getParentSpanId();
    }

    public Map<String, String> getTags() {
        return this.context.getTags();
    }

    @Override
    public void setData(@Nullable String key, @Nullable Object value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.data.remove(key);
        } else {
            this.data.put(key, value);
        }
    }

    @Override
    @Nullable
    public Object getData(@Nullable String key) {
        if (key == null) {
            return null;
        }
        return this.data.get(key);
    }

    @Override
    public void setMeasurement(@NotNull String name, @NotNull Number value) {
        if (this.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The span is already finished. Measurement %s cannot be set", name);
            return;
        }
        this.measurements.put(name, new MeasurementValue(value, null));
        if (this.transaction.getRoot() != this) {
            this.transaction.setMeasurementFromChild(name, value);
        }
    }

    @Override
    public void setMeasurement(@NotNull String name, @NotNull Number value, @NotNull MeasurementUnit unit) {
        if (this.isFinished()) {
            this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The span is already finished. Measurement %s cannot be set", name);
            return;
        }
        this.measurements.put(name, new MeasurementValue(value, unit.apiName()));
        if (this.transaction.getRoot() != this) {
            this.transaction.setMeasurementFromChild(name, value, unit);
        }
    }

    @NotNull
    public Map<String, MeasurementValue> getMeasurements() {
        return this.measurements;
    }

    @Override
    public boolean updateEndDate(@NotNull SentryDate date) {
        if (this.timestamp != null) {
            this.timestamp = date;
            return true;
        }
        return false;
    }

    @Override
    public boolean isNoOp() {
        return false;
    }

    @Override
    public void setContext(@Nullable String key, @Nullable Object context) {
        this.contexts.put(key, context);
    }

    @Override
    @NotNull
    public Contexts getContexts() {
        return this.contexts;
    }

    void setSpanFinishedCallback(@Nullable SpanFinishedCallback callback) {
        this.spanFinishedCallback = callback;
    }

    @Nullable
    SpanFinishedCallback getSpanFinishedCallback() {
        return this.spanFinishedCallback;
    }

    private void updateStartDate(@NotNull SentryDate date) {
        this.startTimestamp = date;
    }

    @NotNull
    SpanOptions getOptions() {
        return this.options;
    }

    @NotNull
    private List<Span> getDirectChildren() {
        ArrayList<Span> children = new ArrayList<Span>();
        for (Span span : this.transaction.getSpans()) {
            if (span.getParentSpanId() == null || !span.getParentSpanId().equals(this.getSpanId())) continue;
            children.add(span);
        }
        return children;
    }

    @Override
    @NotNull
    public ISentryLifecycleToken makeCurrent() {
        return NoOpScopesLifecycleToken.getInstance();
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
        this.context.addFeatureFlag(flag, result);
    }
}

