/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;
import org.bson.types.ObjectId;

class ExtendedJsonObjectIdConverter
implements Converter<ObjectId> {
    ExtendedJsonObjectIdConverter() {
    }

    @Override
    public void convert(ObjectId value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeString("$oid", value.toHexString());
        writer.writeEndObject();
    }
}

