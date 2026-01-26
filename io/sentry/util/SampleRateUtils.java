/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.TracesSamplingDecision;
import io.sentry.util.SentryRandom;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SampleRateUtils {
    public static boolean isValidSampleRate(@Nullable Double sampleRate) {
        return SampleRateUtils.isValidRate(sampleRate, true);
    }

    public static boolean isValidTracesSampleRate(@Nullable Double tracesSampleRate) {
        return SampleRateUtils.isValidTracesSampleRate(tracesSampleRate, true);
    }

    public static boolean isValidTracesSampleRate(@Nullable Double tracesSampleRate, boolean allowNull) {
        return SampleRateUtils.isValidRate(tracesSampleRate, allowNull);
    }

    public static boolean isValidProfilesSampleRate(@Nullable Double profilesSampleRate) {
        return SampleRateUtils.isValidRate(profilesSampleRate, true);
    }

    public static boolean isValidContinuousProfilesSampleRate(@Nullable Double profilesSampleRate) {
        return SampleRateUtils.isValidRate(profilesSampleRate, true);
    }

    @NotNull
    public static Double backfilledSampleRand(@Nullable Double sampleRand, @Nullable Double sampleRate, @Nullable Boolean sampled) {
        if (sampleRand != null) {
            return sampleRand;
        }
        double newSampleRand = SentryRandom.current().nextDouble();
        if (sampleRate != null && sampled != null) {
            if (sampled.booleanValue()) {
                return newSampleRand * sampleRate;
            }
            return sampleRate + newSampleRand * (1.0 - sampleRate);
        }
        return newSampleRand;
    }

    @NotNull
    public static TracesSamplingDecision backfilledSampleRand(@NotNull TracesSamplingDecision samplingDecision) {
        if (samplingDecision.getSampleRand() != null) {
            return samplingDecision;
        }
        @NotNull Double sampleRand = SampleRateUtils.backfilledSampleRand(null, samplingDecision.getSampleRate(), samplingDecision.getSampled());
        return new TracesSamplingDecision(samplingDecision.getSampled(), samplingDecision.getSampleRate(), sampleRand, samplingDecision.getProfileSampled(), samplingDecision.getProfileSampleRate());
    }

    private static boolean isValidRate(@Nullable Double rate, boolean allowNull) {
        if (rate == null) {
            return allowNull;
        }
        return !rate.isNaN() && rate >= 0.0 && rate <= 1.0;
    }
}

