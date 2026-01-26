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

public class IntegerCodec
implements Codec<Integer> {
    @Override
    public void encode(BsonWriter writer, Integer value, EncoderContext encoderContext) {
        writer.writeInt32(value);
    }

    @Override
    public Integer decode(BsonReader reader, DecoderContext decoderContext) {
        return NumberCodecHelper.decodeInt(reader);
    }

    @Override
    public Class<Integer> getEncoderClass() {
        return Integer.class;
    }
}

