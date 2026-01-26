/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class JsonStringConverter
implements Converter<String> {
    JsonStringConverter() {
    }

    @Override
    public void convert(String value, StrictJsonWriter writer) {
        writer.writeString(value);
    }
}

