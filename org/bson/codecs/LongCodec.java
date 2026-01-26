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

public class LongCodec
implements Codec<Long> {
    @Override
    public void encode(BsonWriter writer, Long value, EncoderContext encoderContext) {
        writer.writeInt64(value);
    }

    @Override
    public Long decode(BsonReader reader, DecoderContext decoderContext) {
        return NumberCodecHelper.decodeLong(reader);
    }

    @Override
    public Class<Long> getEncoderClass() {
        return Long.class;
    }
}

