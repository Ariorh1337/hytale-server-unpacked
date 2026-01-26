/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.concurrent.atomic.AtomicBoolean;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class AtomicBooleanCodec
implements Codec<AtomicBoolean> {
    @Override
    public void encode(BsonWriter writer, AtomicBoolean value, EncoderContext encoderContext) {
        writer.writeBoolean(value.get());
    }

    @Override
    public AtomicBoolean decode(BsonReader reader, DecoderContext decoderContext) {
        return new AtomicBoolean(reader.readBoolean());
    }

    @Override
    public Class<AtomicBoolean> getEncoderClass() {
        return AtomicBoolean.class;
    }
}

