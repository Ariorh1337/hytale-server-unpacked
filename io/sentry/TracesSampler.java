/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.SamplingContext;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.TracesSamplingDecision;
import io.sentry.util.Objects;
import io.sentry.util.SampleRateUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class TracesSampler {
    @NotNull
    private final SentryOptions options;

    public TracesSampler(@NotNull SentryOptions options) {
        this.options = Objects.requireNonNull(options, "options are required");
    }

    @NotNull
    public TracesSamplingDecision sample(@NotNull SamplingContext samplingContext) {
        Double downsampledTracesSampleRate;
        TracesSamplingDecision parentSamplingDecision;
        @NotNull Double sampleRand = samplingContext.getSampleRand();
        TracesSamplingDecision samplingContextSamplingDecision = samplingContext.getTransactionContext().getSamplingDecision();
        if (samplingContextSamplingDecision != null) {
            return SampleRateUtils.backfilledSampleRand(samplingContextSamplingDecision);
        }
        Double profilesSampleRate = null;
        if (this.options.getProfilesSampler() != null) {
            try {
                profilesSampleRate = this.options.getProfilesSampler().sample(samplingContext);
            }
            catch (Throwable t) {
                this.options.getLogger().log(SentryLevel.ERROR, "Error in the 'ProfilesSamplerCallback' callback.", t);
            }
        }
        if (profilesSampleRate == null) {
            profilesSampleRate = this.options.getProfilesSampleRate();
        }
        Boolean profilesSampled = profilesSampleRate != null && this.sample(profilesSampleRate, sampleRand);
        if (this.options.getTracesSampler() != null) {
            Double samplerResult = null;
            try {
                samplerResult = this.options.getTracesSampler().sample(samplingContext);
            }
            catch (Throwable t) {
                this.options.getLogger().log(SentryLevel.ERROR, "Error in the 'TracesSamplerCallback' callback.", t);
            }
            if (samplerResult != null) {
                return new TracesSamplingDecision(this.sample(samplerResult, sampleRand), samplerResult, sampleRand, profilesSampled, profilesSampleRate);
            }
        }
        if ((parentSamplingDecision = samplingContext.getTransactionContext().getParentSamplingDecision()) != null) {
            return SampleRateUtils.backfilledSampleRand(parentSamplingDecision);
        }
        @Nullable Double tracesSampleRateFromOptions = this.options.getTracesSampleRate();
        @NotNull Double downsampleFactor = Math.pow(2.0, this.options.getBackpressureMonitor().getDownsampleFactor());
        Double d = downsampledTracesSampleRate = tracesSampleRateFromOptions == null ? null : Double.valueOf(tracesSampleRateFromOptions / downsampleFactor);
        if (downsampledTracesSampleRate != null) {
            return new TracesSamplingDecision(this.sample(downsampledTracesSampleRate, sampleRand), downsampledTracesSampleRate, sampleRand, profilesSampled, profilesSampleRate);
        }
        return new TracesSamplingDecision(false, null, sampleRand, false, null);
    }

    public boolean sampleSessionProfile(double sampleRand) {
        @Nullable Double sampling = this.options.getProfileSessionSampleRate();
        return sampling != null && this.sample(sampling, sampleRand);
    }

    private boolean sample(@NotNull Double sampleRate, @NotNull Double sampleRand) {
        return !(sampleRate < sampleRand);
    }
}

