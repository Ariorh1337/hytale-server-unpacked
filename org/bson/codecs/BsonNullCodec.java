/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonNull;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonNullCodec
implements Codec<BsonNull> {
    @Override
    public BsonNull decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readNull();
        return BsonNull.VALUE;
    }

    @Override
    public void encode(BsonWriter writer, BsonNull value, EncoderContext encoderContext) {
        writer.writeNull();
    }

    @Override
    public Class<BsonNull> getEncoderClass() {
        return BsonNull.class;
    }
}

