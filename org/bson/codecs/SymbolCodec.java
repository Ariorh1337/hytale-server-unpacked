/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Symbol;

public class SymbolCodec
implements Codec<Symbol> {
    @Override
    public Symbol decode(BsonReader reader, DecoderContext decoderContext) {
        return new Symbol(reader.readSymbol());
    }

    @Override
    public void encode(BsonWriter writer, Symbol value, EncoderContext encoderContext) {
        writer.writeSymbol(value.getSymbol());
    }

    @Override
    public Class<Symbol> getEncoderClass() {
        return Symbol.class;
    }
}

