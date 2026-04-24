package io.sentry.metrics;

import io.sentry.Hint;
import io.sentry.SentryAttributes;
import io.sentry.SentryDate;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryMetricsParameters {
   @Nullable
   private SentryDate timestamp;
   @Nullable
   private SentryAttributes attributes;
   @NotNull
   private String origin = "manual";
   @Nullable
   private Hint hint = null;

   @Nullable
   public SentryDate getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(@Nullable SentryDate timestamp) {
      this.timestamp = timestamp;
   }

   @Nullable
   public SentryAttributes getAttributes() {
      return this.attributes;
   }

   public void setAttributes(@Nullable SentryAttributes attributes) {
      this.attributes = attributes;
   }

   @NotNull
   public String getOrigin() {
      return this.origin;
   }

   public void setOrigin(@NotNull String origin) {
      this.origin = origin;
   }

   @Nullable
   public Hint getHint() {
      return this.hint;
   }

   public void setHint(@Nullable Hint hint) {
      this.hint = hint;
   }

   @NotNull
   public static SentryMetricsParameters create(@Nullable SentryDate timestamp, @Nullable SentryAttributes attributes) {
      SentryMetricsParameters params = new SentryMetricsParameters();
      params.setTimestamp(timestamp);
      params.setAttributes(attributes);
      return params;
   }

   @NotNull
   public static SentryMetricsParameters create(@Nullable SentryAttributes attributes) {
      return create(null, attributes);
   }

   @NotNull
   public static SentryMetricsParameters create(@Nullable Map<String, Object> attributes) {
      return create(null, SentryAttributes.fromMap(attributes));
   }
}
