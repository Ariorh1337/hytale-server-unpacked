package io.sentry.metrics;

import io.sentry.SentryMetricsEvent;
import org.jetbrains.annotations.NotNull;

public final class NoOpMetricsBatchProcessor implements IMetricsBatchProcessor {
   private static final NoOpMetricsBatchProcessor instance = new NoOpMetricsBatchProcessor();

   private NoOpMetricsBatchProcessor() {
   }

   public static NoOpMetricsBatchProcessor getInstance() {
      return instance;
   }

   @Override
   public void add(@NotNull SentryMetricsEvent event) {
   }

   @Override
   public void close(boolean isRestarting) {
   }

   @Override
   public void flush(long timeoutMillis) {
   }
}
