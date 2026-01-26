/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonBoolean;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonBooleanCodec
implements Codec<BsonBoolean> {
    @Override
    public BsonBoolean decode(BsonReader reader, DecoderContext decoderContext) {
        boolean value = reader.readBoolean();
        return BsonBoolean.valueOf(value);
    }

    @Override
    public void encode(BsonWriter writer, BsonBoolean value, EncoderContext encoderContext) {
        writer.writeBoolean(value.getValue());
    }

    @Override
    public Class<BsonBoolean> getEncoderClass() {
        return BsonBoolean.class;
    }
}

