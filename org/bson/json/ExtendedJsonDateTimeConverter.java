/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonDateTimeConverter
implements Converter<Long> {
    ExtendedJsonDateTimeConverter() {
    }

    @Override
    public void convert(Long value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeStartObject("$date");
        writer.writeString("$numberLong", Long.toString(value));
        writer.writeEndObject();
        writer.writeEndObject();
    }
}

