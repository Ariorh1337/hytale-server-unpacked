package io.sentry;

import io.sentry.util.Objects;
import io.sentry.util.SampleRateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class TracesSampler {
   @NotNull
   private final SentryOptions options;

   public TracesSampler(@NotNull SentryOptions options) {
      this.options = Objects.requireNonNull(options, "options are required");
   }

   @NotNull
   public TracesSamplingDecision sample(@NotNull SamplingContext samplingContext) {
      Double sampleRand = samplingContext.getSampleRand();
      TracesSamplingDecision samplingContextSamplingDecision = samplingContext.getTransactionContext().getSamplingDecision();
      if (samplingContextSamplingDecision != null) {
         return SampleRateUtils.backfilledSampleRand(samplingContextSamplingDecision);
      }

      Double profilesSampleRate = null;
      if (this.options.getProfilesSampler() != null) {
         try {
            profilesSampleRate = this.options.getProfilesSampler().sample(samplingContext);
         } catch (Throwable t) {
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
         } catch (Throwable t) {
            this.options.getLogger().log(SentryLevel.ERROR, "Error in the 'TracesSamplerCallback' callback.", t);
         }

         if (samplerResult != null) {
            return new TracesSamplingDecision(this.sample(samplerResult, sampleRand), samplerResult, sampleRand, profilesSampled, profilesSampleRate);
         }
      }

      TracesSamplingDecision parentSamplingDecision = samplingContext.getTransactionContext().getParentSamplingDecision();
      if (parentSamplingDecision != null) {
         return SampleRateUtils.backfilledSampleRand(parentSamplingDecision);
      }

      Double tracesSampleRateFromOptions = this.options.getTracesSampleRate();
      Double downsampleFactor = Math.pow(2.0, this.options.getBackpressureMonitor().getDownsampleFactor());
      Double downsampledTracesSampleRate = tracesSampleRateFromOptions == null ? null : tracesSampleRateFromOptions / downsampleFactor;
      return downsampledTracesSampleRate != null
         ? new TracesSamplingDecision(
            this.sample(downsampledTracesSampleRate, sampleRand), downsampledTracesSampleRate, sampleRand, profilesSampled, profilesSampleRate
         )
         : new TracesSamplingDecision(false, null, sampleRand, false, null);
   }

   public boolean sampleSessionProfile(double sampleRand) {
      Double sampling = this.options.getProfileSessionSampleRate();
      return sampling != null && this.sample(sampling, sampleRand);
   }

   private boolean sample(@NotNull Double sampleRate, @NotNull Double sampleRand) {
      return !(sampleRate < sampleRand);
   }
}
