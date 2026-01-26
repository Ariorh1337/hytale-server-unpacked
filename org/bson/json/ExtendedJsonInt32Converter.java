/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonInt32Converter
implements Converter<Integer> {
    ExtendedJsonInt32Converter() {
    }

    @Override
    public void convert(Integer value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeName("$numberInt");
        writer.writeString(Integer.toString(value));
        writer.writeEndObject();
    }
}

