package io.sentry.metrics;

public final class MetricsUnit {
   private MetricsUnit() {
   }

   public static final class Duration {
      public static final String NANOSECOND = "nanosecond";
      public static final String MICROSECOND = "microsecond";
      public static final String MILLISECOND = "millisecond";
      public static final String SECOND = "second";
      public static final String MINUTE = "minute";
      public static final String HOUR = "hour";
      public static final String DAY = "day";
      public static final String WEEK = "week";

      private Duration() {
      }
   }

   public static final class Fraction {
      public static final String RATIO = "ratio";
      public static final String PERCENT = "percent";

      private Fraction() {
      }
   }

   public static final class Information {
      public static final String BIT = "bit";
      public static final String BYTE = "byte";
      public static final String KILOBYTE = "kilobyte";
      public static final String KIBIBYTE = "kibibyte";
      public static final String MEGABYTE = "megabyte";
      public static final String MEBIBYTE = "mebibyte";
      public static final String GIGABYTE = "gigabyte";
      public static final String GIBIBYTE = "gibibyte";
      public static final String TERABYTE = "terabyte";
      public static final String TEBIBYTE = "tebibyte";
      public static final String PETABYTE = "petabyte";
      public static final String PEBIBYTE = "pebibyte";
      public static final String EXABYTE = "exabyte";
      public static final String EXBIBYTE = "exbibyte";

      private Information() {
      }
   }
}
