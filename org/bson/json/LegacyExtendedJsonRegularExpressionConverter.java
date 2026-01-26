/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonRegularExpression;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class LegacyExtendedJsonRegularExpressionConverter
implements Converter<BsonRegularExpression> {
    LegacyExtendedJsonRegularExpressionConverter() {
    }

    @Override
    public void convert(BsonRegularExpression value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeString("$regex", value.getPattern());
        writer.writeString("$options", value.getOptions());
        writer.writeEndObject();
    }
}

