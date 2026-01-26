/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonInt32;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonInt32Codec
implements Codec<BsonInt32> {
    @Override
    public BsonInt32 decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonInt32(reader.readInt32());
    }

    @Override
    public void encode(BsonWriter writer, BsonInt32 value, EncoderContext encoderContext) {
        writer.writeInt32(value.getValue());
    }

    @Override
    public Class<BsonInt32> getEncoderClass() {
        return BsonInt32.class;
    }
}

