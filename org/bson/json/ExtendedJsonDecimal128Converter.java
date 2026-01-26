/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;
import org.bson.types.Decimal128;

class ExtendedJsonDecimal128Converter
implements Converter<Decimal128> {
    ExtendedJsonDecimal128Converter() {
    }

    @Override
    public void convert(Decimal128 value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeName("$numberDecimal");
        writer.writeString(value.toString());
        writer.writeEndObject();
    }
}

