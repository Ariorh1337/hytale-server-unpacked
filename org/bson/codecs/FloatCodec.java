/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.NumberCodecHelper;

public class FloatCodec
implements Codec<Float> {
    @Override
    public void encode(BsonWriter writer, Float value, EncoderContext encoderContext) {
        writer.writeDouble(value.floatValue());
    }

    @Override
    public Float decode(BsonReader reader, DecoderContext decoderContext) {
        double value = NumberCodecHelper.decodeDouble(reader);
        if (value < -3.4028234663852886E38 || value > 3.4028234663852886E38) {
            throw new BsonInvalidOperationException(String.format("%s can not be converted into a Float.", value));
        }
        return Float.valueOf((float)value);
    }

    @Override
    public Class<Float> getEncoderClass() {
        return Float.class;
    }
}

