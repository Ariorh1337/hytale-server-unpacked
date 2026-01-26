/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;
import org.bson.types.Decimal128;

class ShellDecimal128Converter
implements Converter<Decimal128> {
    ShellDecimal128Converter() {
    }

    @Override
    public void convert(Decimal128 value, StrictJsonWriter writer) {
        writer.writeRaw(String.format("NumberDecimal(\"%s\")", value.toString()));
    }
}

