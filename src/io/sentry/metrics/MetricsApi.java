package io.sentry.metrics;

import io.sentry.HostnameCache;
import io.sentry.IScope;
import io.sentry.ISpan;
import io.sentry.PropagationContext;
import io.sentry.Scopes;
import io.sentry.SentryAttribute;
import io.sentry.SentryAttributeType;
import io.sentry.SentryAttributes;
import io.sentry.SentryDate;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEventAttributeValue;
import io.sentry.SentryMetricsEvent;
import io.sentry.SentryOptions;
import io.sentry.SpanId;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.Platform;
import io.sentry.util.TracingUtils;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MetricsApi implements IMetricsApi {
   @NotNull
   private final Scopes scopes;

   public MetricsApi(@NotNull Scopes scopes) {
      this.scopes = scopes;
   }

   @Override
   public void count(@NotNull String name) {
      this.captureMetrics(SentryMetricsParameters.create(null, null), name, "counter", 1.0, null);
   }

   @Override
   public void count(@NotNull String name, @Nullable Double value) {
      this.captureMetrics(SentryMetricsParameters.create(null, null), name, "counter", value, null);
   }

   @Override
   public void count(@NotNull String name, @Nullable String unit) {
      this.captureMetrics(SentryMetricsParameters.create(null, null), name, "counter", 1.0, unit);
   }

   @Override
   public void count(@NotNull String name, @Nullable Double value, @Nullable String unit) {
      this.captureMetrics(SentryMetricsParameters.create(null, null), name, "counter", value, unit);
   }

   @Override
   public void count(@NotNull String name, @Nullable Double value, @Nullable String unit, @NotNull SentryMetricsParameters params) {
      this.captureMetrics(params, name, "counter", value, unit);
   }

   @Override
   public void distribution(@NotNull String name, @Nullable Double value) {
      this.captureMetrics(SentryMetricsParameters.create(null, null), name, "distribution", value, null);
   }

   @Override
   public void distribution(@NotNull String name, @Nullable Double value, @Nullable String unit) {
      this.captureMetrics(SentryMetricsParameters.create(null, null), name, "distribution", value, unit);
   }

   @Override
   public void distribution(@NotNull String name, @Nullable Double value, @Nullable String unit, @NotNull SentryMetricsParameters params) {
      this.captureMetrics(params, name, "distribution", value, unit);
   }

   @Override
   public void gauge(@NotNull String name, @Nullable Double value) {
      this.captureMetrics(SentryMetricsParameters.create(null, null), name, "gauge", value, null);
   }

   @Override
   public void gauge(@NotNull String name, @Nullable Double value, @Nullable String unit) {
      this.captureMetrics(SentryMetricsParameters.create(null, null), name, "gauge", value, unit);
   }

   @Override
   public void gauge(@NotNull String name, @Nullable Double value, @Nullable String unit, @NotNull SentryMetricsParameters params) {
      this.captureMetrics(params, name, "gauge", value, unit);
   }

   private void captureMetrics(
      @NotNull SentryMetricsParameters params, @Nullable String name, @Nullable String type, @Nullable Double value, @Nullable String unit
   ) {
      SentryOptions options = this.scopes.getOptions();

      try {
         if (!this.scopes.isEnabled()) {
            options.getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'metrics' call is a no-op.");
            return;
         }

         if (!options.getMetrics().isEnabled()) {
            options.getLogger().log(SentryLevel.WARNING, "Sentry Metrics is disabled and this 'metrics' call is a no-op.");
            return;
         }

         if (name == null) {
            return;
         }

         if (type == null) {
            return;
         }

         if (value == null) {
            return;
         }

         SentryDate timestamp = params.getTimestamp();
         SentryDate timestampToUse = timestamp == null ? options.getDateProvider().now() : timestamp;
         IScope combinedScope = this.scopes.getCombinedScopeView();
         PropagationContext propagationContext = combinedScope.getPropagationContext();
         ISpan span = combinedScope.getSpan();
         if (span == null) {
            TracingUtils.maybeUpdateBaggage(combinedScope, options);
         }

         SentryId traceId = span == null ? propagationContext.getTraceId() : span.getSpanContext().getTraceId();
         SpanId spanId = span == null ? propagationContext.getSpanId() : span.getSpanContext().getSpanId();
         SentryMetricsEvent metricsEvent = new SentryMetricsEvent(traceId, timestampToUse, name, type, value);
         metricsEvent.setSpanId(spanId);
         metricsEvent.setUnit(unit);
         metricsEvent.setAttributes(this.createAttributes(params));
         this.scopes.getClient().captureMetric(metricsEvent, combinedScope, params.getHint());
      } catch (Throwable e) {
         options.getLogger().log(SentryLevel.ERROR, "Error while capturing metrics event", e);
      }
   }

   @NotNull
   private HashMap<String, SentryLogEventAttributeValue> createAttributes(@NotNull SentryMetricsParameters params) {
      HashMap<String, SentryLogEventAttributeValue> attributes = new HashMap<>();
      String origin = params.getOrigin();
      if (!"manual".equalsIgnoreCase(origin)) {
         attributes.put("sentry.origin", new SentryLogEventAttributeValue(SentryAttributeType.STRING, origin));
      }

      SentryAttributes incomingAttributes = params.getAttributes();
      if (incomingAttributes != null) {
         for (SentryAttribute attribute : incomingAttributes.getAttributes().values()) {
            Object value = attribute.getValue();
            SentryAttributeType type = attribute.getType() == null ? this.getType(value) : attribute.getType();
            attributes.put(attribute.getName(), new SentryLogEventAttributeValue(type, value));
         }
      }

      SdkVersion sdkVersion = this.scopes.getOptions().getSdkVersion();
      if (sdkVersion != null) {
         attributes.put("sentry.sdk.name", new SentryLogEventAttributeValue(SentryAttributeType.STRING, sdkVersion.getName()));
         attributes.put("sentry.sdk.version", new SentryLogEventAttributeValue(SentryAttributeType.STRING, sdkVersion.getVersion()));
      }

      String environment = this.scopes.getOptions().getEnvironment();
      if (environment != null) {
         attributes.put("sentry.environment", new SentryLogEventAttributeValue(SentryAttributeType.STRING, environment));
      }

      SentryId scopeReplayId = this.scopes.getCombinedScopeView().getReplayId();
      if (!SentryId.EMPTY_ID.equals(scopeReplayId)) {
         attributes.put("sentry.replay_id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, scopeReplayId.toString()));
      } else {
         SentryId controllerReplayId = this.scopes.getOptions().getReplayController().getReplayId();
         if (!SentryId.EMPTY_ID.equals(controllerReplayId)) {
            attributes.put("sentry.replay_id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, controllerReplayId.toString()));
            attributes.put("sentry._internal.replay_is_buffering", new SentryLogEventAttributeValue(SentryAttributeType.BOOLEAN, true));
         }
      }

      String release = this.scopes.getOptions().getRelease();
      if (release != null) {
         attributes.put("sentry.release", new SentryLogEventAttributeValue(SentryAttributeType.STRING, release));
      }

      if (Platform.isJvm()) {
         this.setServerName(attributes);
      }

      if (this.scopes.getOptions().isSendDefaultPii()) {
         this.setUser(attributes);
      }

      return attributes;
   }

   private void setServerName(@NotNull HashMap<String, SentryLogEventAttributeValue> attributes) {
      SentryOptions options = this.scopes.getOptions();
      String optionsServerName = options.getServerName();
      if (optionsServerName != null) {
         attributes.put("server.address", new SentryLogEventAttributeValue(SentryAttributeType.STRING, optionsServerName));
      } else if (options.isAttachServerName()) {
         String hostname = HostnameCache.getInstance().getHostname();
         if (hostname != null) {
            attributes.put("server.address", new SentryLogEventAttributeValue(SentryAttributeType.STRING, hostname));
         }
      }
   }

   private void setUser(@NotNull HashMap<String, SentryLogEventAttributeValue> attributes) {
      User user = this.scopes.getCombinedScopeView().getUser();
      if (user == null) {
         String id = this.scopes.getOptions().getDistinctId();
         if (id != null) {
            attributes.put("user.id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, id));
         }
      } else {
         String id = user.getId();
         if (id != null) {
            attributes.put("user.id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, id));
         }

         String username = user.getUsername();
         if (username != null) {
            attributes.put("user.name", new SentryLogEventAttributeValue(SentryAttributeType.STRING, username));
         }

         String email = user.getEmail();
         if (email != null) {
            attributes.put("user.email", new SentryLogEventAttributeValue(SentryAttributeType.STRING, email));
         }
      }
   }

   @NotNull
   private SentryAttributeType getType(@Nullable Object arg) {
      if (arg instanceof Boolean) {
         return SentryAttributeType.BOOLEAN;
      } else if (arg instanceof Integer) {
         return SentryAttributeType.INTEGER;
      } else {
         return arg instanceof Number ? SentryAttributeType.DOUBLE : SentryAttributeType.STRING;
      }
   }
}
