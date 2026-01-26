/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonSymbol;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonSymbolCodec
implements Codec<BsonSymbol> {
    @Override
    public BsonSymbol decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonSymbol(reader.readSymbol());
    }

    @Override
    public void encode(BsonWriter writer, BsonSymbol value, EncoderContext encoderContext) {
        writer.writeSymbol(value.getSymbol());
    }

    @Override
    public Class<BsonSymbol> getEncoderClass() {
        return BsonSymbol.class;
    }
}

