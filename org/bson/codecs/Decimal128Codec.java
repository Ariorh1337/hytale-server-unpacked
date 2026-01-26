/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Decimal128;

public final class Decimal128Codec
implements Codec<Decimal128> {
    @Override
    public Decimal128 decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readDecimal128();
    }

    @Override
    public void encode(BsonWriter writer, Decimal128 value, EncoderContext encoderContext) {
        writer.writeDecimal128(value);
    }

    @Override
    public Class<Decimal128> getEncoderClass() {
        return Decimal128.class;
    }
}

