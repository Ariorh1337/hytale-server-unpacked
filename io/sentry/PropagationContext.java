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
import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.SentryTraceHeader;
import io.sentry.SpanContext;
import io.sentry.SpanId;
import io.sentry.TraceContext;
import io.sentry.exception.InvalidSentryTraceHeaderException;
import io.sentry.protocol.SentryId;
import io.sentry.util.TracingUtils;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class PropagationContext {
    @NotNull
    private SentryId traceId;
    @NotNull
    private SpanId spanId;
    @Nullable
    private SpanId parentSpanId;
    @Nullable
    private Boolean sampled;
    @NotNull
    private final Baggage baggage;

    public static PropagationContext fromHeaders(@NotNull ILogger logger, @Nullable String sentryTraceHeader, @Nullable String baggageHeader) {
        return PropagationContext.fromHeaders(logger, sentryTraceHeader, Arrays.asList(baggageHeader));
    }

    @NotNull
    public static PropagationContext fromHeaders(@NotNull ILogger logger, @Nullable String sentryTraceHeaderString, @Nullable List<String> baggageHeaderStrings) {
        if (sentryTraceHeaderString == null) {
            return new PropagationContext();
        }
        try {
            @NotNull SentryTraceHeader traceHeader = new SentryTraceHeader(sentryTraceHeaderString);
            @NotNull Baggage baggage = Baggage.fromHeader(baggageHeaderStrings, logger);
            return PropagationContext.fromHeaders(traceHeader, baggage, null);
        }
        catch (InvalidSentryTraceHeaderException e) {
            logger.log(SentryLevel.DEBUG, e, "Failed to parse Sentry trace header: %s", e.getMessage());
            return new PropagationContext();
        }
    }

    @NotNull
    public static PropagationContext fromHeaders(@NotNull SentryTraceHeader sentryTraceHeader, @Nullable Baggage baggage, @Nullable SpanId spanId) {
        @NotNull SpanId spanIdToUse = spanId == null ? new SpanId() : spanId;
        return new PropagationContext(sentryTraceHeader.getTraceId(), spanIdToUse, sentryTraceHeader.getSpanId(), baggage, sentryTraceHeader.isSampled());
    }

    @NotNull
    public static PropagationContext fromExistingTrace(@NotNull String traceId, @NotNull String spanId, @Nullable Double decisionSampleRate, @Nullable Double decisionSampleRand) {
        return new PropagationContext(new SentryId(traceId), new SpanId(), new SpanId(spanId), TracingUtils.ensureBaggage(null, null, decisionSampleRate, decisionSampleRand), null);
    }

    public PropagationContext() {
        this(new SentryId(), new SpanId(), null, null, null);
    }

    public PropagationContext(@NotNull PropagationContext propagationContext) {
        this(propagationContext.getTraceId(), propagationContext.getSpanId(), propagationContext.getParentSpanId(), propagationContext.getBaggage(), propagationContext.isSampled());
    }

    public PropagationContext(@NotNull SentryId traceId, @NotNull SpanId spanId, @Nullable SpanId parentSpanId, @Nullable Baggage baggage, @Nullable Boolean sampled) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.baggage = TracingUtils.ensureBaggage(baggage, sampled, null, null);
        this.sampled = sampled;
    }

    @NotNull
    public SentryId getTraceId() {
        return this.traceId;
    }

    public void setTraceId(@NotNull SentryId traceId) {
        this.traceId = traceId;
    }

    @NotNull
    public SpanId getSpanId() {
        return this.spanId;
    }

    public void setSpanId(@NotNull SpanId spanId) {
        this.spanId = spanId;
    }

    @Nullable
    public SpanId getParentSpanId() {
        return this.parentSpanId;
    }

    public void setParentSpanId(@Nullable SpanId parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    @NotNull
    public Baggage getBaggage() {
        return this.baggage;
    }

    @Nullable
    public Boolean isSampled() {
        return this.sampled;
    }

    public void setSampled(@Nullable Boolean sampled) {
        this.sampled = sampled;
    }

    @Nullable
    public TraceContext traceContext() {
        return this.baggage.toTraceContext();
    }

    @NotNull
    public SpanContext toSpanContext() {
        SpanContext spanContext = new SpanContext(this.traceId, this.spanId, "default", null, null);
        spanContext.setOrigin("auto");
        return spanContext;
    }

    @NotNull
    public Double getSampleRand() {
        @Nullable Double sampleRand = this.baggage.getSampleRand();
        return sampleRand == null ? 0.0 : sampleRand;
    }
}

