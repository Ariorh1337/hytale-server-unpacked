/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonBinary;
import org.bson.internal.Base64;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonBinaryConverter
implements Converter<BsonBinary> {
    ExtendedJsonBinaryConverter() {
    }

    @Override
    public void convert(BsonBinary value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeStartObject("$binary");
        writer.writeString("base64", Base64.encode(value.getData()));
        writer.writeString("subType", String.format("%02X", value.getType()));
        writer.writeEndObject();
        writer.writeEndObject();
    }
}

