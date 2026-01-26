/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonNull;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class JsonNullConverter
implements Converter<BsonNull> {
    JsonNullConverter() {
    }

    @Override
    public void convert(BsonNull value, StrictJsonWriter writer) {
        writer.writeNull();
    }
}

