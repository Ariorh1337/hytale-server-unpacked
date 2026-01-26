/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonDecimal128;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonDecimal128Codec
implements Codec<BsonDecimal128> {
    @Override
    public BsonDecimal128 decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonDecimal128(reader.readDecimal128());
    }

    @Override
    public void encode(BsonWriter writer, BsonDecimal128 value, EncoderContext encoderContext) {
        writer.writeDecimal128(value.getValue());
    }

    @Override
    public Class<BsonDecimal128> getEncoderClass() {
        return BsonDecimal128.class;
    }
}

