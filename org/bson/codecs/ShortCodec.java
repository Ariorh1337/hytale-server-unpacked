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

public class ShortCodec
implements Codec<Short> {
    @Override
    public void encode(BsonWriter writer, Short value, EncoderContext encoderContext) {
        writer.writeInt32(value.shortValue());
    }

    @Override
    public Short decode(BsonReader reader, DecoderContext decoderContext) {
        int value = NumberCodecHelper.decodeInt(reader);
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new BsonInvalidOperationException(String.format("%s can not be converted into a Short.", value));
        }
        return (short)value;
    }

    @Override
    public Class<Short> getEncoderClass() {
        return Short.class;
    }
}

