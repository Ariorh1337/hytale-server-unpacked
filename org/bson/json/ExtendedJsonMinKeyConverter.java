/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonMinKey;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonMinKeyConverter
implements Converter<BsonMinKey> {
    ExtendedJsonMinKeyConverter() {
    }

    @Override
    public void convert(BsonMinKey value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeNumber("$minKey", "1");
        writer.writeEndObject();
    }
}

