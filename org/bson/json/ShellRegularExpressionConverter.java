/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonRegularExpression;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ShellRegularExpressionConverter
implements Converter<BsonRegularExpression> {
    ShellRegularExpressionConverter() {
    }

    @Override
    public void convert(BsonRegularExpression value, StrictJsonWriter writer) {
        String escaped = value.getPattern().equals("") ? "(?:)" : value.getPattern().replace("/", "\\/");
        writer.writeRaw("/" + escaped + "/" + value.getOptions());
    }
}

