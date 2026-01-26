/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.ExtendedJsonDoubleConverter;
import org.bson.json.StrictJsonWriter;

class RelaxedExtendedJsonDoubleConverter
implements Converter<Double> {
    private static final Converter<Double> FALLBACK_CONVERTER = new ExtendedJsonDoubleConverter();

    RelaxedExtendedJsonDoubleConverter() {
    }

    @Override
    public void convert(Double value, StrictJsonWriter writer) {
        if (value.isNaN() || value.isInfinite()) {
            FALLBACK_CONVERTER.convert(value, writer);
        } else {
            writer.writeNumber(Double.toString(value));
        }
    }
}

