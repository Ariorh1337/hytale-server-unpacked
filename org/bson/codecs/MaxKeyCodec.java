/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.MaxKey;

public class MaxKeyCodec
implements Codec<MaxKey> {
    @Override
    public void encode(BsonWriter writer, MaxKey value, EncoderContext encoderContext) {
        writer.writeMaxKey();
    }

    @Override
    public MaxKey decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readMaxKey();
        return new MaxKey();
    }

    @Override
    public Class<MaxKey> getEncoderClass() {
        return MaxKey.class;
    }
}

