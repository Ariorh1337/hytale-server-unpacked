/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonInt64;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonInt64Codec
implements Codec<BsonInt64> {
    @Override
    public BsonInt64 decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonInt64(reader.readInt64());
    }

    @Override
    public void encode(BsonWriter writer, BsonInt64 value, EncoderContext encoderContext) {
        writer.writeInt64(value.getValue());
    }

    @Override
    public Class<BsonInt64> getEncoderClass() {
        return BsonInt64.class;
    }
}

