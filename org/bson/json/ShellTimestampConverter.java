/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonTimestamp;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ShellTimestampConverter
implements Converter<BsonTimestamp> {
    ShellTimestampConverter() {
    }

    @Override
    public void convert(BsonTimestamp value, StrictJsonWriter writer) {
        writer.writeRaw(String.format("Timestamp(%d, %d)", value.getTime(), value.getInc()));
    }
}

