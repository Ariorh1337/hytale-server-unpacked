/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.NumberCodecHelper;

public class DoubleCodec
implements Codec<Double> {
    @Override
    public void encode(BsonWriter writer, Double value, EncoderContext encoderContext) {
        writer.writeDouble(value);
    }

    @Override
    public Double decode(BsonReader reader, DecoderContext decoderContext) {
        return NumberCodecHelper.decodeDouble(reader);
    }

    @Override
    public Class<Double> getEncoderClass() {
        return Double.class;
    }
}

