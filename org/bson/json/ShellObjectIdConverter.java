/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;
import org.bson.types.ObjectId;

class ShellObjectIdConverter
implements Converter<ObjectId> {
    ShellObjectIdConverter() {
    }

    @Override
    public void convert(ObjectId value, StrictJsonWriter writer) {
        writer.writeRaw(String.format("ObjectId(\"%s\")", value.toHexString()));
    }
}

