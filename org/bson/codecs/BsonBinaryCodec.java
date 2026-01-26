/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonBinaryCodec
implements Codec<BsonBinary> {
    @Override
    public void encode(BsonWriter writer, BsonBinary value, EncoderContext encoderContext) {
        writer.writeBinaryData(value);
    }

    @Override
    public BsonBinary decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readBinaryData();
    }

    @Override
    public Class<BsonBinary> getEncoderClass() {
        return BsonBinary.class;
    }
}

