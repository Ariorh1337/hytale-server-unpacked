/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.concurrent.atomic.AtomicLong;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.NumberCodecHelper;

public class AtomicLongCodec
implements Codec<AtomicLong> {
    @Override
    public void encode(BsonWriter writer, AtomicLong value, EncoderContext encoderContext) {
        writer.writeInt64(value.longValue());
    }

    @Override
    public AtomicLong decode(BsonReader reader, DecoderContext decoderContext) {
        return new AtomicLong(NumberCodecHelper.decodeLong(reader));
    }

    @Override
    public Class<AtomicLong> getEncoderClass() {
        return AtomicLong.class;
    }
}

