/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonRegularExpression;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonRegularExpressionCodec
implements Codec<BsonRegularExpression> {
    @Override
    public BsonRegularExpression decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readRegularExpression();
    }

    @Override
    public void encode(BsonWriter writer, BsonRegularExpression value, EncoderContext encoderContext) {
        writer.writeRegularExpression(value);
    }

    @Override
    public Class<BsonRegularExpression> getEncoderClass() {
        return BsonRegularExpression.class;
    }
}

