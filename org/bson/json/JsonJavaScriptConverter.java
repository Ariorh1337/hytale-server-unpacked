/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class JsonJavaScriptConverter
implements Converter<String> {
    JsonJavaScriptConverter() {
    }

    @Override
    public void convert(String value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeString("$code", value);
        writer.writeEndObject();
    }
}

