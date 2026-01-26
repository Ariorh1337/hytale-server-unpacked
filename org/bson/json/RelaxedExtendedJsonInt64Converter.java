/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class RelaxedExtendedJsonInt64Converter
implements Converter<Long> {
    RelaxedExtendedJsonInt64Converter() {
    }

    @Override
    public void convert(Long value, StrictJsonWriter writer) {
        writer.writeNumber(Long.toString(value));
    }
}

