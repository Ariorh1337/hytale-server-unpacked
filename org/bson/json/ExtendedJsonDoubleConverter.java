/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonDoubleConverter
implements Converter<Double> {
    ExtendedJsonDoubleConverter() {
    }

    @Override
    public void convert(Double value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeName("$numberDouble");
        writer.writeString(Double.toString(value));
        writer.writeEndObject();
    }
}

