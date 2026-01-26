/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonDateTime;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonDateTimeCodec
implements Codec<BsonDateTime> {
    @Override
    public BsonDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonDateTime(reader.readDateTime());
    }

    @Override
    public void encode(BsonWriter writer, BsonDateTime value, EncoderContext encoderContext) {
        writer.writeDateTime(value.getValue());
    }

    @Override
    public Class<BsonDateTime> getEncoderClass() {
        return BsonDateTime.class;
    }
}

