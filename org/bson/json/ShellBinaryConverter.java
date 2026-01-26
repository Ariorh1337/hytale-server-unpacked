/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonBinary;
import org.bson.internal.Base64;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ShellBinaryConverter
implements Converter<BsonBinary> {
    ShellBinaryConverter() {
    }

    @Override
    public void convert(BsonBinary value, StrictJsonWriter writer) {
        writer.writeRaw(String.format("new BinData(%s, \"%s\")", Integer.toString(value.getType() & 0xFF), Base64.encode(value.getData())));
    }
}

