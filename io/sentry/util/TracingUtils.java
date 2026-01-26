/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.Baggage;
import io.sentry.BaggageHeader;
import io.sentry.FilterString;
import io.sentry.IScope;
import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.NoOpLogger;
import io.sentry.PropagationContext;
import io.sentry.SentryOptions;
import io.sentry.SentryTraceHeader;
import io.sentry.SpanContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.W3CTraceparentHeader;
import io.sentry.util.PropagationTargetsUtils;
import io.sentry.util.SampleRateUtils;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TracingUtils {
    public static void startNewTrace(@NotNull IScopes scopes) {
        scopes.configureScope(scope -> scope.withPropagationContext(propagationContext -> scope.setPropagationContext(new PropagationContext())));
    }

    public static void setTrace(@NotNull IScopes scopes, @NotNull PropagationContext propagationContext) {
        scopes.configureScope(scope -> scope.withPropagationContext(oldPropagationContext -> scope.setPropagationContext(propagationContext)));
    }

    @Nullable
    public static TracingHeaders traceIfAllowed(@NotNull IScopes scopes, @NotNull String requestUrl, @Nullable List<String> thirdPartyBaggageHeaders, @Nullable ISpan span) {
        @NotNull SentryOptions sentryOptions = scopes.getOptions();
        if (sentryOptions.isTraceSampling() && TracingUtils.shouldAttachTracingHeaders(requestUrl, sentryOptions)) {
            return TracingUtils.trace(scopes, thirdPartyBaggageHeaders, span);
        }
        return null;
    }

    @Nullable
    public static TracingHeaders trace(@NotNull IScopes scopes, @Nullable List<String> thirdPartyBaggageHeaders, @Nullable ISpan span) {
        @NotNull SentryOptions sentryOptions = scopes.getOptions();
        if (span != null && !span.isNoOp()) {
            @NotNull SentryTraceHeader sentryTraceHeader = span.toSentryTrace();
            @Nullable BaggageHeader baggageHeader = span.toBaggageHeader(thirdPartyBaggageHeaders);
            W3CTraceparentHeader w3cTraceparentHeader = null;
            if (sentryOptions.isPropagateTraceparent()) {
                @NotNull SpanContext spanContext = span.getSpanContext();
                w3cTraceparentHeader = new W3CTraceparentHeader(spanContext.getTraceId(), spanContext.getSpanId(), sentryTraceHeader.isSampled());
            }
            return new TracingHeaders(sentryTraceHeader, baggageHeader, w3cTraceparentHeader);
        }
        @NotNull PropagationContextHolder returnValue = new PropagationContextHolder();
        scopes.configureScope(scope -> returnValue.propagationContext = TracingUtils.maybeUpdateBaggage(scope, sentryOptions));
        if (returnValue.propagationContext != null) {
            @NotNull PropagationContext propagationContext = returnValue.propagationContext;
            @NotNull Baggage baggage = propagationContext.getBaggage();
            @NotNull BaggageHeader baggageHeader = BaggageHeader.fromBaggageAndOutgoingHeader(baggage, thirdPartyBaggageHeaders);
            @NotNull SentryTraceHeader sentryTraceHeader = new SentryTraceHeader(propagationContext.getTraceId(), propagationContext.getSpanId(), propagationContext.isSampled());
            W3CTraceparentHeader w3cTraceparentHeader = null;
            if (sentryOptions.isPropagateTraceparent()) {
                w3cTraceparentHeader = new W3CTraceparentHeader(propagationContext.getTraceId(), propagationContext.getSpanId(), propagationContext.isSampled());
            }
            return new TracingHeaders(sentryTraceHeader, baggageHeader, w3cTraceparentHeader);
        }
        return null;
    }

    @NotNull
    public static PropagationContext maybeUpdateBaggage(@NotNull IScope scope, @NotNull SentryOptions sentryOptions) {
        return scope.withPropagationContext(propagationContext -> {
            @NotNull Baggage baggage = propagationContext.getBaggage();
            if (baggage.isMutable()) {
                baggage.setValuesFromScope(scope, sentryOptions);
                baggage.freeze();
            }
        });
    }

    private static boolean shouldAttachTracingHeaders(@NotNull String requestUrl, @NotNull SentryOptions sentryOptions) {
        return PropagationTargetsUtils.contain(sentryOptions.getTracePropagationTargets(), requestUrl);
    }

    @ApiStatus.Internal
    public static boolean isIgnored(@Nullable List<FilterString> ignoredTransactions, @Nullable String transactionName) {
        if (transactionName == null) {
            return false;
        }
        if (ignoredTransactions == null || ignoredTransactions.isEmpty()) {
            return false;
        }
        for (FilterString ignoredTransaction : ignoredTransactions) {
            if (!ignoredTransaction.getFilterString().equalsIgnoreCase(transactionName)) continue;
            return true;
        }
        for (FilterString ignoredTransaction : ignoredTransactions) {
            try {
                if (!ignoredTransaction.matches(transactionName)) continue;
                return true;
            }
            catch (Throwable throwable) {
            }
        }
        return false;
    }

    @ApiStatus.Internal
    @NotNull
    public static Baggage ensureBaggage(@Nullable Baggage incomingBaggage, @Nullable TracesSamplingDecision decision) {
        @Nullable Boolean decisionSampled = decision == null ? null : decision.getSampled();
        @Nullable Double decisionSampleRate = decision == null ? null : decision.getSampleRate();
        @Nullable Double decisionSampleRand = decision == null ? null : decision.getSampleRand();
        return TracingUtils.ensureBaggage(incomingBaggage, decisionSampled, decisionSampleRate, decisionSampleRand);
    }

    @ApiStatus.Internal
    @NotNull
    public static Baggage ensureBaggage(@Nullable Baggage incomingBaggage, @Nullable Boolean decisionSampled, @Nullable Double decisionSampleRate, @Nullable Double decisionSampleRand) {
        Baggage baggage;
        Baggage baggage2 = baggage = incomingBaggage == null ? new Baggage(NoOpLogger.getInstance()) : incomingBaggage;
        if (baggage.getSampleRand() == null) {
            @Nullable Double baggageSampleRate = baggage.getSampleRate();
            @Nullable Double sampleRateMaybe = baggageSampleRate == null ? decisionSampleRate : baggageSampleRate;
            @NotNull Double sampleRand = SampleRateUtils.backfilledSampleRand(decisionSampleRand, sampleRateMaybe, decisionSampled);
            baggage.setSampleRand(sampleRand);
        }
        if (baggage.isMutable() && baggage.isShouldFreeze()) {
            baggage.freeze();
        }
        return baggage;
    }

    public static final class TracingHeaders {
        @NotNull
        private final SentryTraceHeader sentryTraceHeader;
        @Nullable
        private final BaggageHeader baggageHeader;
        @Nullable
        private final W3CTraceparentHeader w3cTraceparentHeader;

        public TracingHeaders(@NotNull SentryTraceHeader sentryTraceHeader, @Nullable BaggageHeader baggageHeader) {
            this.sentryTraceHeader = sentryTraceHeader;
            this.baggageHeader = baggageHeader;
            this.w3cTraceparentHeader = null;
        }

        public TracingHeaders(@NotNull SentryTraceHeader sentryTraceHeader, @Nullable BaggageHeader baggageHeader, @Nullable W3CTraceparentHeader w3cTraceparentHeader) {
            this.sentryTraceHeader = sentryTraceHeader;
            this.baggageHeader = baggageHeader;
            this.w3cTraceparentHeader = w3cTraceparentHeader;
        }

        @NotNull
        public SentryTraceHeader getSentryTraceHeader() {
            return this.sentryTraceHeader;
        }

        @Nullable
        public BaggageHeader getBaggageHeader() {
            return this.baggageHeader;
        }

        @Nullable
        public W3CTraceparentHeader getW3cTraceparentHeader() {
            return this.w3cTraceparentHeader;
        }
    }

    private static final class PropagationContextHolder {
        @Nullable
        private PropagationContext propagationContext = null;

        private PropagationContextHolder() {
        }
    }
}

