/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonBinary;
import org.bson.internal.Base64;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class LegacyExtendedJsonBinaryConverter
implements Converter<BsonBinary> {
    LegacyExtendedJsonBinaryConverter() {
    }

    @Override
    public void convert(BsonBinary value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeString("$binary", Base64.encode(value.getData()));
        writer.writeString("$type", String.format("%02X", value.getType()));
        writer.writeEndObject();
    }
}

