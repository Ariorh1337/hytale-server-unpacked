/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonInt64Converter
implements Converter<Long> {
    ExtendedJsonInt64Converter() {
    }

    @Override
    public void convert(Long value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeName("$numberLong");
        writer.writeString(Long.toString(value));
        writer.writeEndObject();
    }
}

