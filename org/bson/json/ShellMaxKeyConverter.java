/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonMaxKey;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ShellMaxKeyConverter
implements Converter<BsonMaxKey> {
    ShellMaxKeyConverter() {
    }

    @Override
    public void convert(BsonMaxKey value, StrictJsonWriter writer) {
        writer.writeRaw("MaxKey");
    }
}

