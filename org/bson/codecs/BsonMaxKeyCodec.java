/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonMaxKey;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonMaxKeyCodec
implements Codec<BsonMaxKey> {
    @Override
    public void encode(BsonWriter writer, BsonMaxKey value, EncoderContext encoderContext) {
        writer.writeMaxKey();
    }

    @Override
    public BsonMaxKey decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readMaxKey();
        return new BsonMaxKey();
    }

    @Override
    public Class<BsonMaxKey> getEncoderClass() {
        return BsonMaxKey.class;
    }
}

