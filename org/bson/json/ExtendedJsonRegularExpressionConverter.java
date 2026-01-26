/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonRegularExpression;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ExtendedJsonRegularExpressionConverter
implements Converter<BsonRegularExpression> {
    ExtendedJsonRegularExpressionConverter() {
    }

    @Override
    public void convert(BsonRegularExpression value, StrictJsonWriter writer) {
        writer.writeStartObject();
        writer.writeStartObject("$regularExpression");
        writer.writeString("pattern", value.getPattern());
        writer.writeString("options", value.getOptions());
        writer.writeEndObject();
        writer.writeEndObject();
    }
}

