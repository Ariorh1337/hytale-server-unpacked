/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonStringCodec
implements Codec<BsonString> {
    @Override
    public BsonString decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonString(reader.readString());
    }

    @Override
    public void encode(BsonWriter writer, BsonString value, EncoderContext encoderContext) {
        writer.writeString(value.getValue());
    }

    @Override
    public Class<BsonString> getEncoderClass() {
        return BsonString.class;
    }
}

