/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonMinKey;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ShellMinKeyConverter
implements Converter<BsonMinKey> {
    ShellMinKeyConverter() {
    }

    @Override
    public void convert(BsonMinKey value, StrictJsonWriter writer) {
        writer.writeRaw("MinKey");
    }
}

