/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonUndefined;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ShellUndefinedConverter
implements Converter<BsonUndefined> {
    ShellUndefinedConverter() {
    }

    @Override
    public void convert(BsonUndefined value, StrictJsonWriter writer) {
        writer.writeRaw("undefined");
    }
}

