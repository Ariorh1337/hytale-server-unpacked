/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonDbPointer;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonDBPointerCodec
implements Codec<BsonDbPointer> {
    @Override
    public BsonDbPointer decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readDBPointer();
    }

    @Override
    public void encode(BsonWriter writer, BsonDbPointer value, EncoderContext encoderContext) {
        writer.writeDBPointer(value);
    }

    @Override
    public Class<BsonDbPointer> getEncoderClass() {
        return BsonDbPointer.class;
    }
}

