/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BooleanCodec
implements Codec<Boolean> {
    @Override
    public void encode(BsonWriter writer, Boolean value, EncoderContext encoderContext) {
        writer.writeBoolean(value);
    }

    @Override
    public Boolean decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readBoolean();
    }

    @Override
    public Class<Boolean> getEncoderClass() {
        return Boolean.class;
    }
}

