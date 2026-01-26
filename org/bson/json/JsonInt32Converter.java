/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class JsonInt32Converter
implements Converter<Integer> {
    JsonInt32Converter() {
    }

    @Override
    public void convert(Integer value, StrictJsonWriter writer) {
        writer.writeNumber(Integer.toString(value));
    }
}

