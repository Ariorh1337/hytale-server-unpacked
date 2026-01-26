/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class JsonSymbolConverter
implements Converter<String> {
    JsonSymbolConverter() {
    }

    @Override
    public void convert(String value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeString("$symbol", value);
        writer.writeEndObject();
    }
}

