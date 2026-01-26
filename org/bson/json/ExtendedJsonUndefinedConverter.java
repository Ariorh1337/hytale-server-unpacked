/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonUndefined;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonUndefinedConverter
implements Converter<BsonUndefined> {
    ExtendedJsonUndefinedConverter() {
    }

    @Override
    public void convert(BsonUndefined value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeBoolean("$undefined", true);
        writer.writeEndObject();
    }
}

