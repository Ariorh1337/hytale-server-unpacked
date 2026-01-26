/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonTimestamp;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonTimestampConverter
implements Converter<BsonTimestamp> {
    ExtendedJsonTimestampConverter() {
    }

    @Override
    public void convert(BsonTimestamp value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeStartObject("$timestamp");
        writer.writeNumber("t", Long.toUnsignedString(Integer.toUnsignedLong(value.getTime())));
        writer.writeNumber("i", Long.toUnsignedString(Integer.toUnsignedLong(value.getInc())));
        writer.writeEndObject();
        writer.writeEndObject();
    }
}

