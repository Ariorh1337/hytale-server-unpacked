/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonUndefined;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonUndefinedCodec
implements Codec<BsonUndefined> {
    @Override
    public BsonUndefined decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readUndefined();
        return new BsonUndefined();
    }

    @Override
    public void encode(BsonWriter writer, BsonUndefined value, EncoderContext encoderContext) {
        writer.writeUndefined();
    }

    @Override
    public Class<BsonUndefined> getEncoderClass() {
        return BsonUndefined.class;
    }
}

