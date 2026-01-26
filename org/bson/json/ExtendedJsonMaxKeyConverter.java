/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonMaxKey;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonMaxKeyConverter
implements Converter<BsonMaxKey> {
    ExtendedJsonMaxKeyConverter() {
    }

    @Override
    public void convert(BsonMaxKey value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeNumber("$maxKey", "1");
        writer.writeEndObject();
    }
}

