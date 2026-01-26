/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonMinKey;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonMinKeyCodec
implements Codec<BsonMinKey> {
    @Override
    public void encode(BsonWriter writer, BsonMinKey value, EncoderContext encoderContext) {
        writer.writeMinKey();
    }

    @Override
    public BsonMinKey decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readMinKey();
        return new BsonMinKey();
    }

    @Override
    public Class<BsonMinKey> getEncoderClass() {
        return BsonMinKey.class;
    }
}

