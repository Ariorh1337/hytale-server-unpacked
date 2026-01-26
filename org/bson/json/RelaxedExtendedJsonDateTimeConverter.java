/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.DateTimeFormatter;
import org.bson.json.ExtendedJsonDateTimeConverter;
import org.bson.json.StrictJsonWriter;

class RelaxedExtendedJsonDateTimeConverter
implements Converter<Long> {
    private static final Converter<Long> FALLBACK_CONVERTER = new ExtendedJsonDateTimeConverter();
    private static final long LAST_MS_OF_YEAR_9999 = 253402300799999L;

    RelaxedExtendedJsonDateTimeConverter() {
    }

    @Override
    public void convert(Long value, StrictJsonWriter writer) {
        if (value < 0L || value > 253402300799999L) {
            FALLBACK_CONVERTER.convert(value, writer);
        } else {
            writer.writeStartObject();
            writer.writeString("$date", DateTimeFormatter.format(value));
            writer.writeEndObject();
        }
    }
}

