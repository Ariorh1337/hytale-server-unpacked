package io.sentry.metrics;

import io.sentry.SentryClient;
import io.sentry.SentryOptions;
import org.jetbrains.annotations.NotNull;

public interface IMetricsBatchProcessorFactory {
   @NotNull
   IMetricsBatchProcessor create(@NotNull SentryOptions var1, @NotNull SentryClient var2);
}
