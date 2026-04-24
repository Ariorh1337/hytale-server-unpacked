package org.bson.json;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

final class DateTimeFormatter {
   private static final int DATE_STRING_LENGTH = "1970-01-01".length();

   static long parse(String dateTimeString) {
      return dateTimeString.length() == DATE_STRING_LENGTH
         ? LocalDate.parse(dateTimeString, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
         : java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(dateTimeString, temporal -> Instant.from(temporal)).toEpochMilli();
   }

   static String format(long dateTime) {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of("Z")).format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME);
   }

   private DateTimeFormatter() {
   }
}
