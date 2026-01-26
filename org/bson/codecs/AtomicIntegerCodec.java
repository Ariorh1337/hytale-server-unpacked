/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.concurrent.atomic.AtomicInteger;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.NumberCodecHelper;

public class AtomicIntegerCodec
implements Codec<AtomicInteger> {
    @Override
    public void encode(BsonWriter writer, AtomicInteger value, EncoderContext encoderContext) {
        writer.writeInt32(value.intValue());
    }

    @Override
    public AtomicInteger decode(BsonReader reader, DecoderContext decoderContext) {
        return new AtomicInteger(NumberCodecHelper.decodeInt(reader));
    }

    @Override
    public Class<AtomicInteger> getEncoderClass() {
        return AtomicInteger.class;
    }
}

