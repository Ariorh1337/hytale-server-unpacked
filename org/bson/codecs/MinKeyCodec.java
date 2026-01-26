/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.MinKey;

public class MinKeyCodec
implements Codec<MinKey> {
    @Override
    public void encode(BsonWriter writer, MinKey value, EncoderContext encoderContext) {
        writer.writeMinKey();
    }

    @Override
    public MinKey decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readMinKey();
        return new MinKey();
    }

    @Override
    public Class<MinKey> getEncoderClass() {
        return MinKey.class;
    }
}

