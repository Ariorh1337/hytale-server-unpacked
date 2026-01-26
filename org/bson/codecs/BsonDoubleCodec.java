/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonDouble;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonDoubleCodec
implements Codec<BsonDouble> {
    @Override
    public BsonDouble decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonDouble(reader.readDouble());
    }

    @Override
    public void encode(BsonWriter writer, BsonDouble value, EncoderContext encoderContext) {
        writer.writeDouble(value.getValue());
    }

    @Override
    public Class<BsonDouble> getEncoderClass() {
        return BsonDouble.class;
    }
}

