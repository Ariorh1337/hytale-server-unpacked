/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ShellInt64Converter
implements Converter<Long> {
    ShellInt64Converter() {
    }

    @Override
    public void convert(Long value, StrictJsonWriter writer) {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            writer.writeRaw(String.format("NumberLong(%d)", value));
        } else {
            writer.writeRaw(String.format("NumberLong(\"%d\")", value));
        }
    }
}

