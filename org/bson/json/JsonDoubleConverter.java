/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class JsonDoubleConverter
implements Converter<Double> {
    JsonDoubleConverter() {
    }

    @Override
    public void convert(Double value, StrictJsonWriter writer) {
        writer.writeNumber(Double.toString(value));
    }
}

