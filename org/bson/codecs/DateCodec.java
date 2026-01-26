/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.Date;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class DateCodec
implements Codec<Date> {
    @Override
    public void encode(BsonWriter writer, Date value, EncoderContext encoderContext) {
        writer.writeDateTime(value.getTime());
    }

    @Override
    public Date decode(BsonReader reader, DecoderContext decoderContext) {
        return new Date(reader.readDateTime());
    }

    @Override
    public Class<Date> getEncoderClass() {
        return Date.class;
    }
}

