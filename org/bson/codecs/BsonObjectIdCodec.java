/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonObjectIdCodec
implements Codec<BsonObjectId> {
    @Override
    public void encode(BsonWriter writer, BsonObjectId value, EncoderContext encoderContext) {
        writer.writeObjectId(value.getValue());
    }

    @Override
    public BsonObjectId decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonObjectId(reader.readObjectId());
    }

    @Override
    public Class<BsonObjectId> getEncoderClass() {
        return BsonObjectId.class;
    }
}

