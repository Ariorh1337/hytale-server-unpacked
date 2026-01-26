/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

final class DateTimeFormatter {
    private static final int DATE_STRING_LENGTH = "1970-01-01".length();

    static long parse(String dateTimeString) {
        if (dateTimeString.length() == DATE_STRING_LENGTH) {
            return LocalDate.parse(dateTimeString, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        }
        return java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse((CharSequence)dateTimeString, new TemporalQuery<Instant>(){

            @Override
            public Instant queryFrom(TemporalAccessor temporal) {
                return Instant.from(temporal);
            }
        }).toEpochMilli();
    }

    static String format(long dateTime) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of("Z")).format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private DateTimeFormatter() {
    }
}

