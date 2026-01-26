/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class LegacyExtendedJsonDateTimeConverter
implements Converter<Long> {
    LegacyExtendedJsonDateTimeConverter() {
    }

    @Override
    public void convert(Long value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeNumber("$date", Long.toString(value));
        writer.writeEndObject();
    }
}

