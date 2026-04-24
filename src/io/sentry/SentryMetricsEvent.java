package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryMetricsEvent implements JsonUnknown, JsonSerializable {
   @NotNull
   private SentryId traceId;
   @Nullable
   private SpanId spanId;
   @NotNull
   private Double timestamp;
   @NotNull
   private String name;
   @Nullable
   private String unit;
   @NotNull
   private String type;
   @NotNull
   private Double value;
   @Nullable
   private Map<String, SentryLogEventAttributeValue> attributes;
   @Nullable
   private Map<String, Object> unknown;

   public SentryMetricsEvent(@NotNull SentryId traceId, @NotNull SentryDate timestamp, @NotNull String name, @NotNull String type, @NotNull Double value) {
      this(traceId, DateUtils.nanosToSeconds(timestamp.nanoTimestamp()), name, type, value);
   }

   public SentryMetricsEvent(@NotNull SentryId traceId, @NotNull Double timestamp, @NotNull String name, @NotNull String type, @NotNull Double value) {
      this.traceId = traceId;
      this.timestamp = timestamp;
      this.name = name;
      this.type = type;
      this.value = value;
   }

   @NotNull
   public Double getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(@NotNull Double timestamp) {
      this.timestamp = timestamp;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   public void setName(@NotNull String name) {
      this.name = name;
   }

   @NotNull
   public String getType() {
      return this.type;
   }

   public void setType(@NotNull String type) {
      this.type = type;
   }

   @Nullable
   public String getUnit() {
      return this.unit;
   }

   public void setUnit(@Nullable String unit) {
      this.unit = unit;
   }

   @Nullable
   public SpanId getSpanId() {
      return this.spanId;
   }

   public void setSpanId(@Nullable SpanId spanId) {
      this.spanId = spanId;
   }

   @NotNull
   public SentryId getTraceId() {
      return this.traceId;
   }

   public void setTraceId(@NotNull SentryId traceId) {
      this.traceId = traceId;
   }

   @NotNull
   public Double getValue() {
      return this.value;
   }

   public void setValue(@NotNull Double value) {
      this.value = value;
   }

   @Nullable
   public Map<String, SentryLogEventAttributeValue> getAttributes() {
      return this.attributes;
   }

   public void setAttributes(@Nullable Map<String, SentryLogEventAttributeValue> attributes) {
      this.attributes = attributes;
   }

   public void setAttribute(@Nullable String key, @Nullable SentryLogEventAttributeValue value) {
      if (key != null) {
         if (this.attributes == null) {
            this.attributes = new HashMap<>();
         }

         this.attributes.put(key, value);
      }
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("timestamp").value(logger, DateUtils.doubleToBigDecimal(this.timestamp));
      writer.name("type").value(this.type);
      writer.name("name").value(this.name);
      writer.name("value").value(this.value);
      writer.name("trace_id").value(logger, this.traceId);
      if (this.spanId != null) {
         writer.name("span_id").value(logger, this.spanId);
      }

      if (this.unit != null) {
         writer.name("unit").value(logger, this.unit);
      }

      if (this.attributes != null) {
         writer.name("attributes").value(logger, this.attributes);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   @Nullable
   @Override
   public Map<String, Object> getUnknown() {
      return this.unknown;
   }

   @Override
   public void setUnknown(@Nullable Map<String, Object> unknown) {
      this.unknown = unknown;
   }

   public static final class Deserializer implements JsonDeserializer<SentryMetricsEvent> {
      @NotNull
      public SentryMetricsEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> unknown = null;
         SentryId traceId = null;
         SpanId spanId = null;
         Double timestamp = null;
         String type = null;
         String name = null;
         String unit = null;
         Double value = null;
         Map<String, SentryLogEventAttributeValue> attributes = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "trace_id":
                  traceId = reader.nextOrNull(logger, new SentryId.Deserializer());
                  break;
               case "span_id":
                  spanId = reader.nextOrNull(logger, new SpanId.Deserializer());
                  break;
               case "timestamp":
                  timestamp = reader.nextDoubleOrNull();
                  break;
               case "type":
                  type = reader.nextStringOrNull();
                  break;
               case "name":
                  name = reader.nextStringOrNull();
                  break;
               case "unit":
                  unit = reader.nextStringOrNull();
                  break;
               case "value":
                  value = reader.nextDoubleOrNull();
                  break;
               case "attributes":
                  attributes = reader.nextMapOrNull(logger, new SentryLogEventAttributeValue.Deserializer());
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (traceId == null) {
            String message = "Missing required field \"trace_id\"";
            Exception exception = new IllegalStateException("Missing required field \"trace_id\"");
            logger.log(SentryLevel.ERROR, "Missing required field \"trace_id\"", exception);
            throw exception;
         } else if (timestamp == null) {
            String message = "Missing required field \"timestamp\"";
            Exception exception = new IllegalStateException("Missing required field \"timestamp\"");
            logger.log(SentryLevel.ERROR, "Missing required field \"timestamp\"", exception);
            throw exception;
         } else if (type == null) {
            String message = "Missing required field \"type\"";
            Exception exception = new IllegalStateException("Missing required field \"type\"");
            logger.log(SentryLevel.ERROR, "Missing required field \"type\"", exception);
            throw exception;
         } else if (name == null) {
            String message = "Missing required field \"name\"";
            Exception exception = new IllegalStateException("Missing required field \"name\"");
            logger.log(SentryLevel.ERROR, "Missing required field \"name\"", exception);
            throw exception;
         } else if (value == null) {
            String message = "Missing required field \"value\"";
            Exception exception = new IllegalStateException("Missing required field \"value\"");
            logger.log(SentryLevel.ERROR, "Missing required field \"value\"", exception);
            throw exception;
         } else {
            SentryMetricsEvent logEvent = new SentryMetricsEvent(traceId, timestamp, name, type, value);
            logEvent.setAttributes(attributes);
            logEvent.setSpanId(spanId);
            logEvent.setUnit(unit);
            logEvent.setUnknown(unknown);
            return logEvent;
         }
      }
   }

   public static final class JsonKeys {
      public static final String TIMESTAMP = "timestamp";
      public static final String TRACE_ID = "trace_id";
      public static final String SPAN_ID = "span_id";
      public static final String NAME = "name";
      public static final String TYPE = "type";
      public static final String UNIT = "unit";
      public static final String VALUE = "value";
      public static final String ATTRIBUTES = "attributes";
   }
}
