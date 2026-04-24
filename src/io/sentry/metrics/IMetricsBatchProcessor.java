package io.sentry.metrics;

import io.sentry.SentryMetricsEvent;
import org.jetbrains.annotations.NotNull;

public interface IMetricsBatchProcessor {
   void add(@NotNull SentryMetricsEvent var1);

   void close(boolean var1);

   void flush(long var1);
}
