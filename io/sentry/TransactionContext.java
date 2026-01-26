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
import io.sentry.PropagationContext;
import io.sentry.SpanContext;
import io.sentry.SpanId;
import io.sentry.TracesSamplingDecision;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.Objects;
import io.sentry.util.TracingUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TransactionContext
extends SpanContext {
    @NotNull
    public static final String DEFAULT_TRANSACTION_NAME = "<unlabeled transaction>";
    @NotNull
    private static final TransactionNameSource DEFAULT_NAME_SOURCE = TransactionNameSource.CUSTOM;
    @NotNull
    private static final String DEFAULT_OPERATION = "default";
    @NotNull
    private String name;
    @NotNull
    private TransactionNameSource transactionNameSource;
    @Nullable
    private TracesSamplingDecision parentSamplingDecision;
    private boolean isForNextAppStart = false;

    @ApiStatus.Internal
    public static TransactionContext fromPropagationContext(@NotNull PropagationContext propagationContext) {
        @Nullable Boolean parentSampled = propagationContext.isSampled();
        @NotNull Baggage baggage = propagationContext.getBaggage();
        @Nullable Double sampleRate = baggage.getSampleRate();
        @Nullable TracesSamplingDecision samplingDecision = parentSampled == null ? null : new TracesSamplingDecision(parentSampled, sampleRate, propagationContext.getSampleRand());
        return new TransactionContext(propagationContext.getTraceId(), propagationContext.getSpanId(), propagationContext.getParentSpanId(), samplingDecision, baggage);
    }

    public TransactionContext(@NotNull String name, @NotNull String operation) {
        this(name, operation, null);
    }

    @ApiStatus.Internal
    public TransactionContext(@NotNull String name, @NotNull TransactionNameSource transactionNameSource, @NotNull String operation) {
        this(name, transactionNameSource, operation, null);
    }

    public TransactionContext(@NotNull String name, @NotNull String operation, @Nullable TracesSamplingDecision samplingDecision) {
        this(name, TransactionNameSource.CUSTOM, operation, samplingDecision);
    }

    @ApiStatus.Internal
    public TransactionContext(@NotNull String name, @NotNull TransactionNameSource transactionNameSource, @NotNull String operation, @Nullable TracesSamplingDecision samplingDecision) {
        super(operation);
        this.name = Objects.requireNonNull(name, "name is required");
        this.transactionNameSource = transactionNameSource;
        this.setSamplingDecision(samplingDecision);
        this.baggage = TracingUtils.ensureBaggage(null, samplingDecision);
    }

    @ApiStatus.Internal
    public TransactionContext(@NotNull SentryId traceId, @NotNull SpanId spanId, @Nullable SpanId parentSpanId, @Nullable TracesSamplingDecision parentSamplingDecision, @Nullable Baggage baggage) {
        super(traceId, spanId, DEFAULT_OPERATION, parentSpanId, null);
        this.name = DEFAULT_TRANSACTION_NAME;
        this.parentSamplingDecision = parentSamplingDecision;
        this.transactionNameSource = DEFAULT_NAME_SOURCE;
        this.baggage = TracingUtils.ensureBaggage(baggage, parentSamplingDecision);
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @Nullable
    public Boolean getParentSampled() {
        if (this.parentSamplingDecision == null) {
            return null;
        }
        return this.parentSamplingDecision.getSampled();
    }

    @Nullable
    public TracesSamplingDecision getParentSamplingDecision() {
        return this.parentSamplingDecision;
    }

    public void setParentSampled(@Nullable Boolean parentSampled) {
        this.parentSamplingDecision = parentSampled == null ? null : new TracesSamplingDecision(parentSampled);
    }

    public void setParentSampled(@Nullable Boolean parentSampled, @Nullable Boolean parentProfileSampled) {
        this.parentSamplingDecision = parentSampled == null ? null : (parentProfileSampled == null ? new TracesSamplingDecision(parentSampled) : new TracesSamplingDecision(parentSampled, null, parentProfileSampled, null));
    }

    @NotNull
    public TransactionNameSource getTransactionNameSource() {
        return this.transactionNameSource;
    }

    public void setName(@NotNull String name) {
        this.name = Objects.requireNonNull(name, "name is required");
    }

    public void setTransactionNameSource(@NotNull TransactionNameSource transactionNameSource) {
        this.transactionNameSource = transactionNameSource;
    }

    @ApiStatus.Internal
    public void setForNextAppStart(boolean forNextAppStart) {
        this.isForNextAppStart = forNextAppStart;
    }

    public boolean isForNextAppStart() {
        return this.isForNextAppStart;
    }
}

